package com.t2;

import java.util.Vector;

import com.t2.biomap.BioLocation;

import android.os.Build;
import android.util.Log;

/**
 * Utility routines for logging
 * @author scott.coleman
 *
 */
public class Util {
    private static final String TAG = "BioMap";
	
	/**
	 * Writes a formatted hex string (created from supplied byte array to the log
	 * @param TAG				Logging tag to use
	 * @param prependString		String to prepend to the hex display of bytes 
	 * @param bytes				Byte array to print
	 */
	static public void logHexByteString(String TAG, String prependString, byte[] bytes) {
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<bytes.length;i++) {
			String s = Integer.toHexString(0xFF & bytes[i]);
			if (s.length() < 2)
			{
				s = "0" + s;
			}
			
		    hexString.append(s);
		    }		
		Log.i(TAG, prependString + new String(hexString));
	}	

	/**
	 * Writes a formatted hex string (created from supplied byte array to the log
	 * @param TAG				Logging tag to use
	 * @param bytes				Byte array to print
	 */
	static public void logHexByteString(String TAG, byte[] bytes) {
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<bytes.length;i++) {
			String s = Integer.toHexString(0xFF & bytes[i]);
			if (s.length() < 2)
			{
				s = "0" + s;
			}
			
		    hexString.append(s);
		    }		
		Log.i(TAG, new String(hexString));
	}
	
	
	static public Vector<BioLocation> setupUsers()
	{
// TODO: send in vector so we don't create new every time		
		Vector<BioLocation> currentUsers = new Vector<BioLocation>();
		String model;
		
	    try {
	    	model = Build.MODEL;
		} catch (Exception e) {
			model = "";
		}
		
		if (model.equalsIgnoreCase("MB860")) // Motorolla Attrix
		{
			Log.i(TAG, "Model = " + model);
		}		
      
		if (model.equalsIgnoreCase("MB860")) // Motorolla Attrix
		{		
			currentUsers.add(new BioLocation("Dave", 272,333, 
					new int[] {Constants.DATA_SIGNAL_STRENGTH,
								Constants.DATA_TYPE_ATTENTION,
								Constants.DATA_TYPE_MEDITATION}, true))	;
			currentUsers.add(new BioLocation("Bob", 272,363, 
					new int[] {Constants.DATA_SIGNAL_STRENGTH,
								Constants.DATA_TYPE_ATTENTION,
								Constants.DATA_TYPE_MEDITATION}, true))	;
			currentUsers.add(new BioLocation("Scott", 126,340, 
					new int[] {Constants.DATA_SIGNAL_STRENGTH,
								Constants.DATA_TYPE_ATTENTION,
								Constants.DATA_TYPE_MEDITATION}, true))	;
		}
		else
		{
			currentUsers.add(new BioLocation("Dave", 240,283, 
					new int[] {Constants.DATA_SIGNAL_STRENGTH,
								Constants.DATA_TYPE_ATTENTION,
								Constants.DATA_TYPE_MEDITATION}, true))	;
			currentUsers.add(new BioLocation("Bob", 240,308, 
					new int[] {Constants.DATA_SIGNAL_STRENGTH,
								Constants.DATA_TYPE_ATTENTION,
								Constants.DATA_TYPE_MEDITATION}, true))	;
			currentUsers.add(new BioLocation("Scott", 110,300, 
					new int[] {Constants.DATA_SIGNAL_STRENGTH,
								Constants.DATA_TYPE_ATTENTION,
								Constants.DATA_TYPE_MEDITATION}, true))	;
			
		}
				
		
		
		return currentUsers;
		
	}

	
	
	
}
