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
package org.enquery.encryptedquery.concurrency;

import java.util.WeakHashMap;

/**
 * Thread synchronization on an integer value.<br/>
 * Use as:<br/>
 * <br/>
 * 
 * int id;<br/>
 * synchronized (IntegerSynchronization.syncObjectFor(id))<br/>
 * {<br/>
 * protected code here. <br/>
 * }
 */
public class IntegerSynchronization {
	private static WeakHashMap<Integer, Integer> locks = new WeakHashMap<>();

	public static Object syncObjectFor(int id) {
		synchronized (locks) {
			Integer lock = locks.get(id);
			if (lock == null) {
				lock = new Integer(id);
				locks.put(lock, lock);
			}
			return lock;
		}
	}
}
