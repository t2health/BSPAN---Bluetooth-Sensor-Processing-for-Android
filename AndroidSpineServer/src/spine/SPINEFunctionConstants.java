/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute
modify it under the terms of the sub-license (below).

*****************************************************************/

/*****************************************************************
BSPAN - BlueTooth Sensor Processing for Android is a framework 
that extends the SPINE framework to work on Android and the 
Android Bluetooth communication services.

Copyright (C) 2011 The National Center for Telehealth and 
Technology

Eclipse Public License 1.0 (EPL-1.0)

This library is free software; you can redistribute it and/or
modify it under the terms of the Eclipse Public License as
published by the Free Software Foundation, version 1.0 of the 
License.

The Eclipse Public License is a reciprocal license, under 
Section 3. REQUIREMENTS iv) states that source code for the 
Program is available from such Contributor, and informs licensees 
how to obtain it in a reasonable manner on or through a medium 
customarily used for software exchange.

Post your updates and modifications to our GitHub or email to 
t2@tee2.org.

This library is distributed WITHOUT ANY WARRANTY; without 
the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the Eclipse Public License 1.0 (EPL-1.0)
for more details.
 
You should have received a copy of the Eclipse Public License
along with this library; if not, 
visit http://www.opensource.org/licenses/EPL-1.0

*****************************************************************/

package spine;

public class SPINEFunctionConstants {
	
	// if new functions are added, declare their codes down here 
	public static final byte FEATURE = 0x01;
	public static final byte ALARM = 0x02;
	public static final byte SIGNAL_PROCESSING = 0x03;
	public static final byte ONE_SHOT = 0x04;
	public static final byte STEP_COUNTER = 0x05;
	public static final byte BUFFERED_RAW_DATA = 0x06;
	public static final byte HMM = 0x07;
	public static final byte HEARTBEAT = 0x08;
	public static final byte ZEPHYR = 0x09;
	public static final byte MINDSET = 0x0A;
	public static final byte SHIMMER = 0x0B;
	
	//alarm types
	public static final byte BELOW_THRESHOLD = 0x01;
	public static final byte ABOVE_THRESHOLD = 0x02;
	public static final byte IN_BETWEEN_THRESHOLDS = 0x03;
	public static final byte OUT_OF_THRESHOLDS = 0x04;
	
	// if new function libraries are added, declare their codes down here
	public static final byte RAW_DATA = 0x01;
	public static final byte MAX = 0x02;
	public static final byte MIN = 0x03;
	public static final byte RANGE = 0x04;
	public static final byte MEAN = 0x05;
	public static final byte AMPLITUDE = 0x06;
	public static final byte RMS = 0x07;
	public static final byte ST_DEV = 0x08;
	public static final byte TOTAL_ENERGY = 0x09;
	public static final byte VARIANCE = 0x0A;
	public static final byte MODE = 0x0B;
	public static final byte MEDIAN = 0x0C;	
	public static final byte PITCH_ROLL = 0x0D;
	public static final byte VECTOR_MAGNITUDE = 0x0E;
	public static final byte KCAL = 0x0F;
	public static final byte ENTROPY = 0x10;
	
	// if new functions are added, declare their labels down here
	public static final String FEATURE_LABEL = "Feature";
	public static final String ALARM_LABEL = "Alarm";
	public static final String SIGNAL_PROCESSING_LABEL = "DSP";
	public static final String ONE_SHOT_LABEL = "OneShot";
	public static final String STEP_COUNTER_LABEL = "StepCounter";
	public static final String BUFFERED_RAW_DATA_LABEL = "BufferedRawData";
	public static final String HMM_LABEL = "Hmm";
	public static final String HEARTBEAT_LABEL = "HeartBeat";
	public static final String ZEPHYR_LABEL = "Zephyr";
	public static final String MINDSET_LABEL = "Mindset";
	public static final String SHIMMER_LABEL = "Shimmer";
	
