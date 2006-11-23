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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.parser.DocumentHandler;
import org.seasar.kijimuna.core.parser.DocumentParser;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.parser.IParseResult;
import org.seasar.kijimuna.core.project.AbstractProcessor;
import org.seasar.kijimuna.core.project.IFileProcessor;
import org.seasar.kijimuna.core.project.ResourceVisitor;
import org.seasar.kijimuna.core.util.FileUtils;
import org.seasar.kijimuna.core.util.MarkerUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class DiconBuilder extends AbstractProcessor implements ConstCore {

	private DocumentParser parser = new DocumentParser();

	public String getNatureID() {
		return ID_NATURE_DICON;
	}

	public IFileProcessor getFileBuilder() {
		return this;
	}

	public void handlePrepareFullProcess(IProject project, IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.beginTask(project.getName(), IProgressMonitor.UNKNOWN);
		}
		DiconNature nature = DiconNature.getInstance(project);
		if (nature != null) {
			nature.getModel().clearContainer();
			nature.getRttiCache().clearRttiCache();
		}
	}

	public void handleFileAdded(IFile addedFile, boolean fullBuild,
			IProgressMonitor monitor) {
		process(addedFile.getProject(), addedFile, monitor);
	}

	public void handleFileChanged(IFile changedFile, IProgressMonitor monitor) {
		removeContainer(changedFile, monitor);
		process(changedFile.getProject(), changedFile, monitor);
	}

	public void handleFileRemoved(IFile removedFile, IProgressMonitor monitor) {
		if (FileUtils.isJavaFile(removedFile)) {
			DiconNature nature = DiconNature.getInstance(removedFile.getProject());
			if (nature != null) {
				nature.getRttiCache().removeRttiFromCache(removedFile);
			}
		} else {
			removeContainer(removedFile, monitor);
		}
	}

	public void handleClassPassChanged(IProject project, IProgressMonitor monitor) {
		DiconNature nature = DiconNature.getInstance(project);
		if (nature != null) {
			nature.getRttiCache().clearRttiCache();
		}
	}

	public void processProject(IProject project, IProgressMonitor monitor) {
		if (project != null) {
			try {
				project.accept(new ResourceVisitor(getNatureID(), this, monitor));
			} catch (CoreException e) {
				KijimunaCore.reportException(e);
			}
		}
	}

	public void process(IProject project, IStorage storage, IProgressMonitor monitor) {
		String fileExt = storage.getFullPath().getFileExtension();
		if (!EXT_DICON.equalsIgnoreCase(fileExt)) {
			return;
		}
		if (monitor != null) {
			monitor.beginTask(storage.getName(), IProgressMonitor.UNKNOWN);
		}
		if (storage instanceof IFile) {
			MarkerUtils.deleteMarker((IFile) storage, ID_MARKER);
		}
		DiconNature nature = DiconNature.getInstance(project);
		if (nature != null) {
			if (monitor != null) {
				monitor.subTask(KijimunaCore.getResourceString("dicon.DiconBuilder.1",
						new Object[] {
							storage.getName()
						}));
			}
			int errorSeverity = MarkerSetting.getDiconMarkerPreference(project,
					MARKER_CATEGORY_XML_ERROR, false);
			int warningSeverity = MarkerSetting.getDiconMarkerPreference(project,
					MARKER_CATEGORY_XML_WARNING, false);
			DocumentHandler handler = new DocumentHandler(new DiconElementFactory(),
					ID_MARKER_DICONXML, errorSeverity, warningSeverity);
			handler.putDtdPath(PUBLIC_ID_DICON_20, DTD_DICON_20);
			handler.putDtdPath(PUBLIC_ID_DICON_21, DTD_DICON_21);
			handler.putDtdPath(PUBLIC_ID_DICON_23, DTD_DICON_23);
			handler.putDtdPath(PUBLIC_ID_DICON_24, DTD_DICON_24);
			IParseResult result = parser.parse(project, storage, monitor, handler);
			if (result != null) {
				IElement root = result.getRootElement();
				if (root instanceof IContainerElement) {
					IContainerElement container = (IContainerElement) root;
					nature.getModel().addContainerAndRelatedFile(container, null);
				}
			}
		}
		if (monitor != null) {
			monitor.done();
		}
	}

	public void handleFinish(IProject project, IProgressMonitor monitor) {
		DiconNature nature = DiconNature.getInstance(project);
		if (nature != null) {
			nature.getModel().fireRecordChanged();
		}
	}

	private void removeContainer(IFile file, IProgressMonitor monitor) {
		if (FileUtils.isDiconFile(file)) {
			if (monitor != null) {
				monitor.beginTask(file.getName(), IProgressMonitor.UNKNOWN);
			}
			DiconNature nature = DiconNature.getInstance(file.getProject());
			if (nature != null) {
				nature.getModel().removeContainer(file);
			}
		}
	}

}
