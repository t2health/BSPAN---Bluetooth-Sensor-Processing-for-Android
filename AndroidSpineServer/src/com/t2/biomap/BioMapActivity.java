package com.t2.biomap;

import java.util.Iterator;
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
import android.graphics.PixelFormat;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.t2.Util;

@SuppressWarnings("deprecation")
public class BioMapActivity extends Activity 
		implements View.OnTouchListener, 
		View.OnLongClickListener,
		SPINEListener {
    private static final String TAG = "BioMap";
	
	private SensorManager mSensorManager;
    private float[] mValues;
    private BioView mBioView; 
    private BioLocation mTarget;
    private String mPrevioiusTargetName;
    private Button mBtnView;    
	static final int test_1 = 1;

    
    private int status;
    private ImageView image;
	private final static int START_DRAGGING = 0;
	private final static int STOP_DRAGGING = 1;
	private LayoutParams params;
	
	float mCompass = 0;

	int mSignalStrength = 0;
	int mAttention = 0;
	int mMeditation = 0;
	int mHeartRate = 0;
	private FrameLayout mLayout;

	
	
	float touchX, touchY;
	Vector<BioLocation> currentUsers;

	private Vector<InfoView> mInfoViews;
	BioMapActivity me;
	private static Object mInfoViewsLock = new Object();
	
	
	
	
	/**
     * The Spine manager contains the bulk of the Spine server. 
     */
    private static SPINEManager manager;
	
	
	
    @Override
	protected void onDestroy() {
		super.onDestroy();
		mInfoViews.clear();
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biomap_layout);
        me = this;
        
        mInfoViews = new Vector<InfoView>();      

        View v1 = findViewById (R.id.staff); 
        v1.setOnTouchListener (this);
        v1.setOnLongClickListener(this);
        mBioView = (BioView)v1;
        
        currentUsers = Util.setupUsers();        
        mBioView.setPeers(currentUsers);

        
        mLayout = (FrameLayout) findViewById(R.id.frameLayout1);

        ImageView image = (ImageView) findViewById(R.id.imageView2);
        image.setImageResource(R.drawable.biomobile);        

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
        	
        	synchronized(mInfoViewsLock)
        	{
	//        	// Get rid of "chatter" by only looking for changes ? delta
	//        	if (Math.abs(values[0] - mCompass) < 1)
	//        		return;
	        	mCompass = values[0];     	
	        	
	            mValues = values;
	            
	            
	            // If a valid target has been established
	            // mTarget will be active, otherwise it will be inactive
	            if (mBioView != null) {
	            	mTarget = mBioView.compassChanged(values[0]);
	            }
	            
	            if (mTarget.mActive)
	            {
	            	if (mPrevioiusTargetName!= null && (mPrevioiusTargetName == mTarget.mName))
	            	{
	            		return;
	            	}
	            	mPrevioiusTargetName = mTarget.mName;
	                Log.i(TAG, "New target, name = " + mTarget.mName);

	            	Iterator<InfoView> iterator = mInfoViews.iterator();
	    			while (iterator.hasNext()) {
	    				InfoView v = iterator.next();
	    				if (v.mTarget.mToggled == false)
	    				{
	    					mLayout.removeView(v);
		    				iterator.remove();
	    				}
	    			}	            	
	            	
	            	boolean found = false;
	    			for (InfoView v: mInfoViews)
	    			{
	    				if (mTarget.mName.equalsIgnoreCase(v.mTarget.mName))
	    					found = true;
	    			}
	            	if (found == false)
	            	{
	    	    		InfoView infoView1 = new InfoView(me);
	    	    		mLayout.addView(infoView1, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));		
	    	        	
	    	    		// Since we know we're on the info view we know that the target location for this valid
	    	    		// It has stuff like name and type of sensors
	    	    		infoView1.updateTargetLocation(mTarget);	    		
	    	    		mInfoViews.add(infoView1);
	    	    		
	            	}
	            }
	            else
	            {
	            	mPrevioiusTargetName = null;
	            	Iterator<InfoView> iterator = mInfoViews.iterator();
	    			while (iterator.hasNext()) {
	    				InfoView v = iterator.next();
	    				if (v.mTarget.mToggled == false)
	    				{
	    					mLayout.removeView(v);
		    				iterator.remove();
	    				}
	    			}	            	
	            	
	            }
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
		
		touchX = event.getX();
		touchY = event.getY();
		
		if (v == mBioView)
		{
//            Log.i(TAG, "X = " + event.getX() + " , Y = " + event.getY());
			
		}

		int action = event.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
        	if (mBioView.isPositionUser(event.getX(), event.getY()))
        	{
				status = START_DRAGGING;
        		mBioView.updateUserLocation(event.getX(), event.getY());
        		mBioView.invalidate();
	        	return true;
        	}
        	else
        	{
        		// Now check to see if we've touched any info views
            	Iterator<InfoView> iterator = mInfoViews.iterator();
    			while (iterator.hasNext()) 
    			{
    				InfoView v1 = iterator.next();
    				if (v1.isPositionMe(event.getX(), event.getY()))
    				{
        				if (v1.mTarget.mToggled == false)
        				{
        					v1.mTarget.mToggled = true;
        				}
        				else
        				{
        					v1.mTarget.mToggled = false;
        				}
    				}
    				
    			}        		
        		
        		return false;
        	}

		case MotionEvent.ACTION_UP:
			status = STOP_DRAGGING;			
			break;
	
		case MotionEvent.ACTION_MOVE:

			if (status == START_DRAGGING)
			{
	        	mBioView.updateUserLocation(event.getX(), event.getY());
	        	mBioView.invalidate();		        	
			}
			break;
	
		}
		
		return false;
	}

	@Override
	public boolean onLongClick(View view) {
		
        Log.d(TAG, "***********************************************************************");
		
		// Now check to see if we've touched any info views
    	Iterator<InfoView> iterator = mInfoViews.iterator();
		while (iterator.hasNext()) 
		{
			InfoView v1 = iterator.next();
			if (v1.isPositionMe(touchX, touchY))
			{
				Intent i = new Intent(this, AndroidSpineServerMainActivity.class);
				Bundle bundle = new Bundle();
	
				//Add the parameters to bundle as 
				bundle.putString("TARGET_NAME",mTarget.mName);
	
				//Add this bundle to the intent
				i.putExtras(bundle);				
				
				
				this.startActivity(i);
	        	return false;
			}
			
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
				

//		        Log.i(TAG,"ch1Value= " + ch1Value);
				break;
				
			} // End case SPINEFunctionConstants.FEATURE:
			
			case SPINEFunctionConstants.MINDSET: {
				Node source = data.getNode();
				
				MindsetData mData = (MindsetData) data;
				if (mData.exeCode == Constants.EXECODE_MEDITATION)
				{
					mMeditation = mData.attention;				
//					Log.i(TAG,"Meditation = " + mData.attention);
				}
				if (mData.exeCode == Constants.EXECODE_ATTENTION)
				{
					mAttention = mData.attention;				
//					Log.i(TAG,"Meditation = " + mData.attention);
				}
				if (mData.exeCode == Constants.EXECODE_POOR_SIG_QUALITY)
				{
					mSignalStrength = mData.poorSignalStrength;				
//					Log.i(TAG,"Meditation = " + mData.attention);
				}
				break;
				
			}			
			
			
			} // End switch (data.getFunctionCode())
			
			
			if (mTarget == null)
				return;
			// Now update the info views (if any)
			for (InfoView v: mInfoViews)
			{
				
				int[] test = {1,2};
				int tlen = test.length;
				int alen = v.mTarget.mSensors.length;
				
				String statusLine = "";
				for (int i = 0; i < v.mTarget.mSensors.length; i++)
				{
					switch (v.mTarget.mSensors[i])
					{
					case Constants.DATA_SIGNAL_STRENGTH:
						statusLine += "Connection = " + mSignalStrength + "\n";
						break;
					case Constants.DATA_TYPE_ATTENTION:
						statusLine += "Attention = " + mAttention + "\n";
						break;
					case Constants.DATA_TYPE_MEDITATION:
						statusLine += "Meditation = " + mMeditation + "\n";
						break;
					case Constants.DATA_TYPE_HEARTRATE:
						statusLine += "Heart Rate = " + mHeartRate + "\n";
						break;
					}
					
				}
				v.setText(statusLine);
				
			}
			
//			if (mInfoView != null && mTarget != null) {
//	        	
//	        	if (mTarget.mName.equalsIgnoreCase("scott"))
//	        	{
//		        	mInfoView.setText("Heartrate: " + mHeartRate, "", "");
//	        		
//	        	}
//	        	else
//	        	{
//		        	mInfoView.setText("Meditation: " + mMeditation, "", "");
//	        		
//	        	}
//	        	mInfoView.invalidate();
//	        }

			
			
			
		} // end if (data != null)
		
	}

	@Override
	public void discoveryCompleted(Vector activeNodes) {
        Log.d(TAG, "discoveryCompleted");
		
	}

    
  
}