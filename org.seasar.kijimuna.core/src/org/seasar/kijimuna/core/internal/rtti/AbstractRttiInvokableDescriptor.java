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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiInvokableDesctiptor;
import org.seasar.kijimuna.core.rtti.RttiLoader;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public abstract class AbstractRttiInvokableDescriptor 
		implements IRttiInvokableDesctiptor {

	private transient IMember member;

	private boolean isIMethod;
	private IRtti parent;
	private String methodName;
	private IRtti returnType;
	private IRtti[] args;
	private boolean fFinal;
	private boolean fStatic;
	private IRtti[] values;
	
	public AbstractRttiInvokableDescriptor(IMember member, IRtti parent) {
	    if(member instanceof IMethod) {
	        isIMethod = true;
	    }
	    this.member = member;
		this.parent = parent;
		methodName = member.getElementName();
		returnType = createReturnType(member, parent.getRttiLoader());
		args = createArgRttis(member, parent.getRttiLoader());
        fFinal = isFinal(member);
        fStatic = isStatic(member);
	}

	private String createTypeNameWithoutGeneric(String type) {
		String typeName = Signature.toString(type);
		int index = typeName.indexOf('<'); 
		if (0 <= index) {
			typeName = typeName.substring(0, index);
		}
		if (parent instanceof DefaultRtti) {
			String parentTypeName = ((DefaultRtti) parent).getGenericTypeName(typeName);
			if (parentTypeName != null) {
				typeName = parentTypeName;
			}
		}
		return typeName;
	}
	
	private IRtti createReturnType(IMember member, RttiLoader loader) {
		if(member instanceof IMethod) {
		    IMethod method = (IMethod)member;
		    try {
	            String retType = method.getReturnType();
	            String resolvedRet = createTypeNameWithoutGeneric(retType);
	            return loader.loadRtti(resolvedRet);
	        } catch (JavaModelException e) {
	            return null;
	        }
		}
		return loader.loadRtti("void");
	}
    
    private IRtti[] createArgRttis(IMember member, RttiLoader loader) {
		if(member instanceof IMethod) {
		    IMethod method = (IMethod)member;
			String[] argTypes = method.getParameterTypes();
			IRtti[] rttiArgs = new IRtti[argTypes.length];
			for (int k = 0; k < argTypes.length; k++) {
				String resolvedName = createTypeNameWithoutGeneric(argTypes[k]);
				rttiArgs[k] = loader.loadRtti(resolvedName);
			}
			return rttiArgs;
		}
		return new IRtti[0];
    }
	
    
    
    
	private boolean isFinal(IMember member) {
		if(member instanceof IMethod) {
	    	try {
				int flag = member.getFlags();
				return Flags.isFinal(flag);
			} catch (JavaModelException ignore) {
				return false;
			}
		}
		return false;
    }
    
    private boolean isStatic(IMember member) {
		if(member instanceof IMethod) {
	    	try {
				int flag = member.getFlags();
				return Flags.isStatic(flag);
			} catch (JavaModelException ignore) {
				return false;
			}
		}
		return false;
	}
	
    private String[] reverseArgRttis() {
        boolean binary = parent.getType().isBinary();
        String[] signature = new String[args.length];
        for(int i = 0; i < args.length; i++) {
            String qname = args[i].getQualifiedName();
            signature[i] = Signature.createTypeSignature(qname, binary);
        }
        return signature;
    }
    
    private IMember findIMethod() {
        IType type = parent.getType();
        IMethod method = type.getMethod(methodName, reverseArgRttis());
        if(!(methodName.equals(parent.getShortName()))) {
	        while(method == null) {
				try {
					String superClassName = type.getSuperclassName();
					if (superClassName == null) {
						superClassName = "java.lang.Object";
					}
					IType superType = type.getJavaProject().findType(superClassName.replace('$', '.'));
					method = superType.getMethod(methodName, reverseArgRttis());  
				} catch (Exception ignore) {
				}
	        }
        }
        if(method != null) {
            return method;
        }
        return type;
    }
    
	public String createDescriptorKey() {
	    StringBuffer buffer = new StringBuffer();
	    buffer.append(getMethodName()).append("(");
	    IRtti[] args = getArgs();
	    for(int i = 0; i < args.length; i++) {
	        if(i != 0) {
	            buffer.append(", ");
	        }
	        buffer.append(args[i].getQualifiedName());
	    }
	    return buffer.toString();
	}

	public boolean equals(Object test) {
        if(test instanceof IRttiInvokableDesctiptor) {
            IRttiInvokableDesctiptor desc = (IRttiInvokableDesctiptor)test;
            return parent.equals(desc.getParent()) &&
            	getMethodName().equals(desc.getMethodName()) &&
            	DefaultRtti.isMatchArgs(args, desc.getArgs());
        }
        return false;
    }
	
	public IMember getMember() {
	    if(member != null) {
	        return member;
	    }
        if(isIMethod) {
            return findIMethod();
        }
        return parent.getType();
	}
    
	public IRtti getParent() {
		return parent;
	}

	public String getMethodName() {
	    return methodName;
	}
	
    public IRtti getReturnType() {
        return returnType;
    }

    public IRtti[] getArgs() {
		return args;
	}

	public boolean isFinal() {
	    return fFinal;
	}

	public boolean isStatic() {
        return fStatic;
    }
	
	public IRtti[] getValues() {
		return values;
	}

	public void setValues(IRtti[] values) {
		this.values = values;
	}
	
    public int compareTo(Object test) {
        if(test instanceof IRttiInvokableDesctiptor) {
            IRttiInvokableDesctiptor invokabler = (IRttiInvokableDesctiptor)test;
            return createDescriptorKey().compareTo(
            		invokabler.createDescriptorKey());
        }
        return 1;
    }

}
