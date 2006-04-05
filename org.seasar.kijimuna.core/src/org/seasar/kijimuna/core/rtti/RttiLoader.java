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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.internal.rtti.DefaultRtti;
import org.seasar.kijimuna.core.internal.rtti.DefaultRttiCache;
import org.seasar.kijimuna.core.util.ProjectUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class RttiLoader implements Serializable {

	private static final int ERROR = -1;
	private static final Map PRIMITIVES;
	static {
		PRIMITIVES = new HashMap();
		PRIMITIVES.put("boolean", "java.lang.Boolean");
		PRIMITIVES.put("byte", "java.lang.Byte");
		PRIMITIVES.put("char", "java.lang.Character");
		PRIMITIVES.put("double", "java.lang.Double");
		PRIMITIVES.put("float", "java.lang.Float");
		PRIMITIVES.put("int", "java.lang.Integer");
		PRIMITIVES.put("long", "java.lang.Long");
		PRIMITIVES.put("short", "java.lang.Short");
		PRIMITIVES.put("void", "java.lang.Void");
	}

	private boolean autoConvert;
	private String projectName;
	private String hostName;

	private transient IJavaProject project;
	private transient IType host;
	private transient Map qualifyNamesCashe;
	
	private IRttiCache cache;

	public RttiLoader(String projectName,
	        boolean autoConvert) {
		this(projectName, autoConvert, new DefaultRttiCache(), null);
	}

	private RttiLoader(String projectName,
	        boolean autoConvert, IRttiCache cache, String hostName) {
	    this.projectName = projectName;
		this.autoConvert = autoConvert;
		this.hostName = hostName;
		this.cache = cache;
		this.qualifyNamesCashe = new HashMap();
	}
	
	public IRttiCache getRttiCache() {
	    return cache;
	}
	
	public IJavaProject getProject() {
		if(project == null) {
		    project = ProjectUtils.getJavaProject(projectName);
		}
		return project;
	}

	public boolean isAutoConvert() {
		return autoConvert;
	}

	public IRtti loadRtti(Class clazz) {
		String name = clazz.getName();
		return loadRtti(name);
	}

	public IRtti loadRtti(String declareName) {
		if (declareName == null) {
			return loadHasErrorRtti(null,
			        KijimunaCore.getResourceString("rtti.RttiLoader.1"));
		}
		declareName = removeIgnorableWhitespace(declareName);
		declareName = removeGeneric(declareName);
		String qualifiedName = null;
		boolean primitive = isPrimitive(declareName);
		int arrayDepth = 0;
		String loaderHostName = null;
		
		if (primitive) {
			qualifiedName = getWrapperName(declareName);
			loaderHostName = declareName;
		} else {
			arrayDepth = getArrayDepth(declareName);
			if (arrayDepth == ERROR) {
			    return loadHasErrorRtti(null,
			            KijimunaCore.getResourceString("rtti.RttiLoader.2"));
			} else if (arrayDepth > 0) {
				qualifiedName = "java.lang.Object";
				loaderHostName = getArrayItemName(declareName);
			} else {
				qualifiedName = qualifyName(declareName);
				loaderHostName = qualifiedName;
			}
		}
		IRtti cached = cacheGet(qualifiedName, primitive, arrayDepth, loaderHostName);
		if(cached == null) {
			IRtti arrayItemRtti = null;
			if(arrayDepth > 0) {
				arrayItemRtti = getArrayRtti(loaderHostName, arrayDepth - 1);
			}
			IType newType = null;
			try {
				newType = getProject().findType(qualifiedName.replace('$', '.'));
			} catch (JavaModelException ignore) {
			}
			if(newType == null) {
			    return loadHasErrorRtti(qualifiedName, KijimunaCore.getResourceString(
			            "rtti.RttiLoader.3", new Object[]{ qualifiedName }));
			}
			RttiLoader childLoader = new RttiLoader(
			        projectName, autoConvert, getRttiCache(), qualifiedName);
			cached = new DefaultRtti(childLoader, newType,
					qualifiedName, primitive, arrayDepth, arrayItemRtti, autoConvert);
			cachePut(qualifiedName, primitive, arrayDepth, loaderHostName, cached);
		}
		return cached;
	}
	
	public HasErrorRtti loadHasErrorRtti(String qualifiedName, String message) {
	    return new HasErrorRtti(qualifiedName, message);
	}
	
	private String createKey(String fullQualifiedName,
			boolean primitive, int arrayDepth, String loaderHostName) {
		StringBuffer buffer = new StringBuffer(fullQualifiedName);
		buffer.append("/").append(primitive);
		if(arrayDepth > 0) {
			buffer.append("/").append(arrayDepth).append("/").append(loaderHostName);
		}
		return buffer.toString();
	}
	
	private IRtti cacheGet(String fullQualifiedName,
			boolean primitive, int arrayDepth, String loaderHostName) {
        String key = createKey(fullQualifiedName, primitive, arrayDepth, loaderHostName);
        return getRttiCache().getRttiFromCache(key);
	}

	private void cachePut(String fullQualifiedName, 
			boolean primitive, int arrayDepth, String loaderHostName, IRtti rtti) {
	    String key = createKey(fullQualifiedName, primitive, arrayDepth, loaderHostName);
	    getRttiCache().putRttiToCache(key, rtti);
	}

	private String removeIgnorableWhitespace(String declareName) {
		declareName = declareName.replaceAll(" ", "");
		declareName = declareName.replaceAll("\r", "");
		declareName = declareName.replaceAll("\n", "");
		declareName = declareName.replaceAll("\t", "");
		return declareName;
	}

	private String removeGeneric(String declareName) {
		int index = declareName.indexOf('<'); 
		if (0 <= index) {
			declareName = declareName.substring(0, index);
		}
		return declareName;
	}

	private String qualifyName(String declareName) {
	    if((host == null) && StringUtils.existValue(hostName)) {
	        try {
                host = getProject().findType(hostName.replace('$', '.'));
            } catch (JavaModelException e) {
            }
	    }
		if (host!= null) {
			String qualifyName = (String) qualifyNamesCashe.get(declareName);
			if (qualifyName != null) {
				return qualifyName;
			}
			try {
				String[][] result = host.resolveType(declareName);
				if (result != null) {
					String pack = result[0][0];
					String cname = result[0][1];
					if ((pack == null) || (pack.length() == 0)) {
						pack = "";
					} else {
						pack = pack + ".";
					}
					qualifyName = pack + cname;
					qualifyNamesCashe.put(declareName, qualifyName);
					return qualifyName;
				}
			} catch (JavaModelException ignore) {
			}
		}
		return declareName;
	}

	private String getArrayItemName(String declareName) {
		int pos = declareName.indexOf("[");
		String declareItemName = declareName.substring(0, pos);
		return qualifyName(declareItemName);
	}
	
	private int getArrayDepth(String declareName) {
		int depth = 0;
		int pos = declareName.indexOf("[");
		if (pos != -1) {
			String arrayDimension = declareName.substring(pos);
			for (int i = 0; i < arrayDimension.length(); i++) {
				char open = arrayDimension.charAt(i);
				i++;
				if (i < arrayDimension.length()) {
					char close = arrayDimension.charAt(i);
					if ((open != '[') || (close != ']')) {
						return ERROR;
					}
					depth++;
				} else {
					return ERROR;
				}
			}
		}
		return depth;
	}

	private IRtti getArrayRtti(String itemQualifiedName, int depth) {
		StringBuffer name = new StringBuffer(itemQualifiedName);
		for(int i = 0; i < depth; i++) {
			name.append("[]");
		}
		return loadRtti(name.toString());
	}

	private boolean isPrimitive(String declareName) {
		return PRIMITIVES.containsKey(declareName);
	}

	private String getWrapperName(String declareName) {
		return (String) PRIMITIVES.get(declareName);
	}


}