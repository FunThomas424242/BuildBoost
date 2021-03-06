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
package de.devboost.buildboost.steps.clone;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import de.devboost.buildboost.ant.AbstractAntTargetGenerator;
import de.devboost.buildboost.ant.AntTarget;
import de.devboost.buildboost.artifacts.RepositoriesFile.Location;
import de.devboost.buildboost.util.AntScriptUtil;
import de.devboost.buildboost.util.XMLContent;

public class CloneRepositoriesBuildStep extends AbstractAntTargetGenerator {

	private final File reposFolder;
	private final Collection<Location> locations;

	public CloneRepositoriesBuildStep(File reposFolder,
			Collection<Location> locations) {
		super();
		this.reposFolder = reposFolder;
		this.locations = locations;
	}

	@Override
	public Collection<AntTarget> generateAntTargets() {
		Collection<AntTarget> result = new ArrayList<AntTarget>();
		result.add(writeRepositoryList());

		for (Location location : locations) {
			String locationURL = location.getUrl();
			String localRepositoryFolderName = url2FolderName(locationURL);

			String rootName = url2FolderName(locationURL.substring(locationURL
					.lastIndexOf("/") + 1));

			File localRepo = new File(new File(reposFolder,
					localRepositoryFolderName), rootName);

			XMLContent content = new XMLContent();

			content.append("<condition property=\"git-executable\" value=\"git.cmd\">");
			content.append("<os family=\"windows\"/>");
			content.append("</condition>");
			content.append("<condition property=\"git-executable\" value=\"git\">");
			content.append("<not>");
			content.append("<os family=\"windows\"/>");
			content.append("</not>");
			content.append("</condition>");

			boolean isGit = location.getType().equals("git");
			boolean isSVN = location.getType().equals("svn");

			String localRepositoryPath = getLocalRepositoryPath(location);

			if (isGit) {
				if (localRepo.exists()) {
					content.append("<exec executable=\"${git-executable}\" dir=\""
							+ localRepositoryPath + "\" failonerror=\"false\">");
					content.append("<arg value=\"pull\"/>");
					content.append("</exec>");
				} else {
					content.append("<mkdir dir=\"" + localRepositoryPath
							+ "\">");
					content.append("</mkdir>");
					content.append("<exec executable=\"${git-executable}\" dir=\""
							+ reposFolder.getAbsolutePath()
							+ "\" failonerror=\"false\">");
					content.append("<arg value=\"clone\"/>");
					content.append("<arg value=\"" + locationURL + "\"/>");
					content.append("<arg value=\"" + localRepositoryPath
							+ "\"/>");
					content.append("</exec>");
				}
				if (!location.getSubDirectories().isEmpty()) {
					// enable sparse checkout
					content.append("<exec executable=\"${git-executable}\" dir=\""
							+ localRepositoryPath + "\" failonerror=\"false\">");
					content.append("<arg value=\"config\"/>");
					content.append("<arg value=\"core.sparsecheckout\"/>");
					content.append("<arg value=\"true\"/>");
					content.append("</exec>");
					String dirList = ".gitignore${line.separator}";
					for (String subDir : location.getSubDirectories()) {
						dirList += subDir;
						dirList += "${line.separator}";
					}
					content.append("<echo message=\"" + dirList + "\" file=\""
							+ localRepositoryPath
							+ "/.git/info/sparse-checkout\"/>");
					content.append("<exec executable=\"${git-executable}\" dir=\""
							+ localRepositoryPath + "\" failonerror=\"false\">");
					content.append("<arg value=\"read-tree\"/>");
					content.append("<arg value=\"-mu\"/>");
					content.append("<arg value=\"HEAD\"/>");
					content.append("</exec>");
				}
			} else if (isSVN) {
				if (localRepo.exists()) {
					// execute update
					content.append("<exec executable=\"svn\" dir=\""
							+ localRepositoryPath + "\" failonerror=\"false\">");
					content.append("<arg value=\"update\"/>");
					content.append("</exec>");
				} else {
					// execute checkout
					content.append("<exec executable=\"svn\" dir=\""
							+ reposFolder + "\" failonerror=\"false\">");
					content.append("<arg value=\"co\"/>");
					content.append("<arg value=\"" + locationURL + "\"/>");
					content.append("<arg value=\"" + localRepositoryPath
							+ "\"/>");
					content.append("</exec>");
				}
			} else /* isGet */{
				if (!localRepo.exists()) {
					content.append("<mkdir dir=\"" + localRepositoryPath
							+ "\"/>");
					if (!location.getSubDirectories().isEmpty()) {
						if (localRepo.getName().endsWith(".zip")) {
							String zipFilePath = new File(localRepo, rootName)
									.getAbsolutePath();
							content.append("<get src=\"" + locationURL
									+ "\" dest=\"" + localRepositoryPath
									+ "\"/>");
							content.append("<unzip src=\"" + zipFilePath
									+ "\" dest=\"" + localRepositoryPath
									+ "\">");
							content.append("<patternset>");
							for (String zipEntry : location.getSubDirectories()) {
								content.append("<include name=\"" + zipEntry
										+ "\"/>");
							}
							content.append("</patternset>");
							content.append("</unzip>");
							content.append("<delete file=\"" + zipFilePath
									+ "\"/>");
						} else /* folder */{
							for (String subPath : location.getSubDirectories()) {
								content.append("<get src=\"" + locationURL
										+ "/" + subPath + "\" dest=\""
										+ localRepositoryPath + "/" + subPath
										+ "\"/>");
							}
						}
					} else {
						AntScriptUtil.addDownloadFileScript(content,
								locationURL, localRepositoryPath);
					}
				}
			}
			result.add(new AntTarget("update-" + localRepositoryFolderName,
					content));
		}
		return result;
	}

