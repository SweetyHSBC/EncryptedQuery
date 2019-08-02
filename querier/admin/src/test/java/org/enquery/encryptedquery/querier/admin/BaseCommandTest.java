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
package org.enquery.encryptedquery.querier.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;



/**
 *
 */
public class BaseCommandTest {

	private static ByteArrayOutputStream consoleOutput;
	private static PrintStream originalStdOut;
	private static PrintStream printStream;

	@BeforeClass
	public static void captureConsole() {
		originalStdOut = System.out;
		consoleOutput = new ByteArrayOutputStream();
		printStream = new PrintStream(consoleOutput);
		System.setOut(printStream);
	}

	@AfterClass
	public static void restoreConsole() throws IOException {
		System.setOut(originalStdOut);
		consoleOutput.close();
	}

	/**
	 * 
	 */
	public BaseCommandTest() {
		super();
	}

	@After
	public void clearConsole() {
		originalStdOut.print(consoleOutput.toString());
		consoleOutput.reset();
	}

	protected String getConsoleOutput() {
		return consoleOutput.toString();
	}

}
