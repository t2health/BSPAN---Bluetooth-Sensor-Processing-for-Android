package com.t2.biomap;

import com.t2.Constants;

public class BioLocation {
	public float mLat;
	public float mLon;
	public String mName = "";
	public float mAngle;
	boolean mActive;
	public int[] mSensors;
	public boolean mEnabled;	
	public boolean mToggled;	
	public int mAddress;
	public int mSignalStrength = 0;
	public int mAttention = 0;
	public int mMeditation = 0;
	public int mHeartRate = 0;
	public int mZHeartRate = 0;
	public int mZRespRate = 0;
	public int mZSkinTemp = 0;
	public int mZBattLevel = 0;
	
	
	public BioLocation()
	{
		
	}

	public BioLocation(String name, float lat, float lon, float angle)
	{
		this.mName = name;
		this.mLat = lat;
		this.mLon = lon;
		this.mAngle = angle;
		this.mActive = false;
		this.mToggled = false;
	}

	public BioLocation(String name, float lat, float lon, float angle, int[] sensors, boolean enabled, int address)
	{
		this.mName = name;
		this.mLat = lat;
		this.mLon = lon;
		this.mAngle = angle;
		this.mActive = false;
		this.mSensors = sensors;
		this.mEnabled = enabled;
		this.mToggled = false;
		this.mAddress = address;
		
	}

	public BioLocation(String name, float lat, float lon, int[] sensors, boolean enabled, int address)
	{
		this.mName = name;
		this.mLat = lat;
		this.mLon = lon;
		this.mActive = false;
		this.mSensors = sensors;
		this.mEnabled = enabled;
		this.mToggled = false;
		this.mAddress = address;
		
		
	}

	public void set(BioLocation l)
	{
		this.mName = l.mName;
		this.mLat = l.mLat;
		this.mLon = l.mLon;
		this.mAngle = l.mAngle;
		this.mSensors = l.mSensors;
		this.mEnabled = l.mEnabled;
		this.mActive = true;
		this.mToggled = l.mToggled;

	}
	
	public String buildStatusText()
	{
		String statusLine = "";
//		String statusLine = mName + ":\n";
		for (int i = 0; i < mSensors.length; i++)
		{
			switch (mSensors[i])
			{
			case Constants.DATA_SIGNAL_STRENGTH:
				statusLine += "Connection = " + mSignalStrength + "\n";
				break;
			case Constants.DATA_TYPE_ATTENTION:
				statusLine += "Attention = " + mAttention + "\n";
				break;
			case Constants.DATA_TYPE_MEDITATION:
				statusLine += "Meditation = " + mMeditation + "\n";
				break;
			case Constants.DATA_TYPE_HEARTRATE:
				statusLine += "Heart Rate = " + mHeartRate + "\n";
				break;
//			case Constants.DATA_ZEPHYR_BATTLEVEL:
//				statusLine += "zBatt Level = " + mZBattLevel + "\n";
//				break;
			case Constants.DATA_ZEPHYR_HEARTRATE:
				statusLine += "zHeart Rate = " + mZHeartRate + "\n";
				break;
			case Constants.DATA_ZEPHYR_RESPRATE:
				statusLine += "zResp Rate = " + mZRespRate + "\n";
				break;
			case Constants.DATA_ZEPHYR_SKINTEMP:
				statusLine += "zSkin Temp = " + mZSkinTemp + "\n";
				break;
			}
		}
		return statusLine;
	}
	
}
