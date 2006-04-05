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
package org.seasar.kijimuna.ui.util;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.DiconElementFactory;
import org.seasar.kijimuna.core.dtd.DtdLoader;
import org.seasar.kijimuna.core.dtd.IDtd;
import org.seasar.kijimuna.core.parser.DocumentHandler;
import org.seasar.kijimuna.core.parser.DocumentParser;
import org.seasar.kijimuna.core.parser.IParseResult;
import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.ConstUI;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class CoreUtils implements ConstUI {
    
    public static String getPublicId(String stringToOffset) {
        if(StringUtils.existValue(stringToOffset)) {
            if(stringToOffset.indexOf(PUBLIC_ID_DICON_24) > 0) {
                return PUBLIC_ID_DICON_24;
            } else if(stringToOffset.indexOf(PUBLIC_ID_DICON_23) > 0) {
                return PUBLIC_ID_DICON_23;
            } else if(stringToOffset.indexOf(PUBLIC_ID_DICON_21) > 0) {
                return PUBLIC_ID_DICON_21;
            } else if(stringToOffset.indexOf(PUBLIC_ID_DICON_20) > 0) {
                return PUBLIC_ID_DICON_20;
            }
        }
        return null;
    }
    
    public static IDtd loadDtd(String publicId) {
        String path;
        if(PUBLIC_ID_DICON_24.equals(publicId)) {
            path = DTD_DICON_24;
        } else if(PUBLIC_ID_DICON_23.equals(publicId)) {
            path = DTD_DICON_23;
        } else if(PUBLIC_ID_DICON_21.equals(publicId)) {
            path = DTD_DICON_21;
        } else {
            path = DTD_DICON_20;
        }
		URL url = KijimunaCore.getEntry(path);
		return DtdLoader.loadDtd(url);
    }
    
	public static IParseResult parse(String text, IFile file) {
		DocumentHandler handler = new DocumentHandler(new DiconElementFactory());
		handler.putDtdPath(PUBLIC_ID_DICON_20, DTD_DICON_20);
		handler.putDtdPath(PUBLIC_ID_DICON_21, DTD_DICON_21);
		handler.putDtdPath(PUBLIC_ID_DICON_23, DTD_DICON_23);
		handler.putDtdPath(PUBLIC_ID_DICON_24, DTD_DICON_24);
		handler.setStorage(file.getProject(), file);
		DocumentParser parser = new DocumentParser();
		return parser.parse(text, file, handler);
	}
    
}
