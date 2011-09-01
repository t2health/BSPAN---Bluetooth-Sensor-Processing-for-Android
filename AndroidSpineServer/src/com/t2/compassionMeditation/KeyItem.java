package com.t2.compassionMeditation;

import java.util.HashMap;

import org.achartengine.model.XYSeries;

import com.t2.compassionUtils.MovingAverage;
import com.t2.compassionUtils.RateOfChange;


public class KeyItem {
	public long id;
	public String title1;
	public String title2;
	public int color;
	public boolean visible;
	public boolean reverseData = false; 
    private MovingAverage mMovingAverage = new MovingAverage(10);
    private RateOfChange mRateOfChange = new RateOfChange(6);
	public XYSeries xySeries;	    
    

	

	int rawValue;
	private int scaledValue;
	int filteredValue;

	private int maxFilteredValue = 0;
	private int minFilteredValue = 9999;
	int numFilterSamples = 0;
	long totalOfFilterSamples = 0;

	
	
	public int getMaxFilteredValue() {
		return maxFilteredValue;
	}


	public void setMaxFilteredValue(int maxFilteredValue) {
		this.maxFilteredValue = maxFilteredValue;
	}


	public int getMinFilteredValue() {
		return minFilteredValue;
	}


	public void setMinFilteredValue(int minFilteredValue) {
		this.minFilteredValue = minFilteredValue;
	}


	public int getAvgFilteredValue() {
		return numFilterSamples != 0 ? (int) (totalOfFilterSamples / numFilterSamples) :0;
	}



	public int getRawValue() {
		return rawValue;
	}


	public void setRawValue(int rawValue) {
		this.rawValue = rawValue;
	}


	public int getScaledValue() {
		return scaledValue;
	}

	public int getFilteredScaledValue() {
		return (int) mMovingAverage.getValue();
	}

	public int getRateOfChangeScaledValue() {
		int filteredLotusValue = (int) (mRateOfChange.getValue() * 10);

		if (filteredLotusValue > 255) filteredLotusValue = 255;
		
		return filteredLotusValue;
	}


	public void updateRateOfChange() {
		mRateOfChange.pushValue(scaledValue);
	}
	
	public void setScaledValue(int scaledValue) {
		this.scaledValue = scaledValue;
		mMovingAverage.pushValue(scaledValue);
		
		// Now do stats
		int value = (int) mMovingAverage.getValue();
		numFilterSamples++;
		totalOfFilterSamples += value;
		
		if (value >= maxFilteredValue) maxFilteredValue = value;
		if (value < minFilteredValue) minFilteredValue = value;
	}


	
	
	public KeyItem(long id, String title1, String title2) {
		this.id = id;
		this.title1 = title1;
		this.title2 = title2;
		this.visible = true;
		xySeries = new XYSeries(title1);		
		
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