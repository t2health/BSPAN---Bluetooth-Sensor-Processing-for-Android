package com.t2;





import java.util.Vector;

import com.t2.SpineReceiver.BioFeedbackData;
import com.t2.SpineReceiver.BioFeedbackSpineData;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;

import spine.SPINEFactory;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import t2.spine.communication.android.AndroidSocketMessageListener;
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
import android.widget.TextView;

public class AndroidSpineServerMainActivity extends Activity implements OnBioFeedbackMessageRecievedListener, SPINEListener {
	private static final String TAG = Constants.TAG;
    EditText mEditText;
    private static SPINEManager manager;
	private SpineReceiver receiver;
	private AlertDialog connectingDialog;
	TextView statusText;
	private static AndroidSpineServerMainActivity instance;
    
	
	public static AndroidSpineServerMainActivity getInstance() 
	{
	   return instance;
	}
	
	private void doThis()
	{
		
//	       AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//	          dialog.setTitle("");
//	          dialog.setMessage("status  is on");
//	          dialog.show();
//
//		       AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
//		          dialog1.setTitle("");
//		          dialog1.setMessage("status  is on");
//		          dialog1.show();
	          
//      // Create a connecting dialog.
//      this.connectingDialog = new AlertDialog.Builder(this)
//      	// Close the app if connecting was not finished.
//	        .setOnCancelListener(new OnCancelListener() {
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					finish();
//				}
//			})
//			// Allow the biofeedback device settings to be used.
//			.setPositiveButton("BioFeedback Settings", new OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					startActivity(new Intent("com.t2.biofeedback.MANAGER"));
//				}
//			})
//			.setMessage("Connecting...")
//			.create();		
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

            	doThis();            	
				manager.discoveryWsn();
            	
            	
            	
            	
            	
            	
            	
            	
            	
            }
        });        
        
        statusText = (TextView) findViewById(R.id.statusText);
        
        
		// Initialize SPINE by passing the fileName with the configuration properties
		try {
//			manager = SPINEFactory.createSPINEManager("resources/SPINETestApp.properties", resources);
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
        		
//        try {
//			//startActivity(new Intent(this, AndroidSocketMessageListener.class));
//			Intent myIntent = new Intent(getApplicationContext(), AndroidSocketMessageListener.class);
////			Intent myIntent = new Intent("t2.spine.communication.android.AndroidSocketThrdServer");
//			this.startActivity(myIntent);			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			Log.i("*******", e.toString());
//			//e.printStackTrace();
//		}
//        		
		
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
			String text = statusText.getText().toString();
			text = value+"\n"+text;
			statusText.setText(text);
		}
			
		
	}

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

//			String text = statusText.getText().toString();
//			text = data.toString() + "\n" + text;
//			statusText.setText(text);		
			statusText.setText(data.toString());		
		}

		
	}

	@Override
	public void discoveryCompleted(Vector activeNodes) {
		Log.i(TAG, "RealSpine: received service ADV: " );	

		
	}

	
}