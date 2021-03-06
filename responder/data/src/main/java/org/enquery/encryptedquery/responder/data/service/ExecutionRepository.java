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
package org.enquery.encryptedquery.responder.data.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.enquery.encryptedquery.responder.data.entity.DataSchema;
import org.enquery.encryptedquery.responder.data.entity.DataSource;
import org.enquery.encryptedquery.responder.data.entity.Execution;


public interface ExecutionRepository {

	Execution findForDataSource(DataSource dataSource, int id);

	Execution find(int id);

	Collection<Execution> list();

	Collection<Execution> listIncomplete();

	/**
	 * Retrieve collection of Executions matching specified criteria.
	 * 
	 * @param dataSchema If not null, restrict to this data schema.
	 * @param dataSource If not null, restrict to this data source
	 * @param incomplete If true, only incomplete executions are returned.
	 * @return collection of executions matching the provided criteria, may be empty, not null
	 */
	Collection<Execution> list(DataSchema dataSchema, DataSource dataSource, boolean incomplete);


	Execution add(Execution ex);

	/**
	 * First, it checks the UUID, if already present, return the previous instance. Otherwise adds a
	 * new execution and returns it.
	 * 
	 * @param ex The Execution to be added.
	 * @param inputStream The Query input stream.
	 * @param onNewOp Optional operation to call if a new Execution is inserted. If this operation
	 *        throws an exception, the transaction is rolled back.
	 * @return
	 */
	Execution addIfNotPresent(Execution ex, InputStream inputStream,
			ThrowingConsumer<Execution> onNewOp);

	/**
	 * Find an Execution by its UUID
	 * 
	 * @param uuid
	 * @return
	 */
	Execution findByUUID(String uuid);

	Execution update(Execution ex);

	void delete(int id);

	/**
	 * Save the query bytes (encrypted query) may override previous value.
	 * 
	 * @param executionId
	 * @param bytes
	 * @throws IOException
	 */
	Execution updateQueryBytes(int executionId, byte[] bytes) throws IOException;

	/**
	 * InputStream from where to read the Query bytes of a given Execution query.
	 * 
	 * @param executionId
	 * @return
	 * @throws IOException
	 */
	InputStream queryBytes(int executionId) throws IOException;

	/**
	 * Opens an OutputStream where the standard output of the execution can be written. Client needs
	 * to close this stream when finished.
	 * 
	 * @param executionId ID of the execution.
	 * @return an OutputStream.
	 * 
	 * @throws IOException
	 */
	OutputStream executionOutputOutputStream(int executionId) throws IOException;

	/**
	 * Opens an InputStream from which the execution standard output can be read. Client needs to
	 * close this stream when finished.
	 * 
	 * @param executionId
	 * @return
	 * @throws IOException
	 */
	InputStream executionOutputInputStream(int executionId) throws IOException;

	/**
	 * Add a new Execution along with its Query.
	 * 
	 * @param ex The JPA Execution entity to add.
	 * @param inputStream The associated XML as a stream
	 * 
	 * @return Created Execution JPA entity
	 */
	Execution add(Execution ex, InputStream inputStream) throws IOException;
}
