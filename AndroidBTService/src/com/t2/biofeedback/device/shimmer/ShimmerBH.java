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