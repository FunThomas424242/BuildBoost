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
package de.devboost.buildboost.buildext.emftext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.emftext.sdk.IPluginDescriptor;
import org.emftext.sdk.codegen.IFileSystemConnector;
import org.emftext.sdk.codegen.resource.ui.CreateResourcePluginsJob.Result;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation;
import org.emftext.sdk.concretesyntax.resource.cs.mopp.CsResourceFactory;
import org.emftext.sdk.concretesyntax.resource.cs.util.CsResourceUtil;

import de.devboost.buildboost.BuildException;
import de.devboost.buildboost.artifacts.Plugin;
import de.devboost.buildboost.genext.emftext.steps.GenerateResourcePluginsStep;

/**
 * The {@link TextResourcePluginGenerator} is executed by build scripts that are
 * generated by the {@link GenerateEMFTextCodeStage} or the
 * {@link GenerateResourcePluginsStep}. It runs the EMFText code generation for
 * a given syntax specification.
 */
public class TextResourcePluginGenerator {

	// TODO use property file to pass arguments instead
	public static void main(String[] args) throws Exception {
		String pathToCsFile = args[0];
		String projectName = args[1];
		String buildDirPath = args[2];
		List<String> pluginPaths = readPluginPaths(args[3]);
		new TextResourcePluginGenerator().run(pathToCsFile, projectName,
				buildDirPath, pluginPaths);
	}

	// TODO Duplicate code
	// @see de.devboost.buildboost.buildext.emf.HeadlessCodeGenerator
	private static List<String> readPluginPaths(final String paraFileName) {
		List<String> paths = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			final FileReader fin = new FileReader(paraFileName);
			reader = new BufferedReader(fin);
			String line = reader.readLine();
			while (line != null) {
				paths.add(line);
				line = reader.readLine();
			}
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return paths;
	}

	public void run(String pathToCsFile, String projectName,
			String buildDirPath, List<String> pluginPaths) throws Exception {

		final File rootFolder = new File(buildDirPath);

		URI uri = URI.createFileURI(pathToCsFile);
		ResourceSet rs = new ResourceSetImpl();
		registerFactoriesAndPackages();
		registerURIMappings(pluginPaths);
		// TODO add checks
		Resource resource = rs.getResource(uri, true);
		ConcreteSyntax syntax = (ConcreteSyntax) resource.getContents().get(0);
		if (syntax == null) {
			throw new BuildException(
					"Generation failed, because the syntax file could not be loaded. Probably it contains syntactical errors.");
		}
		EcoreUtil.resolveAll(resource);

		Set<EObject> unresolvedProxies = CsResourceUtil
				.findUnresolvedProxies(rs);
		for (EObject unresolvedProxy : unresolvedProxies) {
			System.out.println("Found unresolved proxy: " + unresolvedProxy);
		}
		if (unresolvedProxies.size() > 0) {
			throw new BuildException(
					"Generation failed, because the syntax file contains unresolved proxy objects.");
		}

		IFileSystemConnector folderConnector = new IFileSystemConnector() {

			@Override
			public File getProjectFolder(IPluginDescriptor plugin) {
				return new File(rootFolder.getAbsolutePath() + File.separator
						+ plugin.getName());
			}
		};

		BuildBoostGenerationContext context = new BuildBoostGenerationContext(
				folderConnector, new BuildBoostProblemCollector(), syntax,
				rootFolder, pathToCsFile, projectName);
		Result result = new BuildBoostGenerator().run(context,
				new BuildBoostLogMarker(), new BuildBoostProgressMonitor());
		if (result != Result.SUCCESS) {
			if (result == Result.ERROR_FOUND_UNRESOLVED_PROXIES) {
				for (EObject unresolvedProxy : result.getUnresolvedProxies()) {
					System.out.println("Found unresolved proxy \""
							+ ((InternalEObject) unresolvedProxy).eProxyURI()
							+ "\" in " + unresolvedProxy.eResource());
				}
				throw new BuildException("Generation failed " + result);
			} else {
				throw new BuildException("Generation failed " + result);
			}
		}
	}

	private void registerURIMappings(List<String> pluginPaths) throws Exception {
		// TODO this is a copy of this method from class HeadlessCodeGenerator
		Map<URI, URI> uriMap = URIConverter.URI_MAP;
		for (String pluginPath : pluginPaths) {
			File pluginFile = new File(pluginPath);
			if (pluginFile.isDirectory()
					&& !pluginPath.endsWith(File.separator)) {
				pluginPath = pluginPath + File.separator;
			}
			Plugin plugin = new Plugin(pluginFile);
			String identifier = plugin.getIdentifier();
			URI from = URI.createPlatformPluginURI(identifier + "/", true);
			URI to = URI.createFileURI(pluginPath);
			if ("jar".equals(to.fileExtension())) {
				to = URI.createURI("archive:" + to.toString() + "!/");
			} else if (!"".equals(to.lastSegment())) {
				to = to.appendSegment("");
			}
			// System.out.println("Mapping URI " + from + " to " + to);
			uriMap.put(from, to);
		}
	}

	private void registerFactoriesAndPackages() {
		// TODO we must search the target platform for registered resource
		// factories,
		// generator models and EPackages
		// TODO this is a MODIFIED copy of this method from class
		// HeadlessCodeGenerator
		Map<String, Object> extensionToFactoryMap = Resource.Factory.Registry.INSTANCE
				.getExtensionToFactoryMap();
		extensionToFactoryMap.put("ecore", new EcoreResourceFactoryImpl());
		extensionToFactoryMap.put("genmodel", new EcoreResourceFactoryImpl());
		extensionToFactoryMap.put(new CsMetaInformation().getSyntaxName(),
				new CsResourceFactory());
		GenModelPackage genModelPackage = GenModelPackage.eINSTANCE;
		EcorePackage ecorePackage = EcorePackage.eINSTANCE;

		URI ecoreGenModelURI = URI.createPlatformPluginURI(
				"org.eclipse.emf.ecore/model/Ecore.genmodel", true);
		URI genmodelGenModelURI = URI.createPlatformPluginURI(
				"org.eclipse.emf.codegen.ecore/model/GenModel.genmodel", true);

		EcorePlugin.getEPackageNsURIToGenModelLocationMap().put(
				ecorePackage.getNsURI(), ecoreGenModelURI);
		EcorePlugin.getEPackageNsURIToGenModelLocationMap().put(
				genModelPackage.getNsURI(), genmodelGenModelURI);
	}
}
