package com.t2.compassionMeditation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.ArrayAdapter;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;
import com.t2.biomap.SharedPref;

public class UserModeActivity extends Activity {
	private static final String TAG = "BFDemo";
	private static final String mActivityVersion = "1.0";
	
	CheckBox mSaveUserMode;	
    private Button mSingleUserButton;
    private Button mProviderButton;
	int mUserMode;
	
	
	public void onButtonClick(View v)
	{
		 final int id = v.getId();
	    switch (id) {

	    case R.id.buttonSingleUser:
	    	mUserMode = Constants.PREF_USER_MODE_SINGLE_USER;
	//		mSingleUserButton.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
			mProviderButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
	        break;
	    
	    case R.id.buttonProvider:
	    	mUserMode = Constants.PREF_USER_MODE_PROVIDER;
		//	mSingleUserButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
			mProviderButton.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
	        break;
	    
	    case R.id.buttonEndUserMode:
	    	finish();
	        break;
	    
	    
	    }
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
        mUserMode = SharedPref.getInt(this, 
        		com.t2.compassionMeditation.Constants.PREF_USER_MODE, 
        		com.t2.compassionMeditation.Constants.PREF_USER_MODE_DEFAULT);  		
		
		this.setContentView(R.layout.user_mode_layout);
		mSaveUserMode = (CheckBox) findViewById(R.id.checkBoxSaveUserMode);
		
		mSingleUserButton = (Button) findViewById(R.id.buttonSingleUser);		
		mProviderButton = (Button) findViewById(R.id.buttonProvider);		
		
		

	}

	
	@Override
	protected void onDestroy() {

		if (mSaveUserMode.isChecked()) {
			SharedPref.putInt(this, com.t2.compassionMeditation.Constants.PREF_USER_MODE, mUserMode );			
		}
		else {
			SharedPref.putInt(this, com.t2.compassionMeditation.Constants.PREF_USER_MODE, com.t2.compassionMeditation.Constants.PREF_USER_MODE_DEFAULT );			
		}
		super.onDestroy();
	}
	

}
