package com.t2.compassionMeditation;

import org.achartengine.chart.AbstractChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class OffsetGraphicalChartView extends View {
	  private AbstractChart mChart;
	  private Rect mRect = new Rect();
	  /** The paint to be used when drawing the chart. */
	  private Paint mPaint = new Paint();	  
	
	public OffsetGraphicalChartView(Context context, AbstractChart chart) {
	    super(context);
	    mChart = chart;
    }

	@Override
	  protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    canvas.getClipBounds(mRect);
	    int top = mRect.top;
	    int left = mRect.left-10;
	    int width = mRect.width();
	    int height = mRect.height();
	    mChart.draw(canvas, left, top, width, height, mPaint);
	  }
}
