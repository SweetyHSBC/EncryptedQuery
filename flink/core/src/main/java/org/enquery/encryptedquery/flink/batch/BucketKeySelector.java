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
package org.enquery.encryptedquery.flink.batch;

import org.apache.commons.lang3.Validate;
import org.apache.flink.api.java.functions.KeySelector;
import org.enquery.encryptedquery.flink.QueueRecord;

public class BucketKeySelector implements KeySelector<QueueRecord, Integer> {

	private static final long serialVersionUID = 1L;
	private final int numberOfPartions;

	public BucketKeySelector(int numberOfPartions) {
		Validate.isTrue(numberOfPartions > 0);
		this.numberOfPartions = numberOfPartions;
	}

	@Override
	public Integer getKey(QueueRecord record) throws Exception {
		return record.getRowIndex() % numberOfPartions;
	}
}
