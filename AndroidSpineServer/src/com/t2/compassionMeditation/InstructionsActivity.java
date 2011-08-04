package com.t2.compassionMeditation;

import com.t2.R;
import com.t2.biomap.SharedPref;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Spinner;

public class InstructionsActivity extends Activity {
	private static final String mActivityVersion = "1.0";
	CheckBox mShowInstructionsCheckbox;	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
        setContentView(R.layout.instructions);        
		mShowInstructionsCheckbox = (CheckBox) findViewById(R.id.checkBox1);
		
		boolean instructionsOnStart = SharedPref.getBoolean(this, Constants.PREF_INSTRUCTIONS_ON_START, Constants.PREF_INSTRUCTIONS_ON_START_DEFAULT);
		mShowInstructionsCheckbox.setChecked(instructionsOnStart);
        
        
        
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		boolean isChecked = mShowInstructionsCheckbox.isChecked();
		SharedPref.putBoolean(this, Constants.PREF_INSTRUCTIONS_ON_START, isChecked );
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	

}
