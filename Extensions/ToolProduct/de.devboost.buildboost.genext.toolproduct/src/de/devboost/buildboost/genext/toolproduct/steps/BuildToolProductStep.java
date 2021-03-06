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
package de.devboost.buildboost.genext.toolproduct.steps;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AbstractAntTargetGenerator;
import de.devboost.buildboost.ant.AntTarget;
import de.devboost.buildboost.genext.updatesite.artifacts.EclipseUpdateSiteDeploymentSpec;
import de.devboost.buildboost.util.XMLContent;
import de.devboost.buildboost.util.AntScriptUtil;

public class BuildToolProductStep extends AbstractAntTargetGenerator {
	
	private EclipseUpdateSiteDeploymentSpec deploymentSpec;
	private File targetDir;

	public BuildToolProductStep(EclipseUpdateSiteDeploymentSpec deploymentSpec, File targetDir) {
		super();
		this.deploymentSpec = deploymentSpec;
		this.targetDir = targetDir;
	}
	
	@Override
	public Collection<AntTarget> generateAntTargets() throws BuildException {
		AntTarget updateSiteTarget = generateUpdateSiteAntTarget();
		return Collections.singletonList(updateSiteTarget);
	}

	protected AntTarget generateUpdateSiteAntTarget() throws BuildException {
		if (deploymentSpec == null) {
			throw new BuildException("Can't find deployment spec site for product specification.");
		}
		
		XMLContent content = new XMLContent();
		
		content.append("<property environment=\"env\"/>");
		content.append("<!-- Get BUILD_ID from environment -->");
		content.append("<condition property=\"buildid\" value=\"${env.BUILD_ID}\">");
		content.append("<isset property=\"env.BUILD_ID\" />");
		content.append("</condition>");
		content.appendLineBreak();
		//TODO this is not good, because the tstamp should not be stage dependent
		content.append("<!-- fallback if env.BUILD_ID is not set -->");
		content.append("<tstamp/>");
		content.append("<property name=\"buildid\" value=\"${DSTAMP}${TSTAMP}\" />");
		content.appendLineBreak();
		
		File updateSiteFolder = deploymentSpec.getUpdateSite().getFile().getParentFile();
		
		File productBuildDir = new File(targetDir, "products");
		productBuildDir.mkdir();
		
		String productName = deploymentSpec.getValue("product", "name");
		String productFeatureID = deploymentSpec.getValue("product", "feature");
		String siteVersion = deploymentSpec.getFeatureVersion(productFeatureID);
		
		File sdkFolder = new File(targetDir.getParentFile().getParentFile(), "eclipse-sdks");
		File productFolder = new File(productBuildDir, productName);
		sdkFolder.mkdir();
		productFolder.mkdir();
		
		//call director for publishing
		Map<String, String> configs = deploymentSpec.getValues("product", "type");
		for (Entry<String, String> conf : configs.entrySet()) {
			String productType = conf.getKey();
			String url = conf.getValue();
			String sdkZipName = url.substring(url.lastIndexOf("/") + 1);
			File sdkZipFile = new File(sdkFolder, sdkZipName);
			
			if (!sdkZipFile.exists()) {
				AntScriptUtil.addDownloadFileScript(content, url, sdkFolder.getAbsolutePath());
			}
			content.appendLineBreak();
			
			File productInstallationFolder = new File(productFolder, productType);
			productInstallationFolder.mkdir();
			AntScriptUtil.addZipFileExtractionScript(content, sdkZipFile, productInstallationFolder);
			content.appendLineBreak();
			
			content.append("<exec executable=\"eclipse\" failonerror=\"true\">");
			
			content.append("<arg value=\"--launcher.suppressErrors\"/>");
			content.append("<arg value=\"-noSplash\"/>");
			content.append("<arg value=\"-application\"/>");
			content.append("<arg value=\"org.eclipse.equinox.p2.director\"/>");
			
			content.append("<arg value=\"-repository\"/>");
			//TODO Juno and Feedback update-sites are hard coded as dependency here
			content.append("<arg value=\"file:" + updateSiteFolder.getAbsolutePath() + ",http://download.eclipse.org/releases/juno,http://www.devboost.de/eclipse-feedback/update\"/>");
			content.append("<arg value=\"-installIU\"/>");
			content.append("<arg value=\"" + productFeatureID + ".feature.group\"/>");
			content.append("<arg value=\"-tag\"/>");
			content.append("<arg value=\"InstallationOf" + productName + "\"/>");
			content.append("<arg value=\"-destination\"/>");
			content.append("<arg value=\"" + productInstallationFolder.getAbsolutePath() + "/eclipse\"/>");
			content.append("<arg value=\"-profile\"/>");
			content.append("<arg value=\"SDKProfile\"/>");
			
			content.append("</exec>");
			content.appendLineBreak();
			
			//TODO do more stuff:
			// - rename "eclipse" base folder
			// - add workspace (put 'osgi.instance.area.default=../../../workspace' into config.ini)
			// - replace eclipse launcher file/folder by custom launcher (or rename launcher)
			// - configure splash screen
			

			File productsDistFolder = new File(updateSiteFolder.getParentFile().getParentFile(), "products");
			String productZipPath = new File(productsDistFolder, productName + "-" + siteVersion + "-" + productType + ".zip").getAbsolutePath();
			productsDistFolder.mkdir();
			//TODO this needs to use native tar.gz for unix systems in order to preserve file flags
			content.append("<zip destfile=\"" + productZipPath  + "\" basedir=\""+ productInstallationFolder.getAbsolutePath() + "\" />");
			content.appendLineBreak();
			
			//TODO upload ZIP
		}

		String updateSiteID = deploymentSpec.getUpdateSite().getIdentifier();
		AntTarget target = new AntTarget("build-eclipse-tool-product-" + updateSiteID, content);
		return target;
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
package de.devboost.buildboost.genext.toolproduct.steps;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AbstractAntTargetGenerator;
import de.devboost.buildboost.ant.AntTarget;
import de.devboost.buildboost.genext.updatesite.artifacts.EclipseUpdateSiteDeploymentSpec;
import de.devboost.buildboost.util.XMLContent;
import de.devboost.buildboost.util.AntScriptUtil;

public class BuildToolProductStep extends AbstractAntTargetGenerator {
	
	private EclipseUpdateSiteDeploymentSpec deploymentSpec;

	public BuildToolProductStep(EclipseUpdateSiteDeploymentSpec deploymentSpec) {
		super();
		this.deploymentSpec = deploymentSpec;
	}
	
	@Override
	public Collection<AntTarget> generateAntTargets() throws BuildException {
		AntTarget updateSiteTarget = generateUpdateSiteAntTarget();
		return Collections.singletonList(updateSiteTarget);
	}

	protected AntTarget generateUpdateSiteAntTarget() throws BuildException {
		if (deploymentSpec == null) {
			throw new BuildException("Can't find deployment spec site for product specification.");
		}
		
		XMLContent content = new XMLContent();
		
		content.append("<property environment=\"env\"/>");
		content.append("<!-- Get BUILD_ID from environment -->");
		content.append("<condition property=\"buildid\" value=\"${env.BUILD_ID}\">");
		content.append("<isset property=\"env.BUILD_ID\" />");
		content.append("</condition>");
		content.appendLineBreak();
		//TODO this is not good, because the tstamp should not be stage dependent
		content.append("<!-- fallback if env.BUILD_ID is not set -->");
		content.append("<tstamp/>");
		content.append("<property name=\"buildid\" value=\"${DSTAMP}${TSTAMP}\" />");
		content.appendLineBreak();
		
		File deployedUpdateSiteFolder = deploymentSpec.getUpdateSite().getFile().getParentFile();
		File updateSiteFolder = deploymentSpec.getFile().getParentFile();
		
		String distProductsPath = "dist/products";
		content.append("<mkdir dir=\"" + distProductsPath + "\" />");
		
		String productName = deploymentSpec.getValue("product", "name");
		String productFeatureID = deploymentSpec.getValue("product", "feature");
		String siteVersion = deploymentSpec.getUpdateSite().getFeature(productFeatureID).getVersion();
		
		String sdkFolderPath = "../eclipse-sdks";
		content.append("<mkdir dir=\"" + sdkFolderPath + "\" />");

		String productFolderPath = distProductsPath + "/" + productName;
		content.append("<mkdir dir=\"" + productFolderPath + "\" />");
		
		//call director for publishing
		Map<String, String> configs = deploymentSpec.getValues("product", "type");
		for (Entry<String, String> conf : configs.entrySet()) {
			String productType = conf.getKey();
			String url = conf.getValue();
			String sdkZipName = url.substring(url.lastIndexOf("/") + 1);
			File sdkZipFile = new File(sdkFolderPath, sdkZipName);
			
			if (!sdkZipFile.exists()) {
				AntScriptUtil.addDownloadFileScript(content, url, new File(sdkFolderPath).getAbsolutePath());
			}
			content.appendLineBreak();
			
			File productInstallationFolder = new File(productFolderPath + "/" + productType + "/eclipse");
			File brandedProductFolder = new File(productFolderPath + "/" + productType + "/" + productName);
			
			content.append("<mkdir dir=\"" + productInstallationFolder.getParentFile().getAbsolutePath() + "\" />");
			AntScriptUtil.addZipFileExtractionScript(content, sdkZipFile, productInstallationFolder.getParentFile());
			content.appendLineBreak();
			
			content.append("<exec executable=\"eclipse\" failonerror=\"true\">");
			
			content.append("<arg value=\"--launcher.suppressErrors\"/>");
			content.append("<arg value=\"-noSplash\"/>");
			content.append("<arg value=\"-application\"/>");
			content.append("<arg value=\"org.eclipse.equinox.p2.director\"/>");
			
			content.append("<arg value=\"-repository\"/>");
			//TODO Juno update-site is hard coded as dependency here
			content.append("<arg value=\"file:" + deployedUpdateSiteFolder.getAbsolutePath() + ",http://download.eclipse.org/releases/juno\"/>");
			content.append("<arg value=\"-installIU\"/>");
			content.append("<arg value=\"" + productFeatureID + ".feature.group\"/>");
			content.append("<arg value=\"-tag\"/>");
			content.append("<arg value=\"InstallationOf" + productName + "\"/>");
			content.append("<arg value=\"-destination\"/>");
			content.append("<arg value=\"" + productInstallationFolder.getAbsolutePath() + "\"/>");
			content.append("<arg value=\"-profile\"/>");
			content.append("<arg value=\"SDKProfile\"/>");
			
			content.append("</exec>");
			content.appendLineBreak();
			
			
			File splashScreenFile = new File(updateSiteFolder, "splash.bmp");
			File pluginFolder = new File(productInstallationFolder, "plugins");
			File iconFolder = new File(updateSiteFolder, "icons");

			File osxIconFile = new File(iconFolder, "Eclipse.icns");
			File osxAppFolder = new File(productInstallationFolder, "Eclipse.app");
			File osxBrandedAppFolder = new File(productInstallationFolder, productName + ".app");
			File osxIconFolder =  new File(osxAppFolder, "Contents/Resources");
			String[] iconFormats = new String[] { "16.gif", "16.png", "32.gif", "32.png", "48.gif", "48.png", "256.png" };
			
			File windowsExe = null;
			if (productType.contains("64")) {
				windowsExe = new File(updateSiteFolder, "eclipse64.exe");
			} else {
				windowsExe = new File(updateSiteFolder, "eclipse32.exe");
			}
			File windowsBrandedExe = new File(productInstallationFolder, productName + ".exe");
			
			File linuxIconFile = new File(iconFolder, "icon.xpm");
			File linuxExe = new File(productInstallationFolder, "eclipse");
			File linuxBrandedExe = new File(productInstallationFolder, productName);
			
			File workspace = new File(updateSiteFolder, "workspace");
			File configIni = new File(productInstallationFolder, "configuration/config.ini");	
			File uiPrefs = new File(productInstallationFolder, "configuration/.settings/org.eclipse.ui.ide.prefs");	
			
			//copy splash
			content.append("<first id=\"platformPlugin\">");
			content.append("<dirset dir=\"" + pluginFolder.getAbsolutePath() + "\" includes=\"org.eclipse.platform_*\"/>");
			content.append("</first>");
			
			content.append("<copy overwrite=\"true\" file=\"" + splashScreenFile.getAbsolutePath() + "\" todir=\"${toString:platformPlugin}\"/>");
			//copy icons
			for (String iconFormat : iconFormats) {
				content.append("<copy overwrite=\"true\" file=\"" + new File(iconFolder, "eclipse" + iconFormat).getAbsolutePath() + "\" todir=\"${toString:platformPlugin}\"/>");
			}
			
			content.append("<copy overwrite=\"true\" file=\"" + splashScreenFile.getAbsolutePath() + "\" todir=\"${toString:platformPlugin}\"/>");
			
			//copy icon osx
			if (productType.startsWith("osx")) {
				//copy icon osx
				content.append("<copy overwrite=\"true\" file=\"" + osxIconFile.getAbsolutePath() + "\" todir=\"" + osxIconFolder.getAbsolutePath() + "\"/>");
				//rename app folder
				content.append("<move file=\"" + osxAppFolder.getAbsolutePath() + "\" tofile=\"" + osxBrandedAppFolder.getAbsolutePath() +"\"/>");
				//remove command line "eclipse"
				content.append("<delete file=\"" + new File(productInstallationFolder, "eclipse").getAbsolutePath() + "\"/>");
			} else if (productType.startsWith("win")) {
				//use prepared exe
				content.append("<copy overwrite=\"true\" file=\"" + windowsExe.getAbsolutePath() + "\" tofile=\"" + windowsBrandedExe.getAbsolutePath() +"\"/>");
				//remove command line "eclipse"
				content.append("<delete file=\"" + new File(productInstallationFolder, "eclipse.exe").getAbsolutePath() + "\"/>");
				content.append("<delete file=\"" + new File(productInstallationFolder, "eclipsec.exe").getAbsolutePath() + "\"/>");
			} else {
				//copy icon linux
				content.append("<copy overwrite=\"true\" file=\"" + linuxIconFile.getAbsolutePath() + "\" todir=\"" + productInstallationFolder.getAbsolutePath() + "\"/>");
				//rename exe
				content.append("<move file=\"" + linuxExe.getAbsolutePath() + "\" tofile=\"" + linuxBrandedExe.getAbsolutePath() +"\"/>");
			}
			
			//copy workspace
			content.append("<copy todir=\"" + new File(productInstallationFolder, "workspace").getAbsolutePath() + "\">");
			content.append("<fileset dir=\""+ workspace.getAbsolutePath() + "\"/>");
			content.append("</copy>");
			
			String userHomeWorkspace;
			String appRelativeWorkspace;
			if (productType.equals("osx")) {
				userHomeWorkspace = "osgi.instance.area.default=@user.home/Documents/workspace";
				appRelativeWorkspace = "osgi.instance.area.default=../../../workspace";
			} else {
				userHomeWorkspace = "osgi.instance.area.default=@user.home/workspace";
				appRelativeWorkspace = "osgi.instance.area.default=./workspace";
			}
			//change default workspace
			content.append("<replace file=\"" + configIni.getAbsolutePath() + "\" token=\"" + userHomeWorkspace + "\" value=\"" + appRelativeWorkspace + "\"/>");
			content.append("<mkdir dir=\"" + uiPrefs.getParentFile().getAbsolutePath() + "\"/>");
			content.append("<echo file=\"" + uiPrefs.getAbsolutePath() + "\" message=\"SHOW_WORKSPACE_SELECTION_DIALOG=false\"/>");
			
			//rename base folder
			content.append("<move file=\"" + productInstallationFolder.getAbsolutePath() + "\" tofile=\"" + brandedProductFolder.getAbsolutePath() +"\"/>");

			File productsDistFolder = new File(deployedUpdateSiteFolder.getParentFile().getParentFile(), "products");
			content.append("<mkdir dir=\"" + productsDistFolder.getAbsolutePath() + "\" />");

			String zipType;
			if (productType.startsWith("win")) {
				zipType = "zip";
			} else {
				zipType = "tar.gz";
			}
			String productZipPath = new File(productsDistFolder, productName + "-" + siteVersion + "-" + productType + "." + zipType).getAbsolutePath();
			AntScriptUtil.addZipFileCompressionScript(content, productZipPath,  brandedProductFolder.getParentFile().getAbsolutePath());
			content.appendLineBreak();
			
			//TODO upload ZIP
		}

		String updateSiteID = deploymentSpec.getUpdateSite().getIdentifier();
		AntTarget target = new AntTarget("build-eclipse-tool-product-" + updateSiteID, content);
		return target;
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
package de.devboost.buildboost.genext.toolproduct.steps;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.ant.AbstractAntTargetGenerator;
import de.devboost.buildboost.ant.AntTarget;
import de.devboost.buildboost.genext.updatesite.artifacts.EclipseUpdateSiteDeploymentSpec;
import de.devboost.buildboost.util.XMLContent;
import de.devboost.buildboost.util.AntScriptUtil;

public class BuildToolProductStep extends AbstractAntTargetGenerator {
	
	private EclipseUpdateSiteDeploymentSpec deploymentSpec;

	public BuildToolProductStep(EclipseUpdateSiteDeploymentSpec deploymentSpec) {
		super();
		this.deploymentSpec = deploymentSpec;
	}
	
	@Override
	public Collection<AntTarget> generateAntTargets() throws BuildException {
		AntTarget updateSiteTarget = generateUpdateSiteAntTarget();
		return Collections.singletonList(updateSiteTarget);
	}

	protected AntTarget generateUpdateSiteAntTarget() throws BuildException {
		if (deploymentSpec == null) {
			throw new BuildException("Can't find deployment spec site for product specification.");
		}
		
		XMLContent content = new XMLContent();
		
		content.append("<property environment=\"env\"/>");
		content.append("<!-- Get BUILD_ID from environment -->");
		content.append("<condition property=\"buildid\" value=\"${env.BUILD_ID}\">");
		content.append("<isset property=\"env.BUILD_ID\" />");
		content.append("</condition>");
		content.appendLineBreak();
		//TODO this is not good, because the tstamp should not be stage dependent
		content.append("<!-- fallback if env.BUILD_ID is not set -->");
		content.append("<tstamp/>");
		content.append("<property name=\"buildid\" value=\"${DSTAMP}${TSTAMP}\" />");
		content.appendLineBreak();
		
		File deployedUpdateSiteFolder = deploymentSpec.getUpdateSite().getFile().getParentFile();
		File updateSiteFolder = deploymentSpec.getFile().getParentFile();
		
		String distProductsPath = "dist/products";
		content.append("<mkdir dir=\"" + distProductsPath + "\" />");
		
		String productName = deploymentSpec.getValue("product", "name");
		String productFeatureID = deploymentSpec.getValue("product", "feature");
		String siteVersion = deploymentSpec.getUpdateSite().getFeature(productFeatureID).getVersion();
		
		String sdkFolderPath = "../eclipse-sdks";
		content.append("<mkdir dir=\"" + sdkFolderPath + "\" />");

		String productFolderPath = distProductsPath + "/" + productName;
		content.append("<mkdir dir=\"" + productFolderPath + "\" />");
		
		//call director for publishing
		Map<String, String> configs = deploymentSpec.getValues("product", "type");
		for (Entry<String, String> conf : configs.entrySet()) {
			String productType = conf.getKey();
			String url = conf.getValue();
			String sdkZipName = url.substring(url.lastIndexOf("/") + 1);
			File sdkZipFile = new File(sdkFolderPath, sdkZipName);
			
			if (!sdkZipFile.exists()) {
				AntScriptUtil.addDownloadFileScript(content, url, new File(sdkFolderPath).getAbsolutePath());
			}
			content.appendLineBreak();
			
			File productInstallationFolder = new File(productFolderPath + "/" + productType + "/eclipse");
			File brandedProductFolder = new File(productFolderPath + "/" + productType + "/" + productName);
			
			content.append("<mkdir dir=\"" + productInstallationFolder.getParentFile().getAbsolutePath() + "\" />");
			AntScriptUtil.addZipFileExtractionScript(content, sdkZipFile, productInstallationFolder.getParentFile());
			content.appendLineBreak();
			
			content.append("<exec executable=\"eclipse\" failonerror=\"true\">");
			
			content.append("<arg value=\"--launcher.suppressErrors\"/>");
			content.append("<arg value=\"-noSplash\"/>");
			content.append("<arg value=\"-application\"/>");
			content.append("<arg value=\"org.eclipse.equinox.p2.director\"/>");
			
			content.append("<arg value=\"-repository\"/>");
			//TODO Juno update-site is hard coded as dependency here
			content.append("<arg value=\"file:" + deployedUpdateSiteFolder.getAbsolutePath() + ",http://download.eclipse.org/releases/juno\"/>");
			content.append("<arg value=\"-installIU\"/>");
			content.append("<arg value=\"" + productFeatureID + ".feature.group\"/>");
			content.append("<arg value=\"-tag\"/>");
			content.append("<arg value=\"InstallationOf" + productName + "\"/>");
			content.append("<arg value=\"-destination\"/>");
			content.append("<arg value=\"" + productInstallationFolder.getAbsolutePath() + "\"/>");
			content.append("<arg value=\"-profile\"/>");
			content.append("<arg value=\"SDKProfile\"/>");
			
			content.append("</exec>");
			content.appendLineBreak();
			
			
			File splashScreenFile = new File(updateSiteFolder, "splash.bmp");
			File pluginFolder = new File(productInstallationFolder, "plugins");
			File iconFolder = new File(updateSiteFolder, "icons");

			File osxIconFile = new File(iconFolder, "Eclipse.icns");
			File osxAppFolder = new File(productInstallationFolder, "Eclipse.app");
			File osxBrandedAppFolder = new File(productInstallationFolder, productName + ".app");
			File osxIconFolder =  new File(osxAppFolder, "Contents/Resources");
			String[] iconFormats = new String[] { "16.gif", "16.png", "32.gif", "32.png", "48.gif", "48.png", "256.png" };
			
			File windowsExe = null;
			if (productType.contains("64")) {
				windowsExe = new File(updateSiteFolder, "eclipse64.exe");
			} else {
				windowsExe = new File(updateSiteFolder, "eclipse32.exe");
			}
			File windowsBrandedExe = new File(productInstallationFolder, productName + ".exe");
			
			File linuxIconFile = new File(iconFolder, "icon.xpm");
			File linuxExe = new File(productInstallationFolder, "eclipse");
			File linuxBrandedExe = new File(productInstallationFolder, productName);
			
			File workspace = new File(updateSiteFolder, "workspace");
			File configIni = new File(productInstallationFolder, "configuration/config.ini");	
			File uiPrefs = new File(productInstallationFolder, "configuration/.settings/org.eclipse.ui.ide.prefs");	
			
			//copy splash
			content.append("<first id=\"platformPlugin\">");
			content.append("<dirset dir=\"" + pluginFolder.getAbsolutePath() + "\" includes=\"org.eclipse.platform_*\"/>");
			content.append("</first>");
			
			content.append("<copy overwrite=\"true\" file=\"" + splashScreenFile.getAbsolutePath() + "\" todir=\"${toString:platformPlugin}\"/>");
			//copy icons
			for (String iconFormat : iconFormats) {
				content.append("<copy overwrite=\"true\" file=\"" + new File(iconFolder, "eclipse" + iconFormat).getAbsolutePath() + "\" todir=\"${toString:platformPlugin}\"/>");
			}
			
			content.append("<copy overwrite=\"true\" file=\"" + splashScreenFile.getAbsolutePath() + "\" todir=\"${toString:platformPlugin}\"/>");
			
			//copy icon osx
			if (productType.startsWith("osx")) {
				//copy icon osx
				content.append("<copy overwrite=\"true\" file=\"" + osxIconFile.getAbsolutePath() + "\" todir=\"" + osxIconFolder.getAbsolutePath() + "\"/>");
				//rename app folder
				content.append("<move file=\"" + osxAppFolder.getAbsolutePath() + "\" tofile=\"" + osxBrandedAppFolder.getAbsolutePath() +"\"/>");
				//remove command line "eclipse"
				content.append("<delete file=\"" + new File(productInstallationFolder, "eclipse").getAbsolutePath() + "\"/>");
			} else if (productType.startsWith("win")) {
				//use prepared exe
				content.append("<copy overwrite=\"true\" file=\"" + windowsExe.getAbsolutePath() + "\" tofile=\"" + windowsBrandedExe.getAbsolutePath() +"\"/>");
				//remove command line "eclipse"
				content.append("<delete file=\"" + new File(productInstallationFolder, "eclipse.exe").getAbsolutePath() + "\"/>");
				content.append("<delete file=\"" + new File(productInstallationFolder, "eclipsec.exe").getAbsolutePath() + "\"/>");
			} else {
				//copy icon linux
				content.append("<copy overwrite=\"true\" file=\"" + linuxIconFile.getAbsolutePath() + "\" todir=\"" + productInstallationFolder.getAbsolutePath() + "\"/>");
				//rename exe
				content.append("<move file=\"" + linuxExe.getAbsolutePath() + "\" tofile=\"" + linuxBrandedExe.getAbsolutePath() +"\"/>");
			}
			
			//copy workspace
			content.append("<copy todir=\"" + new File(productInstallationFolder, "workspace").getAbsolutePath() + "\">");
			content.append("<fileset dir=\""+ workspace.getAbsolutePath() + "\"/>");
			content.append("</copy>");
			
			String userHomeWorkspace;
			String appRelativeWorkspace;
			if (productType.equals("osx")) {
				userHomeWorkspace = "osgi.instance.area.default=@user.home/Documents/workspace";
				appRelativeWorkspace = "osgi.instance.area.default=../../../workspace";
			} else {
				userHomeWorkspace = "osgi.instance.area.default=@user.home/workspace";
				appRelativeWorkspace = "osgi.instance.area.default=./workspace";
			}
			//change default workspace
			content.append("<replace file=\"" + configIni.getAbsolutePath() + "\" token=\"" + userHomeWorkspace + "\" value=\"" + appRelativeWorkspace + "\"/>");
			content.append("<mkdir dir=\"" + uiPrefs.getParentFile().getAbsolutePath() + "\"/>");
			content.append("<echo file=\"" + uiPrefs.getAbsolutePath() + "\" message=\"SHOW_WORKSPACE_SELECTION_DIALOG=false\"/>");
			
			//rename base folder
			content.append("<move file=\"" + productInstallationFolder.getAbsolutePath() + "\" tofile=\"" + brandedProductFolder.getAbsolutePath() +"\"/>");

			File productsDistFolder = new File(deployedUpdateSiteFolder.getParentFile().getParentFile(), "products");
			content.append("<mkdir dir=\"" + productsDistFolder.getAbsolutePath() + "\" />");

			String zipType;
			if (productType.startsWith("win")) {
				zipType = "zip";
			} else {
				zipType = "tar.gz";
			}
			String productZipPath = new File(productsDistFolder, productName + "-" + siteVersion + "-" + productType + "." + zipType).getAbsolutePath();
			AntScriptUtil.addZipFileCompressionScript(content, productZipPath,  brandedProductFolder.getParentFile().getAbsolutePath());
			content.appendLineBreak();
			
			//TODO upload ZIP
		}

		String updateSiteID = deploymentSpec.getUpdateSite().getIdentifier();
		AntTarget target = new AntTarget("build-eclipse-tool-product-" + updateSiteID, content);
		return target;
	}
}
>>>>>>> upstream/master
