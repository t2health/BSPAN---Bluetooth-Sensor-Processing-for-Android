package com.t2.compassionMeditation;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;
import com.t2.biomap.SharedPref;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class PreferenceActivity extends Activity {

	EditText mSessionLengthEdit;
	EditText mAlphaGainEdit;
	CheckBox mShowInstructionsCheckbox;
	CheckBox mAllowCommentsCheckbox;
	CheckBox mSaveRawWaveCheckbox;
	CheckBox mShowAlphaGainCheckbox;
	Spinner mBandOfInterestSpinner;
	
	
	protected SharedPreferences sharedPref;
	
	public void onButtonClick(View v)
	{
		 final int id = v.getId();
		    switch (id) {
		    case R.id.buttonReScale:
		    	
		    	SharedPref.setIntValues(sharedPref, "BandScales", ",", new int[] {1,1,1,1,1,1,1,1});    		    	
		    	
		    	
		    	finish();
		    	break;
		    	
		    case R.id.buttonGotoUserModeActivity:
				Intent intent2 = new Intent(this, UserModeActivity.class);
				this.startActivityForResult(intent2, com.t2.compassionMeditation.Constants.USER_MODE_ACTIVITY);		
		    	finish();
		    	break;
		    	
		    }
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // This needs to happen BEFORE setContentView
        setContentView(R.layout.preferences);
		super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());   
		
		
		try {
			mSessionLengthEdit = (EditText) findViewById(R.id.editTextSessionLength);
			int sessionLengthSecs = SharedPref.getInt(this, Constants.PREF_SESSION_LENGTH, 	Constants.PREF_SESSION_LENGTH_DEFAULT);
			int sessionLengthMins = sessionLengthSecs / 60;
			mSessionLengthEdit.setText(Integer.toString(sessionLengthMins));

			mAlphaGainEdit = (EditText) findViewById(R.id.editTextAlphaGain);
			mAlphaGainEdit.setText(Float.toString(SharedPref.getFloat(this, Constants.PREF_ALPHA_GAIN, 	Constants.PREF_ALPHA_GAIN_DEFAULT)));

			mShowInstructionsCheckbox = (CheckBox) findViewById(R.id.checkBoxShowInstructions);
			mShowInstructionsCheckbox.setChecked(SharedPref.getBoolean(this, Constants.PREF_INSTRUCTIONS_ON_START, Constants.PREF_INSTRUCTIONS_ON_START_DEFAULT));

			mAllowCommentsCheckbox = (CheckBox) findViewById(R.id.checkBoxAllowComments);
			mAllowCommentsCheckbox.setChecked(SharedPref.getBoolean(this, Constants.PREF_COMMENTS, Constants.PREF_COMMENTS_DEFAULT));

			mSaveRawWaveCheckbox = (CheckBox) findViewById(R.id.checkBoxSaveRawWave);
			mSaveRawWaveCheckbox.setChecked(SharedPref.getBoolean(this, Constants.PREF_SAVE_RAW_WAVE, Constants.PREF_SAVE_RAW_WAVE_DEFAULT));

			mShowAlphaGainCheckbox = (CheckBox) findViewById(R.id.checkBoxAlphaGainVisible);
			mShowAlphaGainCheckbox.setChecked(SharedPref.getBoolean(this, Constants.PREF_SHOW_A_GAIN, Constants.PREF_SHOW_A_GAIN_DEFAULT));

						
			
						
			
			mBandOfInterestSpinner = (Spinner) findViewById(R.id.spinnerBandOfInterest);		
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
			        this, R.array.bands_of_interest_array, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mBandOfInterestSpinner.setAdapter(adapter)	;	
			mBandOfInterestSpinner.setSelection(SharedPref.getInt(this, Constants.PREF_BAND_OF_INTEREST , 	Constants.PREF_BAND_OF_INTEREST_DEFAULT));
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

			SharedPref.putBoolean(this, Constants.PREF_SAVE_RAW_WAVE, mSaveRawWaveCheckbox.isChecked() );
			SharedPref.putBoolean(this, Constants.PREF_COMMENTS, mAllowCommentsCheckbox.isChecked() );
			SharedPref.putBoolean(this, Constants.PREF_INSTRUCTIONS_ON_START, mShowInstructionsCheckbox.isChecked() );
			SharedPref.putBoolean(this, Constants.PREF_SHOW_A_GAIN, mShowAlphaGainCheckbox.isChecked() );

			
			
			SharedPref.putInt(this, Constants.PREF_BAND_OF_INTEREST , 	mBandOfInterestSpinner.getSelectedItemPosition());

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		super.onDestroy();
	}

}
