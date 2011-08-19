package com.t2.compassionMeditation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

import com.t2.AndroidSpineServerMainActivity;
import com.t2.R;
import com.t2.biomap.BioDetailActivity;
import com.t2.biomap.SharedPref;
import com.t2.filechooser.FileChooser;


public class MainChooserActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final String mActivityVersion = "1.0";
	private static boolean firstTime = true;
	
	int mUserMode;
	
	
	/**
	 * Application version info determined by the package manager
	 */
	private String mApplicationVersion = "";	
	
	private String[] mStrings = {
            "New Session", 
            "View EEG Activity", 
            "View Previous Session", 
            };	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_chooser_layout);
        
        mUserMode = SharedPref.getInt(this, 
        		com.t2.compassionMeditation.Constants.PREF_USER_MODE, 
        		com.t2.compassionMeditation.Constants.PREF_USER_MODE_DEFAULT);           
        
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			mApplicationVersion = info.versionName;
			Log.i(TAG, "Compassion Meditation Application Version: " + mApplicationVersion + ", Activity Version: " + mActivityVersion);
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}        
        
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(2);
        gridView.setAdapter(new ImageAdapter(this));

        gridView.setOnItemClickListener(new GridView.OnItemClickListener() 
        {
            public void onItemClick(AdapterView parent, View v, int position, long id) 
            {    
            	long i = id;
            	i++;
                //Insert what to do when you click on an image.
            }
        });
        
    }

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		
		switch(requestCode) {
			case Constants.FILECHOOSER_USER_ACTIVITY:
				if (data == null)
					return;
				String sessionName = data.getStringExtra(Constants.FILECHOOSER_USER_ACTIVITY_RESULT);
		    	Toast.makeText(this, "File Clicked: " + sessionName, Toast.LENGTH_SHORT).show();
		    	
		    	
				Intent intent = new Intent(this, ViewHistoryActivity.class);
				Bundle bundle = new Bundle();
	
				bundle.putString(Constants.EXTRA_SESSION_NAME,sessionName);
	
				//Add this bundle to the intent
				intent.putExtras(bundle);				
				
		    	
				this.startActivity(intent);			    	

				break;
				
		    case (com.t2.compassionMeditation.Constants.SELECT_USER_ACTIVITY) :  
			      if (resultCode == RESULT_OK) {
			  		if (data == null)
						return;
			    	  

			    	// We can't write the note yet because we may not have been re-initialized
			    	// since the not dialog put us into pause.
			    	// We'll save the note and write it at restore
			    	String userName = data.getStringExtra(com.t2.compassionMeditation.Constants.SELECT_USER_ACTIVITY_RESULT);

			    	if (userName == null) {
			    		userName = "";
			    	}

			    	SharedPref.putString(this, "SelectedUser", 	userName);
			    	  
			      } 
			      break; 	
			      
		    case (Constants.INSTRUCTIONS_USER_ACTIVITY):
				intent = new Intent(this, MeditationActivity.class);
				this.startActivity(intent);		
		    	break;
		    	
		    case (Constants.USER_MODE_ACTIVITY):
		 //   	GoAhead();
		    	break;
		    	
				
		}
	}
	    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.main1, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent("com.t2.biofeedback.MANAGER"));
			return true;
			
		case R.id.preferences:
			Intent intent = new Intent(this, PreferenceActivity.class);
			this.startActivity(intent);	
			return true;
			
						
		case R.id.about:
			String content = "National Center for Telehealth and Technology (T2)\n\n";
			content += "Compassion Meditation Application\n";
			content += "Application Version: " + mApplicationVersion + "\n";
			content += TAG + " Version: " + mActivityVersion;
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
			alert.setTitle("About");
			alert.setMessage(content);	
			alert.show();			
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
		
}
