package bz.org.t2health.lib.analytics;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

/**
 * 
 * @author robbiev
 * 
 * Manages the Analytics for the application.
 */
public class Analytics {
	/**
	 * The list of supported providers.
	 * @author robbiev
	 *
	 */
	public static enum Provider {
		FLURRY
	};
	
	private static Provider sCurrentProvider;
	private static String sProviderKey;
	private static AnalyticsProvider sAnalytics;
	private static boolean sIsEnabled = false;
	private static boolean sIsDebugEnabled = false;
	private static boolean sIsSessionStarted = false;
	
	/**
	 * Initialize the analytics system. This method should be the first called
	 * in order to handle analytics collection.
	 * @param provider	The analytics provider to use. eg, Flurry, Localytics.
	 * @param apiKey	The API Key the provider provides in order to use their service.
	 * @param enabled	Whether or not analytics is currently enabled.
	 */
	public static void init(Provider provider, String apiKey, boolean enabled) {
		if(provider == null || apiKey == null) {
			return;
		}
		
		if(sCurrentProvider != null && sCurrentProvider != provider && apiKey != sProviderKey) {
			throw new RuntimeException("Analytics provider was already set. You cannot change it.");
		}
		sCurrentProvider = provider;
		sProviderKey = apiKey;
		
		if(sCurrentProvider == Provider.FLURRY) {
			sAnalytics = new FlurryProvider();
		}
		
		sAnalytics.init();
		sAnalytics.setApiKey(sProviderKey);
		sIsEnabled = enabled;
	}
	
	/**
	 * Retrieves the provider enumeration based on its string representation.
	 * This method is case in-sensitive.
	 * @param providerString	The string form of a provider from the Provider enumeration.
	 * @return	The enumeration element for the string or null if no provider was found.
	 */
	public static Provider providerFromString(String providerString) {
		String newProviderString = providerString.toLowerCase();
		if(newProviderString.equals("flurry")) {
			return Provider.FLURRY;
		}
		return null;
	}
	
	/**
	 * Turn analytics collection on/off.
	 * @param en	Set analytics enabled status. Setting this will inhibit
	 * 				all calls to the underlying provider class.
	 */
	public static void setEnabled(boolean en) {
		sIsEnabled = en;
	}

	/**
	 * Returns the status of analytics collection.
	 * @return	true if analytics is enabled.
	 */
	public static boolean isEnabled() {
		return sIsEnabled;
	}
	
	/**
	 * Determines if the analytics system is ready to start collecting data.
	 * @return	true if analytics is ready to pass data to the underlying 
	 * 			provider class.
	 */
	private static boolean isReady() {
		return sIsEnabled && sAnalytics != null;
	}

	/**
	 * Turns debug mode on or off.
	 * @param b	
	 */
	public static void setDebugEnabled(boolean b) {
		sIsDebugEnabled = b;
	}
	
	/**
	 * Return if debug mode is on or off.
	 * @return
	 */
	public static boolean isDebugEnabled() {
		return sIsDebugEnabled;
	}

	/**
	 * Signals to the provider class that the data collection session has begun.
	 * @param context
	 */
	public static void onStartSession(Context context) {
		if(isReady() && !sIsSessionStarted) {
			sAnalytics.onStartSession(context);
			sIsSessionStarted = true;
		}
	}

	/**
	 * Signals to the provider class that the data colelction session has ended.
	 * @param context
	 */
	public static void onEndSession(Context context) {
		if(isReady() && sIsSessionStarted) {
			sAnalytics.onEndSession(context);
			sIsSessionStarted = false;
		}
	}

	/**
	 * Sends an event to the provider.
	 * @param event
	 * @param key
	 * @param value
	 */
	public static void onEvent(String event, String key, String value) {
		if(isReady()) {
			sAnalytics.onEvent(event, key, value);
		}
	}

	/**
	 * Sends an event to the provider.
	 * @param event
	 * @param parameters
	 */
	public static void onEvent(String event, Bundle parameters) {
		if(isReady()) {
			sAnalytics.onEvent(event, parameters);
		}
	}

	/**
	 * Sends and event to the provider.
	 * @param event
	 */
	public static void onEvent(String event) {
		if(isReady()) {
			sAnalytics.onEvent(event);
		}
	}

	/**
	 * Sends and event to the provider.
	 * @param event
	 * @param parameters
	 */
	public static void onEvent(String event, Map<String,String> parameters) {
		if(isReady()) {
			sAnalytics.onEvent(event, parameters);
		}
	}

	/**
	 * Signals to the provider that the whole screen has changed.
	 */
	public static void onPageView() {
		if(isReady()) {
			sAnalytics.onPageView();
		}
	}
}
