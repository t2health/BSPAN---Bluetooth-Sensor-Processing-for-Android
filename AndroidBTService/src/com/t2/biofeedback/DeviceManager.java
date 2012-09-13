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

package com.t2.biofeedback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;


import com.t2.biofeedback.device.BioFeedbackDevice;
import com.t2.biofeedback.device.Mobi.MobiBH;
import com.t2.biofeedback.device.Spine.SpineBH;
import com.t2.biofeedback.device.neurosky.NeuroskyBH;
import com.t2.biofeedback.device.shimmer.ShimmerBH;
import com.t2.biofeedback.device.zephyr.ZephyrBH;



/**
 * This is the main interface used by the service to the actual Bluetooth 
 * devices which are connected or paired to the mobile device.
 * 
 *  Two options for listing devices are possible
 * 		Display option A - Use this option if you want to list all bonded BT devices regardless of their BT Address
 * 		Display option B - Use this option if you want to list list only devices that match the specific Addresses
 *
 *  Currently the code uses Option A. Option B is commented out
 *  
 * @author scott.coleman
 *
 */
public class DeviceManager{
	private static final String TAG = Constants.TAG;
	
	private static String zephyrName = "";
	private static String neuroskyName = "";
	private static String shimmerName = "";
	private static String mobiName = "";
	private static String spineName = "";
	
	
	/**
	 * All devices which have been explicitly disabled by the user (Bonded or not)
	 */
	private ArrayList<String> disabledAddresses = new ArrayList<String>();

	/**
	 * All bonded devices which have NOT been explicitly disabled by the user
	 */
	private HashMap<String,BioFeedbackDevice> enabledDevices = new HashMap<String,BioFeedbackDevice>();

	/**
	 * Option A - All bonded BT devices regardless of their BT Address
	 * Option B - Only bonded BT devices appearing in the "device" array
	 */
	private HashMap<String,BioFeedbackDevice> availableDevices = new HashMap<String,BioFeedbackDevice>(); 

	/**
	 * Add bonded (paired) devices according to the Android OS
	 */
	private HashMap<String,BioFeedbackDevice> bondedDevices = new HashMap<String,BioFeedbackDevice>(); 


	Context context;

	private static SharedPreferences sharedPref;

	/**
	 * Static instance of this class
	 */
	private static DeviceManager deviceManager;
	
	/**
	 * List of server listeners (used to transmit messages to the Spine server) 
	 */
	ArrayList<Messenger> mServerListeners;	
	
	
	// This array is used only when "Display option B" (see below) is chosen
	public static final BioFeedbackDevice[] devices = new BioFeedbackDevice[] {
//		new ZephyrBH(),
//		new SpineBH(),
//		new TestBH(),
	};
	
//	public void SetServerListeners(BioFeedbackService biofeedbackService) {
//		this.mBiofeedbackService = biofeedbackService;
//	}

	public void update() {
		try {
			setupBTNames();
			updateAvailableDevices(this, mServerListeners);				
			manage();		
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}		
		
	}
	
	private DeviceManager(Context c, ArrayList<Messenger> serverListeners) {
		Log.d(TAG, this.getClass().getSimpleName() + ".DeviceManager()");
		this.mServerListeners = serverListeners;
		this.context = c;
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);

		this.loadSettings();
		
		try {
			setupBTNames();
			updateAvailableDevices(this, serverListeners);				
			manage();		
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		
		

	}
	