	// if new function libraries are added, declare their labels down here
	public static final String RAW_DATA_LABEL = "Raw Data";
	public static final String MAX_LABEL = "Max";
	public static final String MIN_LABEL = "Min";
	public static final String RANGE_LABEL = "Range";
	public static final String MEAN_LABEL = "Mean";
	public static final String AMPLITUDE_LABEL = "Amplitude";
	public static final String RMS_LABEL = "RMS";
	public static final String ST_DEV_LABEL = "Standard Deviation";
	public static final String TOTAL_ENERGY_LABEL = "Total Energy";
	public static final String VARIANCE_LABEL = "Variance";
	public static final String MODE_LABEL = "Mode";
	public static final String MEDIAN_LABEL = "Median";
	public static final String PITCH_ROLL_LABEL = "Pitch & Roll";
	public static final String VECTOR_MAGNITUDE_LABEL = "Vector Magnitude";	
	public static final String KCAL_LABEL = "Kcal";
	public static final String ENTROPY_LABEL = "Entropy";
	
	public static final String BELOW_THRESHOLD_LABEL = "< LT";
	public static final String ABOVE_THRESHOLD_LABEL = "> UT";
	public static final String IN_BETWEEN_THRESHOLDS_LABEL = ">=LT & <=UT";
	public static final String OUT_OF_THRESHOLDS_LABEL = "<=LT | >=UT";
	
	public static String functionCodeToString(byte code) {
		switch (code) {
			// if new functions are added, define the corresponding 'switch case' down here
			case FEATURE: return FEATURE_LABEL;
			case ALARM: return ALARM_LABEL;
			case SIGNAL_PROCESSING: return SIGNAL_PROCESSING_LABEL;
			case ONE_SHOT: return ONE_SHOT_LABEL;
			case STEP_COUNTER: return STEP_COUNTER_LABEL;
			case BUFFERED_RAW_DATA: return BUFFERED_RAW_DATA_LABEL;
			case HMM: return HMM_LABEL;
			case HEARTBEAT: return HEARTBEAT_LABEL;
			case ZEPHYR: return ZEPHYR_LABEL;
			case MINDSET: return MINDSET_LABEL;
			case SHIMMER: return SHIMMER_LABEL;
			default: return "?";
		}
	}
	
	public static byte functionCodeByString(String label) {
		if(label.equals(SHIMMER_LABEL))
			return SHIMMER;
		if(label.equals(MINDSET_LABEL))
			return MINDSET;
		if(label.equals(FEATURE_LABEL))
			return FEATURE;
		if(label.equals(ALARM_LABEL))
			return ALARM;
		if(label.equals(SIGNAL_PROCESSING_LABEL))
			return SIGNAL_PROCESSING;
		if(label.equals(ONE_SHOT_LABEL))
			return ONE_SHOT;
		if(label.equals(STEP_COUNTER_LABEL))
			return STEP_COUNTER;
		if(label.equals(BUFFERED_RAW_DATA_LABEL))
			return BUFFERED_RAW_DATA;
		if(label.equals(HMM_LABEL))
			return HMM;
		if(label.equals(HEARTBEAT_LABEL))
			return HEARTBEAT;
		else 
			return -1;
	}
	
