/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.kijimuna.core.parser;

/**
 * @author Kentaro Matsumae
 */
public class Attribute {

	private final String name;

	private final String value;

	private final int offset;

	private final int length;

	public Attribute(String name, String value, int offset, int length) {
		this.name = name;
		this.value = value;
		this.offset = offset;
		this.length = length;
	}

	public Attribute(String name, String value) {
		this(name, value, -1, -1);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}
}