	/**
	 * Singleton used to get instance of the DeviceManager
	 * @param c					Context application
	 * @param serverListeners	List of server listeners (used to transmit messages to the Spine server)  
	 * @return					Instance of the DeviceManager
	 */
	public static DeviceManager getInstance(Context c, ArrayList<Messenger> serverListeners) {
		Log.d(TAG, "DeviceManager.getInstance()"); 
		if(deviceManager == null) {
			deviceManager = new DeviceManager(c, serverListeners);
			Log.d(TAG, "DeviceManager.getInstance() previous doesn't already exist, creating deviceManager = " + deviceManager); 
		}
		else
		{
			Log.d(TAG, "DeviceManager.getInstance() previous exists: deviceManager = " + deviceManager); 
			
			// We need to reset the list of available devices (in case something changed)
			deviceManager.availableDevices.clear();
			updateAvailableDevices(deviceManager, serverListeners);			
			
			
			// There is already a device manager.
			// Update listeners if a new set of listeners is presented
			// Listeners are the mechanism through which the service sends data (not commands)
			// to the server (commands are send via Intent broadcasts. This was done this way
			// to reduce system load (since potentially lots of data will be transmitted to the server)
			if (serverListeners != null)
			{
				// Note: the reason this might be null is if the service activity were started before
				// server, in which case it doesn't know about any listeners yet. So when
				// the server finally starts it will update us with it's listeners 
				deviceManager.mServerListeners = serverListeners;
				// Now we need to go through each existing device and make sure it knows about this service
				deviceManager.updateAvailableDeviceListeners(serverListeners);
			}
			
		}
		return deviceManager;
	}

	
	/**
	 * Grabs static instance of DeviceManager - Does not create if not present
	 * @return
	 */
	public static DeviceManager getInstanceNoCreate() {
		Log.d(TAG, "getInstanceNoCreate() Returning " + deviceManager); 
		
		return deviceManager;
	}
		
	
	static void updateAvailableDevices(DeviceManager dm, ArrayList<Messenger> serverListeners) {

		// Display option A
		// Use this block if you want to list all bonded BT devices regardless of their BT Address
		Set deviceBondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();	
		Iterator bit = deviceBondedDevices.iterator();
		while(bit.hasNext())
		{
			BluetoothDevice bt = (BluetoothDevice) bit.next();

			boolean deviceExists = false;
			// First check the availableDevices list to see it it's already there if do then
			// do nothing.
			Collection devices = dm.availableDevices.values();
			for (Object dev : devices) {
				BioFeedbackDevice device = (BioFeedbackDevice) dev;
				if (device.getDevice().getName().equalsIgnoreCase(bt.getName())) {
					deviceExists = true;
				}
			}
			
			if (!deviceExists) {
				BioFeedbackDevice d = null;
				String name = bt.getName().toUpperCase();
				
	    		// First check for specifically designated names specified by user
				if (zephyrName.toUpperCase().contains(name)) {
					d = new ZephyrBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Zephyr Device");
				}
				else if (neuroskyName.toUpperCase().contains(name)) {
					d = new NeuroskyBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Neurosky Device");
				}
				else if (mobiName.toUpperCase().contains(name)) {
					d = new MobiBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Mobi Device");
				}
				else if (shimmerName.toUpperCase().contains(name)) {
					d = new ShimmerBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Shimmer Device");
				}
				else if (spineName.toUpperCase().contains(name)) {
					d = new SpineBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Spine Device");
				}
				
				
//				if (name.equalsIgnoreCase("BH ZBH002095"))
				else if (name.startsWith("BH")) {
					d = new ZephyrBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Zephyr Device");
					
				}
				else if (name.startsWith("M") || name.startsWith("m"))  { // Mindset
					d = new NeuroskyBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Neurosky Device");
				}
				else if (name.startsWith("Brain")) {
					d = new NeuroskyBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Neurosky Device");
				}
				else if (name.startsWith("NeXus")) {
					d = new MobiBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Mobi Device");
				}
				else if (name.equalsIgnoreCase("RN42-897A")) {
					d = new SpineBH(serverListeners);
					Log.d(TAG, name + ": Assigned as a Spine Device");
				}
				else {
					if (name.startsWith("RN")) {
						d = new ShimmerBH(serverListeners);
						Log.d(TAG, name + ": Assigned as a Shimmer Device");
					}
				}
				if (d != null) {
					d.setDevice(bt.getAddress());
					
					Log.d(TAG, "Adding device " + name + " to available devices");
					dm.availableDevices.put(d.getAddress(),d);			
					
				}				
			}
		}
		

		// Display option B
 		// Use this block if you want to list only devices that match the specific Addresses
		// of devices in the static array "devices" (see above
//			for(int i = 0; i < devices.length; i++) {
//				BioFeedbackDevice d = devices[i];
//				
//				this.availableDevices.put(
//						d.getAddress(), 
//						d
//				);
//			}		
	}
	
