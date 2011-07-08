package com.t2.biomap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;

public class InfoView extends View {
	
	private Paint   mTextPaint;	
	private Paint   mLinePaint;	
	private Paint   mUserPaint;	
	private float   mX;	
	private int mHeight = 0;
	private int mWidth = 0;
	private int mCx = 0;
	private int mCy = 0;
	
	float left, top, right, bottom;
	Bitmap mBitmap = null;	
	
	
    private double BuildingAngleDegrees = 23;
    private BioLocation mTarget = new BioLocation();
    
    private ShapeDrawable mDrawable;
    String mLine1 = "one";
    String mLine2 = "two";
    String mLine3 = "three";	
	

	public InfoView(Context context) {
		super(context);
	}
	
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
			canvas.drawText(mLine1, left + 5, top + 15, mTextPaint);
			canvas.drawText(mLine2, left + 5, top + 30, mTextPaint);
			canvas.drawText(mLine3, left + 5, top + 45, mTextPaint);
			
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
		mTarget.lat = (int) target.lon;
		mTarget.lon = (int) target.lat;

		
		if (target.active)
		{
			this.setVisibility(VISIBLE);
			left = mTarget.lon - 60 - 40;
			right = mTarget.lon + 60 - 40;
			top = mTarget.lat + 5;
			bottom = mTarget.lat + 60;
		}
		else
		{
			this.setVisibility(GONE);
		}
		invalidate();
		
		
		
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
