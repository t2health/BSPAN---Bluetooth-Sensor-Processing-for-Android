package com.t2;





import java.util.Vector;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;


//import com.t2.chart.widget.FlowingChart;

import spine.datamodel.Node;
import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.datamodel.Address;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.FeatureData;
import spine.datamodel.MindsetData;
import spine.datamodel.ServiceMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;



public class AndroidSpineServerMainActivity extends Activity implements OnBioFeedbackMessageRecievedListener, SPINEListener {
	private static final String TAG = Constants.TAG;
    private static SPINEManager manager;
	private SpineReceiver receiver;
	private AlertDialog connectingDialog;
	private static AndroidSpineServerMainActivity instance;

	// Charting stuff
	private final static int SPINE_CHART_SIZE = 20;
	private GraphicalView mSpineChartView;
	private XYMultipleSeriesDataset mSpineDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mSpineRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSpineSeries;
	
	private GraphicalView mDeviceChartView;
	private XYMultipleSeriesDataset mDeviceDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mDeviceRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentDeviceSeries;
	
	  
    static final int MSG_UNREGISTER_CLIENT = 2;	
	
	private EditText spineLog;
	private EditText deviceLog;
	
	int mSpineChartX = 0;
	int mDeviceChartX = 0;
	boolean mIsBound = false;
	ServiceConnection mConnection;	
	private Messenger mService = null;	
	
	
	public void setmService(Messenger mService) {
		this.mService = mService;
	}

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
        deviceLog = (EditText) findViewById(R.id.deviceLog);
        
		// Initialize SPINE by passing the fileName with the configuration properties
		try {
			manager = SPINEFactory.createSPINEManager("SPINETestApp.properties", resources);
		} catch (InstantiationException e) {
			Log.e(TAG, "Exception creating SPINE manager: " + e.toString());
			e.printStackTrace();
		}        
        
		
		// Since zepher is a static node we have to manually put it in the active node list
		// Note that the sensor id 0xfff1 (-15) is a reserved id for this particular sensor
		Node zepherNode = null;
		zepherNode = new Node(new Address("" + -15));
		manager.getActiveNodes().add(zepherNode);
		
		Node mindsetNode = null;
		mindsetNode = new Node(new Address("" + -14));
		manager.getActiveNodes().add(mindsetNode);
				
		
		// ... then we need to register a SPINEListener implementation to the SPINE manager instance
		// (I register myself since I'm a SPINEListener implementation!)
		manager.addListener(this);	        
                
		// Create a broadcast receiver. Note that this is used ONLY for command messages from the service
		// All data from the service goes through the mail SPINE mechanism (received(Data data)).
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

