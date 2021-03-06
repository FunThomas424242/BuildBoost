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
package de.devboost.buildboost.util;

import java.io.File;
import java.io.FileFilter;

/**
 * A utility class to process Eclipse plug-ins projects.
 */
public class EclipsePluginHelper {

	/**
	 * Checks whether the given directory contains a '.project' file.
	 */
	public boolean isProject(File directory) {
		if (!directory.isDirectory()) {
			return false;
		}
		File[] files = directory.listFiles(new FileFilter() {
			
			public boolean accept(File file) {
				return ".project".equals(file.getName());
			}
		});
		if (files == null) {
			return false;
		}
		return files.length > 0;
	}

	/**
	 * Searches for a parent of the given file that is an Eclipse plug-in 
	 * project. If no such parent is found, null is returned.
	 * 
	 * @param file a file contained in an Eclipse plug-in project
	 * @return the directory root of the plug-in project
	 */
	public File findProjectDir(File file) {
		File parent = file.getParentFile();
		while (parent != null) {
			boolean isProject = isProject(parent);
			if (isProject) {
				return parent;
			}
			parent = parent.getParentFile();
		}
		return null;
	}
}
