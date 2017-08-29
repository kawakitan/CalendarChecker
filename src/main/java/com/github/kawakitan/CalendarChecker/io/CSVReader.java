/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.kawakitan.CalendarChecker.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVリーダークラス
 * 
 * @author kawakitan
 */
public class CSVReader extends BufferedReader {

	public CSVReader(final File file, final Charset charset) throws FileNotFoundException {
		super(new InputStreamReader(new FileInputStream(file), charset));
	}

	public List<String> readCSVLine() throws IOException {
		List<String> result = null;

		String line = readLine();
		if (null != line) {
			result = parseCSV(line);
		}

		return result;
	}

	private List<String> parseCSV(final String string) {
		final List<String> result = new ArrayList<String>();

		boolean dbl = false;

		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c1 = string.charAt(i);

			if (dbl) {
				if ('"' == c1) {
					if (i + 1 < string.length()) {
						char c2 = string.charAt(i + 1);
						if ('"' == c2) {
							buf.append("\"");
							i++;
						} else {
							dbl = false;
						}
					} else {
						dbl = false;
					}
				} else {
					buf.append(c1);
				}
			} else {
				if ('"' == c1) {
					dbl = true;
				} else if (',' == c1) {
					result.add(buf.toString());
					buf = new StringBuilder();
				} else {
					buf.append(c1);
				}
			}

		}
		result.add(buf.toString());

		return result;
	}

}
