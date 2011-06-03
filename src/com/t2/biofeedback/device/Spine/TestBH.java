package com.t2.biofeedback.device.Spine;


import com.t2.biofeedback.Constants;


public class TestBH extends SpineDevice {
	private static final String TAG = Constants.TAG;
	private static final String BH_ADDRESS = "00:06:66:42:89:7A";
	public static final int[] capabilities = new int[] {
		Capability.SPINE_MESSAGE
	};	
		
	
	@Override
	public String getDeviceAddress() {
		return BH_ADDRESS;
	}


	@Override
	public ModelInfo getModelInfo() {
		return new ModelInfo("Generic Spine Device1", "T2 Health");
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
