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
package org.enquery.encryptedquery.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.enquery.encryptedquery.encryption.CipherText;

/**
 * Class to hold the encrypted response elements for the PIR query
 * <p>
 * Serialized and returned to the querier for decryption
 */
public class Response implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final long responseSerialVersionUID = 1L;

	public final long responseVersion = responseSerialVersionUID;

	private QueryInfo queryInfo = null; // holds all query info

	// encrypted response columns, colNum -> column
	private List<Map<Integer, CipherText>> responseElements = null;

	public Response(QueryInfo queryInfoInput) {
		queryInfo = queryInfoInput;
		responseElements = new ArrayList<>();
	}

	public List<Map<Integer, CipherText>> getResponseElements() {
		return responseElements;
	}

	public void addResponseElements(Map<Integer, CipherText> newItems) {
		responseElements.add(newItems);
	}

	public void setResponseElements(List<Map<Integer, CipherText>> elements) {
		responseElements = elements;
	}

	public QueryInfo getQueryInfo() {
		return queryInfo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Response response = (Response) o;

		if (!queryInfo.equals(response.queryInfo))
			return false;
		return responseElements.equals(response.responseElements);

	}

	@Override
	public int hashCode() {
		return Objects.hash(queryInfo, responseElements);
	}
}
