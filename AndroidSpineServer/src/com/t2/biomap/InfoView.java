package com.t2.biomap;

import java.util.StringTokenizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class InfoView extends View {
    private static final String TAG = "BioMap";
	
	

    public String mOwner = "";
	
	private Paint   mTextPaint;	
	private Paint   mLinePaint;	
	private Paint   mUserPaint;	
	private float   mX;	
	public int mHeight = 80;
	public int mWidth = 120;
	private int mCx = 0;
	private int mCy = 0;

	
	float left, top, right, bottom;
	
	Bitmap mBitmap = null;	
	
	
    private double BuildingAngleDegrees = 23;
    public BioLocation mTarget = new BioLocation();
    
    private ShapeDrawable mDrawable;
    String mLine1 = "one";
    String mLine2 = "two";
    String mLine3 = "three";
    static final int MAX_LINES = 4;
    static final int LINE_HEIGHT = 15;
    int mNumLines = 0;
    
    String[] lines = new String[MAX_LINES];
	

	/**
	 * This constructor is used for adding dynamic views
	 * @param context
	 */
	public InfoView(Context context) {
		super(context);
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(0xf0606000);
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
        
        float[] outerR = new float[] { 5,5,5,5,5,5,5,5};
        mDrawable = new ShapeDrawable(new RoundRectShape(outerR, null,null));       	
        
        
        left = 10;
        top = 20;
	}
	
	/**
	 * 	 * This constructor is used for adding view via static layout

	 * @param context
	 * @param attrs
	 */
	public InfoView (Context context, AttributeSet attrs) {
		super (context, attrs);
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(0xf0606000);
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
        
        float[] outerR = new float[] { 5,5,5,5,5,5,5,5};
        mDrawable = new ShapeDrawable(new RoundRectShape(outerR, null,null));       	
        
        
        left = 10;
        top = 20;
	}
	public InfoView (Context context, AttributeSet attrs, int style) {
		super (context, attrs, style);
	}

	@Override
	public void draw(Canvas canvas) {
		
//		if (mBitmap != null)
//		{
//			canvas.drawBitmap(mBitmap, left + 5, top + 15, mLinePaint);
//		}
//		else
//		{
	        mDrawable.getPaint().setColor(0xD0ff0000);
	        mDrawable.setBounds((int)left, (int)top, (int)right, (int)bottom);
	        
	        mDrawable.draw(canvas);

	        float x = left + 5;
	        float y = top + 15;
	        
	        for (int i = 0; i < mNumLines; i++)
	        {
				canvas.drawText(lines[i], x, y + (LINE_HEIGHT * i), mTextPaint);
	        }
	        
//			canvas.drawText(mLine1, left + 5, top + 15, mTextPaint);
//			canvas.drawText(mLine2, left + 5, top + 30, mTextPaint);
//			canvas.drawText(mLine3, left + 5, top + 45, mTextPaint);
			
//		}
		
		super.draw(canvas);
		
	}	

	public boolean isPositionMe(float lon, float lat)
	{
		if (lon < left || lon > right)
		{
			return false;
		}
		if (lat < top || lat > bottom)
		{
			return false;
		}
		return true;
		
	}
	public void updateTargetLocation(BioLocation target)
	{
		mTarget.mLat = (int) target.mLon;
		mTarget.mLon = (int) target.mLat;
		mTarget.mEnabled = target.mEnabled;
		mTarget.mSensors = target.mSensors;
		mTarget.mName = target.mName;
		
		if (target.mActive)
		{
			this.setVisibility(VISIBLE);
			left = mTarget.mLon - mWidth/2 - 40;
			right = mTarget.mLon + mWidth/2 - 40;
			top = mTarget.mLat + 5;
			bottom = mTarget.mLat + mHeight;
		}
		else
		{
			this.setVisibility(GONE);
		}
		invalidate();
	}

	public void bumpXY(float X, float Y)
	{
		Log.i(TAG, "Bump from " + mTarget.mLat + ", " + mTarget.mLon );
//		mTarget.mLat += mHeight;
		mTarget.mLat += Y;
		mTarget.mLon += X;
		Log.i(TAG, "Bump to " + mTarget.mLat + ", " + mTarget.mLon );
		
			this.setVisibility(VISIBLE);
			left = mTarget.mLon - mWidth/2 - 40;
			right = mTarget.mLon + mWidth/2 - 40;
			top = mTarget.mLat + 5;
			bottom = mTarget.mLat + mHeight;
		invalidate();
	}
	
	
	public void setText(String line)
	{
		 StringTokenizer st = new StringTokenizer(line, "\n");
		 
		 int i = 0;
		 while (st.hasMoreTokens()) {
			 lines[i++] = st.nextToken();
			 if (i >= MAX_LINES)
				 break;
		 }	
		 mNumLines = i;
		 
	}

	
	public void setText(String line1, String line2,String line3)
		{
		mLine1 = line1;
	    mLine2 = line2;
	    mLine3 = line3;
	}	

	public void setDrawable(ShapeDrawable drawable)
	{
		mDrawable = drawable;
	}
	
	public void setBitmap(Bitmap drawingCache) {
		this.mBitmap = drawingCache;
		
	}
	
}
