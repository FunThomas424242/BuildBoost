<<<<<<< HEAD
<<<<<<< HEAD
/*******************************************************************************
 * Copyright (c) 2006-2012
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package de.devboost.buildboost.genext.toolproduct.stages;

import java.io.File;

import de.devboost.buildboost.AutoBuilder;
import de.devboost.buildboost.BuildContext;
import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AntScript;
import de.devboost.buildboost.discovery.EclipseFeatureFinder;
import de.devboost.buildboost.discovery.EclipseTargetPlatformAnalyzer;
import de.devboost.buildboost.discovery.PluginFinder;
import de.devboost.buildboost.genext.toolproduct.steps.BuildToolProductStepProvider;
import de.devboost.buildboost.genext.updatesite.discovery.EclipseUpdateSiteDeploymentSpecFinder;
import de.devboost.buildboost.genext.updatesite.discovery.EclipseUpdateSiteFinder;
import de.devboost.buildboost.model.IUniversalBuildStage;
import de.devboost.buildboost.stages.AbstractBuildStage;

public class BuildToolProductStage extends AbstractBuildStage implements IUniversalBuildStage {

	private String artifactsFolder;

	public void setArtifactsFolder(String artifactsFolder) {
		this.artifactsFolder = artifactsFolder;
	}

	@Override
	public AntScript getScript() throws BuildException {
		File buildDir = new File(artifactsFolder);

		BuildContext context = createContext(false);

		context.addBuildParticipant(new EclipseTargetPlatformAnalyzer(buildDir));

		context.addBuildParticipant(new PluginFinder(buildDir));
		context.addBuildParticipant(new EclipseFeatureFinder(buildDir));

		context.addBuildParticipant(new EclipseUpdateSiteDeploymentSpecFinder(buildDir));
		
		File distDir = new File(buildDir, "dist");
		context.addBuildParticipant(new EclipseUpdateSiteFinder(distDir));
		
		context.addBuildParticipant(new BuildToolProductStepProvider(buildDir));
		
		AutoBuilder builder = new AutoBuilder(context);
		
		AntScript script = new AntScript();
		script.setName("Build Eclipse product(s)");
		script.addTargets(builder.generateAntTargets());
		
		return script;
	}
	
	@Override
	public int getPriority() {
		return 15000;
	}
}
=======
/*******************************************************************************
 * Copyright (c) 2006-2012
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package de.devboost.buildboost.genext.toolproduct.stages;

import java.io.File;

import de.devboost.buildboost.AutoBuilder;
import de.devboost.buildboost.BuildContext;
import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AntScript;
import de.devboost.buildboost.discovery.EclipseFeatureFinder;
import de.devboost.buildboost.discovery.EclipseTargetPlatformAnalyzer;
import de.devboost.buildboost.discovery.PluginFinder;
import de.devboost.buildboost.genext.toolproduct.steps.BuildToolProductStepProvider;
import de.devboost.buildboost.genext.updatesite.discovery.EclipseUpdateSiteDeploymentSpecFinder;
import de.devboost.buildboost.genext.updatesite.discovery.EclipseUpdateSiteFinder;
import de.devboost.buildboost.model.IUniversalBuildStage;
import de.devboost.buildboost.stages.AbstractBuildStage;

public class BuildToolProductStage extends AbstractBuildStage implements IUniversalBuildStage {

	private String artifactsFolder;

	public void setArtifactsFolder(String artifactsFolder) {
		this.artifactsFolder = artifactsFolder;
	}

	@Override
	public AntScript getScript() throws BuildException {
		File artifactsDir = new File(artifactsFolder);

		BuildContext context = createContext(false);

		context.addBuildParticipant(new EclipseTargetPlatformAnalyzer(artifactsDir));

		context.addBuildParticipant(new PluginFinder(artifactsDir));
		context.addBuildParticipant(new EclipseFeatureFinder(artifactsDir));

		context.addBuildParticipant(new EclipseUpdateSiteDeploymentSpecFinder(artifactsDir));
		
		File distDir = new File(artifactsDir, "dist");
		context.addBuildParticipant(new EclipseUpdateSiteFinder(distDir));
		
		context.addBuildParticipant(new BuildToolProductStepProvider());
		
		AutoBuilder builder = new AutoBuilder(context);
		
		AntScript script = new AntScript();
		script.setName("Build Eclipse product(s)");
		script.addTargets(builder.generateAntTargets());
		
		return script;
	}
	
	@Override
	public int getPriority() {
		return 15000;
	}
}
>>>>>>> refs/remotes/upstream/master
=======
/*******************************************************************************
 * Copyright (c) 2006-2012
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package de.devboost.buildboost.genext.toolproduct.stages;

import java.io.File;

import de.devboost.buildboost.AutoBuilder;
import de.devboost.buildboost.BuildContext;
import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AntScript;
import de.devboost.buildboost.discovery.EclipseFeatureFinder;
import de.devboost.buildboost.discovery.EclipseTargetPlatformAnalyzer;
import de.devboost.buildboost.discovery.PluginFinder;
import de.devboost.buildboost.genext.toolproduct.steps.BuildToolProductStepProvider;
import de.devboost.buildboost.genext.updatesite.discovery.EclipseUpdateSiteDeploymentSpecFinder;
import de.devboost.buildboost.genext.updatesite.discovery.EclipseUpdateSiteFinder;
import de.devboost.buildboost.model.IUniversalBuildStage;
import de.devboost.buildboost.stages.AbstractBuildStage;

public class BuildToolProductStage extends AbstractBuildStage implements IUniversalBuildStage {

	private String artifactsFolder;

	public void setArtifactsFolder(String artifactsFolder) {
		this.artifactsFolder = artifactsFolder;
	}

	@Override
	public AntScript getScript() throws BuildException {
		File artifactsDir = new File(artifactsFolder);

		BuildContext context = createContext(false);

		context.addBuildParticipant(new EclipseTargetPlatformAnalyzer(artifactsDir));

		context.addBuildParticipant(new PluginFinder(artifactsDir));
		context.addBuildParticipant(new EclipseFeatureFinder(artifactsDir));

		context.addBuildParticipant(new EclipseUpdateSiteDeploymentSpecFinder(artifactsDir));
		
		File distDir = new File(artifactsDir, "dist");
		context.addBuildParticipant(new EclipseUpdateSiteFinder(distDir));
		
		context.addBuildParticipant(new BuildToolProductStepProvider());
		
		AutoBuilder builder = new AutoBuilder(context);
		
		AntScript script = new AntScript();
		script.setName("Build Eclipse product(s)");
		script.addTargets(builder.generateAntTargets());
		
		return script;
	}
	
	@Override
	public int getPriority() {
		return 15000;
	}
}
>>>>>>> upstream/master
