package org.seasar.kijimuna.core.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.core.runtime.CoreException;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.util.ProjectUtils;

public class KijimunaProject extends TestProject implements ConstCore {

	public KijimunaProject() throws CoreException, MalformedURLException, IOException {
		super();
		ProjectUtils.addNature(getProject(), ID_NATURE_DICON);
		addJar(ID_PLUGIN_CORE, "test/resources/lib/aopalliance-1.0.jar");
		addJar(ID_PLUGIN_CORE, "test/resources/lib/commons-logging-1.1.jar");
		addJar(ID_PLUGIN_CORE, "test/resources/lib/javassist-3.4.ga.jar");
		addJar(ID_PLUGIN_CORE, "test/resources/lib/log4j-1.2.13.jar");
		addJar(ID_PLUGIN_CORE, "test/resources/lib/ognl-2.6.9-patch-20070908.jar");
		addJar(ID_PLUGIN_CORE, "test/resources/lib/s2-extension-2.4.22.jar");
		addJar(ID_PLUGIN_CORE, "test/resources/lib/s2-framework-2.4.22.jar");
	}

}
