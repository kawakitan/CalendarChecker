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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

/**
 * CSVライタークラス
 * 
 * @author kawakitan
 */
public class CSVWriter extends BufferedWriter {

	public CSVWriter(final File file, final Charset charset) throws FileNotFoundException {
		super(new OutputStreamWriter(new FileOutputStream(file), charset));
	}

	public void writeCSVLine(final List<String> line) throws IOException {
		final StringBuilder s = new StringBuilder();
		for (int i = 0; i < line.size(); i++) {
			if (0 < i) {
				s.append(",");
			}
			s.append(value(line.get(i)));
		}
		s.append(System.lineSeparator());

		write(s.toString());
	}

	private String value(final String string) {
		String s = string;
		boolean dbl = false;
		if (-1 != s.indexOf(",")) {
			dbl = true;
		}
		if (-1 != s.indexOf("\"")) {
			dbl = true;
			s = s.replace("\"", "\"\"");
		}
		if (dbl) {
			s = "\"" + s + "\"";
		}
		return s;
	}
}
