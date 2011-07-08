package com.t2.biomap;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * This subclass of ImageView keeps track of the view's position relative to where it was initially placed.
 * Every time it draws itself on a canvas it draws its image displaced by an x and y amount.
 * Methods setPos and changePos are provided to change the position of the view.
 * 
 * <p> Method onDraw has been redefined here in this class. That is where the translation of position occurs.
 *
 */

public class T2ImageView extends ImageView 
{

private float mPosX;
private float mPosY;
private float mScaleFactor = 1.f;

/**
 * Return a new image view.
 */

public T2ImageView (Context context) {
	super (context);
}
public T2ImageView (Context context, AttributeSet attrs) {
	super (context, attrs);
}
public T2ImageView (Context context, AttributeSet attrs, int style) {
	super (context, attrs, style);
}

/**
 * Draw the image on the canvas, after translating the canvas position by mPosX and mPosY.
 * 
 * @param canvas Canvas
 * @return void
 */

public void onDraw(Canvas canvas) 
{
//    canvas.translate(50,50);
    canvas.translate(mPosX, mPosY);
    super.onDraw(canvas);
}

/**
 * Take the current x and y values used to translate the canvas and change them by dx and dy.
 * Also calls invalidate so the view will be redrawn.
 * 
 * @param dx float change in x position
 * @param dy float change in y
 * @return void
 */

public void changePos (float dx, float dy)
{
    mPosX += dx;
    mPosY += dy;
    this.invalidate ();
} // end changePos

/**
 * Sets the x and y values used to translate the canvas.
 * Also calls invalidate so the view will be redrawn.
 * 
 * @param x float
 * @param y float
 * @return void
 */

public void setPos (float x, float y)
{
    mPosX = x;
    mPosY = y;
    this.invalidate ();
} // end setPos


} // end class
