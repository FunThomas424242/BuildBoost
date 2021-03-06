package de.devboost.buildboost;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is a singleton!
 * 
 */
public class GlobalBuildConfiguration {

	final private static Properties userProperties = new Properties();

	// none extern instance creation support
	private GlobalBuildConfiguration() {
		System.out.println("GlobalUserConfig: set defaults");
		setDefaultValues();
		System.out.println("GlobalUserConfig: read config file");
		readGlobalConfiguration();
	}

	// lazy init by static holder class
	private static class Holder {
		private static final GlobalBuildConfiguration INSTANCE = new GlobalBuildConfiguration();
	}

	public static GlobalBuildConfiguration getInstance() {
		return Holder.INSTANCE;
	}

	private void setDefaultValues() {
		// debugging support
		userProperties.setProperty(GlobalBuildConfiguration.DEBUG,
				GlobalBuildConfiguration.DEBUG_DEFAULT);
		// remote debug option line
		userProperties.setProperty(GlobalBuildConfiguration.JVMARG_DEBUG,
				GlobalBuildConfiguration.JVMARG_DEBUG_DEFAULT);
		// jvm mx para
		userProperties.setProperty(GlobalBuildConfiguration.JVMARG_MX,
				GlobalBuildConfiguration.JVMARG_MX_DEFAULT);
		// jvm maxperm para
		userProperties.setProperty(GlobalBuildConfiguration.JVMARG_MAXPERM,
				GlobalBuildConfiguration.JVMARG_MAXPERM_DEFAULT);
	}

	private static void readGlobalConfiguration() {
		final String userHomePath = System.getProperty("user.home");
		final String globalConfigFileName = userHomePath + File.separator
				+ ".buildboost";
		final File globalConfigFile = new File(globalConfigFileName);

		if (globalConfigFile.exists() && globalConfigFile.length() > 1) {
			System.out
					.println("Loading user defined global configuration from .buildboost file.");
			FileReader reader = null;
			try {
				reader = new FileReader(globalConfigFile);
				userProperties.load(reader);
			} catch (IOException e) {
				e.printStackTrace();
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
		} else {
			System.out
					.println("There was no user configuration found in the users .buildboost file");
		}
	}

	public String getConfigItem(final String key) {
		return userProperties.getProperty(key);
	}

	public String getConfigItem(final String key, final String defaultValue) {
		return userProperties.getProperty(key, defaultValue);
	}

	public boolean isDebugEnabled() {
		return !DEBUG_DEFAULT
				.equals(getConfigItem(GlobalBuildConfiguration.DEBUG));
	}

	/* static constants for standard global entries and defaults */

	// remote debugging support
	public static final String DEBUG = "debug";
	// 0=disabled, >0=remote debugging support && loglevel
	// 1 = log level 1, 2 = log level 2, ...
	public static final String DEBUG_DEFAULT = "0";

	// jvm remote debug option line
	final public static String JVMARG_DEBUG = "jvm_debugoption_line";
	final public static String JVMARG_DEBUG_DEFAULT = "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000";

	// jvm mx arg
	final public static String JVMARG_MX = "jvmarg_mx";
	final public static String JVMARG_MX_DEFAULT = "-Xmx2048m";

	// jvm maxperm arg
	final public static String JVMARG_MAXPERM = "jvm_maxperm";
	final public static String JVMARG_MAXPERM_DEFAULT = "-XX:MaxPermSize=256m";

}
