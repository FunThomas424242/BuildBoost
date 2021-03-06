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
package de.devboost.buildboost.steps.copy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.devboost.buildboost.ant.AbstractAntTargetGeneratorProvider;
import de.devboost.buildboost.ant.IAntTargetGenerator;
import de.devboost.buildboost.artifacts.TargetPlatformZip;
import de.devboost.buildboost.model.IArtifact;
import de.devboost.buildboost.model.IBuildContext;

/**
 * The {@link ExtractZipFileBuildStepProvider} add a {@link CopyPluginsAndFeaturesBuildStep} 
 * for each bundled plug-in. This provider does not apply to plug-in projects.
 */
public class ExtractZipFileBuildStepProvider extends AbstractAntTargetGeneratorProvider {

	private File targetDir;
	
	public ExtractZipFileBuildStepProvider(File targetDir) {
		super();
		this.targetDir = targetDir;
	}

	public List<IAntTargetGenerator> getAntTargetGenerators(IBuildContext context, IArtifact artifact) {
		if (artifact instanceof TargetPlatformZip) {
			TargetPlatformZip zip = (TargetPlatformZip) artifact;
			List<IAntTargetGenerator> steps = new ArrayList<IAntTargetGenerator>(1);
			steps.add(new ExtractZipFileBuildStep(zip, targetDir));
			return steps;
		}
		return Collections.emptyList();
	}
}
