package com.t2.compassionMeditation;

import java.util.ArrayList;
import java.util.HashMap;

public class DataPointCache {
	private HashMap<String,DataPointCacheEntry> cache = new HashMap<String,DataPointCacheEntry>();
	
	public ArrayList<DataPoint> getCache(String key, long startTime, long endTime, int calendarGroupByField) {
		DataPointCacheEntry c = cache.get(key);
		if(c == null) {
			return null;
		}
		
		if(c.isCacheExpired(startTime, endTime, calendarGroupByField)) {
			cache.remove(key);
			return null;
		}
		
		return c.data;
	}
	
	public void setCache(String key, ArrayList<DataPoint> data, long startTime, long endTime, int calendarGroupByField) {
		cache.remove(key);
		cache.put(key, new DataPointCacheEntry(
				data,
				startTime,
				endTime,
				calendarGroupByField
		));
	}
	
	public void clearCache(String key) {
		cache.remove(key);
	}
	
	private static class DataPointCacheEntry {
		public long startTime;
		public long endTime;
		public int calendarGroupByField;
		public ArrayList<DataPoint> data;
		
		public DataPointCacheEntry(ArrayList<DataPoint> data, long startTime, long endTime, int calendarGroupByField) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.calendarGroupByField = calendarGroupByField;
			this.data = data;
		}
		
		public boolean isCacheExpired(long startTime, long endTime, int calendarGroupByField) {
			return !(this.startTime == startTime && this.endTime == endTime && this.calendarGroupByField == calendarGroupByField);
		}
	}
}
