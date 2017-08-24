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
package com.github.kawakitan.CalendarChecker.entity;

import java.io.File;
import java.nio.charset.Charset;

/**
 * チェック条件情報
 * 
 * @author kawakitan
 */
public class CheckCondition {

	private String warekiColumnName;
	private String seirekiColumnName;
	private File srcFile;
	private File destFile;
	private File gengoFile;
	private Charset srcCharset;
	private Charset destCharset;
	private Charset gengoCharset;

	public String getWarekiColumnName() {
		return warekiColumnName;
	}

	public void setWarekiColumnName(String warekiColumnName) {
		this.warekiColumnName = warekiColumnName;
	}

	public String getSeirekiColumnName() {
		return seirekiColumnName;
	}

	public void setSeirekiColumnName(String seirekiColumnName) {
		this.seirekiColumnName = seirekiColumnName;
	}

	public File getSrcFile() {
		return srcFile;
	}

	public void setSrcFile(File srcFile) {
		this.srcFile = srcFile;
	}

	public File getDestFile() {
		return destFile;
	}

	public void setDestFile(File destFile) {
		this.destFile = destFile;
	}

	public File getGengoFile() {
		return gengoFile;
	}

	public void setGengoFile(File gengoFile) {
		this.gengoFile = gengoFile;
	}

	public Charset getSrcCharset() {
		return srcCharset;
	}

	public void setSrcCharset(Charset srcCharset) {
		this.srcCharset = srcCharset;
	}

	public Charset getDestCharset() {
		return destCharset;
	}

	public void setDestCharset(Charset destCharset) {
		this.destCharset = destCharset;
	}

	public Charset getGengoCharset() {
		return gengoCharset;
	}

	public void setGengoCharset(Charset gengoCharset) {
		this.gengoCharset = gengoCharset;
	}

}
