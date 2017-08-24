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

/**
 * 元号情報
 * 
 * @author kawakitan
 */
public class Gengo {

	private final String name;
	private final Integer startYear;
	private final Integer endYear;

	public Gengo(final String name, final Integer startYear, final Integer endYear) {
		this.name = name;
		this.startYear = startYear;
		this.endYear = endYear;
	}

	public String getName() {
		return name;
	}

	public Integer getStartYear() {
		return startYear;
	}

	public Integer getEndYear() {
		return endYear;
	}

}
