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

	@AttributeDefinition(name = "Name",
			required = true,
			description = "Short name of this query runner. This name is used "
					+ "to identify the DataSource to the Querier component"
					+ "so queries can be submitted using this name for execution.")
	String name();

	@AttributeDefinition(name = "Description",
			required = true,
			description = "Description of this query runner for display to the end user. ")
	String description();

	@AttributeDefinition(name = "type",
			required = true,
			description = "Type: Batch or Streaming.")
	String type();

	@AttributeDefinition(name = "data.schema.name",
			required = false,
			description = "Name of the DataSchema describing the fields and partitioners.")
	String data_schema_name();

	@AttributeDefinition(name = ".column.encryption.class.name",
			required = true,
			description = "Class name of the encryption method to be used.")
	String _column_encryption_class_name();

	@AttributeDefinition(name = ".mod.pow.class.name",
			required = true,
			description = "Class name of the Modulus Power method to be used.")
	String _mod_pow_class_name();

	@AttributeDefinition(name = ".jni.library.path",
			required = false,
			description = "Comma separated list of native libraries to load. Optional.")
	String _jni_library_path();

	@AttributeDefinition(name = ".computer.threshold",
			required = false,
			description = "Amount of data to process before consolidation.   Larger numbers require more memory per task. Defaults to 30000")
	String _compute_threshold();

	@AttributeDefinition(name = ".flink.parallelism",
			required = false,
			description = "Number of Flink concurrent tasks assigned to execute this query.  Defaults to Flinks configured default.")
	String _flink_parallelism();

	@AttributeDefinition(name = ".column.encryption.partition.count",
			required = false,
			description = "How many partitions to create for column encryption. Defaults to 1.")
	String _column_encryption_partition_count();

	@AttributeDefinition(name = "kafka.brokers",
			required = true,
			description = "List of Kafka Brokers to connect to.")
	String _kafka_brokers();

	@AttributeDefinition(name = "kafka.topic",
			required = true,
			description = "Kafka topic to injest data from.")
	String _kafka_topic();

	@AttributeDefinition(name = "kafka.groupId",
			required = false,
			description = "Kafka Group Id.")
	String _kafka_groupId();

	@AttributeDefinition(name = "kafka.force.from.start",
			required = true,
			description = "Injest data from beginning of kafka topic (true or false)")
	String _kafka_force_from_start();

	@AttributeDefinition(name = "kafka.offset.location",
			required = true,
			description = "Offset Location to start reading data from")
	String _kafka_offset_location();
	
	@AttributeDefinition(name = "stream.window.length",
			required = true,
			description = "Amount of time to stream data before processing and return a result")
	String _stream_window_length();
	
	@AttributeDefinition(name = "stream.window.iterations",
			required = true,
			description = "Number of window iterations to capture streaming data.  Set to 0 for continuous streaming processing.")
	String _stream_window_iterations();

	@AttributeDefinition(name = "Flink Installation Directory",
			required = true,
			description = "Directory where Flink runtime is installed.")
	String _flink_install_dir() default "/opt/flink";

	@AttributeDefinition(name = "Additional Flink Argumments",
			required = false,
			description = "Additional arguments to be passed to Flink 'run' command when executing the query.")
	String _additional_flink_arguments();

	@AttributeDefinition(name = ".jar.file.path",
			required = true,
			description = "Path to the flink-jdbc jar file.  This is the jar file implementing the query execution.")
	String _jar_file_path();

	@AttributeDefinition(name = "Run Directory",
			required = true,
			description = "Path to a directory to use as the parent directory to store temporary files during the execution of the query."
					+ "Every execution will create temporary directories under this one.")
	String _run_directory();


	@AttributeDefinition(name = "Maximum number of hits per selector.",
			required = false,
			description = "Optional, default is 100. Must be positive integer > 0.")
	String _max_hits_per_selector();
}