	/**
	 * Sets up static names from special shared preference file for sensors
	 * 
	 *  This is in case the user wants to map a device with an odd name to a specific type of sensor
	 */
	void setupBTNames() {
		Log.d(TAG, this.getClass().getSimpleName() + ".setupBTNames()");

		Context otherAppsContext;
    	try {
    		
    		
//    		otherAppsContext = context.createPackageContext("com.t2",0);
//            SharedPreferences myPrefs = otherAppsContext.getSharedPreferences("com.t2.compassionMeditation.BTNAMES", Context.MODE_WORLD_READABLE);
            String APP_SHARED_PREFS = "com.t2.compassionMeditation.BTNAMES";         
            SharedPreferences myPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_WORLD_READABLE);

            
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            shimmerName = myPrefs.getString("shimmer_sensor", "");			
            zephyrName = myPrefs.getString("zephyr_sensor", "");			
            neuroskyName = myPrefs.getString("neurosky_sensor", "");			
            mobiName = myPrefs.getString("mobi_sensor", "");			
            spineName = myPrefs.getString("spine_sensor", "");	
 //           spineName += ",RN42-8B36";
            
    	} 
    	catch (Exception e) {
    		Log.e(TAG, e.toString());
    	}  				
		
	}
	

	/**
	 * Load settings (disabled devices) from shared preferences
	 */
	private void loadSettings() {
		String val = this.sharedPref.getString("disabledAddresses", "");
		String[] addresses = val.split(",");
		this.disabledAddresses.clear();
		this.disabledAddresses.addAll(Arrays.asList(addresses));
	}
	
	/**
	 * Saves settings (disabled devices) to shared preferences
	 */
	private void saveSettings() {
		String val = "";
		for(int i = 0; i < this.disabledAddresses.size(); i++) {
			val += this.disabledAddresses.get(i)+",";
		}
		this.sharedPref.edit().putString("disabledAddresses", val).commit();
	}
	
	/**
	 * Enables/Disables specified list of BT devices
	 * @param addresses	List of BT device addresses to enable/disable
	 * @param b			True = Add devices, false = remove devices
	 */
	public void setDevicesEnabled(String[] addresses, boolean add) {
		List<String> addressesList = Arrays.asList(addresses);
		
		this.disabledAddresses.removeAll(addressesList);
		
		if(add) {
			this.disabledAddresses.addAll(addressesList);
		}

		this.saveSettings();
		manage();
	}
	
	/**
	 * Enables/Disables specified BT device
	 * @param address	BT device addresse to enable/disable
	 * @param b			True = Add devices, false = remove devices
	 */
	public void setDeviceEnabled(String address, boolean b) {
		address = address.toUpperCase();
		if(b) {
			this.disabledAddresses.remove(address);
			this.saveSettings();
			manage();
		} else {
			if(!this.disabledAddresses.contains(address)) {
				this.disabledAddresses.add(address);
				this.saveSettings();
				manage();
			}
		}
		
	}
	
	/**
	 * Returns "enabled" state of a specific device
	 * @param address
	 * @return
	 */
	public boolean isDeviceEnabled(String address) {
		return this.enabledDevices.containsKey(address);
	}
	
	/**
	 * Connects all bonded (paired) devices
	 */
	public void connectAll() {
		for(BioFeedbackDevice d: this.enabledDevices.values()) {
			if(d.isBonded() && !d.isConencted() && !d.isConnecting()) {
				d.connect();
			}
		}
	}
	
	/**
	 * Closes all device connections
	 */
	public void closeAll() {
		for(BioFeedbackDevice d: this.enabledDevices.values()) {
			d.close();
		}
	}
	
	/**
	 * @return	true if any enabled device is connected
	 */
	public boolean isAnyDeviceConnected() {
		for(BioFeedbackDevice d: this.enabledDevices.values()) {
			if(d.isConencted()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return	true if all devices are closed (not connected)
	 */
	public boolean isAllDevicesClosed() {
		for(BioFeedbackDevice d: this.enabledDevices.values()) {
			if(d.isConencted()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return List of enabled devices
	 */
	public BioFeedbackDevice[] getEnabledDevices() {
		return this.enabledDevices.values().toArray(new BioFeedbackDevice[this.enabledDevices.size()]);
	}
	
	/**
	 * @return List of bonded (paired) devices
	 */
	public BioFeedbackDevice[] getBondedDevices() {
		return this.bondedDevices.values().toArray(new BioFeedbackDevice[this.bondedDevices.size()]);
	}
	
	/**
	 * Note that this list will either be:
	 * 	list of all bonded(paired) devices if Display option A is used (see above)
	 *  list of only devices in the array BioFeedbackDevice if options B is used (see above)
	 *  
	 * @return List of all devices that the service knows about
	 */
	public BioFeedbackDevice[] getAvailableDevices() {
		return this.availableDevices.values().toArray(new BioFeedbackDevice[this.availableDevices.size()]);
	}
	
	/**
	 * The biofeedback service may have changed, we need to tell each device about it.
	 * This updates the server listeners array of all available devices
	 * @param mServerListeners New list of server listeners
	 */
	private void updateAvailableDeviceListeners(ArrayList<Messenger> mServerListeners)
	{
		for(String address: this.availableDevices.keySet()) {
			BioFeedbackDevice d = this.availableDevices.get(address);
			Log.i(TAG, "Updating device listeners for device: " + d.getAddress());
			d.setServerListeners(mServerListeners);
		}		
	}
	
	/**
	 *  Add/remove devices from the bonded list.
	 * 	Note: Any device that is bonded is also considered "enabled" 
	 */
	public void manage() {
		setupBTNames();
		updateAvailableDevices(this, mServerListeners);				
		
		for(String address: this.availableDevices.keySet()) {
			BioFeedbackDevice d = this.availableDevices.get(address);
			if(d.isBonded()) {
				this.bondedDevices.put(address, d);
				this.enabledDevices.put(address, d);
			} else {
				this.bondedDevices.remove(address);
			}
		}
		
		// Remove enabled devices if they have been marked as disabled.
		for(int i = 0; i < this.disabledAddresses.size(); i++) {
			String address = this.disabledAddresses.get(i);
			BioFeedbackDevice d = this.enabledDevices.get(address);
			
			if(d != null) {
				if(d.isConencted() || d.isConnecting()) {
					d.close();
				}
				this.enabledDevices.remove(address);
			}
		}
	}

}
