package com.t2.biofeedback;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.t2.biofeedback.device.Spine.SpineBH;
import com.t2.biofeedback.device.neurosky.NeuroskyBH;
import com.t2.biofeedback.device.zephyr.ZephyrBH;

public class DeviceManager {
	private static final String TAG = Constants.TAG;
	
	private ArrayList<String> disabledAddresses = new ArrayList<String>();
	private HashMap<String,BioFeedbackDevice> availableDevices = new HashMap<String,BioFeedbackDevice>(); 
	private HashMap<String,BioFeedbackDevice> bondedDevices = new HashMap<String,BioFeedbackDevice>(); 
	private HashMap<String,BioFeedbackDevice> enabledDevices = new HashMap<String,BioFeedbackDevice>();

	private Context context;

	private SharedPreferences sharedPref;
	private static DeviceManager deviceManager;
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

	private DeviceManager(Context c, ArrayList<Messenger> serverListeners) {
		this.mServerListeners = serverListeners;
		this.context = c;
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
		
		this.loadSettings();
		
		// Display option A
		// Use this block if you want to list all bonded BT devices regardless of their BT Address
		Set deviceBondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();	
		Iterator bit = deviceBondedDevices.iterator();
		while(bit.hasNext())
		{
			BioFeedbackDevice d;
			BluetoothDevice bt = (BluetoothDevice) bit.next();
			String name = bt.getName();
			if (name.equalsIgnoreCase("BH ZBH002095"))
			{
				d = new ZephyrBH(mServerListeners);
				
			}
			else if (name.equalsIgnoreCase("MINDSET")) 
			{
				d = new NeuroskyBH(mServerListeners);
			}
			else
			{
				d = new SpineBH(mServerListeners);
			}
			d.setDevice(bt.getAddress());
			this.availableDevices.put(d.getAddress(),d);			
		}
		

		// Display option B
 		// Use this block if you want to list only devices that match the specific Addresses
		// of devices in the static array "devices" (see above
//		for(int i = 0; i < devices.length; i++) {
//			BioFeedbackDevice d = devices[i];
//			
//			this.availableDevices.put(
//					d.getAddress(), 
//					d
//			);
//		}
		
		
		
		
		manage();
	}
	
	public static DeviceManager getInstance(Context c, ArrayList<Messenger> serverListeners) {
		if(deviceManager == null) {
			deviceManager = new DeviceManager(c, serverListeners);
		}
		else
		{
			deviceManager.mServerListeners = serverListeners;
			// Now we need to go through each existing device and make sure it knows about this service
			deviceManager.updateAvailableDevices(serverListeners);
			
		}
		return deviceManager;
	}
	
	public static DeviceManager getInstanceNoCreate() {
		return deviceManager;
	}
	
	private void loadSettings() {
		String val = this.sharedPref.getString("disabledAddresses", "");
		String[] addresses = val.split(",");
		this.disabledAddresses.clear();
		this.disabledAddresses.addAll(Arrays.asList(addresses));
	}
	
	private void saveSettings() {
		String val = "";
		for(int i = 0; i < this.disabledAddresses.size(); i++) {
			val += this.disabledAddresses.get(i)+",";
		}
		this.sharedPref.edit().putString("disabledAddresses", val).commit();
	}
	
	public void setDevicesEnabled(String[] addresses, boolean b) {
		List<String> addressesList = Arrays.asList(addresses);
		
		this.disabledAddresses.removeAll(addressesList);
		
		if(b) {
			this.disabledAddresses.addAll(addressesList);
		}

		this.saveSettings();
		manage();
	}
	
	public void setDeviceEnabled(String address, boolean b) {
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
	
	public boolean isDeviceEnabled(String address) {
		return this.enabledDevices.containsKey(address);
	}
	
	public void connectAll() {
		for(BioFeedbackDevice d: this.enabledDevices.values()) {
			if(d.isBonded() && !d.isConencted() && !d.isConnecting()) {
				d.connect();
			}
		}
	}
	
	public void closeAll() {
		for(BioFeedbackDevice d: this.enabledDevices.values()) {
			d.close();
		}
	}
	
	public boolean isAnyDeviceConnected() {
		for(BioFeedbackDevice d: this.enabledDevices.values()) {
			if(d.isConencted()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAllDevicesClosed() {
		for(BioFeedbackDevice d: this.enabledDevices.values()) {
			if(d.isConencted()) {
				return false;
			}
		}
		return true;
	}
	
	public BioFeedbackDevice[] getEnabledDevices() {
		return this.enabledDevices.values().toArray(new BioFeedbackDevice[this.enabledDevices.size()]);
	}
	
	public BioFeedbackDevice[] getBondedDevices() {
		return this.bondedDevices.values().toArray(new BioFeedbackDevice[this.bondedDevices.size()]);
	}
	
	public BioFeedbackDevice[] getAvailableDevices() {
		return this.availableDevices.values().toArray(new BioFeedbackDevice[this.availableDevices.size()]);
	}
	
	// The biofeedback service may have changed, we need to tell each device about it.
	private void updateAvailableDevices(ArrayList<Messenger> mServerListeners)
	{
		for(String address: this.availableDevices.keySet()) {
			BioFeedbackDevice d = this.availableDevices.get(address);
			Log.i(TAG, "Updating device: " + d.getAddress());
			d.setServerListeners(mServerListeners);
		}		
	}
	
	
	public void manage() {
		// Add/remove devices from the bonded list.
		// Any device that is bonded is also considered "enabled"
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
