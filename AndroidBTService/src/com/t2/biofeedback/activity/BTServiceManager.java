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

package com.t2.biofeedback.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.t2.biofeedback.R;
import com.t2.biofeedback.BioFeedbackService;
import com.t2.biofeedback.Constants;
import com.t2.biofeedback.DeviceManager;
import com.t2.biofeedback.device.BioFeedbackDevice;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Activity used to manually administer list of available devices.
 * This is instantiated in two conditions
 *  1. When the user starts the service manually (without, or more accurately before the Spine server).
 *  2. When the user selects "Manage Devices" from the Spine Server main menu.
 *  
 * @author scott.coleman
 *
 */
public class BTServiceManager extends Activity implements OnClickListener {
	private static final String TAG = Constants.TAG;

	private static final int BLUETOOTH_SETTINGS = 987;
	
	private ListView deviceList;
	private DeviceManager deviceManager;
	private ManagerItemAdapter deviceListAdapter;

	private GeneralReceiver generalBroadcastReceiver;

	private AlertDialog bluetoothDisabledDialog;
    ArrayList<Messenger> mServerListeners = new ArrayList<Messenger>();
	String mVersionName = "";    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//this.sendBroadcast(new Intent(BioFeedbackService.ACTION_SERVICE_START));
		
		this.setContentView(R.layout.manager);

		this.findViewById(R.id.bluetoothSettingsButton).setOnClickListener(this);
		this.findViewById(R.id.about).setOnClickListener(this);
		

		// *************************
		// Note - when used only in conjunction with the Spine server
		// where we know that a device manager has been instantiated then use the nocreate
		// option.
		// Otherwise use the normal DeviceManager.getInstance.
		// *************************
		this.deviceManager = DeviceManager.getInstanceNoCreate();
//		this.deviceManager = DeviceManager..getInstance();
		if (this.deviceManager == null) {
		   	Log.e(TAG, "There is no device manager instance!");
		   	return;
			
		}
		
		//		this.deviceManager = DeviceManager.getInstance(this.getBaseContext(), null);
		this.deviceList = (ListView)this.findViewById(R.id.list);
		
		this.deviceListAdapter = new ManagerItemAdapter(
				this,
				this.deviceManager,
				R.layout.manager_item
		);
		this.deviceList.setAdapter(this.deviceListAdapter);
		
		
		this.generalBroadcastReceiver = new GeneralReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BioFeedbackService.ACTION_STATUS_BROADCAST);
		this.registerReceiver(
				this.generalBroadcastReceiver, 
				filter
		);
		
		this.bluetoothDisabledDialog = new AlertDialog.Builder(this)
			.setMessage("Bluetooth is not enabled.")
			.setOnCancelListener(new OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
//					sendBroadcast(new Intent(BioFeedbackService.ACTION_SERVICE_STOP));
					finish();
				}
			})
			.setPositiveButton("Setup Bluetooth", new DialogInterface.OnClickListener () {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startBluetoothSettings();
				}
			})
			.create();
		
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			mVersionName = info.versionName;
			Log.i(TAG, "Spine server Test Application Version " + mVersionName);
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}			
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
			case BLUETOOTH_SETTINGS:
				break;
		}
	}
	
	private void startBluetoothSettings() {
		this.startActivityForResult(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS), BLUETOOTH_SETTINGS);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			this.bluetoothDisabledDialog.show();
		}
		if (deviceListAdapter != null) {
			deviceListAdapter.reloadItems();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//    	this.sendBroadcast(new Intent("com.t2.biofeedback.service.STOP"));		
		this.unregisterReceiver(this.generalBroadcastReceiver);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.bluetoothSettingsButton) {
			this.startBluetoothSettings();
		} else if (id == R.id.about) {
			String content = "National Center for Telehealth and Technology (T2)\n\n";
			content += "Spine Bluetooth Service\n";
			content += "Version " + mVersionName;
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("About");
			alert.setMessage(content);
			alert.show();
		}
	}
	
	private class GeneralReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (deviceListAdapter != null)
				deviceListAdapter.reloadItems();
		}
	}
	
	private class ManagerItemAdapter extends ArrayAdapter<BioFeedbackDevice> implements OnCheckedChangeListener {
		private DeviceManager deviceManager;
		private int layoutId;
		private LayoutInflater layoutInflater;
		private HashMap<View,String> viewDeviceMap = new HashMap<View,String>();

		public ManagerItemAdapter(Context context, DeviceManager dm, int textViewResourceId) {
			super(context, textViewResourceId, new ArrayList<BioFeedbackDevice>());
			
			this.deviceManager = dm;
			this.layoutId = textViewResourceId;
			layoutInflater = (LayoutInflater)this.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			
			this.setNotifyOnChange(false);
			
			this.reloadItems();
		}
		
		public void reloadItems() {
			this.reloadItems(true);
		}
		
		private void reloadItems(boolean notify) {
			this.clear();
			
			BioFeedbackDevice[] items = this.deviceManager.getBondedDevices();
			for(int i = 0; i < items.length; i++) {
				this.add(items[i]);
			}
			
			if(notify) {
				this.notifyDataSetChanged();
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BioFeedbackDevice d = this.getItem(position);
			View v = layoutInflater.inflate(this.layoutId, null);
			
			if(d == null) {
				return v;
			}
			
			int statusString;
			if(d.isBonded()) {
				if(!this.deviceManager.isDeviceEnabled(d.getAddress())) {
					statusString = R.string.paired_disabled;
				} else if(d.isConencted()) {
					statusString = R.string.paired_connected;
				} else if(d.isConnecting()) {
					statusString = R.string.paired_connecting;
				} else {
					statusString = R.string.paired_not_connected;
				}
			} else {
				statusString = R.string.not_paired;
			}
			
			((TextView)v.findViewById(R.id.text1)).setText(d.getName());
			((TextView)v.findViewById(R.id.status)).setText(statusString);
			((ToggleButton)v.findViewById(R.id.enabled)).setChecked(this.deviceManager.isDeviceEnabled(d.getAddress()));
			((ToggleButton)v.findViewById(R.id.enabled)).setOnCheckedChangeListener(this);
			((ToggleButton)v.findViewById(R.id.enabled)).setTag(d.getAddress());
			
			viewDeviceMap.put(v, d.getAddress());
			
			return v;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			String address = (String)buttonView.getTag();
			if(isChecked) {
				this.deviceManager.setDeviceEnabled(address, true);
			} else {
				this.deviceManager.setDeviceEnabled(address, false);
			}
			
			this.reloadItems();
		}
	}
}
