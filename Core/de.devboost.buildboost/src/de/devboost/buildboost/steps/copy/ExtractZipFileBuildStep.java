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
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AbstractAntTargetGenerator;
import de.devboost.buildboost.ant.AntTarget;
import de.devboost.buildboost.artifacts.TargetPlatformZip;
import de.devboost.buildboost.util.XMLContent;
import de.devboost.buildboost.util.AntScriptUtil;

public class ExtractZipFileBuildStep extends AbstractAntTargetGenerator {

	private TargetPlatformZip zip;
	private File targetDir;
	
	public ExtractZipFileBuildStep(TargetPlatformZip zip, File targetDir) {
		super();
		this.zip = zip;
		this.targetDir = new File(targetDir, "target-platform");
	}

	public Collection<AntTarget> generateAntTargets() throws BuildException {
		XMLContent content = new XMLContent();
		File file = zip.getZipFile();
		AntScriptUtil.addZipFileExtractionScript(content, file, new File(targetDir, determineEclipseTargetStructurePrefix(file)));
		return Collections.singleton(new AntTarget("unzip-target-platform-" + zip.getIdentifier(), content));
	}



	public String determineEclipseTargetStructurePrefix(File file) {
		if (!file.getName().endsWith(".zip")) {
			return "";
		}
		try {
			Enumeration<? extends ZipEntry> entries = new ZipFile(file).entries();
			while (entries.hasMoreElements()) {			
				String entryName = entries.nextElement().getName();
				if (entryName.startsWith("eclipse/plugins/")) {
					return "";
				}
				if (entryName.startsWith("plugins/")) {
					return "eclipse";
				}
			}
		} catch (IOException e) { 
			//ignore
			e.printStackTrace();
		}
		return "eclipse/plugins";
	}
}
