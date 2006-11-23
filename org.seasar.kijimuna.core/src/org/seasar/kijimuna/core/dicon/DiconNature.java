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
package org.seasar.kijimuna.core.dicon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.KijimunaPreferences;
import org.seasar.kijimuna.core.preference.IPreferences;
import org.seasar.kijimuna.core.project.IProjectConfiguable;
import org.seasar.kijimuna.core.project.IProjectRecordable;
import org.seasar.kijimuna.core.rtti.IRttiCache;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.kijimuna.core.util.MarkerUtils;
import org.seasar.kijimuna.core.util.ProjectUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class DiconNature implements IProjectNature, IProjectConfiguable,
		IProjectRecordable, ConstCore {

	private static final String[] BUILDERS = new String[] {
			ID_PROCESSOR_DICON_BUILDER,
			ID_PROCESSOR_DICON_VALIDATOR
	};

	public static DiconNature getInstance(IProject project) {
		if (project != null) {
			try {
				IProjectNature nature = project.getNature(ID_NATURE_DICON);
				if (nature instanceof DiconNature) {
					return (DiconNature) nature;
				}
			} catch (CoreException e) {
				KijimunaCore.reportException(e);
			}
		}
		return null;
	}

	// ------------------------------------------------------------

	private ModelManager model = new ModelManager();
	private RttiLoader rootLoader;
	private IProject project;
	private IPreferences pref;

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
		model.setProjectName(project.getName());
	}

	public IRttiCache getRttiCache() {
		return getRttiLoader().getRttiCache();
	}

	public ModelManager getModel() {
		return model;
	}

	public void configure() throws CoreException {
		ProjectUtils.addBuilders(getProject(), BUILDERS);
	}

	public void deconfigure() throws CoreException {
		ProjectUtils.removeBuilders(getProject(), BUILDERS);
		MarkerUtils.deleteMarker(getProject(), ID_MARKER);
		KijimunaCore.getProjectRecorder().cleanup(getProject(), null);
	}

	public RttiLoader getRttiLoader() {
		if (rootLoader == null) {
			rootLoader = new RttiLoader(getProject().getName(), true);
		}
		return rootLoader;
	}

	public void initProjectRecords(IProgressMonitor monitor) {
		model.init(monitor);
	}

	public boolean restoreProjectRecords(IPath recorderPath, IProgressMonitor monitor) {
		File file = recorderPath.append(getProject().getName()).addFileExtension(
				RECORDER_EXT_MODEL).toFile();
		boolean success = false;
		if (file.exists()) {
			try {
				ObjectInputStream stream = new ObjectInputStream(
						new FileInputStream(file));
				Object obj = stream.readObject();
				if (obj instanceof ModelManager) {
					synchronized (model) {
						model = (ModelManager) obj;
						model.afterRestoring();
					}
					success = true;
				} else {
					KijimunaCore.reportInfo(KijimunaCore
							.getResourceString("dicon.DiconNature.1"));
				}
			} catch (Exception e) {
				KijimunaCore.reportException(e);
			}
		}
		return success;
	}

	public boolean saveProjectRecords(IPath recorderPath, IProgressMonitor monitor) {
		boolean success = false;
		if (model.isDirty()) {
			File file = recorderPath.append(getProject().getName()).addFileExtension(
					RECORDER_EXT_MODEL).toFile();
			try {
				ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(
						file));
				model.prepareStoraging();
				stream.writeObject(model);
				success = true;
			} catch (Exception e) {
				KijimunaCore.reportException(e);
			}
		}
		return success;
	}

	public void customProcess(int type, IPath recorderPath, IProgressMonitor monitor) {
		if (type == RECORDER_VALIDATE) {
			model.validate(monitor);
		}
	}

	public IPreferences getPreferences() {
		if (pref == null) {
			pref = new KijimunaPreferences(project.getName());
		}
		return pref;
	}

}
