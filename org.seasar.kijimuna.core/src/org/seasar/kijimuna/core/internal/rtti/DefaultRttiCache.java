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
package org.seasar.kijimuna.core.internal.rtti;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IStorage;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiCache;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DefaultRttiCache implements IRttiCache {

	private Map rttiMap = new HashMap();
	
	public DefaultRttiCache() {
	}
	
    public IRtti getRttiFromCache(String key) {
        return (IRtti)rttiMap.get(key);
    }
    
    public void putRttiToCache(String key, IRtti rtti) {
        rttiMap.put(key, rtti);
    }
   
    public void removeRttiFromCache(IStorage storage) {
        Set set = new HashSet();
        for(Iterator it = rttiMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            IRtti rtti = (IRtti)entry.getValue();
            IStorage rttiStorage = (IStorage)rtti.getAdapter(IStorage.class);
            if((rttiStorage != null) && (rttiStorage.equals(storage))) {
                set.add(entry.getKey());
            }
        }
        for(Iterator it = set.iterator(); it.hasNext();) {
            rttiMap.remove(it.next());
        }
    }
    
    public void clearRttiCache() {
        rttiMap.clear();
    }
	
}
