package com.t2.biomap;

public class BioLocation {
	float lat;
	float lon;
	String name = "";
	float angle;
	boolean active;
	
	public BioLocation()
	{
		
	}

	public BioLocation(String name, float lat, float lon, float angle)
	{
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.angle = angle;
		this.active = false;
	}

	public void set(BioLocation l)
	{
		this.name = l.name;
		this.lat = l.lat;
		this.lon = l.lon;
		this.angle = l.angle;
		this.active = true;
	}
	
	
}