        // Set up Spine data chart
        if (mSpineChartView == null) 
        {
          LinearLayout layout = (LinearLayout) findViewById(R.id.spineChart);
          mSpineChartView = ChartFactory.getLineChartView(this, mSpineDataset, mSpineRenderer);
          layout.addView(mSpineChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }    
        
        mSpineRenderer.setShowLabels(false);
        mSpineRenderer.setShowAxes(true);
        mSpineRenderer.setShowLegend(true);
        mSpineRenderer.setMargins(new int[] {0,0,0,0});
        mSpineRenderer.setZoomEnabled(false, false);
        mSpineRenderer.setPanEnabled(false, false);
        mSpineRenderer.setYAxisMin(0);
        mSpineRenderer.setYAxisMax(255);

   
        mCurrentSpineSeries = new XYSeries("Test Data");
        mSpineDataset.addSeries(mCurrentSpineSeries);
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(Color.WHITE); // White
        mSpineRenderer.addSeriesRenderer(renderer);
        
        // Set up Device data chart
        if (mDeviceChartView == null) 
        {
          LinearLayout layout = (LinearLayout) findViewById(R.id.deviceChart);
          mDeviceChartView = ChartFactory.getLineChartView(this, mDeviceDataset, mDeviceRenderer);
          layout.addView(mDeviceChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }    
        mDeviceRenderer.setShowLabels(false);
        mDeviceRenderer.setMargins(new int[] {0,0,0,0});
        mDeviceRenderer.setShowAxes(true);
        mDeviceRenderer.setShowLegend(true);
        
        mDeviceRenderer.setZoomEnabled(false, false);
        mDeviceRenderer.setPanEnabled(false, false);
        mDeviceRenderer.setYAxisMin(0);
        mDeviceRenderer.setYAxisMax(255);

        
        mCurrentDeviceSeries = new XYSeries("Attention");
        mDeviceDataset.addSeries(mCurrentDeviceSeries);
        renderer = new XYSeriesRenderer();
        renderer.setColor(Color.WHITE); // White
        mDeviceRenderer.addSeriesRenderer(renderer);
        
        
//        doBindService();
        
    }

    
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	this.sendBroadcast(new Intent("com.t2.biofeedback.service.STOP"));
    	this.unregisterReceiver(this.receiver);
    	doUnbindService();    	
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
		
		if (data != null)
		{
			
			switch (data.getFunctionCode()) {
			case SPINEFunctionConstants.FEATURE: {
				Node source = data.getNode();
				Feature[] feats = ((FeatureData)data).getFeatures();
				Feature firsFeat = feats[0];
				byte sensor = firsFeat.getSensorCode();
				byte featCode = firsFeat.getFeatureCode();
				int ch1Value = firsFeat.getCh1Value();

//				new UpdateUITask().execute(ch1Value);
				
		    	 String result = Integer.toString(ch1Value);
		    	 spineLog.setText(result);				
				if (mCurrentSpineSeries.getItemCount() > SPINE_CHART_SIZE)
				{
					mCurrentSpineSeries.remove(0);
				}
				mCurrentSpineSeries.add(mSpineChartX++, ch1Value);
		        if (mSpineChartView != null) {
		            mSpineChartView.repaint();
		        }        

						
				Log.i(TAG,"ch1Value= " + ch1Value);
		
				break;
			}				
			case SPINEFunctionConstants.ZEPHYR: {
				Node source = data.getNode();
				Feature[] feats = ((FeatureData)data).getFeatures();
				Feature firsFeat = feats[0];
				
				byte sensor = firsFeat.getSensorCode();
				byte featCode = firsFeat.getFeatureCode();
				int batLevel = firsFeat.getCh1Value();
				int heartRate = firsFeat.getCh2Value();
				double respRate = firsFeat.getCh3Value() / 10;
				int skinTemp = firsFeat.getCh4Value() / 10;
				double skinTempF = (skinTemp * 9 / 5) + 32;				
				Log.i(TAG,"heartRate= " + heartRate + ", respRate= " + respRate + ", skinTemp= " + skinTempF);
				
				String text = deviceLog.getText().toString();
				text = heartRate + "\n" + text;
				deviceLog.setText(text);		
				if (mCurrentDeviceSeries.getItemCount() > SPINE_CHART_SIZE)
				{
					mCurrentDeviceSeries.remove(0);
				}
				mCurrentDeviceSeries.add(mSpineChartX++, heartRate);
		        if (mDeviceChartView != null) {
		            mDeviceChartView.repaint();
		        }        
				
				break;
			}
			
			case SPINEFunctionConstants.MINDSET: {
				Node source = data.getNode();
				
				MindsetData mData = (MindsetData) data;
//				if (mData.exeCode == 2)
//				{
//					Log.i(TAG, "poorSignalStrength= "  + mData.poorSignalStrength);
//					int b = mData.poorSignalStrength &  0xff;
//					String result = Integer.toHexString(b);					
//					deviceLog.setText(result);
//					
//				}
				if (mData.exeCode == 4)
				{
					Log.i(TAG, "attention= "  + mData.attention);
					if (mCurrentDeviceSeries.getItemCount() > SPINE_CHART_SIZE)
					{
						mCurrentDeviceSeries.remove(0);
					}
					mCurrentDeviceSeries.add(mDeviceChartX++, mData.attention);
			        if (mDeviceChartView != null) {
			            mDeviceChartView.repaint();
			        }   					
				}
				break;
			}			
				case SPINEFunctionConstants.ONE_SHOT:
					Log.i(TAG, "SPINEFunctionConstants.ONE_SHOT"  );
					break;
					
				case SPINEFunctionConstants.ALARM:
					Log.i(TAG, "SPINEFunctionConstants.ALARM"  );
					break;
			}
		}
	}


//	private class UpdateUITask extends AsyncTask<Integer, Void, String>{
//	     protected void onPostExecute(final String result) {
//				spineLog.setText(result);
//	     }
//
//		@Override
//		protected String doInBackground(final Integer... params) {
//
//			
//			String result = Integer.toString(params[0]);
//			return result;
//		}
//	 }
	private class UpdateUITask extends AsyncTask<Integer, Void, Integer>{
	     protected void onPostExecute(final Integer value) {

	    	 String result = Integer.toString(value);
	    	 spineLog.setText(result);
				if (mCurrentSpineSeries.getItemCount() > SPINE_CHART_SIZE)
				{
					mCurrentSpineSeries.remove(0);
				}
				mCurrentSpineSeries.add(mSpineChartX++, value);
		        if (mSpineChartView != null) {
		            mSpineChartView.repaint();
		        }        
	     }
		@Override
		protected Integer doInBackground(final Integer... params) {

			

			return params[0];
		}
	 }
	
	
	
	
	@Override
	public void discoveryCompleted(Vector activeNodes) {
		Log.i(TAG, "discovery completed" );	

//		// Since zepher is a static node we have to manually put it in the active node list
//		// Note that the sensor id 0xfff1 (-15) is a reserved id for this particular sensor
//		Node zepherNode = null;
//		zepherNode = new Node(new Address("" + -15));
//		activeNodes.add(zepherNode);
//		
//		Node mindsetNode = null;
//		mindsetNode = new Node(new Address("" + -14));
//		activeNodes.add(mindsetNode);
//		
	}

//	@Override
//	// This is only used when the message server sends data directly to the application.
//	// (It should only send via Spine path)
//	public void onZephyrDataReceived(ZephyrData bfmd) {
////		//int data = byteArrayToInt(new byte[] {bfmd.msgBytes[12], bfmd.msgBytes[13]});;  // Heart rate		
////		int data = byteArrayToInt(new byte[] {bfmd.msgBytes[16], bfmd.msgBytes[17]})/10;		// Skin temp
////		String text = deviceLog.getText().toString();
////		text = data + "\n" + text;
////		deviceLog.setText(text);		
////		
////		if (mCurrentDeviceSeries.getItemCount() > SPINE_CHART_SIZE)
////		{
////			mCurrentDeviceSeries.remove(0);
////		}
////		mCurrentDeviceSeries.add(mDeviceChartX++, data);
////        if (mDeviceChartView != null) {
////            mDeviceChartView.repaint();
////        }        
//	}
	public static int byteArrayToInt(byte[] bytes) {
		int val = 0;
		
		for(int i = 0; i < bytes.length; i++) {
			int n = (bytes[i] < 0 ? (int)bytes[i] + 256 : (int)bytes[i]) << (8 * i);
			val += n;
		}
		
		return val;
	}

