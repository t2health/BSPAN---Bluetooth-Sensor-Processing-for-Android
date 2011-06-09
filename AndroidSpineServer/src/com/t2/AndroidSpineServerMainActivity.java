package com.t2;





import java.util.Vector;

import com.t2.SpineReceiver.BioFeedbackData;
import com.t2.SpineReceiver.BioFeedbackSpineData;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;

//import com.t2.biofeedback.demo.R;
import com.t2.chart.widget.FlowingChart;

import spine.SPINEFactory;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.datamodel.Data;
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

public class AndroidSpineServerMainActivity extends Activity implements OnBioFeedbackMessageRecievedListener, SPINEListener {
	private static final String TAG = Constants.TAG;
    private static SPINEManager manager;
	private SpineReceiver receiver;
	private AlertDialog connectingDialog;
	private static AndroidSpineServerMainActivity instance;

	
	private EditText spineLog;
	private FlowingChart spineChart;
	
	
	
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
//        this.respirationRateChart = (FlowingChart)this.findViewById(R.id.respirationRateChart);
//        spineChart = (FlowingChart)this.findViewById(R.id.spineChart);
        
        
        
		// Initialize SPINE by passing the fileName with the configuration properties
		try {
			manager = SPINEFactory.createSPINEManager("SPINETestApp.properties", resources);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
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

//        String messageId = bfmd.messageId;
//		if(messageId.equals("SPINE_MESSAGE")) {
//			StringBuffer hexString = new StringBuffer();
//			
//			for (int i=0; i <bfmd.msgBytes.length; i++) 
//			{
//			    hexString.append(Integer.toHexString(0xFF & bfmd.msgBytes[i]));
//			}		
//			String str = new String(hexString);
//			Log.i(TAG, "Spine Data Received: " + str );		
//			
//			
//			
//			String text = statusText.getText().toString();
//			text = str+ "\n" + text;
//			statusText.setText(text);
//		}
		
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

	@Override
	public void received(Data data) {
		
		if (data != null)
		{
			Log.i(TAG, "RealSpine: Received data: " + data.toString() );

			String text = spineLog.getText().toString();
			text = data.toString() + "\n" + text;
			spineLog.setText(text);		
//			statusText.setText(data.toString());
			spineChart.addValue(new Float(10));
			
		}

		
	}

	@Override
	public void discoveryCompleted(Vector activeNodes) {
		Log.i(TAG, "RealSpine: received service ADV: " );	
	}
}