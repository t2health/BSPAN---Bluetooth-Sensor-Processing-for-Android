package com.t2.biofeedback.device.zephyr;


import com.t2.biofeedback.Constants;
import com.t2.biofeedback.device.BioFeedbackDevice;

import com.t2.biofeedback.device.BioFeedbackDevice.Capability;

import android.util.Log;


public class ZephyrBH extends ZephyrDevice {
	private static final String TAG = Constants.TAG;
	private static final String BH_ADDRESS = "00:07:80:99:9E:8C";
	public static final int[] capabilities = new int[] {
		Capability.HEART_RATE,
		Capability.RESPIRATION_RATE,
		Capability.SKIN_TEMP,
	};	
	
	
	@Override
	public String getDeviceAddress() {
		return BH_ADDRESS;
	}


	@Override
	public ModelInfo getModelInfo() {
		return new ModelInfo("Zephyr BioHarness", "Zephyr Technology");
	}

	@Override
	public int[] getCapabilities() {
		return capabilities;
	}
		
	/*@Override
	public int getDeviceId() {
		return Device.ZEPHYR_BIOHARNESS;
	}*/
}
