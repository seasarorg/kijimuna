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
package org.seasar.kijimuna.core.rtti;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class HasErrorRtti extends RttiWrapper {

    private String qualifiedName;
    private String message;
    
    public HasErrorRtti(String qualifiedName, String message) {
        super(null);
        this.qualifiedName = qualifiedName;
        this.message = message;
    }
    
    public String getQualifiedName() {
        if(StringUtils.existValue(qualifiedName)) {
            return qualifiedName;
        }
        return KijimunaCore.getResourceString("rtti.HasErrorRtti.1");
    }
   
    public String getErrorMessage() {
        return message;
    }
    
}
