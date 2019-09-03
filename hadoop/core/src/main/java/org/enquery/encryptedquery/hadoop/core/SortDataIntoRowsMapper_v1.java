/*
 * EncryptedQuery is an open source project allowing user to query databases with queries under- *
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
package org.enquery.encryptedquery.hadoop.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.enquery.encryptedquery.core.Partitioner;
import org.enquery.encryptedquery.data.QueryInfo;
import org.enquery.encryptedquery.data.RecordEncoding;
import org.enquery.encryptedquery.json.JSONStringConverter;
import org.enquery.encryptedquery.utils.KeyedHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapper class for the SortDataIntoRows job
 *
 * <p>
 * This mapper breaks each input data element into parts and computes its selector hash ("row
 * number"). It emits key-value pairs {@code (row, parts)} where each value {@code parts} is a byte
 * array representing the concatenation of all the parts in the data element.
 */
public class SortDataIntoRowsMapper_v1 extends Mapper<LongWritable, Text, IntWritable, BytesWritable> {
	private static final Logger log = LoggerFactory.getLogger(SortDataIntoRowsMapper_v1.class);

	private IntWritable keyOut;
	private BytesWritable valueOut;
	private QueryInfo queryInfo;
	private Partitioner partitioner;
	private JSONStringConverter jsonConverter;
	private RecordEncoding recordEncoding;

	private DistCacheLoader loader;


	@Override
	public void setup(Context ctx) throws IOException, InterruptedException {
		super.setup(ctx);
		try {
			log.info("SortDataIntoRowsMapper - Setup Running");
			keyOut = new IntWritable();
			valueOut = new BytesWritable();
			partitioner = new Partitioner();
			loader = new DistCacheLoader();

			queryInfo = loader.loadQueryInfo();

			jsonConverter = new JSONStringConverter(queryInfo.getQuerySchema().getDataSchema());
			recordEncoding = new RecordEncoding(queryInfo);
			log.info("Query Identifer: {}", queryInfo.getIdentifier());

		} catch (Exception e) {
			throw new IOException("Error initializing mapper.", e);
		}
	}

	/**
	 * The key is the docID/line number and the value is the doc
	 */
	@Override
	public void map(LongWritable key, Text value, Context ctx) throws IOException, InterruptedException {
		// Extract the selector, compute the hash, and partition the data element
		// according to query type
		Pair<Integer, byte[]> returnTuple;
		ctx.getCounter(HadoopConfigurationProperties.MRStats.NUM_RECORDS_INIT_MAPPER).increment(1);
		boolean passFilter = true;
		// TODO: Add Record Filtering to discard those who do not pass filter.

		// if (filter != null)
		// {
		// passFilter = ((DataFilter) filter).filterDataElement(value, dSchema);
		// }

		if (passFilter) {
			try {

				if (log.isDebugEnabled()) {
					log.debug("Input Line: {}", value.toString());
				}
				Map<String, Object> recordData = jsonConverter.toStringObjectFlatMap(value.toString());
				if (log.isDebugEnabled()) {
					log.debug("selector {} value ({})", "Caller #", recordData.get("Caller #"));
				}
				returnTuple = createRecordPair(recordData);

			} catch (Exception e) {
				log.error("Error in partitioning data element value {} ", value.toString());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			if (returnTuple != null) {
				if (log.isDebugEnabled()) {
					log.debug("rowIndex ( {} ) bytes: ( {} )", returnTuple.getLeft(),
							Hex.encodeHexString(returnTuple.getRight()));
				}
				Integer rowIndex = returnTuple.getLeft();
				byte[] data = returnTuple.getRight();

				keyOut.set(rowIndex);
				valueOut.set(data, 0, data.length);
				ctx.write(keyOut, valueOut);
				if (log.isDebugEnabled()) {
					log.debug("Writing rowIndex {} parts {}", rowIndex, data.length);
				}
				ctx.getCounter(HadoopConfigurationProperties.MRStats.NUM_RECORDS_PROCESSED_INIT_MAPPER).increment(1);
			} else {
				log.warn("Input Record not processed: {}", value.toString());
				// Not an error, just bad input data probably
			}
		}
	}


	private Pair<Integer, byte[]> createRecordPair(Map<String, Object> recordData) throws Exception {
		Pair<Integer, byte[]> pair = null;

		if (recordData == null)
			return pair;

		String selectorValue = recordEncoding.getSelectorStringValue(recordData);
		// if (selectorValue != null && selectorValue.length() > 0) {
		Integer rowIndex = KeyedHash.hash(queryInfo.getHashKey(), queryInfo.getHashBitSize(), selectorValue);
		ByteBuffer encoded = recordEncoding.encode(recordData);
		List<byte[]> recordParts = partitioner.createPartitions(encoded, queryInfo.getDataChunkSize());

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		for (byte[] ba : recordParts) {
			stream.write(ba);
		}
		byte[] parts = stream.toByteArray();
		stream.close();
		pair = Pair.of(rowIndex, parts);
		// } else {
		// if (log.isDebugEnabled()) {
		// log.debug("No Value for Selector field {} / value ({})",
		// queryInfo.getQuerySchema().getSelectorField(), selectorValue);
		// }
		// }
		return pair;
	}

	@Override
	public void cleanup(Context ctx) throws IOException, InterruptedException {
		try {
			if (loader != null) loader.close();
		} catch (Exception e) {
			throw new IOException("Error cleaning up.", e);
		}
	}
}
