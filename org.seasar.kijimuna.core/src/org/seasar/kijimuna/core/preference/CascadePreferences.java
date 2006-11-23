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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.service.prefs.BackingStoreException;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import org.seasar.kijimuna.core.KijimunaCore;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class CascadePreferences implements IPreferences {

	private IEclipsePreferences pref;
	private IEclipsePreferences defaultPref;
	private IEclipsePreferences basePref;
	private Set preferenceChangeListeners = new HashSet(); 

	public CascadePreferences(IEclipsePreferences pref, IEclipsePreferences defaultPref) {
		this(pref, defaultPref, null);
	}

	public CascadePreferences(IEclipsePreferences pref, IEclipsePreferences defaultPref,
			IEclipsePreferences basePref) {
		if ((pref == null) || (defaultPref == null)) {
			throw new IllegalArgumentException();
		}
		this.pref = pref;
		this.defaultPref = defaultPref;
		this.basePref = basePref;
	}

	public String getDefault(String key) {
		if (basePref == null) {
			return defaultPref.get(key, "");
		}
		return defaultPref.get(key, basePref.get(key, ""));
	}

	public boolean getDefaultBoolean(String key) {
		if (basePref == null) {
			return defaultPref.getBoolean(key, false);
		}
		return defaultPref.getBoolean(key, basePref.getBoolean(key, false));
	}

	public byte[] getDefaultByteArray(String key) {
		if (basePref == null) {
			return defaultPref.getByteArray(key, new byte[0]);
		}
		return defaultPref.getByteArray(key, basePref.getByteArray(key, new byte[0]));
	}

	public double getDefaultDouble(String key) {
		if (basePref == null) {
			return defaultPref.getDouble(key, 0);
		}
		return defaultPref.getDouble(key, basePref.getDouble(key, 0));
	}

	public float getDefaultFloat(String key) {
		if (basePref == null) {
			return defaultPref.getFloat(key, 0);
		}
		return defaultPref.getFloat(key, basePref.getFloat(key, 0));
	}

	public int getDefaultInt(String key) {
		if (basePref == null) {
			return defaultPref.getInt(key, 0);
		}
		return defaultPref.getInt(key, basePref.getInt(key, 0));
	}

	public long getDefaultLong(String key) {
		if (basePref == null) {
			return defaultPref.getLong(key, 0);
		}
		return defaultPref.getLong(key, basePref.getLong(key, 0));
	}

	public void putDefault(String key, String value) {
		defaultPref.put(key, value);
	}

	public void putDefaultBoolean(String key, boolean value) {
		defaultPref.putBoolean(key, value);
	}

	public void putDefaultByteArray(String key, byte[] value) {
		defaultPref.putByteArray(key, value);
	}

	public void putDefaultDouble(String key, double value) {
		defaultPref.putDouble(key, value);
	}

	public void putDefaultFloat(String key, float value) {
		defaultPref.putFloat(key, value);
	}

	public void putDefaultInt(String key, int value) {
		defaultPref.putInt(key, value);
	}

	public void putDefaultLong(String key, long value) {
		defaultPref.putLong(key, value);
	}

	public void removeDefault(String key) {
		defaultPref.remove(key);
	}

	public void clearDefault() {
		try {
			defaultPref.clear();
		} catch (BackingStoreException e) {
			KijimunaCore.reportException(e);
		}
	}

	public String get(String key) {
		return pref.get(key, getDefault(key));
	}

	public boolean getBoolean(String key) {
		return pref.getBoolean(key, getDefaultBoolean(key));
	}

	public byte[] getByteArray(String key) {
		return pref.getByteArray(key, getDefaultByteArray(key));
	}

	public double getDouble(String key) {
		return pref.getDouble(key, getDefaultDouble(key));
	}

	public float getFloat(String key) {
		return pref.getFloat(key, getDefaultFloat(key));
	}

	public int getInt(String key) {
		return pref.getInt(key, getDefaultInt(key));
	}

	public long getLong(String key) {
		return pref.getLong(key, getDefaultLong(key));
	}

	public void put(String key, String value) {
		PreferenceChangeEvent e = createPreferenceChangeEvent(key, get(key), value);
		pref.put(key, value);
		firePreferenceChangeEvent(e);
	}

	public void putBoolean(String key, boolean value) {
		PreferenceChangeEvent e = createPreferenceChangeEvent(key, Boolean
				.valueOf(getBoolean(key)), Boolean.valueOf(value));
		pref.putBoolean(key, value);
		firePreferenceChangeEvent(e);
	}

	public void putByteArray(String key, byte[] value) {
		PreferenceChangeEvent e = createPreferenceChangeEvent(key,
				getByteArray(key), value);
		pref.putByteArray(key, value);
		firePreferenceChangeEvent(e);
	}

	public void putDouble(String key, double value) {
		PreferenceChangeEvent e = createPreferenceChangeEvent(key,
				new Double(getDouble(key)), new Double(value));
		pref.putDouble(key, value);
		firePreferenceChangeEvent(e);
	}

	public void putFloat(String key, float value) {
		PreferenceChangeEvent e = createPreferenceChangeEvent(key,
				new Float(getFloat(key)), new Float(value));
		pref.putFloat(key, value);
		firePreferenceChangeEvent(e);
	}

	public void putInt(String key, int value) {
		PreferenceChangeEvent e = createPreferenceChangeEvent(key,
				new Integer(getInt(key)), new Integer(value));
		pref.putInt(key, value);
		firePreferenceChangeEvent(e);
	}

	public void putLong(String key, long value) {
		PreferenceChangeEvent e = createPreferenceChangeEvent(key,
				new Long(getLong(key)), new Long(value));
		pref.putLong(key, value);
		firePreferenceChangeEvent(e);
	}

	public void remove(String key) {
		PreferenceChangeEvent e = createPreferenceChangeEvent(key,
				get(key), null);
		pref.remove(key);
		firePreferenceChangeEvent(e);
	}

	public void clear() {
		try {
			pref.clear();
		} catch (BackingStoreException e) {
			KijimunaCore.reportException(e);
		}
	}

	public void flash() {
		try {
			pref.flush();
			defaultPref.flush();
		} catch (BackingStoreException e) {
			KijimunaCore.reportException(e);
		}
	}
	
	public void addPreferenceChangeListener(IPreferenceChangeListener listener) {
		if (listener != null) {
			preferenceChangeListeners.add(listener);
		}
	}
	
	public void removePreferenceChangeListener(IPreferenceChangeListener listener) {
		preferenceChangeListeners.remove(listener);
	}
	
	protected void firePreferenceChangeEvent(PreferenceChangeEvent event) {
		for (Iterator it = preferenceChangeListeners.iterator(); it.hasNext();) {
			IPreferenceChangeListener listener = (IPreferenceChangeListener)
					it.next();
			listener.preferenceChanged(event);
		}
	}
	
	private PreferenceChangeEvent createPreferenceChangeEvent(String preferenceName,
			Object oldValue, Object newValue) {
		return new PreferenceChangeEvent(this, preferenceName, oldValue, newValue);
	}

}
