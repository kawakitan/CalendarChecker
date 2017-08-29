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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author kawakitan
 */
public class ExcelReport implements Report {

	private final File file;
	private final Workbook workbook;
	private final Sheet sheet;
	private final Font font;

	private final CellStyle styleHeader1;
	private final CellStyle styleHeader2;
	private final CellStyle styleRecordMatch;
	private final CellStyle styleRecordUnmatch;
	private final CellStyle styleRecordUnknown;

	public ExcelReport(final File file) {
		this.file = file;
		final String name = file.getAbsolutePath().toUpperCase();
		if (name.endsWith(".XLS")) {
			workbook = new HSSFWorkbook();
		} else if (name.endsWith(".XLSX")) {
			workbook = new XSSFWorkbook();
		} else {
			workbook = null;
		}
		if (null != workbook) {
			font = workbook.createFont();
			font.setFontName("ＭＳ Ｐゴシック");

			styleHeader1 = workbook.createCellStyle();
			styleHeader2 = workbook.createCellStyle();
			styleRecordMatch = workbook.createCellStyle();
			styleRecordUnmatch = workbook.createCellStyle();
			styleRecordUnknown = workbook.createCellStyle();

			// styleHeader1.setFillPattern(CellStyle.SOLID_FOREGROUND);
			styleHeader1.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
			// styleHeader2.setFillPattern(CellStyle.SOLID_FOREGROUND);
			styleHeader2.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());

			// styleRecordMatch.setFillPattern(CellStyle.SOLID_FOREGROUND);
			styleRecordMatch.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			// styleRecordUnmatch.setFillPattern(CellStyle.SOLID_FOREGROUND);
			styleRecordUnmatch.setFillForegroundColor(IndexedColors.RED.getIndex());
			// styleRecordUnknown.setFillPattern(CellStyle.SOLID_FOREGROUND);
			styleRecordUnknown.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

			workbook.getFontAt((short) 0).setFontName("ＭＳ Ｐゴシック");
			workbook.getFontAt((short) 0).setFontHeightInPoints((short) 11);

			sheet = workbook.createSheet("結果");
		} else {
			font = null;

			styleHeader1 = null;
			styleHeader2 = null;
			styleRecordMatch = null;
			styleRecordUnmatch = null;
			styleRecordUnknown = null;

			sheet = null;
		}
	}

	@Override
	public void setDataHeader(final List<String> names) throws IOException {
		final Row row = sheet.createRow(0);

		row.createCell(0).setCellStyle(styleHeader1);
		row.getCell(0).setCellValue("判定");
		row.createCell(1).setCellStyle(styleHeader1);
		row.getCell(1).setCellValue("期待値");
		for (int i = 0; i < names.size(); i++) {
			row.createCell(i + 2).setCellStyle(styleHeader2);
			row.getCell(i + 2).setCellValue(names.get(i));
		}
	}

	@Override
	public void addDataRecord(final List<String> datas, final String result, final String expected) throws IOException {
		final Row row = sheet.createRow(sheet.getLastRowNum() + 1);

		CellStyle style = null;
		if ("○".equals(result)) {
			style = styleRecordMatch;
		} else if ("×".equals(result)) {
			style = styleRecordUnmatch;
		} else {
			style = styleRecordUnknown;
		}

		row.createCell(0).setCellStyle(style);
		row.getCell(0).setCellValue(result);
		row.createCell(1).setCellStyle(style);
		row.getCell(1).setCellValue(expected);
		for (int i = 0; i < datas.size(); i++) {
			row.createCell(i + 2).setCellStyle(style);
			row.getCell(i + 2).setCellValue(datas.get(i));
		}
	}

	@Override
	public void save() throws IOException {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
			workbook.write(stream);
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (null != stream) {
				stream.close();
			}
		}
	}
}
