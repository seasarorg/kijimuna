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
package org.seasar.kijimuna.core.internal.dicon.validation;

import org.eclipse.core.resources.IProject;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.dicon.IProjectValidation;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.RttiLoader;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ClasspathValidation implements IProjectValidation, ConstCore {

	public void validation(IProject project) {
		DiconNature nature = DiconNature.getInstance(project);
		if (nature != null) {
			RttiLoader loader = nature.getRttiLoader();
			IRtti rtti = loader.loadRtti(MODEL_INTERFACE_S2CONTAINER);
			if (rtti instanceof HasErrorRtti) {
				MarkerSetting.createProjectMarker(
						"dicon.validation.ClasspathValidation.1", project,
						((HasErrorRtti) rtti).getErrorMessage());
			}
		}
	}

}
