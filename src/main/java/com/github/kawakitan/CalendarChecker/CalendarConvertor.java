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
package com.github.kawakitan.CalendarChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.kawakitan.CalendarChecker.entity.Gengo;
import com.github.kawakitan.CalendarChecker.utils.Utility;

/**
 * 
 * @author kawakitan
 */
public class CalendarConvertor {

	private final static String FORMAT_PTN = "^[\\s]*([\\(（]{0,1}(%s)[\\)）]{0,1}){0,1}[\\s]*(([元0-9]{1,2})年){0,1}[\\s]*(([0-9]{1,2})月){0,1}[\\s]*(([0-9]{1,2})日){0,1}[\\s]*$";

	private final Map<String, Gengo> gengos;
	private final Pattern ptn;

	public CalendarConvertor(final Map<String, Gengo> gengos) {
		this.gengos = new HashMap<String, Gengo>(gengos);

		StringBuffer gengo = new StringBuffer();
		for (String s : gengos.keySet()) {
			if (0 < gengo.length()) {
				gengo.append("|");
			}
			gengo.append(s);
		}
		ptn = Pattern.compile(String.format(FORMAT_PTN, gengo.toString()));
	}

	public String convert(final String string) {
		String buf = string;
		buf = buf.replaceAll("[閏甲乙丙丁戊己庚辛壬癸子丑寅卯辰巳午未申酉戌亥]", "");
		buf = buf.replaceAll("[\\(\\)（）]", "");
		buf = buf.replaceAll("ｶ", "");
		buf = buf.replaceAll("以降", "");

		String result = null;
		final Matcher m = ptn.matcher(buf);
		if (m.find()) {
			final StringBuilder s = new StringBuilder();
			// 年
			if (null != m.group(1) && null != m.group(3)) {
				// 平成29年 / 平成元年 / (平成)29年
				final Gengo gengo = gengos.get(m.group(2));
				Integer nen = ("元".equals(m.group(4))) ? 1 : Integer.parseInt(m.group(4));
				if (null == gengo) {
					return null;
				} else {
					if (Utility.equalsAny(m.group(2), "近代", "近世")) {
						s.append(String.format("%04d", gengo.getStartYear()));
					} else {
						s.append(String.format("%04d", gengo.getStartYear() + nen - 1));
					}
				}
			} else if (null != m.group(1)) {
				// 平成 / (平成)
				final Gengo gengo = gengos.get(m.group(2));
				if (null == gengo) {
					return null;
				} else {
					s.append(String.format("%04d", gengo.getEndYear()));
				}
			} else if (null != m.group(3)) {
				// 29年 / 元年
				s.append("9999");
			} else {
				s.append("9999");
			}

			// 月
			if (null != m.group(5)) {
				s.append(String.format("%02d", Integer.parseInt(m.group(6))));
			} else {
				s.append("99");
			}

			// 日
			if (null != m.group(7)) {
				s.append(String.format("%02d", Integer.parseInt(m.group(8))));
			} else {
				s.append("99");
			}

			result = s.toString();
		} else {
			// System.out.println("Unmatch " + string);
		}
		return result;
	}
}
