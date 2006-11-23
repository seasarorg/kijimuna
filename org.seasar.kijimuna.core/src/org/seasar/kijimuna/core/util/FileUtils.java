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

import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class FileUtils implements ConstCore {

	public static IFile createFile(IPackageFragment pack, String fileName,
			InputStream contents) {
		try {
			IContainer folder = (IContainer) pack.getUnderlyingResource();
			IFile file = folder.getFile(new Path(fileName));
			file.create(contents, false, null);
			return file;
		} catch (CoreException e) {
			KijimunaCore.reportException(e);
			return null;
		}
	}

	public static String getShortName(IStorage storage) {
		String name = storage.getName();
		int pos = name.indexOf(".");
		if (pos != -1) {
			name = name.substring(0, pos);
		}
		return name;
	}

	public static boolean isInJavaSourceFolder(IFile file) {
		IContainer container = file.getParent();
		return (container != null) && (JavaCore.create(container) != null);
	}

	public static boolean isJavaFile(IStorage storage) {
		String fileExt = storage.getFullPath().getFileExtension();
		return "java".equalsIgnoreCase(fileExt);
	}

	public static boolean isDiconFile(IStorage storage) {
		String fileExt = storage.getFullPath().getFileExtension();
		return EXT_DICON.equalsIgnoreCase(fileExt);
	}

	public static void deleteAllFiles(IPath path) {
		File directory = path.toFile();
		if (directory.exists()) {
			deleteDirectory(directory);
		}
	}

	private static void deleteDirectory(File directory) {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				deleteDirectory(files[i]);
			} else {
				files[i].delete();
			}
		}
		directory.delete();
	}

}
