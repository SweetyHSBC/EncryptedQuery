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
package org.enquery.encryptedquery.responder.flink.kafka.runner;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
		name = "EncryptedQuery Flink-Kafka Runner",
		description = "Allows for the instantiation of Flink-Kafka Runners through OSGi configuration.")
public @interface Config {

	@AttributeDefinition(name = "name",
			required = true,
			description = "Short name of this query runner. This name is used "
					+ "to identify the DataSource to the Querier component"
					+ "so queries can be submitted using this name for execution.")
	String name();

	@AttributeDefinition(name = "description",
			required = true,
			description = "Description of this query runner for display to the end user. ")
	String description();

	@AttributeDefinition(name = "data.schema.name",
			required = false,
			description = "Name of the DataSchema describing the fields and partitioners.")
	String data_schema_name();

	@AttributeDefinition(name = ".column.buffer.memory.mb",
			required = false,
			description = "Amount of memory in MB to be allocated for Column processing. Default is 100M.")
	String _column_buffer_memory_mb();

	@AttributeDefinition(name = ".flink.parallelism",
			required = false,
			description = "Number of Flink concurrent tasks assigned to execute this query.  Defaults to Flinks configured default.")
	String _flink_parallelism();

	@AttributeDefinition(name = ".kafka.brokers",
			required = true,
			description = "List of Kafka Brokers to connect to.")
	String _kafka_brokers();

	@AttributeDefinition(name = ".kafka.topic",
			required = true,
			description = "Kafka topic to ingest data from.")
	String _kafka_topic();

	@AttributeDefinition(name = ".kafka.emission.rate.per.second",
			required = false,
			description = "Maximum rate at which Kafka source emits record to flink stream. Defaults to unlimited.")
	String _kafka_emission_rate_per_second();

	@AttributeDefinition(name = ".flink.install.dir",
			required = true,
			description = "Directory where Flink runtime is installed.")
	String _flink_install_dir() default "/opt/flink";

	@AttributeDefinition(name = ".additional.flink.arguments",
			required = false,
			description = "Additional arguments to be passed to Flink 'run' command when executing the query.")
	String _additional_flink_arguments();

	@AttributeDefinition(name = "Application jar file",
			required = true,
			description = "Fully Qualified application jar file name.")
	String _application_jar_path();

	@AttributeDefinition(name = ".run.directory",
			required = true,
			description = "Path to a directory to use as the parent directory to store temporary files during the execution of the query."
					+ "Every execution will create temporary directories under this one.")
	String _run_directory();

	@AttributeDefinition(name = ".flink.history.server.uri",
			required = true,
			description = "URI to Flink's history server, including scheme, host and port.  Example: http://flink.local:8095")
	String _flink_history_server_uri() default "localhost:8095";

	@AttributeDefinition(name = ".chunk.size",
			required = false,
			description = "Overrides the chunk size specified by Querier when processing a query.")
	String _chunk_size();
}
