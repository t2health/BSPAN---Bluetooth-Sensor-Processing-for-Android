package com.t2.biofeedback.device.shimmer;

import java.util.ArrayList;
import android.os.Messenger;
import com.t2.biofeedback.Constants;

/**
 * Super class of all NON-Spine Shimmer devices
 *  Note that this is for devices that do NOT use the SPINE protocol
 *  This is for shimmer devices that have been programmed with the BoilerPlate firmware
 * Contains device specific info (Like Address)
 * @author scott.coleman
 *
 */
public class ShimmerBH extends ShimmerDevice {
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
	
	public ShimmerBH(ArrayList<Messenger> serverListeners) {
		super(serverListeners);
	}

	@Override
	public String getDeviceAddress() {
		return BH_ADDRESS;
	}

	@Override
	public ModelInfo getModelInfo() {
		return new ModelInfo("Shimmer Generic", "Shimmer");
	}
}