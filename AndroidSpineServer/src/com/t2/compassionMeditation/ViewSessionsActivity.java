package com.t2.compassionMeditation;

import com.t2.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class ViewSessionsActivity extends Activity implements View.OnTouchListener {
	private static final String TAG = "ViewSessionsActivity";
	private static final String mActivityVersion = "1.0";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
        setContentView(R.layout.view_sessions_layout);        

//		final RelativeLayout parent = (RelativeLayout) findViewById(R.id.instructionsLayout	);
//		parent.setOnTouchListener (this);
        
	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		
		
	}
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		finish();
		return false;
	}
	

}
