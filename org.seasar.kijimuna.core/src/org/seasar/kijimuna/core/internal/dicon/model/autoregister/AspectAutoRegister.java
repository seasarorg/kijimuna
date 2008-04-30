/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import org.seasar.kijimuna.core.internal.dicon.model.AspectElement;
import org.seasar.kijimuna.core.internal.dicon.model.ComponentElement;

/**
 * <code>AspectAutoRegister</code>の動作をエミュレートするIAutoRegisterの実装。
 */
public class AspectAutoRegister extends AbstractComponentTargetAutoRegister {

	private AspectElement aspectElement;

	public void setAspectElement(AspectElement aspectElement) {
		this.aspectElement = aspectElement;
	}

	public AspectElement getAspectElement() {
		return aspectElement;
	}

	protected void register(ComponentElement componentElement) {
		aspectElement.setRootElement(containerElement);
		aspectElement.setParent(componentElement);
	}

}
