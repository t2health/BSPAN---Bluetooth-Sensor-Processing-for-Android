package com.t2;





import java.util.Vector;

import com.t2.SpineReceiver.BioFeedbackData;
import com.t2.SpineReceiver.BioFeedbackSpineData;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;
import com.t2.SpineReceiver.ZephyrData;

//import com.t2.biofeedback.demo.R;
import com.t2.chart.widget.FlowingChart;

import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.FeatureData;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import spine.datamodel.functions.*;


public class AndroidSpineServerMainActivity extends Activity implements OnBioFeedbackMessageRecievedListener, SPINEListener {
	private static final String TAG = Constants.TAG;
    private static SPINEManager manager;
	private SpineReceiver receiver;
	private AlertDialog connectingDialog;
	private static AndroidSpineServerMainActivity instance;

	
	private EditText spineLog;
	private FlowingChart spineChart;
	private EditText deviceLog;
	private FlowingChart deviceChart;
	
	
	
	public static AndroidSpineServerMainActivity getInstance() 
	{
	   return instance;
	}
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        instance = this;
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
				manager.discoveryWsn();
            }
        });        
        
        spineLog = (EditText) findViewById(R.id.spineLog);
        spineChart = (FlowingChart)this.findViewById(R.id.spineChart);
        deviceLog = (EditText) findViewById(R.id.deviceLog);
        deviceChart = (FlowingChart)this.findViewById(R.id.deviceChart);
        
		// Initialize SPINE by passing the fileName with the configuration properties
		try {
			manager = SPINEFactory.createSPINEManager("SPINETestApp.properties", resources);
		} catch (InstantiationException e) {
			Log.e(TAG, "Exception creating SPINE manager: " + e.toString());
			e.printStackTrace();
		}        
        
		// ... then we need to register a SPINEListener implementation to the SPINE manager instance
		// (I register myself since I'm a SPINEListener implementation!)
		manager.addListener(this);	        
                
        // This one will go away soon!
		// Create a broadcast receiver.
        this.receiver = new SpineReceiver(this);
        
        // Create a connecting dialog.
        this.connectingDialog = new AlertDialog.Builder(this)
        	// Close the app if connecting was not finished.
	        .setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			})
			// Allow the biofeedback device settings to be used.
			.setPositiveButton("BioFeedback Settings", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent("com.t2.biofeedback.MANAGER"));
				}
			})
			.setMessage("Connecting...")
			.create();
    }

    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	this.sendBroadcast(new Intent("com.t2.biofeedback.service.STOP"));
    	this.unregisterReceiver(this.receiver);
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.sendBroadcast(new Intent("com.t2.biofeedback.service.START"));
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.t2.biofeedback.service.spinedata.BROADCAST");
		filter.addAction("com.t2.biofeedback.service.data.BROADCAST");
		filter.addAction("com.t2.biofeedback.service.status.BROADCAST");
		filter.addAction("com.t2.biofeedback.service.zephyrdata.BROADCAST");
		
		this.registerReceiver(this.receiver,filter);
        		
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.settings:
				startActivity(new Intent("com.t2.biofeedback.MANAGER"));
				return true;
		
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	/* (non-Javadoc)
	 * @see com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener#onDataReceived(com.t2.SpineReceiver.BioFeedbackData)
	 * This is where we receive data directly from a bluetooth device
	 * (as opposed to receiving through Spine)
	 */
	@Override
	public void onDataReceived(BioFeedbackData bfmd) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Regular Data Received" );		

        String messageId = bfmd.messageId;
		if(messageId.equals("SPINE_MESSAGE")) {
			double value = (bfmd.avgValue * 9 / 5) + 32;
			String text = spineLog.getText().toString();
			text = value+"\n"+text;
			spineLog.setText(text);
		}
			
		
	}

	/* (non-Javadoc)
	 * @see com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener#onSpineDataReceived(com.t2.SpineReceiver.BioFeedbackSpineData)
	 * 
	 * This is now simply a deprecated placeholder.
	 * Before the full SPINE data path was implemented we were
	 * directing data directly from the SERVICE to here.
	 * 
	 * Now the full data path is used and data goes to received(Data data)
	 */
	@Override
	public void onSpineDataReceived(BioFeedbackSpineData bfmd) {
	}

	
	@Override
	public void onStatusReceived(BioFeedbackStatus bfs) {
		if(bfs.messageId.equals("CONN_CONNECTING")) {
			Log.i(TAG, "Received command : CONN_CONNECTING" );		
			this.connectingDialog.show();
			
		} else if(bfs.messageId.equals("CONN_ANY_CONNECTED")) {
			Log.i(TAG, "Received command : CONN_ANY_CONNECTED" );		
			this.connectingDialog.hide();
		}
	}

	@Override
	public void newNodeDiscovered(Node newNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void received(ServiceMessage msg) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see spine.SPINEListener#received(spine.datamodel.Data)
	 * This is where we receive data that comes through the actual
	 * Spine channel
	 */
	@Override
	public void received(Data data) {
		int ch1Value;
		
		if (data != null)
		{
			
			switch (data.getFunctionCode()) {
				case SPINEFunctionConstants.FEATURE: {
					Node source = data.getNode();
					Feature[] feats = ((FeatureData)data).getFeatures();
					Feature firsFeat = feats[0];
					byte sensor = firsFeat.getSensorCode();
					byte featCode = firsFeat.getFeatureCode();
					ch1Value = firsFeat.getCh1Value();
					String text = spineLog.getText().toString();
					text = ch1Value + "\n" + text;
					spineLog.setText(text);		
					spineChart.addValue(new Float(ch1Value));
					break;
				}
				case SPINEFunctionConstants.ONE_SHOT:
					Log.i(TAG, "SPINEFunctionConstants.ONE_SHOT"  );
					break;
					
				case SPINEFunctionConstants.ALARM:
					Log.i(TAG, "SPINEFunctionConstants.ALARM"  );
					break;
			}
			
			
			
			Log.i(TAG, "RealSpine: Received data: " + data.toString() );

			
		}

		
	}

	@Override
	public void discoveryCompleted(Vector activeNodes) {
		Log.i(TAG, "RealSpine: received service ADV: " );	
	}

	@Override
	public void onZephyrDataReceived(ZephyrData bfmd) {
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<10;i++) 
		{
		    hexString.append(Integer.toHexString(0xFF & bfmd.msgBytes[i]));
		}			
//		Log.i(TAG, "onZephyrDataReceived"  );
		Log.i(TAG, "Zephyr bytes: " + new String(hexString));	
		
		// TODO: do a real decode here
		int data = byteArrayToInt(new byte[] {bfmd.msgBytes[12], bfmd.msgBytes[13]});;		
		String text = deviceLog.getText().toString();
		text = data + "\n" + text;
		deviceLog.setText(text);		
		deviceChart.addValue(new Float(data));

		
	}
	public static int byteArrayToInt(byte[] bytes) {
		int val = 0;
		
		for(int i = 0; i < bytes.length; i++) {
			int n = (bytes[i] < 0 ? (int)bytes[i] + 256 : (int)bytes[i]) << (8 * i);
			val += n;
		}
		
		return val;
	}
	
	
}