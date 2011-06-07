package com.t2.biofeedback.device.Spine;


import com.t2.biofeedback.Constants;


public class SpineBH extends SpineDevice {
	private static final String TAG = Constants.TAG;
	
	// Note that this address is only used 
	// This array is used only when "Display option B" (see Device Manager) is chosen.
	// Otherwise the Device Manager will set up the address on it's own

//	private static final String BH_ADDRESS = "00:17:A0:01:64:79";
	private static final String BH_ADDRESS = "00:00:00:00:00:00";
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
