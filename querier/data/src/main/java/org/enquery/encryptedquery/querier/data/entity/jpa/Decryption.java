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
package org.enquery.encryptedquery.querier.data.entity.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "decryptions")
@NamedEntityGraphs({
		@NamedEntityGraph(
				name = Decryption.ALL_ENTITY_GRAPH,
				attributeNodes = {
						@NamedAttributeNode(value = "retrieval", subgraph = "retrieval")
				},
				subgraphs = {
						@NamedSubgraph(
								name = "retrieval",
								attributeNodes = @NamedAttributeNode(value = "result", subgraph = "result")),
						@NamedSubgraph(
								name = "result",
								attributeNodes = @NamedAttributeNode(value = "schedule", subgraph = "schedule")),
						@NamedSubgraph(
								name = "schedule",
								attributeNodes = @NamedAttributeNode(value = "query", subgraph = "query")),
						@NamedSubgraph(
								name = "query",
								type = Query.class,
								attributeNodes = @NamedAttributeNode(value = "querySchema", subgraph = "qschema")),
						@NamedSubgraph(
								name = "qschema",
								type = QuerySchema.class,
								attributeNodes = {
										@NamedAttributeNode(value = "dataSchema"),
										@NamedAttributeNode("fields")
								})
				})})
public class Decryption {

	public static final String ALL_ENTITY_GRAPH = "Decryption.all";

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequences")
	private Integer id;

	@OneToOne()
	@JoinColumn(name = "retrieval_id", nullable = false)
	private Retrieval retrieval;

	@Column(name = "error_msg")
	private String errorMessage;

	@Column(name = "payload_uri")
	private String payloadUri;

	public Decryption() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getPayloadUri() {
		return payloadUri;
	}

	public void setPayloadUri(String payloadUri) {
		this.payloadUri = payloadUri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Decryption other = (Decryption) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the retrieval
	 */
	public Retrieval getRetrieval() {
		return retrieval;
	}

	/**
	 * @param retrieval the retrieval to set
	 */
	public void setRetrieval(Retrieval retrieval) {
		this.retrieval = retrieval;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Decryption [id=").append(id).append(", retrieval=").append(retrieval).append(", errorMessage=").append(errorMessage).append(", payloadUri=").append(payloadUri).append("]");
		return builder.toString();
	}

}
