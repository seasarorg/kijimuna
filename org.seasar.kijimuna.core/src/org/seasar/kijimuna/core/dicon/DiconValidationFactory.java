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

import org.seasar.kijimuna.core.internal.dicon.validation.AspectAssemble;
import org.seasar.kijimuna.core.internal.dicon.validation.AspectValidation;
import org.seasar.kijimuna.core.internal.dicon.validation.AutoConstructorInvoke;
import org.seasar.kijimuna.core.internal.dicon.validation.AutoMethodInvoke;
import org.seasar.kijimuna.core.internal.dicon.validation.AutoSetterInjection;
import org.seasar.kijimuna.core.internal.dicon.validation.ClasspathValidation;
import org.seasar.kijimuna.core.internal.dicon.validation.ComponentHolderValidation;
import org.seasar.kijimuna.core.internal.dicon.validation.ComponentValidation;
import org.seasar.kijimuna.core.internal.dicon.validation.DestroyMethodValidation;
import org.seasar.kijimuna.core.internal.dicon.validation.ExpressionMethodInvoke;
import org.seasar.kijimuna.core.internal.dicon.validation.IncludeValidation;
import org.seasar.kijimuna.core.internal.dicon.validation.ManualConstructorInvoke;
import org.seasar.kijimuna.core.internal.dicon.validation.ManualMethodInvoke;
import org.seasar.kijimuna.core.internal.dicon.validation.ManualSetterInjection;
import org.seasar.kijimuna.core.internal.dicon.validation.MethodValidation;
import org.seasar.kijimuna.core.internal.dicon.validation.PropertyValidation;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconValidationFactory {

	public static IValidation[] createValidation() {
		return new IValidation[] {
				new AspectAssemble(), new AspectValidation(),
				new AutoConstructorInvoke(), new AutoMethodInvoke(),
				new AutoSetterInjection(), new ComponentHolderValidation(),
				new ComponentValidation(), new DestroyMethodValidation(),
				new ExpressionMethodInvoke(), new IncludeValidation(),
				new ManualConstructorInvoke(), new ManualMethodInvoke(),
				new ManualSetterInjection(), new MethodValidation(),
				new PropertyValidation()
		};
	}

	public static IProjectValidation[] createProjectValidation() {
		return new IProjectValidation[] {
			new ClasspathValidation()
		};
	}

}
