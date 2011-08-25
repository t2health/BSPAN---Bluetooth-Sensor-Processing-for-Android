package com.t2.compassionMeditation;

import java.util.ArrayList;

public class DataPoint {
	public final long time;
	private double valueSum = 0.00;
	private int count = 0;
	public double minValue = 0.00;
	public double maxValue = 0.00;
	private ArrayList<Double> values = new ArrayList<Double>();
	private double defaultValue = 0.00;
	
	public DataPoint(long time, double defaultValue) {
		this.time = time;
		this.defaultValue = defaultValue;
	}
	
	public void addValue(double val) {
		values.add(val);
		valueSum += val;
		++count;
		
		if(val > maxValue || count == 1) {
			maxValue = val;
		}
		
		if(val < minValue || count == 1) {
			minValue = val;
		}
	}
	
	public double getAverageValue() {
		if(valueSum == 0 && count == 0) {
			return defaultValue;
		}
		return valueSum / count;
	}
	
	public double[] getValues() {
		double[] out = new double[values.size()];
		for(int i = 0; i < values.size(); ++i) {
			out[i] = values.get(i);
		}
		return out;
	}
}
