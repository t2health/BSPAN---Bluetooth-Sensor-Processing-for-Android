package com.t2.biomap;

import java.util.Vector;

import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.datamodel.Address;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.FeatureData;
import spine.datamodel.MindsetData;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
//import android.widget.FrameLayout.LayoutParams;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.AndroidSpineServerMainActivity;
import com.t2.Constants;
import com.t2.R;

@SuppressWarnings("deprecation")
public class BioMapActivity extends Activity implements View.OnTouchListener, SPINEListener {
    private static final String TAG = "BioMap";
	
	private SensorManager mSensorManager;
    private float[] mValues;
    private BioView mBioView; 
    private InfoView mInfoView; 
    private BioLocation mTarget;
    private Button mBtnView;    
    private FrameLayout mLayout;
    
    
    private int status;
    private ImageView image;
	private final static int START_DRAGGING = 0;
	private final static int STOP_DRAGGING = 1;
	private LayoutParams params;
	
	float mCompass = 0;
	int mMeditation = 0;
	int mHeartRate = 0;
	
	
	
	/**
     * The Spine manager contains the bulk of the Spine server. 
     */
    private static SPINEManager manager;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biomap_layout);

        View v1 = findViewById (R.id.staff); 
        v1.setOnTouchListener (this);
        mBioView = (BioView)v1;

        View v2 = findViewById (R.id.info); 
        mInfoView = (InfoView)v2;
        
        mLayout = (FrameLayout) findViewById(R.id.frameLayout1);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);    
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
        
		// Initialize SPINE by passing the fileName with the configuration properties
		try {
			manager = SPINEFactory.createSPINEManager("SPINETestApp.properties", resources);
		} catch (InstantiationException e) {
			Log.e(TAG, "Exception creating SPINE manager: " + e.toString());
			e.printStackTrace();
		}        
		        
		Node mindsetNode = null;
		mindsetNode = new Node(new Address("" + Constants.RESERVED_ADDRESS_MINDSET));
		manager.getActiveNodes().add(mindsetNode);
		
		manager.addListener(this);	   
		manager.discoveryWsn();
		
        
		
		
    }
    
	private final SensorListener mListener = new SensorListener() {
        
        public void onSensorChanged(int sensor, float[] values) {
        	
        	
//        	// Get rid of "chatter" by only looking for changes ? delta
//        	if (Math.abs(values[0] - mCompass) < 1)
//        		return;
        	mCompass = values[0];     	
        	
        	//            if (Config.LOGD) Log.d(TAG, "sensorChanged (" + values[0] + ", " + values[1] + ", " + values[2] + ")");
//            Log.d(TAG, "sensorChanged (" + values[0] + ", " + values[1] + ", " + values[2] + ")");
            mValues = values;
            
            
            if (mBioView != null) {
            	mTarget = mBioView.compassChanged(values[0]);
            }
            
            if (mInfoView != null) {
//            	mInfoView.setText("Heartrate: 86", "SkinTemp: 98.6", "");
            	mInfoView.updateTargetLocation(mTarget);
            }
        }

        public void onAccuracyChanged(int sensor, int accuracy) {
        }
    };    
    
    @Override
    protected void onResume()
    {
        if (Config.LOGD) Log.d(TAG, "onResume");
        super.onResume();
        mSensorManager.registerListener(mListener, 
        		SensorManager.SENSOR_ORIENTATION,
        		SensorManager.SENSOR_DELAY_GAME);
    }
    
    @Override
    protected void onStop()
    {
        if (Config.LOGD) Log.d(TAG, "onStop");
        mSensorManager.unregisterListener(mListener);
        super.onStop();
    }
    
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		
		if (v == mInfoView)
		{
            Log.d(TAG, "foun INFO");
			
		}
		if (v == mBioView)
		{
            Log.d(TAG, "foun Bio");
			
		}

		int action = event.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			status = START_DRAGGING;
	        if (mInfoView.isPositionMe(event.getX(), event.getY()))
	        {
				Intent i = new Intent(this, AndroidSpineServerMainActivity.class);
				this.startActivity(i);	        	
	        	return false;
	        }
	        else
	        {
	        	if (mBioView.isPositionUser(event.getX(), event.getY()))
	        	{
	        		mBioView.updateUserLocation(event.getX(), event.getY());
	        		mBioView.invalidate();
		        	return true;
	        	}
	        	else
	        	{
	        		return false;
	        	}
	        }
	
		case MotionEvent.ACTION_UP:
			status = STOP_DRAGGING;			
			break;
	
		case MotionEvent.ACTION_MOVE:

        	mBioView.updateUserLocation(event.getX(), event.getY());
        	mBioView.invalidate();		        	
			break;
	
		}
		
		return false;
	}

	@Override
	public void newNodeDiscovered(Node newNode) {
        Log.d(TAG, "newNodeDiscovered");
		
	}

	@Override
	public void received(ServiceMessage msg) {
        Log.d(TAG, "received msg ");
		
	}

	@Override
	public void received(Data data) {
//        Log.d(TAG, "received data");
        
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
				mHeartRate = ch1Value;				
				

		        Log.i(TAG,"ch1Value= " + ch1Value);
				break;
				
			} // End case SPINEFunctionConstants.FEATURE:
			
			case SPINEFunctionConstants.MINDSET: {
				Node source = data.getNode();
				
				MindsetData mData = (MindsetData) data;
				if (mData.exeCode == 4)
				{
					mMeditation = mData.attention;				
		        	mInfoView.setText("Heartrate: " + mHeartRate, "Meditation: " + mMeditation, "");
					
					Log.i(TAG,"Meditation = " + mData.attention);
				}
				break;
				
			}			
			
			
			} // End switch (data.getFunctionCode())
			
	        if (mInfoView != null) {
	        	
	        	if (mTarget.name.equalsIgnoreCase("scott"))
	        	{
		        	mInfoView.setText("Heartrate: " + mHeartRate, "", "");
	        		
	        	}
	        	else
	        	{
		        	mInfoView.setText("Meditation: " + mMeditation, "", "");
	        		
	        	}
	        	
//	        	mInfoView.setText("Heartrate: " + Integer.toString(ch1Value), "SkinTemp: 98.6", "");
	        	mInfoView.invalidate();
	        }

			
			
			
		} // end if (data != null)
		
	}

	@Override
	public void discoveryCompleted(Vector activeNodes) {
        Log.d(TAG, "discoveryCompleted");
		
	}
    
  
}