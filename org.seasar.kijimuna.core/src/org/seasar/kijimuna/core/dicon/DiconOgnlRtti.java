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

import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.internal.dicon.info.ContainerPropertyAccessor;
import org.seasar.kijimuna.core.internal.dicon.info.ContainerRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.kijimuna.core.rtti.ognl.OgnlRtti;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconOgnlRtti extends OgnlRtti {

    private static ContainerPropertyAccessor accessor = new ContainerPropertyAccessor();
    
    public DiconOgnlRtti(RttiLoader loader) {
        super(loader);
		setPropertyAccessor(IContainerElement.class, accessor);
		setPropertyAccessor(ContainerRtti.class, accessor);
    }

    public void setComponent(IComponentElement component) {
	    IRtti rtti = (IRtti)component.getAdapter(IRtti.class);
        setVariableValue("self", rtti);
		IRtti printStream = getRttiLoader().loadRtti("java.io.PrintStream");
		setVariableValue("out", printStream);
		setVariableValue("err", printStream);
    }
    
}
