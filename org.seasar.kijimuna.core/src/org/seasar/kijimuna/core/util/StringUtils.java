/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
package org.seasar.kijimuna.core.util;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class StringUtils {

	public static boolean isWhitespace(char c) {
		if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
			return true;
		}
		return false;
	}

	public static String replaceIgnorableChars(String source) {
		if (source != null) {
			source = source.replaceAll("\r", " ");
			source = source.replaceAll("\n", " ");
			source = source.replaceAll("\t", " ");
			return source.trim();
		}
		return null;
	}

	public static boolean existValue(String value) {
		value = replaceIgnorableChars(value);
		if ((value == null) || (value.length() == 0)) {
			return false;
		}
		return true;
	}

	public static boolean noneValue(String value) {
		return !existValue(value);
	}

}
