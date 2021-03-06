/*
 * EncryptedQuery is an open source project allowing user to query databases with queries under
 * homomorphic encryption to securing the query and results set from database owner inspection.
 * Copyright (C) 2018 EnQuery LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.enquery.encryptedquery.responder.it.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.sshd.common.util.io.IoUtils;
import org.enquery.encryptedquery.data.Query;
import org.enquery.encryptedquery.data.QuerySchema;
import org.enquery.encryptedquery.loader.SchemaLoader;
import org.enquery.encryptedquery.querier.encrypt.EncryptQuery;
import org.enquery.encryptedquery.querier.encrypt.Querier;
import org.enquery.encryptedquery.responder.business.execution.QueryExecutionScheduler;
import org.enquery.encryptedquery.responder.data.entity.DataSchema;
import org.enquery.encryptedquery.responder.data.entity.DataSource;
import org.enquery.encryptedquery.responder.data.entity.Execution;
import org.enquery.encryptedquery.responder.data.service.DataSourceRegistry;
import org.enquery.encryptedquery.responder.data.service.ExecutionRepository;
import org.enquery.encryptedquery.responder.data.service.ResultRepository;
import org.enquery.encryptedquery.responder.it.AbstractResponderItest;
import org.enquery.encryptedquery.xml.transformation.QueryTypeConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.service.cm.ConfigurationAdmin;
import org.quartz.JobExecutionException;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class QueryExecutionSchedulerIT extends AbstractResponderItest {

	private static final String QUERY_SCHEMA = "target/test-classes/schemas/get-price-query-schema.xml";
	private static final String DATA_SOURCE_NAME = "query-runner-mock";
	private static final String SELECTOR = "A Cup of Java";
	private static final List<String> SELECTORS = Arrays.asList(new String[] {SELECTOR});
	private static final Integer DATA_CHUNK_SIZE = 1;
	private static final Integer HASH_BIT_SIZE = 9;
	public static final int BIT_SIZE = 384;
	public static final int CERTAINTY = 128;


	@Inject
	private DataSourceRegistry dsRegistry;
	@Inject
	private ConfigurationAdmin confAdmin;
	@Inject
	private EncryptQuery querierFactory;
	@Inject
	private QueryExecutionScheduler scheduler;
	@Inject
	private ExecutionRepository executionRepository;
	@Inject
	private ResultRepository resultRepository;
	@Inject
	private QueryTypeConverter converter = new QueryTypeConverter();

	private Querier querier;
	private Query query;
	private DataSchema booksDataSchema;
	private DataSource dataSource;
	private org.enquery.encryptedquery.xml.schema.Query xmlQuery;


	@Override
	@ProbeBuilder
	public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe = super.probeConfiguration(probe);
		probe.setHeader("Service-Component",
				"OSGI-INF/org.enquery.encryptedquery.responder.it.business.QueryRunnerMock.xml");
		return probe;
	}

	@Configuration
	public Option[] configuration() {
		return combineOptions(super.baseOptions(),
				CoreOptions.options(
						systemProperty("query.schema.path")
								.value(Paths.get(QUERY_SCHEMA).toAbsolutePath().toString())));
	}

	@Before
	@Override
	public void init() throws Exception {
		super.init();
		installBooksDataSchema();
		booksDataSchema = dataSchemaService.findByName(BOOKS_DATA_SCHEMA_NAME);
		assertNotNull(booksDataSchema);

		// register a MOCK query runner
		org.osgi.service.cm.Configuration conf = confAdmin.createFactoryConfiguration(
				"org.enquery.encryptedquery.responder.it.business.QueryRunnerMock", "?");

		Hashtable<String, String> properties = new Hashtable<>();
		conf.update(properties);

		waitUntilQueryRunnerRegistered(DATA_SOURCE_NAME);

		dataSource = dsRegistry.find(DATA_SOURCE_NAME);
		Assert.assertNotNull(dataSource);
		assertNotNull(dataSource.getRunner());
		querier = createQuerier("Books", SELECTORS);
		query = querier.getQuery();
		xmlQuery = converter.toXMLQuery(query);
	}

	@Test
	public void runSingleJob() throws Exception {

		Execution ex = scheduleJob(new Date());

		tryUntilTrue(30,
				3000,
				"Result not created.",
				e -> resultRepository.listForExecution(e).size() > 0,
				ex);


		Integer resultId = resultRepository.listForExecution(ex).iterator().next().getId();
		try (InputStream is = resultRepository.payloadInputStream(resultId)) {
			String fileContent = new String(IoUtils.toByteArray(is));
			assertEquals(QueryRunnerMock.class.getName(), fileContent);
		}

		// the Execution record is updated with run time stamps
		Execution updated = executionRepository.find(ex.getId());
		assertNotNull(updated.getStartTime());
		assertNotNull(updated.getEndTime());
		assertTrue(updated.getEndTime().after(updated.getStartTime()));
	}

	@Test
	public void runsOneAtATime() throws Exception {

		Date date = new Date();
		Execution ex1 = scheduleJob(date);
		Execution ex2 = scheduleJob(date);

		tryUntilTrue(30,
				3000,
				"Result not created.",
				e -> resultRepository.listForExecution(e).size() > 0,
				ex1);

		tryUntilTrue(30,
				3000,
				"Result not created.",
				e -> resultRepository.listForExecution(e).size() > 0,
				ex2);

		// the Execution record is updated with run time stamps
		ex1 = executionRepository.find(ex1.getId());
		log.info("Execution 1 ran from: {} to {}.", ex1.getStartTime(), ex1.getEndTime());
		ex2 = executionRepository.find(ex2.getId());
		log.info("Execution 2 ran from: {} to {}.", ex2.getStartTime(), ex2.getEndTime());

		assertFalse(overlap(ex1.getStartTime().getTime(),
				ex1.getEndTime().getTime(),
				ex2.getStartTime().getTime(),
				ex2.getEndTime().getTime()));
	}

	private boolean overlap(long x1, long x2, long y1, long y2) {
		return x1 <= y2 && y1 <= x2;
	}

	@Test
	public void runsWhenScheduleDateInPast() throws Exception {
		// it should run the job even if the schedule date is in the past
		long ts = 0;
		Date date = new Date(ts);
		Execution ex1 = scheduleJob(date);

		tryUntilTrue(30,
				3000,
				"Result not created.",
				e -> resultRepository.listForExecution(e).size() > 0,
				ex1);

		// the Execution record is updated with run time stamps
		ex1 = executionRepository.find(ex1.getId());
		log.info("Execution 1 ran from: {} to {}.", ex1.getStartTime(), ex1.getEndTime());
	}


	private Execution scheduleJob(Date date) throws JAXBException, IOException, JobExecutionException {
		Execution ex = new Execution();
		ex.setDataSchema(booksDataSchema);
		ex.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
		ex.setReceivedTime(date);
		ex.setScheduleTime(date);
		ex.setDataSourceName(DATA_SOURCE_NAME);
		ex = executionRepository.add(ex);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		converter.marshal(xmlQuery, os);
		ex = executionRepository.updateQueryBytes(ex.getId(), os.toByteArray());

		scheduler.add(ex);
		return ex;
	}

	private Querier createQuerier(String queryType, List<String> selectors) throws Exception {
		SchemaLoader loader = new SchemaLoader();
		QuerySchema querySchema = loader.loadQuerySchema(Paths.get(System.getProperty("query.schema.path")));
		return querierFactory.encrypt(querySchema, selectors, DATA_CHUNK_SIZE, HASH_BIT_SIZE);
	}
}
