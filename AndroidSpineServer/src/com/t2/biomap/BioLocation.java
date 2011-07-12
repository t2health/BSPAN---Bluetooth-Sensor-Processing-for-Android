package com.t2.biomap;

public class BioLocation {
	public float mLat;
	public float mLon;
	public String mName = "";
	public float mAngle;
	boolean mActive;
	public int[] mSensors;
	public boolean mEnabled;	
	
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
	}

	public BioLocation(String name, float lat, float lon, float angle, int[] sensors, boolean enabled)
	{
		this.mName = name;
		this.mLat = lat;
		this.mLon = lon;
		this.mAngle = angle;
		this.mActive = false;
		this.mSensors = sensors;
		this.mEnabled = enabled;
		
	}

	public BioLocation(String name, float lat, float lon, int[] sensors, boolean enabled)
	{
		this.mName = name;
		this.mLat = lat;
		this.mLon = lon;
		this.mActive = false;
		this.mSensors = sensors;
		this.mEnabled = enabled;
		
	}

	public void set(BioLocation l)
	{
		this.mName = l.mName;
		this.mLat = l.mLat;
		this.mLon = l.mLon;
		this.mAngle = l.mAngle;
		this.mActive = true;
	}
	
	
}
