//package pergatory;
//
//
//import android.app.Activity;
//import android.graphics.Color;
//import android.graphics.PorterDuff;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//
//
////Need the following import to get access to the app resources, since this
////class is in a sub-package.
//import com.t2.R;
//
//public class UserModeActivity extends Activity {
//	private static final String TAG = "BFDemo";
//	private static final String mActivityVersion = "1.0";
//	
//	CheckBox mSaveUserMode;	
//    private Button mSingleUserButton;
//    private Button mProviderButton;
//	int mUserMode;
//	
//	public void onButtonClick(View v)
//	{
//		 final int id = v.getId();
//	    switch (id) {
//
//	    case R.id.buttonSingleUser:
//	    	mUserMode = BioZenConstants.PREF_USER_MODE_SINGLE_USER;
//	    	setButtonColors();
//			mProviderButton.invalidate();
//			mSingleUserButton.invalidate();
//	        break;
//	    
//	    case R.id.buttonProvider:
//	    	mUserMode = BioZenConstants.PREF_USER_MODE_PROVIDER;
//	    	setButtonColors();
//			mSingleUserButton.invalidate();
//			mProviderButton.invalidate();
//	        break;
//	    
//	    case R.id.buttonEndUserMode:
//	    	finish();
//	        break;
//	    
//	    
//	    }
//	}
//	
//	void setButtonColors() {
//		switch (mUserMode) {
//		case BioZenConstants.PREF_USER_MODE_SINGLE_USER:
//			mProviderButton.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
//			mSingleUserButton.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
//			
//			break;
//
//		case BioZenConstants.PREF_USER_MODE_PROVIDER:
//			mProviderButton.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
//			mSingleUserButton.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
//			
//			break;
//		
//		}
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		
//		this.setContentView(R.layout.user_mode_layout);
//		mSaveUserMode = (CheckBox) findViewById(R.id.checkBoxSaveUserMode);
//		
//		mSingleUserButton = (Button) findViewById(R.id.buttonSingleUser);		
//		mProviderButton = (Button) findViewById(R.id.buttonProvider);		
//		
//        mUserMode = SharedPref.getInt(this, 
//        		BioZenConstants.PREF_USER_MODE, 
//        		BioZenConstants.PREF_USER_MODE_DEFAULT);  		
//		
//		setButtonColors();		
//	}
//
//	
//	@Override
//	protected void onDestroy() {
//
//		if (mSaveUserMode.isChecked()) {
//			SharedPref.putInt(this, BioZenConstants.PREF_USER_MODE, mUserMode );			
//		}
//		else {
//			SharedPref.putInt(this, BioZenConstants.PREF_USER_MODE, BioZenConstants.PREF_USER_MODE_DEFAULT );			
//		}
//		super.onDestroy();
//	}
//	
//
//}
