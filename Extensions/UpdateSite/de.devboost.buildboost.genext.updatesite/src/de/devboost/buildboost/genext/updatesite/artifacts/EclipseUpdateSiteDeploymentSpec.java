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
package de.devboost.buildboost.genext.updatesite.artifacts;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import de.devboost.buildboost.artifacts.AbstractArtifact;
import de.devboost.buildboost.model.IDependable;
import de.devboost.buildboost.model.UnresolvedDependency;

@SuppressWarnings("serial")
public class EclipseUpdateSiteDeploymentSpec extends AbstractArtifact {

	private File file;
	private Properties properties;

	public EclipseUpdateSiteDeploymentSpec(File file) {
		this.file = file;
		readVersionFile();
		// use parent directory name as identifier
		String identifier = file.getParentFile().getName();
		setIdentifier(identifier);
		getUnresolvedDependencies().add(new UnresolvedDependency(EclipseUpdateSite.class, identifier, null, true, null, true, false, false));
	}
	
	public File getFile() {
		return file;
	}

	private void readVersionFile() {
		properties = new Properties();
		try {
			Reader reader = new FileReader(file);
			properties.load(reader);
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException("Exception while reading deployment specificiation: " + e.getMessage());
		}
	}
	
	public String getValue(String... path) {
		StringBuilder key = new StringBuilder();
		for (int i = 0; i < path.length - 1; i++) {
			key.append(path[i]);
			key.append("/");
		}
		if (path.length > 0) {
			key.append(path[path.length - 1]);
		}
		return getValue(key.toString());
	}
	
	private String getValue(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			return null;
		}
		if (value.startsWith("$")) {
			return getValue(value.substring(1));
		}
		return value;
	}
	
	public Map<String, String> getValues(String... path) {
		StringBuilder key = new StringBuilder();
		Map<String, String> values = new LinkedHashMap<String, String>();
		for (int i = 0; i < path.length; i++) {
			key.append(path[i]);
			key.append("/");
		}
		for(Map.Entry<Object, Object> entry : properties.entrySet()) {
			if (entry.getKey().toString().startsWith(key.toString())) {
				String subKey = entry.getKey().toString().substring(
						key.toString().length());
				if (!subKey.contains("/")) {
					values.put(subKey, entry.getValue().toString());
				}
			}
		}
		return values;
	}

	public EclipseUpdateSite getUpdateSite() {
		for (IDependable dependency : getDependencies()) {
			if (dependency instanceof EclipseUpdateSite) {
				EclipseUpdateSite eclipseUpdateSite = (EclipseUpdateSite) dependency;
				return eclipseUpdateSite;
			}
		}
		return null;
	}

	public String getSiteVendor() {
		return getValue("site", "vendor");
	}

	public String getFeatureVendor(String featureID) {
		String featureVendor =  getValue("feature", featureID, "vendor");
		if (featureVendor == null) {
			featureVendor = getSiteVendor();
		}
		if (featureVendor == null) {
			featureVendor = "Unknown vendor";
		}
		return featureVendor;
	}

	public String getSiteVersion() {
		return getValue("site", "version");
	}

	/* Feature version are now retrieved from the feature descriptors
	public String getFeatureVersion(String featureID) {
		String featureVersion = getValue("feature", featureID, "version");
		if (featureVersion == null) {
			featureVersion =  getSiteVersion();
		}
		if (featureVersion == null) {
			featureVersion = "0.0.1";
		}
		return featureVersion;
	}
	*/

	@Override
	public long getTimestamp() {
		return file.lastModified();
	}
}
