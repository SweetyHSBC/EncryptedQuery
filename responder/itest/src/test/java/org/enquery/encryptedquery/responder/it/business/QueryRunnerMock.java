package org.enquery.encryptedquery.responder.it.business;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.enquery.encryptedquery.data.Query;
import org.enquery.encryptedquery.responder.data.entity.DataSourceType;
import org.enquery.encryptedquery.responder.data.entity.ExecutionStatus;
import org.enquery.encryptedquery.responder.data.service.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueryRunnerMock implements QueryRunner {

	private static final Logger log = LoggerFactory.getLogger(QueryRunnerMock.class);

	private String name = "query-runner-mock";
	private String description = "A runner intended for test only";
	private String dataSchemaName = "Books";


	void activate(Map<String, String> config) {
		log.info(
				"Creating QueryRunnerMock with name '{}' and description '{}'.",
				name,
				description);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String description() {
		return description;
	}

	@Override
	public String dataSchemaName() {
		return dataSchemaName;
	}

	@Override
	public byte[] run(Query query, Map<String, String> properties, String outputFileName, OutputStream stdOutput) {
		log.info("Running a query and storing results to " + outputFileName);
		Validate.notNull(query);
		Validate.notNull(outputFileName);
		Validate.isTrue(!Files.exists(Paths.get(outputFileName)));
		try {
			FileUtils.write(new File(outputFileName), this.getClass().getName(), "UTF-8");
			Thread.sleep(1000);
			return "".getBytes();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DataSourceType getType() {
		return DataSourceType.Batch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.enquery.encryptedquery.responder.data.service.QueryRunner#status(byte[])
	 */
	@Override
	public ExecutionStatus status(byte[] handle) {
		return new ExecutionStatus(new Date(), null, false);
	}

}
