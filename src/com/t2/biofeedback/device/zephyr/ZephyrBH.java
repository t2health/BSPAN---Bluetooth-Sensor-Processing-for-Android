package com.t2.biofeedback.device.zephyr;


import java.util.ArrayList;

import android.os.Messenger;

import com.t2.biofeedback.BioFeedbackService;
import com.t2.biofeedback.Constants;


public class ZephyrBH extends ZephyrDevice {
	private static final String TAG = Constants.TAG;
	private static final String BH_ADDRESS = "00:07:80:99:9E:8C";
	public static final int[] capabilities = new int[] {
		Capability.HEART_RATE,
		Capability.RESPIRATION_RATE,
		Capability.SKIN_TEMP,
	};	
	
	
	public ZephyrBH(BioFeedbackService biofeedbackService) {
		super(biofeedbackService);
	}


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
