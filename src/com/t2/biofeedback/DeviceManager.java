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

/**
 * This is the main interface used by the service to the actual Bluetooth 
 * devices which are connected or paired to the mobile device.
 *  
 * @author scott.coleman
 *
 */
public class DeviceManager {
	private static final String TAG = Constants.TAG;
	
	private ArrayList<String> disabledAddresses = new ArrayList<String>();
	private HashMap<String,BioFeedbackDevice> availableDevices = new HashMap<String,BioFeedbackDevice>(); 
	private HashMap<String,BioFeedbackDevice> bondedDevices = new HashMap<String,BioFeedbackDevice>(); 
	private HashMap<String,BioFeedbackDevice> enabledDevices = new HashMap<String,BioFeedbackDevice>();

	private Context context;

	private SharedPreferences sharedPref;

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
	
	/**
	 * Singleton used to get instance of the DeviceManager
	 * @param c					Context application
	 * @param serverListeners	List of server listeners (used to transmit messages to the Spine server)  
	 * @return					Instance of the DeviceManager
	 */
	public static DeviceManager getInstance(Context c, ArrayList<Messenger> serverListeners) {
		if(deviceManager == null) {
			deviceManager = new DeviceManager(c, serverListeners);
		}
		else
		{
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
				deviceManager.updateAvailableDevices(serverListeners);
			}
			
		}
		return deviceManager;
	}
	
	/**
	 * Grabs static instance of DeviceManager - Does not create if not present
	 * @return
	 */
	public static DeviceManager getInstanceNoCreate() {
		return deviceManager;
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
	private void updateAvailableDevices(ArrayList<Messenger> mServerListeners)
	{
		for(String address: this.availableDevices.keySet()) {
			BioFeedbackDevice d = this.availableDevices.get(address);
			Log.i(TAG, "Updating device listeners: " + d.getAddress());
			d.setServerListeners(mServerListeners);
		}		
	}
	
	/**
	 *  Add/remove devices from the bonded list.
	 * 	Note: Any device that is bonded is also considered "enabled" 
	 */
	public void manage() {

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
