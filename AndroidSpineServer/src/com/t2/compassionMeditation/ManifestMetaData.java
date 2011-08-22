package com.t2.compassionMeditation;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * Gather meta-data from the application tag of the app's manifest file.
 * @see http://developer.android.com/guide/topics/manifest/meta-data-element.html
 * @author robbiev
 *
 */
public class ManifestMetaData {
	private static final String ANALYTICS_PROVIDER = "analyticsProvider";
	private static final String ANALYTICS_KEY = "analyticsKey";
	private static final String ANALYTICS_ENABLED = "analyticsEnabled";
	
	private static final String REMOTE_STACK_TRACK_URL = "stackTraceURL";
	private static final String REMOTE_STACK_TRACE_ENABLED = "remoteStackTraceEnabled";
	
	private static final String DATABSE_NAME = "databaseName";
	private static final String DATABASE_VERSION = "databaseVersion";
	private static final String DATABASE_OPEN_HELPER = "databaseOpenHelper";
	
	private static final String SECURITY_ENABLED = "securityEnabled";
	
	private static final String DEBUG_MODE = "debugMode";
	
	private static Bundle sApplicationMetaData;
	private static HashMap<String,Object> aItemCache = new HashMap<String,Object>();
	
	/**
	 * Loads the manifest meta-data into a cached bundle.
	 * @param c
	 */
	private static void initAppBundle(Context c) {
		// meta-data already read, use the cache version.
		if(sApplicationMetaData != null) {
			return;
		}
		
		Context context = c.getApplicationContext();
		
		// Load the application meta-data (if it is there)
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), 
					PackageManager.GET_META_DATA
			);
			sApplicationMetaData = ai.metaData;
			
		} catch (NameNotFoundException e) {
			// ignore
		}
		
		// Could not load meta-data, make empty bundle
		if(sApplicationMetaData == null) {
			sApplicationMetaData = new Bundle();
		}
	}
	
	private static Object getCache(String key) {
		return aItemCache.get(key);
	}
	
	private static void setCache(String key, Object value) {
		aItemCache.put(key, value);
	}
	
	
	/**
	 * Get a string from the manifest. Caches result for later use.
	 * @param c	The context
	 * @param name	The name of the variable.
	 * @return	The string from the tag, null otherwise.
	 */
	public static String getString(Context c, String name) {
		initAppBundle(c);
		return sApplicationMetaData.getString(name);
	}
	
	/**
	 * Get an integer from the manifest. Caches result for later use.
	 * @param c
	 * @param name
	 * @return	The integer or 0 if it cannot be converted to an integer.
	 */
	public static int getInt(Context c, String name) {
		initAppBundle(c);
		return sApplicationMetaData.getInt(name);
	}
	
	/**
	 * Get a boolean from the manifest. Caches result for later use.
	 * @param c
	 * @param name
	 * @return	true if the value is "true" otherwise false.
	 */
	public static boolean getBoolean(Context c, String name) {
		initAppBundle(c);
		return sApplicationMetaData.getBoolean(name);
	}
	
	/**
	 * Get a float from the manifest. Caches result for later use.
	 * @param c
	 * @param name
	 * @return	the float or 0 otherwise.
	 */
	public static float getFloat(Context c, String name) {
		initAppBundle(c);
		return sApplicationMetaData.getFloat(name);
	}
	
	/**
	 * Returns true if the app has been configured to be in debug mode.
	 * @param c
	 * @return (default false)
	 */
	public static boolean isDebugEnabled(Context c) {
		initAppBundle(c);
		return getBoolean(c, ManifestMetaData.DEBUG_MODE);
	}
	
	/**
	 * Database specific settings
	 * @author robbiev
	 *
	 */
	public static class Database {
		private static String isConfiguredKey = Database.class.getSimpleName()+".isConfiguredKey";
		
		/**
		 * Returns true if the database has been correctly configured via the manifest file.
		 * Caches result for later use.
		 * @param c
		 * @return
		 */
		public static boolean isConfigured(Context c) {
			Object val = getCache(isConfiguredKey);
			if(val != null) {
				return (Boolean)val;
			}
			
			String classPath = ManifestMetaData.getString(c, ManifestMetaData.DATABASE_OPEN_HELPER);
			String databaseName = ManifestMetaData.getString(c, ManifestMetaData.DATABSE_NAME);
			int databaseVersion = ManifestMetaData.getInt(c, ManifestMetaData.DATABASE_VERSION);
			
			val = databaseVersion > 0 && 
					databaseName != null && 
					databaseName.trim().length() > 0 &&
					classPath != null &&
					classPath.trim().length() > 0;
			setCache(isConfiguredKey, val);
			return (Boolean)val;
		}
		
		/**
		 * Get the name for the database file.
		 * @param c
		 * @return
		 */
		public static String getName(Context c) {
			return ManifestMetaData.getString(c, ManifestMetaData.DATABSE_NAME);
		}
		
		/**
		 * Get the version of the database.
		 * @param c
		 * @return
		 */
		public static int getVersion(Context c) {
			return ManifestMetaData.getInt(c, ManifestMetaData.DATABASE_VERSION);
		}
		
		/**
		 * Get the DatabaseOpenHelper for the database.
		 * @param c
		 * @return The class path of the open helper.
		 */
		public static String getOpenHelper(Context c) {
			return ManifestMetaData.getString(c, ManifestMetaData.DATABASE_OPEN_HELPER);
		}
	}
	
	/**
	 * Analytics specific settings
	 * @author robbiev
	 *
	 */
	public static class Analytics {
		private static String isConfiguredKey = Analytics.class.getSimpleName()+".isConfiguredKey";
		
		/**
		 * Returns true if analytics has been correctly configured via the manifest file.
		 * @param c
		 * @return
		 */
		public static boolean isConfigured(Context c) {
			Object val = getCache(isConfiguredKey);
			if(val != null) {
				return (Boolean)val;
			}
			
			String key = getString(c, ANALYTICS_KEY);
			String provider = getString(c, ANALYTICS_PROVIDER);
			
			val = provider != null && provider.trim().length() > 0 &&
					key != null && key.trim().length() > 0 &&
					getBoolean(c, ANALYTICS_ENABLED);
			setCache(isConfiguredKey, val);
			return (Boolean)val;
		}
		
		/**
		 * Returns true if analytics has been properly configured and "enabled"
		 * set to "true" in the manifest file.
		 * @param c
		 * @return (Default false)
		 */
		public static boolean isEnabled(Context c) {
			return getBoolean(c, ANALYTICS_ENABLED) && isConfigured(c);
		}
		
		/**
		 * Get the analytics provider string.
		 * @param c
		 * @return
		 */
		public static String getProvider(Context c) {
			return getString(c, ANALYTICS_PROVIDER);
		}
		
		/**
		 * Get the analytics provider string.
		 * @param c
		 * @return
		 */
		public static String getProviderKey(Context c) {
			return getString(c, ANALYTICS_KEY);
		}
	}
	
	/**
	 * RemoteStackTrace specific settings
	 * @author robbiev
	 *
	 */
	public static class RemoteStackTrace {
		private static String isConfiguredKey = RemoteStackTrace.class.getSimpleName()+".isConfiguredKey";
		
		/**
		 * Returns true if remote stack trace has been correctly configured via the manifest file.
		 * @param c
		 * @return
		 */
		public static boolean isConfigured(Context c) {
			Object val = getCache(isConfiguredKey);
			if(val != null) {
				return (Boolean)val;
			}
			
			String url = getString(c, REMOTE_STACK_TRACK_URL);
			val = url != null && url.trim().length() > 0 &&
					getBoolean(c, REMOTE_STACK_TRACE_ENABLED);
			setCache(isConfiguredKey, url);
			return (Boolean)val;
		}
		
		/**
		 * Return the URL to post stack traces.
		 * @param c
		 * @return
		 */
		public static String getURL(Context c) {
			return getString(c, REMOTE_STACK_TRACK_URL);
		}
		
		/**
		 * Return true if remote stack trace is enabled.
		 * @param c
		 * @return	(Default false)
		 */
		public static boolean isEnabled(Context c) {
			return getBoolean(c, REMOTE_STACK_TRACE_ENABLED);
		}
	}
	
	/**
	 * Security related settings.
	 * @author robbiev
	 *
	 */
	public static class SecurityManager {
		/**
		 * Returns true if security has been enabled via the manifest file.
		 * @param c
		 * @return
		 */
		public static boolean isEnabled(Context c) {
			return getBoolean(c, SECURITY_ENABLED);
		}
	}
}
