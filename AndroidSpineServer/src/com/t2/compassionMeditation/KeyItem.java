package com.t2.compassionMeditation;

import java.util.HashMap;


public class KeyItem {
	public long id;
	public String title1;
	public String title2;
	public int color;
	public boolean visible;
	public boolean reverseData = false; 

	int value;
	float fValue;
	
	
	public KeyItem(long id, String title1, String title2) {
		this.id = id;
		this.title1 = title1;
		this.title2 = title2;
		this.visible = true;
	}
	
	
	public HashMap<String,Object> toHashMap() {
		HashMap<String,Object> data = new HashMap<String,Object>();
		data.put("id", id);
		data.put("title1", title1);
		data.put("title2", title2);
		data.put("color", color);
		data.put("visible", visible);
		return data;
	}
}	