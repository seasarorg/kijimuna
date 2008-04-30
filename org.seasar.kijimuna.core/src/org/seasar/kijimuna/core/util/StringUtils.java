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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class StringUtils {

	/**
	 * 空の文字列の配列です。
	 */
	public static final String[] EMPTY_STRINGS = new String[0];

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

	/**
	 * JavaBeansの仕様にしたがってデキャピタライズを行ないます。大文字が2つ以上続く場合は、小文字にならないので注意してください。
	 * 
	 * @param name
	 *            名前
	 * @return 結果の文字列
	 */
	public static String decapitalize(final String name) {
		if (isEmpty(name)) {
			return name;
		}
		char chars[] = name.toCharArray();
		if (chars.length >= 2 && Character.isUpperCase(chars[0])
				&& Character.isUpperCase(chars[1])) {
			return name;
		}
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * JavaBeansの仕様にしたがってキャピタライズを行ないます。大文字が2つ以上続く場合は、大文字にならないので注意してください。
	 * 
	 * @param name
	 *            名前
	 * @return 結果の文字列
	 */
	public static String capitalize(final String name) {
		if (isEmpty(name)) {
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	/**
	 * 空かどうかを返します。
	 * 
	 * @param text
	 *            文字列
	 * @return 空かどうか
	 */
	public static final boolean isEmpty(final String text) {
		return text == null || text.length() == 0;
	}

	/**
	 * 文字列を分割します。
	 * 
	 * @param str
	 *            文字列
	 * @param delim
	 *            分割するためのデリミタ
	 * @return 分割された文字列の配列
	 */
	public static String[] split(final String str, final String delim) {
		if (isEmpty(str)) {
			return EMPTY_STRINGS;
		}
		List list = new ArrayList();
		StringTokenizer st = new StringTokenizer(str, delim);
		while (st.hasMoreElements()) {
			list.add(st.nextElement());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * 文字列リテラルをデコードします。
	 * 
	 * @param value
	 *            文字列リテラル
	 * @return デコード後の文字列
	 */
	public static String decodeString(String value) {
		value = value.replaceAll("(^\"|\"$)", "");
		value = value.replaceAll("\\\"", "\"");
		return value;
	}

	/**
	 * 引数で渡した文字列が文字列リテラルかどうかをチェックします。
	 * 文字列がダブルクォートで開始し、ダブルクォートで終了している場合にtrueを返却します。
	 * 
	 * @param value
	 *            チェック対象の文字列
	 * @return 文字列リテラルの場合true、そうでない場合false
	 */
	public static boolean isString(String value) {
		if (value == null) {
			return false;
		}
		if (value.startsWith("\"") && value.endsWith("\"")) {
			return true;
		}
		return false;
	}

}