	private AntTarget writeRepositoryList() {
		XMLContent content = new XMLContent();
		String revisionFile = new File(reposFolder,
				"buildboost_repository_list.txt").getAbsolutePath();
		content.append("<echo message=\"\" file=\"" + revisionFile
				+ "\" append=\"false\">");
		content.append("</echo>");
		for (Location location : locations) {
			boolean isGit = location.getType().equals("git");
			boolean isSVN = location.getType().equals("svn");

			String locationURL = location.getUrl();

			String localRepositoryPath = getLocalRepositoryPath(location);

			if (isSVN || isGit) {
				content.append("<echo message=\"BuildBoost-Repository-Type: "
						+ location.getType() + "\" file=\"" + revisionFile
						+ "\" append=\"true\">");
				content.append("</echo>");
				content.append("<echo message=\"BuildBoost-Repository-URL: "
						+ locationURL + "\" file=\"" + revisionFile
						+ "\" append=\"true\">");
				content.append("</echo>");
				content.append("<echo message=\"BuildBoost-Repository-Local: "
						+ localRepositoryPath + "\" file=\"" + revisionFile
						+ "\" append=\"true\">");
				content.append("</echo>");
			}
		}

		return new AntTarget("write-repository-list", content);
	}

	protected String getLocalRepositoryPath(Location location) {
		String locationURL = location.getUrl();
		String localRepositoryFolderName = url2FolderName(locationURL);
		String rootName = url2FolderName(locationURL.substring(locationURL
				.lastIndexOf("/") + 1));
		return new File(new File(reposFolder, localRepositoryFolderName),
				rootName).getAbsolutePath();
	}

	protected String url2FolderName(String url) {
		int idx;
		String folderName = url;
		// cut leading protocol
		idx = folderName.indexOf("//");
		if (idx != -1) {
			folderName = folderName.substring(idx + 2);
		}
		// cut arguments
		idx = folderName.indexOf("?");
		if (idx != -1) {
			folderName = folderName.substring(0, idx);
		}
		folderName = encodeFileOrFolderName(folderName);
		return folderName;
	}

	/**
	 * @param folderName
	 *            Name of file or folder (os dependend)
	 * @return encoded Folder name as new object (copy)
	 */
	public static String encodeFileOrFolderName(String orgFolderName) {
		// explicit copy to avoid the same object in a special case without
		// special chars.
		String folderName = new String(orgFolderName);
		folderName = folderName.replace(":", "");
		folderName = folderName.replace("~", "_");
		folderName = folderName.replace("/", "_");
		folderName = folderName.replace("\\", "_");
		folderName = folderName.replace(" ", "-");
		return folderName;
	}
}
