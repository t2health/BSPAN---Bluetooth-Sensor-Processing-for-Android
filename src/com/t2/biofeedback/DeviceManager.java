package com.t2.biofeedback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.t2.biofeedback.device.BioFeedbackDevice;
import com.t2.biofeedback.device.Spine.SpineBH;
import com.t2.biofeedback.device.Spine.SpineDevice;
import com.t2.biofeedback.device.Spine.TestBH;
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
	
	public static final BioFeedbackDevice[] devices = new BioFeedbackDevice[] {
		new ZephyrBH(),
		new SpineBH(),
		new TestBH(),
	};
	
	private DeviceManager(Context c) {
		this.context = c;
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
		
		this.loadSettings();
		
		for(int i = 0; i < devices.length; i++) {
			BioFeedbackDevice d = devices[i];
			
			this.availableDevices.put(
					d.getAddress(), 
					d
			);
		}
		
		manage();
	}
	
	public static DeviceManager getInstance(Context c) {
		if(deviceManager == null) {
			deviceManager = new DeviceManager(c);
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
