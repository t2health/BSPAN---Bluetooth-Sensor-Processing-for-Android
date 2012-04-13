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

package com.t2.androidspineexample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINESensorConstants;
import spine.datamodel.Address;
import spine.datamodel.Data;
import spine.datamodel.MindsetData;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import spine.datamodel.ShimmerData;
import spine.datamodel.functions.ShimmerNonSpineSetupSensor;

import com.t2.Constants;
import com.t2.R;
import com.t2.SpineReceiver;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;
import com.t2.biofeedback.device.shimmer.ShimmerDevice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is a trivial example activity to show how to connect an Android application to the Spine server
 *   Note that only the NeuroSky Mindset and Shimmer devices are implemented in this example
 *   
 * @author scott.coleman
 *
 */
public class AndroidSpineExampleActivity extends Activity
	implements OnBioFeedbackMessageRecievedListener, SPINEListener{
	
	private static final String TAG = "BFDemo";

	// ****** THESE MUST BE CHANGED TO CORRESPOND TO YOUR SHIMMER DEVICES! ******
	// This mapping must be set so we can correspond specific Shimmer devices to specific parameters
	private static final String GSR_SENSOR_NAME = "RN42-A6A1";
	private static final String ECG_SENSOR_NAME = "RN42-A774";
	private static final String EMG_SENSOR_NAME = "RN42-1111";
	
	
	/**
	 * Static instance of this activity
	 */
	private static AndroidSpineExampleActivity mInstance;
	
    /**
     * The Spine manager contains the bulk of the Spine server. 
     */
    private static SPINEManager mSpineManager;

    /**
	 * This is a broadcast receiver. Note that this is used ONLY for command/status messages from the AndroidBTService
	 * All data from the service goes through the mail SPINE mechanism (received(Data data)).
	 */
	private SpineReceiver mCommandReceiver;        
    
	/**
	 * Mindset data - storage for incoming mindset data
	 */
	private MindsetData mCurrentMindsetData;

    /**
     * Text view to display incoming attention data
     */
    private TextView mTextViewDataMindset;
	
    /**
     * Text view to display incoming attention Shimmmer ecg data
     */
    private TextView mTextViewDataShimmerEcg;
	
    /**
     * Text view to display incoming attention Shimmer gsr data
     */
    private TextView mTextViewDataShimmerGsr;
	
    /**
     * Text view to display incoming attention Shimmer emg data
     */
    private TextView mTextViewDataShimmerEmg;
	
	/**
	 * Node object for shimmer device as returned by spine
	 */
	public Node mShimmerNode = null;
	
	/**
	 * Node object for mindset device as returned by spine
	 */
	public Node mMindsetNode = null;

	/**
	 * List of all currently PAIRED BioSensors
	 */
	private ArrayList<BioSensor> mBioSensors = new ArrayList<BioSensor>();	    
    
    /**
     * Whether or not the device has bluetooth enabled
     */
    private Boolean mBluetoothEnabled = false;
	
	/**
	 * Class to help in saving received data to file
	 */
	private LogWriter mLogWriter;	
    
	/**
	 * The configured GSR resistance range
	 */
	private int mConfiguredGSRRange = ShimmerDevice.GSR_RANGE_HW_RES_3M3;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, this.getClass().getSimpleName() + ".onCreate()");         
		mInstance = this;
        setContentView(R.layout.main);
        
        // Set up member variables to UI Elements
        mTextViewDataMindset = (TextView) findViewById(R.id.textViewData);
        mTextViewDataShimmerEcg = (TextView) findViewById(R.id.textViewDataShimmerEcg);
        mTextViewDataShimmerGsr = (TextView) findViewById(R.id.textViewDataShimmerGsr);
        mTextViewDataShimmerEmg = (TextView) findViewById(R.id.textViewDataShimmerEmg);
        
        // ----------------------------------------------------
		// Initialize SPINE by passing the fileName with the configuration properties
        // ----------------------------------------------------
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();        
		try {
			mSpineManager = SPINEFactory.createSPINEManager("SPINETestApp.properties", resources);
		} catch (InstantiationException e) {
			Log.e(TAG, "Exception creating SPINE manager: " + e.toString());
			Log.e(TAG, "Check to see that valid defaults.properties, and SpineTestApp.properties files exist in the Assets folder!");
			e.printStackTrace();
		}        
		        
		// ... then we need to register a SPINEListener implementation to the SPINE manager instance
		// to receive sensor node data from the Spine server
		// (I register myself since I'm a SPINEListener implementation!)
		mSpineManager.addListener(this);	        
                
		// Create a broadcast receiver. Note that this is used ONLY for command messages from the service
		// All data from the service goes through the mail SPINE mechanism (received(Data data)).
		// See public void received(Data data)
        this.mCommandReceiver = new SpineReceiver(this);    
        
		// Set up filter intents so we can receive broadcasts
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.t2.biofeedback.service.status.BROADCAST");
		this.registerReceiver(this.mCommandReceiver,filter);        
        
		try {
			mCurrentMindsetData = new MindsetData(this);
		} catch (Exception e1) {
			Log.e(TAG, "Exception creating MindsetData: " + e1.toString());
		}
		
		// Since Mindset is a static node we have to manually put it in the active node list
		// Note that the sensor id 0xfff1 (-15) is a reserved id for this particular sensor
		Node MindsetNode = null;
		MindsetNode = new Node(new Address("" + Constants.RESERVED_ADDRESS_MINDSET));
		mSpineManager.getActiveNodes().add(MindsetNode);
		
		// Since Shimmer is a static node we have to manually put it in the active node list
		mShimmerNode = new Node(new Address("" + Constants.RESERVED_ADDRESS_SHIMMER));
		mSpineManager.getActiveNodes().add(mShimmerNode);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
        Log.d(TAG, this.getClass().getSimpleName() + ".onStart()");         
	}

	@Override
	protected void onResume() {
		super.onResume();
        Log.d(TAG, this.getClass().getSimpleName() + ".onResume()");         

		mLogWriter = new LogWriter(this);

		// Create a log file name from the date/time
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
		String currentDateTimeString = sdf.format(new Date());
		String logFileName = "_" + currentDateTimeString + ".log";			
		mLogWriter.open(logFileName);
		
		mSpineManager.discoveryWsn();			
	}
    
	@Override
	protected void onPause() {
		super.onPause();

        Log.d(TAG, this.getClass().getSimpleName() + ".onPause()");         
        StopBioSensors();
		mLogWriter.close();
	}

	@Override
	protected void onStop() {
        Log.d(TAG, this.getClass().getSimpleName() + ".onStop()");         

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        Log.d(TAG, this.getClass().getSimpleName() + ".onDestroy()");         
		
		mSpineManager.removeListener(this);	
    	this.unregisterReceiver(this.mCommandReceiver);
	}

	@Override
	public void newNodeDiscovered(Node newNode) {
	}

	@Override
	public void received(ServiceMessage msg) {
		Log.i(TAG, "received(ServiceMessage msg)" );
	}

	/**
	 * This is where we receive sensor data that comes through the Spine channel. 
	 * @param data		Generic Spine data packet. Should be cast to specified data type indicated by data.getFunctionCode()
	 *
	 * @see spine.SPINEListener#received(spine.datamodel.Data)
	 */
	@Override
	public void received(Data data) {
		if (data != null) {
			switch (data.getFunctionCode()) {

			case SPINEFunctionConstants.MINDSET: {
				Node source = data.getNode();
				MindsetData mindsetData = (MindsetData) data;
					
					// We'll get here once every time a main update occurs from the mindset
					// i.e. once a second.
					// The main update has the spectral data
					if (mindsetData.exeCode == Constants.EXECODE_RAW_ACCUM) {
						mCurrentMindsetData.updateSpectral(mindsetData);
						mCurrentMindsetData.updateRawWave(mindsetData);
						
						Log.i(TAG, ", " + mCurrentMindsetData.getLogDataLine());
						int delta = mCurrentMindsetData.getFeatureValue(MindsetData.DELTA_ID);
						mTextViewDataMindset.setText("Mindset: Delta = " + delta);  	
						
						mLogWriter.write("Mindset: Delta = " + delta);
					}

					if (mindsetData.exeCode == Constants.EXECODE_POOR_SIG_QUALITY) {
						mCurrentMindsetData.poorSignalStrength = mindsetData.poorSignalStrength;
					}
					
					if (mindsetData.exeCode == Constants.EXECODE_ATTENTION) {
					}
					
					if (mindsetData.exeCode == Constants.EXECODE_MEDITATION) {						
						mCurrentMindsetData.meditation= mindsetData.meditation;
					}						
					
					break;
				} // End case SPINEFunctionConstants.MINDSET:
				
				case SPINEFunctionConstants.SHIMMER: {
					Node source = data.getNode();
					ShimmerData shimmerData = (ShimmerData) data;
					
					switch (shimmerData.sensorCode) {
						case SPINESensorConstants.SHIMMER_GSR_SENSOR:
							int resistance = Util.GsrResistance(shimmerData.gsr, shimmerData.gsrRange, mConfiguredGSRRange); 
							String verboseLogLine = String.format("sensor:%-5d gsrADC= %-5d, range= %d, resistance= %d", 
									shimmerData.sensorCode, shimmerData.gsr, shimmerData.gsrRange,
									resistance);
							Log.d(TAG,verboseLogLine );		

							mTextViewDataShimmerGsr.setText(verboseLogLine);  		
							mLogWriter.write("Shimmer: GSR = " + resistance);
							
							
							break;

						case SPINESensorConstants.SHIMMER_EMG_SENSOR:
							verboseLogLine = String.format("sensor:%-5d emgADC= %04x", 
									shimmerData.sensorCode,
									shimmerData.emg);
							Log.d(TAG,verboseLogLine );
						
							mTextViewDataShimmerEmg.setText(verboseLogLine);  		
							mLogWriter.write("Shimmer: EMG = " + shimmerData.emg);
							
							break;

						case SPINESensorConstants.SHIMMER_ECG_SENSOR:
							int ecgLa_Ra = shimmerData.ecgLaLL - shimmerData.ecgRaLL;
							verboseLogLine = String.format("sensor:%-5d ECGLaLL= %5d ,ECGRaLL= %5d ,ECGLaRa= %5d", 
										shimmerData.sensorCode,
										shimmerData.ecgLaLL,
										shimmerData.ecgRaLL,
										ecgLa_Ra);
							Log.d(TAG,verboseLogLine );
							mTextViewDataShimmerEcg.setText("ECG = " + ecgLa_Ra);  		
							mLogWriter.write("Shimmer: ECG = " + ecgLa_Ra);
							
							
						break;
						}
				} // End case SPINEFunctionConstants.SHIMMER:
			}
		}
	}

	/* (non-Javadoc)
	 * This is called by the Spine service a specified number of seconds after the discovery command.
	 * The idea is that Spine should have found all of it's sensors by now.
	 * The only thing we do here is to tell the spind manager to give us a list
	 * of connected devices and their status.
	 * 
	 * @see spine.SPINEListener#discoveryCompleted(java.util.Vector)
	 */
	@Override
	public void discoveryCompleted(Vector activeNodes) {
		// Tell the bluetooth service to send us a list of bluetooth devices and system status
		// Response comes in public void onStatusReceived(BioFeedbackStatus bfs) STATUS_PAIRED_DEVICES
		mSpineManager.pollBluetoothDevices();	
	}

	@Override
	public void onStatusReceived(BioFeedbackStatus bfs) {
        Log.d(TAG, this.getClass().getSimpleName() + ".onStatusReceived(" + bfs.messageId + ")"); 

		String name = bfs.name;
		if (name == null ) name = "sensor node";
		
		if(bfs.messageId.equals("CONN_CONNECTING")) {
			Log.i(TAG, this.getClass().getSimpleName() + " Received command : " + bfs.messageId + " to "  + name );
			Toast.makeText (getApplicationContext(), "Connecting to " + name, Toast.LENGTH_SHORT).show ();
		} 
		else if(bfs.messageId.equals("CONN_ANY_CONNECTED")) {
			Log.i(TAG, this.getClass().getSimpleName() + " Received command : " + bfs.messageId + " to "  + name );
			Toast.makeText (getApplicationContext(), name + " Connected", Toast.LENGTH_SHORT).show ();

			startBioSensors();			
		} 
		else if(bfs.messageId.equals("CONN_CONNECTION_LOST")) {
			Log.i(TAG, this.getClass().getSimpleName() + " Received command : " + bfs.messageId + " to "  + name );
			Toast.makeText (getApplicationContext(), name + " Connection lost ****", Toast.LENGTH_SHORT).show ();
		}		
		else if(bfs.messageId.equals("STATUS_PAIRED_DEVICES")) {
			// We get here in response to mSpineManager.pollBluetoothDevices() from above
			// bfs.address contains a json arrary containing status of all BT Devices
			Log.i(TAG, this.getClass().getSimpleName() + " Received command : " + bfs.messageId + " to "  + name );
			Log.i(TAG, this.getClass().getSimpleName() + bfs.address );
			
			populateBioSensors(bfs.address);
			validateBioSensors();
			startBioSensors();
		}        
	}
	
	/**
	 * Receives a json string containing data about all of the paired sensors
	 * the adds a new BioSensor for each one to the mBioSensors collection
	 * 
	 * @param jsonString String containing info on all paired devices
	 */
	private void populateBioSensors(String jsonString) {
		
		Log.d(TAG, this.getClass().getSimpleName() + " populateBioSensors");
		// Now clear it out and populate it. The only difference is that
		// if a sensor previously existed, then 
		mBioSensors.clear();
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Boolean enabled = jsonObject.getBoolean("enabled");
				String name = jsonObject.getString("name"); 
				String address = jsonObject.getString("address"); 
				int connectionStatus = jsonObject.getInt("connectionStatus");					
				
				if (name.equalsIgnoreCase("system")) {
					mBluetoothEnabled = enabled;
				}
				else {
					
					Log.d(TAG, "Adding sensor " + name + ", " + address + (enabled ? ", enabled":", disabled") + " : " + Util.connectionStatusToString(connectionStatus));
					BioSensor bioSensor = new BioSensor(name, address, enabled);
					bioSensor.mConnectionStatus = connectionStatus;	
					mBioSensors.add(bioSensor);
				}
			}			
		} catch (JSONException e) {
		   	Log.e(TAG, e.toString());
		}
	}	
	
	/**
	 * Validates sensors, makes sure that bluetooth is on and each sensor has a parameter associated with it
	 */
	void validateBioSensors() {
		
		// First make sure that bluetooth is enabled
		if (!mBluetoothEnabled) {
			AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

			alert1.setMessage("Bluetooth is not enabled on your device.");

			alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
				});
			alert1.show();		
		}
		String badSensorName = null;
	}	
	
	/**
	 * Sends startup command to every shimmer device
	 */
	void startBioSensors() {
		for (BioSensor  sensor : mBioSensors) {
			Log.d(TAG, "Sending Start command to sensor " + sensor.mBTName + " (" + sensor.mBTAddress + ")");
			
			ShimmerNonSpineSetupSensor shimmerSetupCommand = new ShimmerNonSpineSetupSensor();
			
			if (sensor.mBTName.equalsIgnoreCase(GSR_SENSOR_NAME)) {
				shimmerSetupCommand.setSensor(SPINESensorConstants.SHIMMER_GSR_SENSOR);
			} 
			else if (sensor.mBTName.equalsIgnoreCase(ECG_SENSOR_NAME)) {
				shimmerSetupCommand.setSensor(SPINESensorConstants.SHIMMER_ECG_SENSOR);
			}
			else if (sensor.mBTName.equalsIgnoreCase(EMG_SENSOR_NAME)) {
				shimmerSetupCommand.setSensor(SPINESensorConstants.SHIMMER_EMG_SENSOR);
			}
			
			shimmerSetupCommand.setBtAddress(Util.AsciiBTAddressToBytes(sensor.mBTAddress));
			
			shimmerSetupCommand.setCommand(ShimmerNonSpineSetupSensor.SHIMMER_COMMAND_RUNNING_4HZ_AUTORANGE);	
			mConfiguredGSRRange = Util.getGsrRangeFromShimmerCommand(ShimmerNonSpineSetupSensor.SHIMMER_COMMAND_RUNNING_4HZ_AUTORANGE);			
			mSpineManager.setup(mShimmerNode, shimmerSetupCommand);				
		}
	}

	/**
	 * Sends stop command to every shimmer device
	 */
	void StopBioSensors() {
		for (BioSensor  sensor : mBioSensors) {
			Log.d(TAG, "Sending Stop command to sensor " + sensor.mBTName + " (" + sensor.mBTAddress + ")");
			ShimmerNonSpineSetupSensor shimmerSetupCommand = new ShimmerNonSpineSetupSensor();
			
			if (sensor.mBTName.equalsIgnoreCase(GSR_SENSOR_NAME)) {
				shimmerSetupCommand.setSensor(SPINESensorConstants.SHIMMER_GSR_SENSOR);
			} 
			else if (sensor.mBTName.equalsIgnoreCase(ECG_SENSOR_NAME)) {
				shimmerSetupCommand.setSensor(SPINESensorConstants.SHIMMER_ECG_SENSOR);
			}
			else if (sensor.mBTName.equalsIgnoreCase(EMG_SENSOR_NAME)) {
				shimmerSetupCommand.setSensor(SPINESensorConstants.SHIMMER_EMG_SENSOR);
			}
			
			shimmerSetupCommand.setBtAddress(Util.AsciiBTAddressToBytes(sensor.mBTAddress));
			
			shimmerSetupCommand.setCommand(ShimmerNonSpineSetupSensor.SHIMMER_COMMAND_STOPPED	);				
			mSpineManager.setup(mShimmerNode, shimmerSetupCommand);				
		}		
	}
}