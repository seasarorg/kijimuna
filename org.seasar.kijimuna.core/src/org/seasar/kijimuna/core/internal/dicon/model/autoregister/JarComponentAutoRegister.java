/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.kijimuna.core.internal.dicon.model.autoregister;

import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.launching.JavaRuntime;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.util.ResourceUtil;

/**
 * <code>JarComponentAutoRegister</code>の動作をエミュレートするIAutoRegisterの実装。
 */
public class JarComponentAutoRegister extends AbstractComponentAutoRegister {

	private String referenceClass;
	private Pattern[] jarFileNamePatterns;

	public void registerAll() {
		try {
			IType type = getProject().findType(referenceClass);
			if (!type.exists()) {
				return;
			}
			String[] classPaths = JavaRuntime
					.computeDefaultRuntimeClassPath(getProject());
			for (int i = 0; i < classPaths.length; i++) {
				File file = new File(classPaths[i]);
				if (file.isDirectory()) {
					continue;
				}
				if (!isAppliedJar(file.getName())) {
					continue;
				}
				JarFile jarFile = new JarFile(file);
				Enumeration enumeration = jarFile.entries();
				while (enumeration.hasMoreElements()) {
					final JarEntry entry = (JarEntry) enumeration.nextElement();
					final String entryName = entry.getName().replace('\\', '/');
					if (entryName.endsWith(".class")) {
						final String className = entryName.substring(0,
								entryName.length() - ".class".length()).replace('/', '.');
						final int pos = className.lastIndexOf('.');
						final String packageName = (pos == -1) ? null : className
								.substring(0, pos);
						final String shortClassName = (pos == -1) ? className : className
								.substring(pos + 1);

						processClass(packageName, shortClassName);
					}
				}
			}
		} catch (Exception ex) {
			KijimunaCore.reportException(ex);
		}
	}

	public void setReferenceClass(String className) {
		referenceClass = className;
	}

	public void setJarFileNames(String jarFileNames) {
		String[] array = jarFileNames.split(",");
		jarFileNamePatterns = new Pattern[array.length];
		for (int i = 0; i < array.length; ++i) {
			String s = array[i].trim();
			jarFileNamePatterns[i] = Pattern.compile(s);
		}
	}

	private boolean isAppliedJar(final String jarFileName) {
		if (jarFileNamePatterns == null) {
			return true;
		}
		String extention = ResourceUtil.getExtension(jarFileName);
		if (extention == null || !extention.equalsIgnoreCase("jar")) {
			return false;
		}
		String name = ResourceUtil.removeExtension(jarFileName);
		for (int i = 0; i < jarFileNamePatterns.length; ++i) {
			if (jarFileNamePatterns[i].matcher(name).matches()) {
				return true;
			}
		}
		return false;
	}

}
