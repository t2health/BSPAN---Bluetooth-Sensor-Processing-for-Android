package com.t2.biofeedback;

import android.util.Log;

/**
 * Utility routines for logging
 * @author scott.coleman
 *
 */
public class Util {

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
		Log.i(TAG, hexString.toString());
			
	}
	
	
}
