package com.t2;

import android.app.Activity;
import android.content.ServiceConnection;

public class AndroidSpineConnector {
	
	public static AndroidSpineServerMainActivity mainActivityInstance;
	
	public static void setMainActivityInstance(AndroidSpineServerMainActivity instance)
	{
		mainActivityInstance = instance;
	}
	
	/**
	 * @return Static instance of this activity
	 */
	public static AndroidSpineServerMainActivity getInstance() 
	{
	   return mainActivityInstance;
	}
    	


}
