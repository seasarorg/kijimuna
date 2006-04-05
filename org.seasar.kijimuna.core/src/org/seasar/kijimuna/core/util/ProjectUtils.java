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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.seasar.kijimuna.core.ConstCore;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ProjectUtils implements ConstCore {

	// TODO: cash
	private static Map projectWithDiconStorageMap = new HashMap();
	
    private static List getCommands(IProjectDescription desc, String[] ignore)
            throws CoreException {
        ICommand[] commands = desc.getBuildSpec();
        List newCommands = new ArrayList();
        for (int i = 0; i < commands.length; i++) {
            boolean flag = true;
            for (int k = 0; k < ignore.length; k++) {
                if (commands[i].getBuilderName().equals(ignore[k])) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                newCommands.add(commands[i]);
            } else {
                flag = true;
            }
        }
        return newCommands;
    }

    private static void setCommands(
            IProjectDescription desc, List newCommands) {
        desc.setBuildSpec((ICommand[]) newCommands
                .toArray(new ICommand[newCommands.size()]));
    }

    public static void addBuilders(IProject project, String[] id)
    		throws CoreException {
        IProjectDescription desc = project.getDescription();
        List newCommands = getCommands(desc, id);
        for (int i = 0; i < id.length; i++) {
            ICommand command = desc.newCommand();
            command.setBuilderName(id[i]);
            newCommands.add(command);
        }
        setCommands(desc, newCommands);
        project.setDescription(desc, null);
    }

    public static void removeBuilders(IProject project, String[] id)
    		throws CoreException {
        IProjectDescription desc = project.getDescription();
        List newCommands = getCommands(desc, id);
        setCommands(desc, newCommands);
        project.setDescription(desc, null);
    }

    public static void addNature(IProject project, String natureID)
    		throws CoreException {
        if ((project != null) && project.isAccessible()) {
            IProjectDescription desc = project.getDescription();
            String[] natureIDs = desc.getNatureIds();
            int length = natureIDs.length;
            String[] newIDs = new String[length + 1];
            for (int i = 0; i < length; i++) {
                if (natureIDs[i].equals(natureID)) {
                    return;
                }
                newIDs[i] = natureIDs[i];
            }
            newIDs[length] = natureID;
            desc.setNatureIds(newIDs);
            project.setDescription(desc, null);
        }
    }

    public static void removeNature(IProject project, String natureID)
    		throws CoreException {
        if ((project != null) && project.isAccessible()) {
            IProjectDescription desc = project.getDescription();
            String[] natureIDs = desc.getNatureIds();
            int length = natureIDs.length;
            for (int i = 0; i < length; i++) {
                if (natureIDs[i].equals(natureID)) {
                    String[] newIDs = new String[length - 1];
                    System.arraycopy(natureIDs, 0, newIDs, 0, i);
                    System.arraycopy(natureIDs, i + 1, newIDs, i, length - i
                            - 1);
                    desc.setNatureIds(newIDs);
                    project.setDescription(desc, null);
                    return;
                }
            }
        }
    }

    public static IProjectNature getNature(IProject project, String natureID)
    		throws CoreException {
        if ((project != null) && (project.isOpen())) {
            return project.getNature(natureID);
        }
        return null;
    }

    public static boolean hasNature(IProject project, String natureID) {
        try {
            return getNature(project, natureID) != null;
        } catch(CoreException e) {
            return false;
        }
    }
    
    public static String[] getNatureIds(IProject project) {
        try {
            return project.getDescription().getNatureIds();
        } catch (CoreException e) {
            return new String[0];
        }
    }
    
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }
    
    public static IWorkspaceRoot getWorkspaceRoot() {
        return getWorkspace().getRoot();
    }
    
    public static IProject[] getAllProjects() {
        return getWorkspaceRoot().getProjects();
    }

    public static IProject getProject(String projectName) {
        return getWorkspaceRoot().getProject(projectName);
    }

	public static IJavaProject getJavaProject(String projectName) {
        return JavaCore.create(getProject(projectName));
	}
	
	public static IJavaProject getJavaProject(IResource resource) {
		return JavaCore.create(resource.getProject());
	}

	private static IPath getQualifiedDiconPath(String fullPath) {
		if (fullPath.endsWith(EXT_DICON)) {
			return new Path(fullPath);
		}
		return new Path(fullPath.replace('.', '/') + "." + EXT_DICON);
	}
	
	public static IStorage findDiconStorage(IProject proj, String fullPath) {
	    IPath path = getQualifiedDiconPath(fullPath);
	    String pack = path.removeLastSegments(1).toString();
	    pack = pack.replace('/', '.');
	    if(pack.startsWith(".")) {
	        pack = pack.substring(1);
	    }
	    if(pack.endsWith(".")) {
	        pack = pack.substring(0, pack.length() - 1);
	    }
		String name = path.lastSegment();
        try {
		    IJavaProject project = ProjectUtils.getJavaProject(proj);

            IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
            for(int i = 0; i < roots.length; i++) {
                if (pack.length() == 0) {
                	Object[] os = roots[i].getNonJavaResources();
                	for (int j = 0; j < os.length; j++) {
                		if (os[j] instanceof IStorage) {
	                		IStorage storage = (IStorage) os[j];
	                		if (name.equals(storage.getName())) {
	                			return storage;
	                		}
                		}
                	}
                }
                IPackageFragment frag = roots[i].getPackageFragment(pack);
                if(frag.exists()) {
                    if(frag.isDefaultPackage()) {
    	                IPath testPath = roots[i].getPath().append(name);
    	                // remove project name segment from path.
    	                testPath = testPath.removeFirstSegments(1);
    	                IFile dicon = proj.getFile(testPath);
    		            if(dicon.exists()) {
    		                return dicon;
    		            }
                    } else {
	                    Object[] resources = frag.getNonJavaResources();
	                    for(int k = 0; k < resources.length; k++) {
	                        if(resources[k] instanceof IStorage) {
	                            IStorage dicon = (IStorage)resources[k];
	                            if(name.equals(dicon.getName())) {
	                                return dicon;
	                            }
	                        }
	                    }
                    }
                }
            }
        } catch (JavaModelException e) {
            // -> getAllPackageFragmentRoots()
        	// when non java project
            IFile dicon = proj.getFile(getQualifiedDiconPath(fullPath));
            if(dicon.exists()) {
                return dicon;
            }
        }
        return null;
	}

	public static IStorage findDiconStorageWithCash(IProject proj, String fullPath) {
		Map diconWithStorage = (Map) projectWithDiconStorageMap.get(proj);
		if (diconWithStorage == null) {
			diconWithStorage = new HashMap();
			projectWithDiconStorageMap.put(proj, diconWithStorage);
		}
		IStorage storage = (IStorage) diconWithStorage.get(fullPath);
		if (storage == null) {
			storage = findDiconStorage(proj, fullPath);
			diconWithStorage.put(fullPath, storage);
		}
		return storage;
	}

	public static void clearDiconStorageWithCash(IProject proj) {
		projectWithDiconStorageMap.remove(proj);
	}

	public static IProject getProjectFromDiconStorage(IStorage storage) {
	    String fullPath = storage.getFullPath().toString();
	    IProject projects[] = getAllProjects();
	    for(int i = 0; i < projects.length; i++) {
	        if(hasNature(projects[i], ID_NATURE_DICON)) {
	            IStorage test = findDiconStorage(projects[i], fullPath);
	            if(test != null) {
	                return projects[i];
	            }
	        }
	    }
	    return null;
	}
	
    public static IStorage getStorage(IProject project, String fullPath) {
    	try {
	    	IFile file = getWorkspaceRoot().getFile(new Path(fullPath));
	    	if((file != null) && (file.exists())) {
	    	    return file;
	    	}
    	} catch (IllegalArgumentException e) {
    		// dicon is in a root of jar.
    	}
    	return findDiconStorage(project, fullPath);
    }
    
    public static String getPathString(IStorage storage) {
    	return storage.getFullPath().toString();
    }
    
    public static String getResourceLoaderPath(IStorage storage) {
    	IPath path = storage.getFullPath();
    	if(storage instanceof IFile) {
    	    IContainer folder = ((IFile)storage).getParent();
	    	IJavaElement pack = JavaCore.create(folder);
			while(true) {
	    		if(pack instanceof IPackageFragmentRoot) {
					int depth = pack.getPath().segmentCount();
					return path.removeFirstSegments(depth).toString();
	    		} else if(pack instanceof IPackageFragment) {
	    		    pack = pack.getParent();
	    		    if(pack == null) {
	    		        break;
	    		    }
	    		} else {
	    		    break;
	    		}
    		}	
    	} else {
    	    return path.toString();
    	}
		return path.removeFirstSegments(1).toString();
    }

}