	public void doBindService(ServiceConnection mConnection ) {
		this.mConnection = mConnection; 
		Log.i(TAG, "*****************binding **************************");

		try {
			Intent intent2 = new Intent("com.t2.biofeedback.IBioFeedbackService");
			AndroidSpineServerMainActivity.getInstance().bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
			Log.i(TAG, "*****************binding SUCCESS**************************");
			
			mIsBound = true;
		} catch (Exception e) {
			Log.i(TAG, "*****************binding FAIL**************************");
			Log.e(TAG, e.toString());
			
		}
	}	
	void doUnbindService() {
	    if (mIsBound) {
			Log.i(TAG, "*****************UN-binding **************************");
	    	
	        // If we have received the service, and hence registered with
	        // it, then now is the time to unregister.
	        if (mService != null) {
	            try {
	                Message msg = Message.obtain(null,MSG_UNREGISTER_CLIENT);
	    			Log.i(TAG, "*****************UN- binding SUCCESS**************************");
	    			// msg.replyTo = mMessenger; We don't care about reply to because we're shutting down
	                mService.send(msg);
	            } catch (RemoteException e) {
	                // There is nothing special we need to do if the service
	                // has crashed.
	            }
	        }

	        // Detach our existing connection.
	        AndroidSpineServerMainActivity.getInstance().unbindService(mConnection);
	        mIsBound = false;
	    }
	}		
	
	
}