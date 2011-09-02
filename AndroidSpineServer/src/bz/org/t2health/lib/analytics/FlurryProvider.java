package bz.org.t2health.lib.analytics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

class FlurryProvider implements AnalyticsProvider {

	private String mApiKey;
	private Class<?> mAnalyticsClass;

	@Override
	public void init() {
		try {
			this.mAnalyticsClass = java.lang.Class.forName("com.flurry.android.FlurryAgent");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setApiKey(String key) {
		this.mApiKey = key;
	}

	@Override
	public void setDebugEnabled(boolean b) {
		
	}

	@Override
	public void onStartSession(Context context) {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onStartSession", Context.class, String.class);
			m.invoke(null, context, this.mApiKey);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		/*FlurryAgent.onStartSession(
				context, 
				apiKey
		);*/
	}

	@Override
	public void onEndSession(Context context) {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onEndSession", Context.class);
			m.invoke(null, context);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		//FlurryAgent.onEndSession(context);
	}

	@Override
	public void onEvent(String event, String key, String value) {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put(key, value);
		onEvent(event, params);
	}

	@Override
	public void onEvent(String event, Bundle parameters) {
		HashMap<String,String> params = new HashMap<String,String>();
		for(String key: parameters.keySet()) {
			Object val = parameters.get(key);
			params.put(key, val+"");
		}

		onEvent(event, params);
	}

	@Override
	public void onEvent(String event) {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onEvent", String.class);
			m.invoke(null, event);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//FlurryAgent.onEvent(event);
	}

	@Override
	public void onEvent(String event, Map<String, String> parameters) {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onEvent", Map.class);
			m.invoke(null, parameters);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//FlurryAgent.onEvent(event, parameters);
	}

	@Override
	public void onPageView() {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onPageView");
			m.invoke(null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//FlurryAgent.onPageView();
	}
}
