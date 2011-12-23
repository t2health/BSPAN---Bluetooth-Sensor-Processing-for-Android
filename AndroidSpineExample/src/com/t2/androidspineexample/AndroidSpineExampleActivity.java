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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.datamodel.Address;
import spine.datamodel.Data;
import spine.datamodel.MindsetData;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;

import com.t2.Constants;
import com.t2.R;
import com.t2.SpineReceiver;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is a trivial example activity to show how to connect an Android application to the Spine server
 *   Note that only the NeuroSky Mindset device is implemented in this example
 *   
 * @author scott.coleman
 *
 */
public class AndroidSpineExampleActivity extends Activity
	implements OnBioFeedbackMessageRecievedListener, SPINEListener{
	
	private static final String TAG = "BFDemo";

	/**
	 * Static instance of this activity
	 */
	private static AndroidSpineExampleActivity instance;
	
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
    private TextView mTextViewData;
	
	
	// Some files for dealing with log writing
	private BufferedWriter mLogWriter = null;
	private boolean mLoggingEnabled = true;
	File mLogFile;
	String mLogFileName = "";
	private String mSessionName = "sessionData";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		instance = this;
        setContentView(R.layout.main);
        
        // Set up member variables to UI Elements
        mTextViewData = (TextView) findViewById(R.id.textViewData);
        
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
		Node mindsetNode = null;
		mindsetNode = new Node(new Address("" + Constants.RESERVED_ADDRESS_MINDSET));
		mSpineManager.getActiveNodes().add(mindsetNode);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		openLogFile();
		mSpineManager.discoveryWsn();			
	}
    
	@Override
	protected void onPause() {
		super.onPause();
		closeLogFile();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
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
						mTextViewData.setText("Delta = " + delta);  		
						
			        	if (mLogWriter != null) {
			        		try {
								mLogWriter.write("Delta = " + delta + "\n");
							} catch (IOException e) {
								Log.e(TAG, "Exception writing to file: " + e.toString());
							}
			        	}						
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
		}        
        
	}
	
	void openLogFile() {
		if (mLoggingEnabled) {

    		try {
    		    File root = Environment.getExternalStorageDirectory();
    		    if (root.canWrite()){

    		    	mLogFile = new File(root, mSessionName);
    				mLogFileName = mLogFile.getAbsolutePath();
    		        
    		        FileWriter gpxwriter = new FileWriter(mLogFile, true); // open for append
    		        mLogWriter = new BufferedWriter(gpxwriter);
    				Log.i(TAG, this.getClass().getSimpleName() + "Writing data to log file: " + mLogFileName );
    		    } 
    		    else {
        		    Log.e(TAG, "Could not write file " );
    		    }
    		} catch (IOException e) {
    		    Log.e(TAG, "Could not write file " + e.getMessage());
    		}
		}
	}
	
	void closeLogFile() {
    	try {
        	if (mLogWriter != null)
        		mLogWriter.close();
        		mLogWriter = null;
        	
		} catch (IOException e) {
			Log.e(TAG, "Exeption closing file " + e.toString());
		}        	
	}
}