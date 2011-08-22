package com.t2.compassionMeditation;

import com.t2.R;
import com.t2.biomap.SharedPref;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

public class InstructionsActivity extends Activity implements View.OnTouchListener {
	private static final String mActivityVersion = "1.0";
	CheckBox mShowInstructionsCheckbox;	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
        setContentView(R.layout.instructions_activity_layout);        
		mShowInstructionsCheckbox = (CheckBox) findViewById(R.id.checkBox1);
		
		boolean instructionsOnStart = SharedPref.getBoolean(this, Constants.PREF_INSTRUCTIONS_ON_START, Constants.PREF_INSTRUCTIONS_ON_START_DEFAULT);
		mShowInstructionsCheckbox.setChecked(instructionsOnStart);

		final RelativeLayout parent = (RelativeLayout) findViewById(R.id.instructionsLayout	);
		parent.setOnTouchListener (this);
        
	}
	@Override
	protected void onDestroy() {
		boolean isChecked = mShowInstructionsCheckbox.isChecked();
		SharedPref.putBoolean(this, Constants.PREF_INSTRUCTIONS_ON_START, isChecked );
		
		Intent resultIntent;
		resultIntent = new Intent();
		resultIntent.putExtra(Constants.INSTRUCTIONS_USER_ACTIVITY_RESULT, "");
		setResult(RESULT_OK, resultIntent);
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
