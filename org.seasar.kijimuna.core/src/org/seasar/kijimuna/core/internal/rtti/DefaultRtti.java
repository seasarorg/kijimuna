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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiConstructorDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiFieldDescriptor;
import org.seasar.kijimuna.core.rtti.IRttiInvokableDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.kijimuna.core.util.ProjectUtils;

/**
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public class DefaultRtti implements IRtti {

	private static final Map WRAPPERS;
	static {
		WRAPPERS = new HashMap();
		WRAPPERS.put("java.lang.Boolean", "boolean");
		WRAPPERS.put("java.lang.Byte", "byte");
		WRAPPERS.put("java.lang.Character", "char");
		WRAPPERS.put("java.lang.Double", "double");
		WRAPPERS.put("java.lang.Float", "float");
		WRAPPERS.put("java.lang.Integer", "int");
		WRAPPERS.put("java.lang.Long", "long");
		WRAPPERS.put("java.lang.Short", "short");
		WRAPPERS.put("java.lang.Void", "void");
	}

	private class RttiInfo {

		private DefaultRtti rtti;

		RttiInfo(DefaultRtti rtti) {
			this.rtti = rtti;
		}

		boolean isArray() {
			return rtti.isArray();
		}

		boolean isPrimitive() {
			return rtti.isPrimitive();
		}

		String getWrapperName() {
			return rtti.getWrapperName();
		}

		IRtti getArrayItemClass() {
			return rtti.getArrayItemClass();
		}
	}

	public static boolean isMatchArgs(IRtti[] source, IRtti[] test) {
		if (test == null) {
			test = new IRtti[0];
		}
		if (source.length == test.length) {
			boolean match = true;
			for (int k = 0; k < source.length; k++) {
				if (!source[k].isAssignableFrom(test[k])) {
					match = false;
					break;
				}
			}
			if (match) {
				return true;
			}
		}
		return false;
	}

	private transient IType thisType;

	private RttiLoader loader;
	private String projectName;
	private String qualifiedName;
	private boolean primitive;
	private Map genericMap;
	private int arrayDepth;
	private IRtti arrayItem;
	private boolean autoConvert;

	public DefaultRtti(RttiLoader loader, IType thisType, String qualifiedName,
			boolean primitive, int arrayDepth, IRtti arrayItem, boolean autoConvert) {
		this.loader = loader;
		this.thisType = thisType;
		this.qualifiedName = qualifiedName;
		this.primitive = primitive;
		this.genericMap = createGenericMap(thisType);
		this.arrayDepth = arrayDepth;
		this.arrayItem = arrayItem;
		this.autoConvert = autoConvert;
		projectName = loader.getProject().getElementName();
	}

	private HashMap createGenericMap(IType type) {
		HashMap map = new HashMap();
		try {
			String[] signatures = type.getTypeParameterSignatures();
			for (int i = 0; i < signatures.length; i++) {
				String signature = signatures[i];
				int separator = signature.indexOf(':');
				String generic = signature.substring(0, separator);
				String extend = signature.substring(separator + 1);
				if (extend.length() > 0) {
					extend = Signature.toString(extend);
				} else {
					extend = "java.lang.Object";
				}
				map.put(generic, extend);
			}
		} catch (JavaModelException e) {
		}
		return map;
	}

	public String getGenericTypeName(String genericName) {
		String genericTypeName = (String) genericMap.get(genericName);
		if (genericTypeName == null || genericTypeName.indexOf('.') < 0) {
			IRtti rtti = getSuperClass();
			if (rtti instanceof DefaultRtti) {
				String superGenericTypeName = ((DefaultRtti) rtti)
						.getGenericTypeName(genericName);
				if (superGenericTypeName != null) {
					genericTypeName = superGenericTypeName;
				}
			}
		}
		if (genericTypeName != null && genericTypeName.indexOf('.') < 0) {
			String parentGenericTypeName = getGenericTypeName(genericTypeName);
			if (parentGenericTypeName != null) {
				genericTypeName = parentGenericTypeName;
			}
		}
		return genericTypeName;
	}

	private boolean isTypeAvailable() {
		return getType() != null;
	}

	private boolean isArray() {
		return arrayDepth > 0;
	}

	private boolean isPrimitive() {
		return primitive;
	}

	private String getWrapperName() {
		return qualifiedName;
	}

	private IRtti getArrayItemClass() {
		if (isArray()) {
			return arrayItem;
		}
		return null;
	}

	private boolean isWideningPrimitiveConversion(IRtti testRtti) {
		String thisQName = getQualifiedName();
		String testQName = testRtti.getQualifiedName();
		if (autoConvert) {
			if (WRAPPERS.containsKey(thisQName)) {
				thisQName = WRAPPERS.get(thisQName).toString();
			}
			if (WRAPPERS.containsKey(testQName)) {
				testQName = WRAPPERS.get(testQName).toString();
			}
		}
		if (testQName.equals("byte")) {
			return "short,int,long,float,double".indexOf(thisQName) != -1;
		} else if (testQName.equals("short")) {
			return "int,long,float,double".indexOf(thisQName) != -1;
		} else if (testQName.equals("char")) {
			return "int,long,float,double".indexOf(thisQName) != -1;
		} else if (testQName.equals("int")) {
			return "long,float,double".indexOf(thisQName) != -1;
		} else if (testQName.equals("long")) {
			return "float,double".indexOf(thisQName) != -1;
		} else if (testQName.equals("float")) {
			return "double".indexOf(thisQName) != -1;
		} else {
			return false;
		}
	}

	private boolean isWideningArrayConversion(IRtti testRtti) {
		String thisQName = getQualifiedName();
		if (thisQName.equals("java.lang.Object")
				|| thisQName.equals("java.lang.Cloneable")
				|| thisQName.equals("java.io.Serializable")) {
			return true;
		}
		IRtti thisArray = getArrayItemClass();
		RttiInfo testInfo = (RttiInfo) testRtti.getAdapter(RttiInfo.class);
		IRtti testArray = testInfo.getArrayItemClass();
		return (thisArray != null) && (testArray != null)
				&& thisArray.isAssignableFrom(testArray);
	}

	private boolean isPublicMember(IMember member) {
		if (!isInterface()) {
			try {
				int flags = member.getFlags();
				if (!Flags.isPublic(flags)) {
					return false;
				}
			} catch (JavaModelException e) {
			}
		}
		return true;
	}

	private Map getInvokableMap(Pattern pattern, boolean isConstructor) {
		Map descriptors = new HashMap();
		if (isTypeAvailable()) {
			try {
				IMethod[] methods = getType().getMethods();
				for (int i = 0; i < methods.length; i++) {
					if (isPublicMember(methods[i])) {
						String methodName = methods[i].getElementName();
						if (pattern.matcher(methodName).matches()) {
							IRttiInvokableDesctiptor descriptor;
							if (isConstructor) {
								descriptor = createConstructorDescriptor(methods[i]);
							} else {
								if (methodName.equals(getShortName())) {
									continue;
								}
								descriptor = createMethodDescriptor(methods[i]);
							}
							descriptors.put(descriptor.createDescriptorKey(), descriptor);
						}
					}
				}
				if (!isConstructor) {
					DefaultRtti superClass = (DefaultRtti) getSuperClass();
					if (superClass != null) {
						Map superMethods = superClass.getInvokableMap(pattern,
								isConstructor);
						for (Iterator it = superMethods.keySet().iterator(); it.hasNext();) {
							String key = (String) it.next();
							if (!descriptors.containsKey(key)) {
								descriptors.put(key, superMethods.get(key));
							}
						}
					}
				}
			} catch (JavaModelException ignore) {
			}
		}
		return descriptors;
	}

	private List getSortedInvokableList(Pattern pattern, boolean isConstructor) {
		List list = new ArrayList();
		Map descriptors = getInvokableMap(pattern, isConstructor);
		for (Iterator it = descriptors.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			list.add(entry.getValue());
		}
		Collections.sort(list);
		return list;
	}

	private IRttiConstructorDesctiptor createConstructorDescriptor(IMethod constructor) {
		return new DefaultRttiConstructorDescriptor(constructor, this, false);
	}

	private IRttiMethodDesctiptor createMethodDescriptor(IMethod method) {
		return new DefaultRttiMethodDescriptor(method, this);
	}

	private IRttiConstructorDesctiptor createDefaultConstructor() {
		try {
			IMethod[] methods = getType().getMethods();
			String shortName = getShortName();
			for (int i = 0; i < methods.length; i++) {
				if ((methods[i].getElementName().equals(shortName) && methods[i]
						.getParameterTypes().length == 0)) {
					return null;
				}
			}
			return new DefaultRttiConstructorDescriptor(getType(), this, true);
		} catch (JavaModelException e) {
			return null;
		}
	}

	private String getPropertyType(IMethod method, String returnType, int parameterNum) {
		try {
			String[] args = method.getParameterTypes();
			if (args.length == parameterNum) {
				if (parameterNum == 0) {
					String ret = Signature.toString(method.getReturnType());
					if ((returnType == null) || returnType.equals(ret)) {
						return ret;
					}
				} else {
					return Signature.toString(args[0]);
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	private DefaultRttiPropertyDescriptor getPropertyDescriptor(IMethod method,
			Pattern pattern) {
		String methodName = method.getElementName();
		String propertyName;
		boolean isReader;
		String propertyType;
		if (methodName.startsWith("set")) {
			propertyName = methodName.substring(3);
			isReader = false;
			propertyType = getPropertyType(method, "void", 1);
		} else if (methodName.startsWith("get")) {
			propertyName = methodName.substring(3);
			isReader = true;
			propertyType = getPropertyType(method, null, 0);
		} else if (methodName.startsWith("is")) {
			propertyName = methodName.substring(2);
			isReader = true;
			propertyType = getPropertyType(method, "boolean", 0);
		} else {
			return null;
		}
		if (propertyType == null) {
			return null;
		}
		if ((propertyName.length() == 0) || Character.isLowerCase(propertyName.charAt(0))) {
			return null;
		}
		if ((propertyName.length() == 1)
				|| (Character.isLowerCase(propertyName.charAt(1)))) {
			char[] chars = propertyName.toCharArray();
			chars[0] = Character.toLowerCase(chars[0]);
			propertyName = new String(chars);
		}
		if (!pattern.matcher(propertyName).matches()) {
			return null;
		}
		IRtti propertyRtti = loader.loadRtti(propertyType);
		return new DefaultRttiPropertyDescriptor(this, propertyName, propertyRtti,
				isReader);
	}

	private void margePropertyDescriptor(Map map, DefaultRttiPropertyDescriptor descriptor) {
		String propertyName = descriptor.getName();
		DefaultRttiPropertyDescriptor old = (DefaultRttiPropertyDescriptor) map
				.get(propertyName);
		if (old != null) {
			IRtti propertyType = descriptor.getType();
			boolean isReadable = descriptor.isReadable();
			boolean isWritable = descriptor.isWritable();
			if (propertyType.equals(old.getType())) {
				if (isReadable) {
					old.doReadable(propertyType);
				}
				if (isWritable) {
					old.doWritable(propertyType);
				}
			}
		} else {
			map.put(propertyName, descriptor);
		}
	}

	private Map getPropertyMap(Pattern pattern) {
		if (isTypeAvailable()) {
			try {
				IMethod[] methods = getType().getMethods();
				Map map = new HashMap();
				for (int i = 0; i < methods.length; i++) {
					if (isPublicMember(methods[i])) {
						DefaultRttiPropertyDescriptor descriptor = getPropertyDescriptor(
								methods[i], pattern);
						if (descriptor != null) {
							margePropertyDescriptor(map, descriptor);
						}
					}
				}
				IRtti parent = getSuperClass();
				if (parent != null) {
					IRttiPropertyDescriptor[] props = parent.getProperties(pattern);
					if (props != null) {
						for (int i = 0; i < props.length; i++) {
							margePropertyDescriptor(map,
									(DefaultRttiPropertyDescriptor) props[i]);
						}
					}
				}
				return map;
			} catch (JavaModelException ignore) {
			}
		}
		return new HashMap();
	}

	private IRtti[] getInterfaces(boolean includeThis) {
		if (isTypeAvailable()) {
			Set ret = getInterfacesRecursively(getType());
			if (includeThis && isInterface()) {
				ret.add(this);
			}
			return (IRtti[]) ret.toArray(new IRtti[ret.size()]);
		}
		return new IRtti[0];
	}
	
	private Set getInterfacesRecursively(IType type) {
		Set ret = new TreeSet();
		if (type == null) {
			return ret;
		}
		try {
			String[] interfaces = type.getSuperInterfaceNames();
			for (int i = 0; i < interfaces.length; i++) {
				IRtti rtti = loader.loadRtti(interfaces[i]);
				ret.add(rtti);
				IRtti[] rtties = rtti.getInterfaces();
				for (int j = 0; j < rtties.length; j++) {
					ret.addAll(getInterfacesRecursively(rtties[j].getType()));
				}
			}
			String superclass = type.getSuperclassName();
			if (superclass != null) {
				ret.addAll(getInterfacesRecursively(loader.loadRtti(superclass)
						.getType()));
			}
		} catch (JavaModelException ignore) {
		}
		return ret;
	}

	public boolean equals(Object test) {
		if ((test != null) && (test instanceof IRtti)) {
			IRtti testRtti = (IRtti) test;
			RttiInfo testInfo = (RttiInfo) testRtti.getAdapter(RttiInfo.class);
			if (testInfo == null) {
				return false;
			}
			if (autoConvert && (isPrimitive() || testInfo.isPrimitive())) {
				if (getWrapperName().equals(testInfo.getWrapperName())) {
					return true;
				}
			}
			if (isArray()) {
				if (testInfo.isArray()) {
					IRtti thisArrayItem = getArrayItemClass();
					IRtti testArrayItem = testInfo.getArrayItemClass();
					return thisArrayItem.equals(testArrayItem);
				}
				return false;
			}
			String testQname = testRtti.getQualifiedName().replace('$', '.');
			String thisQname = this.getQualifiedName().replace('$', '.');
			if (thisQname.equals(testQname)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return getQualifiedName();
	}

	public int compareTo(Object test) {
		if (test instanceof IRtti) {
			IRtti testRtti = (IRtti) test;
			return getQualifiedName().compareTo(testRtti.getQualifiedName());
		}
		return 1;
	}

	public Object getAdapter(Class adapter) {
		if (RttiInfo.class.equals(adapter)) {
			return new RttiInfo(this);
		} else if (IStorage.class.equals(adapter)) {
			if (isTypeAvailable()) {
				IType type = getType();
				if (type != null) {
					IResource resource = type.getResource();
					if (resource != null) {
						return resource;
					}
				}
			}
		} else if (IProject.class.equals(adapter)) {
			return ProjectUtils.getProject(projectName);
		} else if (IRtti.class.equals(adapter)) {
			return this;
		}
		return null;
	}

	public IType getType() {
		if (thisType == null) {
			IJavaProject project = ProjectUtils.getJavaProject(projectName);
			try {
				thisType = project.findType(qualifiedName.replace('$', '.'));
			} catch (JavaModelException e) {
			}
		}
		return thisType;
	}

	public RttiLoader getRttiLoader() {
		return loader;
	}

	public boolean isInterface() {
		if (isTypeAvailable()) {
			try {
				return getType().isInterface();
			} catch (JavaModelException ignore) {
			}
		}
		return false;
	}

	public boolean isFinal() {
		if (isTypeAvailable()) {
			try {
				return Flags.isFinal(getType().getFlags());
			} catch (JavaModelException ignore) {
			}
		}
		return false;
	}

	public boolean isAssignableFrom(IRtti testRtti) {
		if (testRtti == null) {
			return true;
		}
		if (testRtti instanceof HasErrorRtti) {
			return false;
		}
		if (equals(testRtti)) {
			return true;
		}
		RttiInfo testInfo = (RttiInfo) testRtti.getAdapter(RttiInfo.class);
		if (testInfo == null) {
			return true;
		}
		if (isPrimitive() || testInfo.isPrimitive()) {
			// widening primitive conversion
			return isWideningPrimitiveConversion(testRtti);
		} else if (testInfo.isArray()) {
			return isWideningArrayConversion(testRtti);
		} else {
			if (isInterface()) {
				IRtti[] superInterfaces = testRtti.getInterfaces();
				for (int i = 0; i < superInterfaces.length; i++) {
					if (equals(superInterfaces[i])) {
						return true;
					}
				}
			}
			if (testRtti.isInterface()) {
				return getQualifiedName().equals("java.lang.Object");
			}
			IRtti superClass = testRtti.getSuperClass();
			if (superClass == null) {
				return false;
			}
			return isAssignableFrom(superClass);
		}
	}

	public String getQualifiedName() {
		if (isArray()) {
			StringBuffer buffer = new StringBuffer(arrayItem.getQualifiedName());
			for (int i = 0; i < arrayDepth; i++) {
				buffer.append("[]");
			}
			return buffer.toString();
		}
		if (isPrimitive()) {
			return (String) WRAPPERS.get(qualifiedName);
		}
		return qualifiedName;
	}

	public String getShortName() {
		String qname = getQualifiedName();
		int pos = qname.lastIndexOf('.');
		if (pos != -1) {
			String shortName = qname.substring(pos + 1);
			pos = shortName.lastIndexOf('$');
			if (pos != -1) {
				return shortName.substring(pos + 1);
			}
			return shortName;
		}
		return qname;
	}

	public IRtti[] getInterfaces() {
		return getInterfaces(true);
	}

	public IRtti getSuperClass() {
		if (isTypeAvailable() && !isInterface()
				&& !getQualifiedName().equals("java.lang.Object")) {
			try {
				String superClassName = getType().getSuperclassName();
				if (superClassName == null) {
					superClassName = "java.lang.Object";
				}
				return loader.loadRtti(superClassName);
			} catch (Exception ignore) {
			}
		}
		return null;
	}

	public IRttiFieldDescriptor getField(String name, boolean staticAccess) {
		IRttiFieldDescriptor[] fields = getFields(Pattern.compile(name));
		if (fields.length == 1) {
			if (!staticAccess || fields[0].isStatic()) {
				return fields[0];
			}
		}
		return null;
	}

	private void addAllFields(Map ret, Pattern pattern) {
		if (isTypeAvailable()) {
			try {
				IField[] fields = getType().getFields();
				for (int i = 0; i < fields.length; i++) {
					String name = fields[i].getElementName();
					if (!ret.containsKey(name) && pattern.matcher(name).matches()) {
						if (isPublicMember(fields[i])) {
							int flags = fields[i].getFlags();
							String typeSignature = Signature.toString(fields[i]
									.getTypeSignature());
							ret.put(name, new DefaultRttiFieldDescriptor(this, name,
									loader.loadRtti(typeSignature), Flags.isFinal(flags),
									Flags.isStatic(flags)));
						}
					}
				}
			} catch (JavaModelException e) {
			} catch (IllegalArgumentException e) {
			}
			IRtti[] interfaces = getInterfaces(false);
			for (int i = 0; i < interfaces.length; i++) {
				if (interfaces[i] instanceof DefaultRtti) {
					((DefaultRtti) interfaces[i]).addAllFields(ret, pattern);
				}
			}
			IRtti superClass = getSuperClass();
			if ((superClass != null) && (superClass instanceof DefaultRtti)) {
				((DefaultRtti) superClass).addAllFields(ret, pattern);
			}
		}
	}

	public IRttiFieldDescriptor[] getFields(Pattern pattern) {
		Map ret = new HashMap();
		try {
			if (pattern.matcher("class").matches()) {
				ret.put("class", new DefaultRttiFieldDescriptor(this, "class", loader
						.loadRtti("java.lang.Class"), true, true));
			}
			if (isArray() && pattern.matcher("length").matches()) {
				ret.put("length", new DefaultRttiFieldDescriptor(this, "length", loader
						.loadRtti("int"), true, false));
			}
			addAllFields(ret, pattern);
		} catch (Exception ignore) {
		}
		return (IRttiFieldDescriptor[]) ret.values().toArray(
				new IRttiFieldDescriptor[ret.size()]);
	}

	public IRttiConstructorDesctiptor getConstructor(IRtti[] args) {
		IRttiConstructorDesctiptor[] constructors = getConstructors();
		if (constructors.length > 0) {
			for (int i = 0; i < constructors.length; i++) {
				IRtti[] rttiArgs = constructors[i].getArgs();
				if (isMatchArgs(rttiArgs, args)) {
					return constructors[i];
				}
			}
		} else if ((args == null) || (args.length == 0)) {
			return createDefaultConstructor();
		}
		return null;
	}

	public IRttiConstructorDesctiptor[] getConstructors() {
		Pattern pattern = Pattern.compile(getShortName());
		List descriptors = getSortedInvokableList(pattern, true);
		if (descriptors.size() == 0) {
			IRttiConstructorDesctiptor def = createDefaultConstructor();
			if (def != null) {
				return new IRttiConstructorDesctiptor[] {
					def
				};
			}
			return new IRttiConstructorDesctiptor[0];
		}
		return (IRttiConstructorDesctiptor[]) descriptors
				.toArray(new IRttiConstructorDesctiptor[descriptors.size()]);
	}

	public IRttiMethodDesctiptor getMethod(String methodName, IRtti[] args,
			boolean staticAccess) {
		Pattern pattern = Pattern.compile(methodName);
		IRttiMethodDesctiptor[] methods = getMethods(pattern);
		if (methods.length > 0) {
			for (int i = 0; i < methods.length; i++) {
				if (staticAccess && !methods[i].isStatic()) {
					continue;
				}
				IRtti[] rttiArgs = methods[i].getArgs();
				if (isMatchArgs(rttiArgs, args)) {
					return methods[i];
				}
			}
		}
		return null;
	}

	public IRttiMethodDesctiptor[] getMethods(Pattern pattern) {
		List descriptors = getSortedInvokableList(pattern, false);
		return (IRttiMethodDesctiptor[]) descriptors
				.toArray(new IRttiMethodDesctiptor[descriptors.size()]);
	}

	public IRttiPropertyDescriptor getProperty(String name) {
		if (isTypeAvailable()) {
			Map map = getPropertyMap(Pattern.compile(name));
			return (IRttiPropertyDescriptor) map.get(name);
		}
		return null;
	}

	public IRttiPropertyDescriptor[] getProperties(Pattern pattern) {
		if (isTypeAvailable()) {
			Map map = getPropertyMap(pattern);
			List ret = new ArrayList();
			for (Iterator it = map.values().iterator(); it.hasNext();) {
				IRttiPropertyDescriptor desc = (IRttiPropertyDescriptor) it.next();
				String propertyName = desc.getName();
				if (pattern.matcher(propertyName).matches()) {
					ret.add(desc);
				}
			}
			return (IRttiPropertyDescriptor[]) ret
					.toArray(new IRttiPropertyDescriptor[ret.size()]);
		}
		return new IRttiPropertyDescriptor[0];
	}

}