	public static String functionalityCodeToString(byte functionCode, byte functionalityCode) {
		switch (functionCode) {
			// if new functions are added, define the corresponding 'switch case' down here
			case FEATURE:
				switch (functionalityCode) {
					// if new features are added, define the corresponding 'switch case' down here
					case RAW_DATA: return RAW_DATA_LABEL;
					case MAX: return MAX_LABEL;
					case MIN: return MIN_LABEL;
					case RANGE: return RANGE_LABEL;
					case MEAN: return MEAN_LABEL;
					case AMPLITUDE: return AMPLITUDE_LABEL;
					case RMS: return RMS_LABEL;
					case ST_DEV: return ST_DEV_LABEL;
					case TOTAL_ENERGY: return TOTAL_ENERGY_LABEL;
					case VARIANCE: return VARIANCE_LABEL;
					case MODE: return MODE_LABEL;
					case MEDIAN: return MEDIAN_LABEL;
					case PITCH_ROLL: return PITCH_ROLL_LABEL;
					case VECTOR_MAGNITUDE: return VECTOR_MAGNITUDE_LABEL;
					case KCAL: return KCAL_LABEL;
					case ENTROPY: return ENTROPY_LABEL;
					default: return functionalityCode + " (?)";	
				}
			case ALARM: 
				switch (functionalityCode) {
				// if new alarm type are added, define the corresponding 'switch case' down here
				case BELOW_THRESHOLD: return BELOW_THRESHOLD_LABEL;
				case ABOVE_THRESHOLD: return ABOVE_THRESHOLD_LABEL;
				case IN_BETWEEN_THRESHOLDS: return IN_BETWEEN_THRESHOLDS_LABEL;
				case OUT_OF_THRESHOLDS: return OUT_OF_THRESHOLDS_LABEL;
				default: return "?";	
			}
			case ONE_SHOT: return RAW_DATA_LABEL;
			case STEP_COUNTER: return STEP_COUNTER_LABEL;
			case BUFFERED_RAW_DATA: return BUFFERED_RAW_DATA_LABEL;
			case HMM: return HMM_LABEL;
			case HEARTBEAT: return HEARTBEAT_LABEL;
			case SIGNAL_PROCESSING:	return "?"; 
			case ZEPHYR: return ZEPHYR_LABEL;
			case SHIMMER: return SHIMMER_LABEL;
			
			
			default: return "?";
		}
	}
	
	public static byte functionalityCodeByString(String functionLabel, String functionalityLabel) {
		if(functionLabel.equals(FEATURE_LABEL)) {
			if(functionalityLabel.equals(RAW_DATA_LABEL)) 
					return RAW_DATA;
			if(functionalityLabel.equals(MAX_LABEL)) 
				return MAX;
			if(functionalityLabel.equals(MIN_LABEL)) 
				return MIN;
			if(functionalityLabel.equals(RANGE_LABEL)) 
				return RANGE;
			if(functionalityLabel.equals(MEAN_LABEL)) 
				return MEAN;
			if(functionalityLabel.equals(AMPLITUDE_LABEL)) 
				return AMPLITUDE;
			if(functionalityLabel.equals(RMS_LABEL)) 
				return RMS;
			if(functionalityLabel.equals(ST_DEV_LABEL)) 
				return ST_DEV;
			if(functionalityLabel.equals(TOTAL_ENERGY_LABEL)) 
				return TOTAL_ENERGY;
			if(functionalityLabel.equals(VARIANCE_LABEL)) 
				return VARIANCE;
			if(functionalityLabel.equals(MODE_LABEL)) 
				return MODE;
			if(functionalityLabel.equals(MEDIAN_LABEL)) 
				return MEDIAN;
			if(functionalityLabel.equals(PITCH_ROLL_LABEL)) 
				return PITCH_ROLL;
			if(functionalityLabel.equals(VECTOR_MAGNITUDE_LABEL)) 
				return VECTOR_MAGNITUDE;
			if(functionalityLabel.equals(KCAL_LABEL)) 
				return KCAL;
			if(functionalityLabel.equals(ENTROPY_LABEL)) 
				return ENTROPY;
			else
				return -1;	
		}
		if (functionLabel.equals(ALARM_LABEL)) {
			if(functionalityLabel.equals(BELOW_THRESHOLD_LABEL)) 
				return BELOW_THRESHOLD;
			if(functionalityLabel.equals(ABOVE_THRESHOLD_LABEL)) 
				return ABOVE_THRESHOLD;
			if(functionalityLabel.equals(IN_BETWEEN_THRESHOLDS_LABEL)) 
				return IN_BETWEEN_THRESHOLDS;
			if(functionalityLabel.equals(OUT_OF_THRESHOLDS_LABEL)) 
				return OUT_OF_THRESHOLDS;
			else
				return -1;
		}
		if (functionLabel.equals(STEP_COUNTER_LABEL)) 
			return STEP_COUNTER;
		if (functionLabel.equals(BUFFERED_RAW_DATA_LABEL)) 
			return BUFFERED_RAW_DATA;
		if (functionLabel.equals(HMM_LABEL)) 
			return HMM;
		else
			return -1;
	}
}
