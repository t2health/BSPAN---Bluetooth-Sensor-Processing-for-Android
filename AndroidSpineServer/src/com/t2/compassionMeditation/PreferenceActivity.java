package com.t2.compassionMeditation;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;
import com.t2.biomap.SharedPref;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class PreferenceActivity extends Activity {

	EditText mSessionLengthEdit;
	EditText mAlphaGainEdit;
	CheckBox mShowInstructionsCheckbox;
	CheckBox mAllowMultipleUsersCheckbox;
	Spinner mBandOfInterestSpinner;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // This needs to happen BEFORE setContentView
        setContentView(R.layout.preferences);
		super.onCreate(savedInstanceState);
		
		try {
			int sessionLengthSecs = SharedPref.getInt(this, Constants.PREF_SESSION_LENGTH, 	Constants.PREF_SESSION_LENGTH_DEFAULT);
			int sessionLengthMins = sessionLengthSecs / 60;
			float alphaGain = SharedPref.getFloat(this, Constants.PREF_ALPHA_GAIN, 	Constants.PREF_ALPHA_GAIN_DEFAULT);
			boolean instructionsOnStart = SharedPref.getBoolean(this, Constants.PREF_INSTRUCTIONS_ON_START, Constants.PREF_INSTRUCTIONS_ON_START_DEFAULT);
			boolean allowMultipleUsers = SharedPref.getBoolean(this, Constants.PREF_MULTIPLE_USERS, Constants.PREF_MULTIPLE_USERS_DEFAULT);
			int bandOfInterest = SharedPref.getInt(this, Constants.PREF_BAND_OF_INTEREST , 	Constants.PREF_BAND_OF_INTEREST_DEFAULT);
			
			
			mSessionLengthEdit = (EditText) findViewById(R.id.editTextSessionLength);
			mAlphaGainEdit = (EditText) findViewById(R.id.editTextAlphaGain);
			mShowInstructionsCheckbox = (CheckBox) findViewById(R.id.checkBoxShowInstructions);
			mAllowMultipleUsersCheckbox = (CheckBox) findViewById(R.id.checkBoxMultipleUsers);
			mBandOfInterestSpinner = (Spinner) findViewById(R.id.spinnerBandOfInterest);		
			
			mSessionLengthEdit.setText(Integer.toString(sessionLengthMins));
			mAlphaGainEdit.setText(Float.toString(alphaGain));
			mShowInstructionsCheckbox.setChecked(instructionsOnStart);
			mAllowMultipleUsersCheckbox.setChecked(allowMultipleUsers);
			
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
			        this, R.array.bands_of_interest_array, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mBandOfInterestSpinner.setAdapter(adapter)	;	
			mBandOfInterestSpinner.setSelection(bandOfInterest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onDestroy() {

		try {
			String s = mSessionLengthEdit.getText().toString();
			int i = Integer.parseInt(s);
			SharedPref.putInt(this, Constants.PREF_SESSION_LENGTH, 	i * 60);
			SharedPref.putFloat(this, Constants.PREF_ALPHA_GAIN, Float.parseFloat(mAlphaGainEdit.getText().toString())	);

			boolean isChecked = mShowInstructionsCheckbox.isChecked();
			SharedPref.putBoolean(this, Constants.PREF_INSTRUCTIONS_ON_START, isChecked );
			isChecked = mAllowMultipleUsersCheckbox.isChecked();
			SharedPref.putBoolean(this, Constants.PREF_MULTIPLE_USERS, isChecked );
			int selection = mBandOfInterestSpinner.getSelectedItemPosition();
			SharedPref.putInt(this, Constants.PREF_BAND_OF_INTEREST , 	selection);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		super.onDestroy();
	}

}
