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
package org.enquery.encryptedquery.standalone;

/**
 * Properties constants for the Standalone Executor
 */
public interface StandaloneConfigurationProperties {
	String MAX_QUEUE_SIZE = "standalone.max.queue.size";
	String MAX_RECORD_QUEUE_SIZE = "standalone.max.record.queue.size";
	String MAX_COLUMN_QUEUE_SIZE = "standalone.max.column.queue.size";
	String MAX_RESPONSE_QUEUE_SIZE = "standalone.max.response.queue.size";
	String PROCESSING_THREADS = "standalone.thread.count";
	String COMPUTE_THRESHOLD = "standalone.compute.threshold";
	String MAX_HITS_PER_SELECTOR = "standalone.max.hits.per.selector";
	String ALG_VERSION = "standalone.algorithm.version";
	String COLUMN_BUFFER_MEMORY_MB = "standalone.column.buffer.memory.mb";
}
