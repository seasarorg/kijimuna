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
package org.seasar.kijimuna.core.preference;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public interface IPreferences {

	// default preferences
	String getDefault(String key);
	boolean getDefaultBoolean(String key);
	byte[] getDefaultByteArray(String key);
	double getDefaultDouble(String key);
	float getDefaultFloat(String key);
	int getDefaultInt(String key);
	long getDefaultLong(String key);
	void putDefault(String key, String value);
	void putDefaultBoolean(String key, boolean value);
	void putDefaultByteArray(String key, byte[] value);
	void putDefaultDouble(String key, double value);
	void putDefaultFloat(String key, float value);
	void putDefaultInt(String key, int value);
	void putDefaultLong(String key, long value);
	void removeDefault(String key);
	void clearDefault();

	// project preferences
	String get(String key);
	boolean getBoolean(String key);
	byte[] getByteArray(String key);
	double getDouble(String key);
	float getFloat(String key);
	int getInt(String key);
	long getLong(String key);
	void put(String key, String value);
	void putBoolean(String key, boolean value);
	void putByteArray(String key, byte[] value);
	void putDouble(String key, double value);
	void putFloat(String key, float value);
	void putInt(String key, int value);
	void putLong(String key, long value);
	void remove(String key);
	void clear();

	// flash
	void flash();

}
