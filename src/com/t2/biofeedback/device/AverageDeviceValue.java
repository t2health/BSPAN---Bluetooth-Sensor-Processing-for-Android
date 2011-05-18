package com.t2.biofeedback.device;

public abstract class AverageDeviceValue extends DeviceValue {
	public double avgValue;
	
//	public double currentValue;
//	public long currentTimetamp;
	
	public double[] sampleValues;
	public long[] sampleTimestamps;
	
	private int sampleSize = 2;
	private int sampleIndex = 0;
	private boolean sampleFilled = false;
	
	public AverageDeviceValue() {
		this.sampleSize = 5;
		this.sampleValues = new double[this.sampleSize];
		this.sampleTimestamps = new long[this.sampleSize];
		this.sampleIndex = 0;
	}
	
	public AverageDeviceValue(int sampleSize) {
		this.sampleSize = sampleSize;
		this.sampleValues = new double[this.sampleSize];
		this.sampleTimestamps = new long[this.sampleSize];
		this.sampleIndex = 0;
	}
	
	public void addValue(long timestamp, double value) {
		this.currentTimetamp = timestamp;
		this.currentValue = value;
		
		this.sampleTimestamps[this.sampleIndex] = timestamp;
		this.sampleValues[this.sampleIndex] = value;
		
		
		double sampleCount = 0.00;
		if(this.sampleFilled) {
			sampleCount += this.sampleValues.length;
		} else {
			sampleCount += this.sampleIndex + 1;
		}
		
		int currentTotal = 0;
		for(int i = 0; i < sampleCount; i++) {
			currentTotal += this.sampleValues[i];
		}
		
		this.avgValue = currentTotal / sampleCount;
		
		this.sampleIndex++;
		if(this.sampleIndex >= this.sampleValues.length) {
			this.sampleIndex = 0;
			this.sampleFilled = true;
		}
	}
}
