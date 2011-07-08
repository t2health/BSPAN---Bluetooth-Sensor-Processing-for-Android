package com.t2.biomap;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class T2TextView extends TextView {
	
	private float mPosX;
	private float mPosY;
	private float mScaleFactor = 1.f;
	private int mHeight = 0;
	private int mWidth = 0;
	private int mCx = 0;
	private int mCy = 0;
	
	
	
	public T2TextView (Context context) {
		super (context);
	}
	public T2TextView (Context context, AttributeSet attrs) {
		super (context, attrs);
	}
	public T2TextView (Context context, AttributeSet attrs, int style) {
		super (context, attrs, style);
	}	

	public void onDraw(Canvas canvas) 
	{
		 //setTranslationX(100);
	//	setPadding(100,100,100,100);
        int w = canvas.getWidth();
        int h = canvas.getHeight();		
	    canvas.translate(20,20);
//	    canvas.translate(mPosX, mPosY);
	    super.onDraw(canvas);
	}	
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//	{
//		mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
//		mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
//		mCx = mWidth/2;
//		mCy = mHeight/2;
//		
//
//		setMeasuredDimension(mWidth,mHeight);
//		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//	}	
	
}
