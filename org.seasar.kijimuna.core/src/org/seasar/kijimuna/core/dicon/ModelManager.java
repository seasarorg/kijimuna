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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.project.IFileProcessor;
import org.seasar.kijimuna.core.project.IProjectRecordChangeListener;
import org.seasar.kijimuna.core.util.ProjectUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ModelManager implements Serializable {

	private transient IProject project;
	private String projectName;

	// HashMap<String diconFile, ContainerElement diconElement>
	private Map containerMap = new HashMap();

	// HashMap<String diconFile, HashSet<String javaOrIncludeDiconFile>>
	private Map relatedMap = new HashMap();
	private Set listeners = new HashSet();

	private transient IFileProcessor builder;
	private transient IFileProcessor validator;

	public void setProjectName(String projectName) {
		this.projectName = projectName;
		builder = new DiconBuilder();
		validator = new DiconValidator();
	}

	public boolean isDirty() {
		return relatedMap.size() != 0;
	}

	private IProject getProject() {
		if (project == null) {
			project = ProjectUtils.getProject(projectName);
		}
		return project;
	}

	private void addRelatedMapItem(IStorage key, IStorage entry) {
		Set item = null;
		String keyString = ProjectUtils.getPathString(key);
		item = (Set) relatedMap.get(keyString);
		if (item == null) {
			item = new HashSet(3);
		}
		if (entry != null) {
			item.add(ProjectUtils.getPathString(entry));
		}
		relatedMap.put(keyString, item);
	}

	public void validate(IProgressMonitor monitor) {
		validator.processProject(getProject(), monitor);
	}

	public void init(IProgressMonitor monitor) {
		builder.processProject(getProject(), monitor);
		validate(monitor);
	}

	public void addContainerAndRelatedFile(IContainerElement container,
			IStorage relatedFile) {
		IStorage file = (IStorage) container.getAdapter(IStorage.class);
		String keyString = ProjectUtils.getPathString(file);
		synchronized (this) {
			containerMap.put(keyString, container);
			addRelatedMapItem(file, relatedFile);
		}
	}

	public IContainerElement getContainer(IStorage storage, IProgressMonitor monitor) {
		String keyString = ProjectUtils.getPathString(storage);
		IContainerElement ret = (IContainerElement) containerMap.get(keyString);
		if (ret == null) {
			builder.process(getProject(), storage, monitor);
			ret = (IContainerElement) containerMap.get(keyString);
		}
		return ret;
	}

	public IContainerElement[] getContainers(IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.beginTask(KijimunaCore.getResourceString("dicon.ModelManager.1"),
					relatedMap.size());
		}
		Set ret = new HashSet();
		for (Iterator it = relatedMap.keySet().iterator(); it.hasNext();) {
			String keyString = (String) it.next();
			ret.add(getContainer(ProjectUtils.getStorage(getProject(), keyString),
					monitor));
			if (monitor != null) {
				monitor.worked(1);
			}
		}
		if (monitor != null) {
			monitor.done();
		}
		return (IContainerElement[]) ret.toArray(new IContainerElement[ret.size()]);
	}

	public String[] getAllContainerPaths() {
		Collection values = containerMap.values();
		String[] ret = new String[values.size()];
		int i = 0;
		for (Iterator it = values.iterator(); it.hasNext(); i++) {
			ret[i] = ((IContainerElement) it.next()).getPath();
		}
		return ret;
	}

	public void findRelatedFiles(IStorage depend, boolean infiniti, Set ret, Stack stack) {
		stack.push(depend);
		for (Iterator it = relatedMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Set item = (Set) entry.getValue();
			if (item.contains(ProjectUtils.getPathString(depend))) {
				String keyString = (String) entry.getKey();
				IStorage storage = ProjectUtils.getStorage(getProject(), keyString);
				if (storage instanceof IFile) {
					ret.add(storage);
				}
				if (infiniti && !stack.contains(storage)) {
					findRelatedFiles(storage, true, ret, stack);
				}
			}
		}
		stack.pop();
	}

	public IFile[] getRelatedFiles(IStorage depend, boolean infiniti) {
		Set ret = new HashSet();
		Stack stack = new Stack();
		findRelatedFiles(depend, infiniti, ret, stack);
		return (IFile[]) ret.toArray(new IFile[ret.size()]);
	}

	public List getRootContainers(IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.beginTask(KijimunaCore.getResourceString("dicon.ModelManager.2"),
					relatedMap.size());
		}
		List ret = new ArrayList();
		for (Iterator it = relatedMap.keySet().iterator(); it.hasNext();) {
			String keyString = (String) it.next();
			boolean isRoot = true;
			for (Iterator kt = relatedMap.entrySet().iterator(); kt.hasNext();) {
				Set set = (Set) ((Map.Entry) kt.next()).getValue();
				if (set.contains(keyString)) {
					isRoot = false;
					break;
				}
			}
			if (isRoot) {
				ret.add(getContainer(ProjectUtils.getStorage(getProject(), keyString),
						monitor));
			}
			if (monitor != null) {
				monitor.worked(1);
			}
		}
		if (monitor != null) {
			monitor.done();
		}
		return ret;
	}

	public void removeContainer(IStorage build) {
		String keyString = ProjectUtils.getPathString(build);
		containerMap.remove(keyString);
		relatedMap.remove(keyString);
	}

	public void clearContainer() {
		containerMap.clear();
		relatedMap.clear();
	}

	public void fireRecordChanged() {
		for (Iterator it = listeners.iterator(); it.hasNext();) {
			final IProjectRecordChangeListener listener = (IProjectRecordChangeListener) it
					.next();
			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					listener.finishChanged();
				}
			};
			try {
				getProject().getWorkspace().run(runnable, null);
			} catch (CoreException e) {
				KijimunaCore.reportException(e);
			}
		}
	}

	public void addRecordChangeListener(IProjectRecordChangeListener listener) {
		listeners.add(listener);
	}

	public void removeRecordChangeListener(IProjectRecordChangeListener listener) {
		listeners.remove(listener);
	}

	public void prepareStoraging() {
		listeners.clear();
	}

	public void afterRestoring() {
		builder = new DiconBuilder();
		validator = new DiconValidator();
	}

}
