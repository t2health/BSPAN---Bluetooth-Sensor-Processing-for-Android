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
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

public class BTServiceManager extends Activity implements OnClickListener {
	private static final String TAG = Constants.TAG;

	private static final int BLUETOOTH_SETTINGS = 987;
	
	private ListView deviceList;
	private DeviceManager deviceManager;
	private ManagerItemAdapter deviceListAdapter;

	private GeneralReceiver generalBroadcastReceiver;

	private AlertDialog bluetoothDisabledDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.sendBroadcast(new Intent(BioFeedbackService.ACTION_SERVICE_START));
		
		this.setContentView(R.layout.manager);
		
		((ToggleButton)this.findViewById(R.id.serviceToggleButton)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked) {
					sendBroadcast(new Intent(BioFeedbackService.ACTION_SERVICE_START));
				} else {
					sendBroadcast(new Intent(BioFeedbackService.ACTION_SERVICE_STOP));
				}
			}
		});
		this.findViewById(R.id.bluetoothSettingsButton).setOnClickListener(this);
		
		this.deviceManager = DeviceManager.getInstance(this.getBaseContext());
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
					sendBroadcast(new Intent(BioFeedbackService.ACTION_SERVICE_STOP));
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
		this.deviceListAdapter.reloadItems();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.generalBroadcastReceiver);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.bluetoothSettingsButton:
				this.startBluetoothSettings();
				break;
		}
	}
	
	
	private class GeneralReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
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
