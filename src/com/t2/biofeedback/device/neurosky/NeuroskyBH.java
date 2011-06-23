package com.t2.biofeedback.device.neurosky;

import java.util.ArrayList;

import android.os.Messenger;

import com.t2.biofeedback.Constants;


public class NeuroskyBH extends NeuroskyDevice {
	private static final String TAG = Constants.TAG;
	private static final String BH_ADDRESS = "00:00:00:00:00:00";
	public static final int[] capabilities = new int[] {
	};	
	
	
	public NeuroskyBH(ArrayList<Messenger> serverListeners) {
		super(serverListeners);
	}


	@Override
	public String getDeviceAddress() {
		return BH_ADDRESS;
	}


	@Override
	public ModelInfo getModelInfo() {
		return new ModelInfo("Neurosky Mindset", "Neurosky");
	}

	@Override
	public int[] getCapabilities() {
		return capabilities;
	}



		

}