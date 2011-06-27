package com.t2.biofeedback.device.Spine;

import java.util.ArrayList;

import android.os.Messenger;

import com.t2.biofeedback.Constants;

/**
 * Super class of all Spine devices
 * Contains device specific info (Like Address)
 * @author scott.coleman
 *
 */
public class SpineBH extends SpineDevice {

	public SpineBH(ArrayList<Messenger> serverListeners) {
		super(serverListeners);
		
	}

	private static final String TAG = Constants.TAG;

	/**
	 * Bluetooth address of device
	 *   	Note that the bluetooth device is optional for the current configuration
	 *   	because the service will list ALL paired devices	 * 
	 * 	This array is used only when "Display option B" (see Device Manager) is chosen.
	 * 		Otherwise the Device Manager will set up the address on it's own
	 * @see DeviceManager
	 */
	private static final String BH_ADDRESS = "00:00:00:00:00:00";
//	private static final String BH_ADDRESS = "00:17:A0:01:64:79";

	@Override
	public String getDeviceAddress() {
		return BH_ADDRESS;
	}

	@Override
	public ModelInfo getModelInfo() {
		return new ModelInfo("Generic Spine Device", "T2 Health");
	}
}
