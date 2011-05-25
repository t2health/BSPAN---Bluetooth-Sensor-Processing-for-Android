package com.t2.biofeedback.device.Spine;


import com.t2.biofeedback.Constants;
import com.t2.biofeedback.device.BioFeedbackDevice;

import com.t2.biofeedback.device.BioFeedbackDevice.Capability;

import android.util.Log;


public class SpineBH extends SpineDevice {
	private static final String TAG = Constants.TAG;
	private static final String BH_ADDRESS = "00:17:A0:01:64:79";
	public static final int[] capabilities = new int[] {
		Capability.SPINE_MESSAGE
	};	
	
	@Override
	public String getDeviceAddress() {
		return BH_ADDRESS;
	}


	@Override
	public ModelInfo getModelInfo() {
		return new ModelInfo("Generic Spine Device", "T2 Health");
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
