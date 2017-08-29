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
package com.github.kawakitan.CalendarChecker.frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azkfw.io.MonitoringInputStream;
import org.azkfw.io.csv.CSVReader;

import com.github.kawakitan.CalendarChecker.CalendarConvertor;
import com.github.kawakitan.CalendarChecker.entity.CheckCondition;
import com.github.kawakitan.CalendarChecker.entity.Gengo;
import com.github.kawakitan.CalendarChecker.report.Report;
import com.github.kawakitan.CalendarChecker.report.ReportFactory;
import com.github.kawakitan.CalendarChecker.utils.Utility;

/**
 * 
 * @author kawakitan
 */
public class CalendarCheckerFrame extends AbstractCalendarCheckerFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 181840455171874578L;

	public CalendarCheckerFrame() {
	}

	protected void doExecute(final CheckCondition condition) {
		if (!condition.getSrcFile().isFile()) {
			fail(String.format("入力ファイルが見つかりません。[%s]", condition.getSrcFile().getAbsolutePath()));
			return;
		}
		if (!condition.getGengoFile().isFile()) {
			fail(String.format("元号ファイルが見つかりません。[%s]", condition.getGengoFile().getAbsolutePath()));
			return;
		}

		int cntTotal = 0;
		int cntMatch = 0;
		int cntUnMatch = 0;
		int cntUnknown = 0;
		int cntSkip = 0;

		final long tmStart = System.nanoTime();

		CSVReader reader = null;
		Report report = null;
		try {
			lblMessage.setText(String.format("処理を開始します。"));
			txtReport.setText(String.format("一致件数　　 : %7d 件\n不一致件数　 : %7d 件\n不明件数　　 : %7d 件\nスキップ件数 : %7d 件", cntMatch, cntUnMatch, cntUnknown, cntSkip));

			do {
				final Map<String, Gengo> gengos = getGengo(condition.getGengoFile(), condition.getGengoCharset());
				final CalendarConvertor convertor = new CalendarConvertor(gengos);

				reader = new CSVReader(new InputStreamReader(new FileInputStream(condition.getSrcFile()), condition.getSrcCharset()));
				report = ReportFactory.generate(condition.getDestFile(), condition.getDestCharset());
				if (null == report) {
					fail(String.format("出力ファイルの形式が未サポートです。[%s]", condition.getDestFile().getAbsolutePath()));
					break;
				}

				List<String> data = reader.readRecord();

				int warekiColumnNum = -1;
				int seirekiColumnNum = -1;
				int columnSize = data.size();
				for (int i = 0; i < data.size(); i++) {
					if (condition.getWarekiColumnName().equals(data.get(i))) {
						warekiColumnNum = i;
					} else if (condition.getSeirekiColumnName().equals(data.get(i))) {
						seirekiColumnNum = i;
					}
				}
				if (-1 == warekiColumnNum) {
					fail(String.format("入力ファイルのヘッダに指定の和暦カラム名『%s』が見つかりません。", condition.getWarekiColumnName()));
					break;
				}
				if (-1 == seirekiColumnNum) {
					fail(String.format("入力ファイルのヘッダに指定の西暦カラム名『%s』が見つかりません。", condition.getSeirekiColumnName()));
					break;
				}

				report.setDataHeader(data);

				int row = 2;
				while (null != (data = reader.readRecord())) {
					if (isCancel()) {
						break;
					}

					if (0 == data.size() || (1 == data.size() && 0 == data.get(0).length())) {
						row++;
						continue;
					}

					if (columnSize != data.size()) {
						warn(String.format("入力ファイルの %d行目をスキップしました。（列数不一致）", row));
						row++;
						cntSkip++;
						continue;
					}

					final String strWareki = data.get(warekiColumnNum);
					final String strSeireki = data.get(seirekiColumnNum);
					final String strExpected = convertor.convert(strWareki);

					String result = null;
					cntTotal++;
					if (null == strExpected) {
						cntUnknown++;
						result = "？";
					} else if (strSeireki.equals(strExpected)) {
						cntMatch++;
						result = "○";
					} else {
						cntUnMatch++;
						result = "×";
					}

					report.addDataRecord(data, result, (null != strExpected) ? strExpected : "未知のフォーマット");

					if (0 == cntTotal % 100) {
						final long tmEnd = System.nanoTime();
						final double tmInterval = (double) (tmEnd - tmStart) / (double) (1000000000f);
						lblMessage.setText(String.format("%d 件処理しました。[%.2f sec]", cntTotal, tmInterval));
						txtReport.setText(String.format("一致件数　　 : %7d 件\n不一致件数　 : %7d 件\n不明件数　　 : %7d 件\nスキップ件数 : %7d 件", cntMatch, cntUnMatch, cntUnknown,
								cntSkip));
					}

					row++;
				}

				final long tmEnd = System.nanoTime();
				final double tmInterval = (double) (tmEnd - tmStart) / (double) (1000000000f);
				if (isCancel()) {
					lblMessage.setText(String.format("処理がキャンセルされました。[%.2f sec]", tmInterval));
					txtReport.setText(String
							.format("一致件数　　 : %7d 件\n不一致件数　 : %7d 件\n不明件数　　 : %7d 件\nスキップ件数 : %7d 件", cntMatch, cntUnMatch, cntUnknown, cntSkip));
				} else {
					lblMessage.setText(String.format("%d 件処理しました。[%.2f sec]", cntTotal, tmInterval));
					txtReport.setText(String
							.format("一致件数　　 : %7d 件\n不一致件数　 : %7d 件\n不明件数　　 : %7d 件\nスキップ件数 : %7d 件", cntMatch, cntUnMatch, cntUnknown, cntSkip));
				}

			} while (false);

		} catch (Exception ex) {
			final long tmEnd = System.nanoTime();
			final double tmInterval = (double) (tmEnd - tmStart) / (double) (1000000000f);
			lblMessage.setText(String.format("処理中にエラーが発生しました。[%.2f sec]", tmInterval));
			txtReport.setText(String.format("一致件数　　 : %7d 件\n不一致件数　 : %7d 件\n不明件数　　 : %7d 件\nスキップ件数 : %7d 件", cntMatch, cntUnMatch, cntUnknown, cntSkip));

			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			pw.flush();

			fail(sw.toString());
		} finally {
			if (null != report) {
				try {
					report.save();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			Utility.release(reader);
		}
	}

	private Map<String, Gengo> getGengo(final File file, final Charset charset) {
		final Map<String, Gengo> gengos = new HashMap<String, Gengo>();

		CSVReader reader = null;
		try {
			final MonitoringInputStream monitoring = new MonitoringInputStream(new FileInputStream(file));
			reader = new CSVReader(new InputStreamReader(monitoring, charset));

			List<String> data = reader.readRecord();

			int row = 2;
			while (null != (data = reader.readRecord())) {
				try {
					if (3 == data.size()) {
						final String name = data.get(0);
						final Integer startYear = Integer.parseInt(data.get(1));
						final Integer endYear = Integer.parseInt(data.get(2));

						final Gengo gengo = new Gengo(name, startYear, endYear);
						if (!gengos.containsKey(gengo.getName())) {
							gengos.put(gengo.getName(), gengo);
						} else {
							warn(String.format("元号ファイルの %d行目をスキップしました。（元号重複[%s]）", row, gengo.getName()));
						}

					} else if (0 == data.size() || (1 == data.size() && 0 == data.get(0).length())) {
					} else {
						warn(String.format("元号ファイルの %d行目をスキップしました。（列数が不正です）", row));
					}
				} catch (NumberFormatException ex) {
					warn(String.format("元号ファイルの %d行目をスキップしました。（数値フォーマットが不正です）", row));
				}
				row++;
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			Utility.release(reader);
		}

		return gengos;
	}
}
