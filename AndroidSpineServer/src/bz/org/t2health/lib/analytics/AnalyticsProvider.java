package bz.org.t2health.lib.analytics;

import java.util.Map;

import android.content.Context;
import android.os.Bundle;

interface AnalyticsProvider {
	/**
	 * Called on first instantiation.
	 */
	public void init();
	
	/**
	 * Set the key the provider requires for collecting data.
	 * @param key
	 */
	public void setApiKey(String key);
	
	/**
	 * 
	 * @param b
	 */
	public void setDebugEnabled(boolean b);
	
	/**
	 * Fires when a new session begins, this typically occurs onStart
	 * in an Activity.
	 * @param context
	 */
	public void onStartSession(Context context);
	
	/**
	 * Fires when a session ends, this typically occurs onStop in an Activity.
	 * @param context
	 */
	public void onEndSession(Context context);
	
	/**
	 * Sends an event to the provider.
	 * @param event
	 * @param key
	 * @param value
	 */
	public void onEvent(String event, String key, String value);
	
	/**
	 * Sends an event to the provider.
	 * @param event
	 * @param parameters
	 */
	public void onEvent(String event, Bundle parameters);
	
	/**
	 * Sends and event to the provider.
	 * @param event
	 */
	public void onEvent(String event);
	
	/**
	 * Sends and event to the provider.
	 * @param event
	 * @param parameters
	 */
	public void onEvent(String event, Map<String,String> parameters);
	
	/**
	 * Signals to the provider that the whole screen has changed.
	 */
	public void onPageView();
}
