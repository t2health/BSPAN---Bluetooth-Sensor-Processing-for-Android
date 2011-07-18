package com.t2.biomap;

import java.util.Vector;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BioView extends View
{
	private Paint   mTextPaint;	
	private Paint   mLinePaint;	
	private Paint   mUserPaint;	
	private float   mX;	
	private int mHeight = 0;
	private int mWidth = 0;
	private int mCx = 0;
	private int mCy = 0;
    private double BuildingAngleDegrees = 23;
	float left, top, right, bottom;
	boolean mDebug = true;
    
    float mCompass;
    
    Vector<BioLocation> mPeers;
    BioLocation mUser;
    BioLocation mTarget = new BioLocation();
    
	public BioView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(0x800000FF);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeWidth(5);
		
		mUserPaint = new Paint();
		mUserPaint.setAntiAlias(true);
		mUserPaint.setColor(Color.RED);
		mUserPaint.setStyle(Paint.Style.FILL);	
        
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(14);
        //  mTextPaint.setTypeface(Typeface.SERIF);		
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);    
        mTextPaint.setColor(Color.BLACK);
        
        mPeers =  new Vector <BioLocation>();
        mUser = new BioLocation("Scott", 110,300, 229);
	}
	
	public boolean isPositionUser(float lat, float lon)
	{
		if (lat < left || lat > right)
		{
			return false;
		}
		if (lon < top || lon > bottom)
		{
			return false;
		}
		return true;
		
	}	
	public void updateUserLocation(float lon, float lat)
	{
		int angle1;
		int angle2;
		int quadrant;
		mUser.mLat =  lon;
		mUser.mLon =  lat;
		Log.i("arnie", " *************Old user latlon: " + mUser.mLat + ", " + mUser.mLon);
		Log.i("arnie", " *************user latlon: " + lat + ", " + lon);
		
        for (BioLocation peer: mPeers )
        {
        	int x = (int) (peer.mLat - mUser.mLat - 5);
            // TOTO change (mUser.mName.length() * 3) to mCorrectionToCenter and put in class
//        	int y = (int) (peer.mLon - mUser.mLon + (mUser.mName.length() * 3));
        	int y = (int) (peer.mLon - mUser.mLon);

        	if (y >= 0)
        		quadrant = 1;
        	else
        		quadrant = 4;
        		
        	if (y >= 0)
        	{
        		angle1 = (int) Math.toDegrees(Math.atan2(y,x));
	        	if (angle1 >= 0)
	        	{
	            	angle2 = 90 - (int) BuildingAngleDegrees + angle1;
	        	}
	        	else
	        	{
	            	angle2 = 90 - (int) BuildingAngleDegrees - angle1;
	        	}
        	}
        	else
        	{
        		angle1 = (int) Math.toDegrees(Math.atan2(y,x));
	        	if (angle1 < 0)
	        	{
	            	angle2 = 90 - (int) BuildingAngleDegrees + angle1;
	        	}
	        	else
	        	{
	            	angle2 = 90 - (int) BuildingAngleDegrees - angle1;
	        	}        		
	        		
        	}
        	
        	if (angle2 < 0)
        		angle2 += 360;
        		
        	peer.mAngle = angle2;
    		Log.i("arnie", " peer " + peer.mName + ": " + angle1 + ",  " + angle2);
        }
        
		left = (float) lon - 20;
		right = (float) lon + 20;
		top = (float) lat - 20;
		bottom = (float) lat + 20;        
        
	}

	/**
	 * Sets up mTarget active depending on what direction the compass is pointing.
	 * 
	 * Iterates through all of the peers and finds the peer(target) that most closely matches
	 * the current compass angle. Makes that target active if the angle is within 10 degrees.
	 * 
	 * @param compass
	 * @return
	 */
	public BioLocation compassChanged(float compass)
	{
		int delta = 360;
		int deltaP;
		mCompass = compass;
		mTarget.mActive = false;
		
        for (BioLocation peer: mPeers )
        {
        	deltaP = (int) Math.abs(peer.mAngle - mCompass); 
        	
        	if (deltaP < delta)
        	{
        		delta = deltaP;
//        		Log.i("fred", "delta= " + delta + ", Target = " + mTarget.name);
        		
        		// Onnly set if within 5 degrees
        		if (delta <= 10 || delta > 350)
        			mTarget.set(peer);
        		else
        			mTarget.mActive = false;
        			
//        		break;
        	}
        }
	        			
//		if (mTarget.active)
			invalidate();
		return mTarget;
		
	}
	
	@Override
	public void draw(Canvas canvas)
	{
       // canvas.drawColor(Color.WHITE);
        float x = mX;
        float y = 0;        
		Paint xPaint = new Paint();
		xPaint.setAntiAlias(true);
		xPaint.setColor(Color.BLACK);
		xPaint.setStyle(Paint.Style.STROKE);
        float dx, dy;
        
        Paint p = mLinePaint;        
        p.setColor(Color.RED);
        
        if (mTarget.mActive)
        	canvas.drawLine(mUser.mLat,mUser.mLon,mTarget.mLat, mTarget.mLon - 5,p);
//    	canvas.drawLine(mUser.mLat,mUser.mLon,mTarget.mLat + (mUser.mName.length() * 3), mTarget.mLon - 5,p);
        // TOTO change (mUser.mName.length() * 3) to mCorrectionToCenter and put in class
        
        // Draw User
        canvas.drawCircle(mUser.mLat, mUser.mLon, 10, mUserPaint);
    	if (mDebug)
    	{
    		canvas.drawText("" + mCompass, mUser.mLat + 20, mUser.mLon, mTextPaint);
    	}
        
    	dx = (float) (50 * Math.cos(Math.toRadians(mCompass - 90 + BuildingAngleDegrees)));
    	dy = (float) (50 * Math.sin(Math.toRadians(mCompass - 90 + BuildingAngleDegrees)));
    	canvas.drawLine(mUser.mLat,mUser.mLon,mUser.mLat + dx,mUser.mLon+dy,xPaint);        	

        
        // Draw Peers
        for (BioLocation peer: mPeers )
        {
        	
        	if (peer.mName.equalsIgnoreCase(mTarget.mName))
        	{
        		mTextPaint.setColor(Color.RED);
        	}
        	else
        	{
        		mTextPaint.setColor(Color.BLACK);
        	}
        	
        	if (mDebug)
        	{
            	canvas.drawText(peer.mName + ": " + peer.mAngle, peer.mLat, peer.mLon, mTextPaint);
        	}
        	else
        	{
            	canvas.drawText(peer.mName, peer.mLat, peer.mLon, mTextPaint);
        	}
        	
        	
        	if (mDebug)
        	{
                // Debug - draw lines in the directions of all targets
        		
        		// Make the line for Bob blue
        		if (peer.mName.equalsIgnoreCase("bob"))
        		{
        			xPaint.setColor(Color.BLUE);
        		}
        		else
        		{
        			xPaint.setColor(Color.BLACK);
        			
        		}
        		
	        	// Draw a ray to the angle to each peer
	        	dx = (float) (100 * Math.cos(Math.toRadians(peer.mAngle - 90 + BuildingAngleDegrees)));
	        	dy = (float) (100 * Math.sin(Math.toRadians(peer.mAngle - 90 + BuildingAngleDegrees)));
	        	canvas.drawLine(mUser.mLat,mUser.mLon,mUser.mLat + dx,mUser.mLon+dy,xPaint);        	
        	}        	
        	
        	// Draw line to all for now
            //canvas.drawLine(mUser.lat,mUser.lon,peer.lat, peer.lon,p);
        	
        }
		super.draw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
        mX = w * 0.5f;  // remember the center of the screen		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
		mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
		mCx = mWidth/2;
		mCy = mHeight/2;
		
//		mUser.mLat = mCx;
//		mUser.mLon = mCy;
		
		setMeasuredDimension(mWidth,mHeight);
//		updateUserLocation(mCx, mCy);
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	void setPeers(Vector<BioLocation> peers)
	{
		mPeers = peers;
	}

}
