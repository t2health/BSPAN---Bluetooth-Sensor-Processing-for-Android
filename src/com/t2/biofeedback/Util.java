package com.t2.biofeedback;

import android.util.Log;

public class Util {

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
