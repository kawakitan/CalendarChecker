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
package com.github.kawakitan.CalendarChecker.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.github.kawakitan.CalendarChecker.io.CSVWriter;

/**
 * 
 * @author kawakitan
 */
public class CSVReport implements Report {

	private final CSVWriter writer;

	public CSVReport(final File file, final Charset charset) throws IOException {
		writer = new CSVWriter(file, charset);
	}

	@Override
	public void setDataHeader(final List<String> names) throws IOException {
		final List<String> buf = new ArrayList<String>();
		buf.add("判定");
		buf.add("期待値");
		buf.addAll(names);
		writer.writeCSVLine(buf);
	}

	@Override
	public void addDataRecord(final List<String> datas, final String result, final String expected) throws IOException {
		final List<String> buf = new ArrayList<String>();
		buf.add(result);
		buf.add(expected);
		buf.addAll(datas);
		writer.writeCSVLine(buf);
	}

	@Override
	public void save() throws IOException {
		writer.close();
	}
}