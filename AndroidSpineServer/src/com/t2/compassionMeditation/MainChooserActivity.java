package com.t2.compassionMeditation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;

import com.t2.biomap.SharedPref;
import com.t2.filechooser.FileChooser;


/**
 * Main menu activity for application
 * @author scott.coleman
 *
 */
public class MainChooserActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final String mActivityVersion = "1.1";
	private static boolean firstTime = true;

	private MainChooserActivity instance;

	
	// ID index variables - The enumerations MUST match the image references below
	private static final int ID_LEARN = 0;
	private static final int ID_NEW_SESSION = 1;
	private static final int ID_VIEW_ACTIVITY = 2;
	private static final int ID_REVIEW = 3;
	
    // references to our images - The order MUST match the ID index variables above
    private Integer[] mThumbIds = {
            R.drawable.learn_tab, 
            R.drawable.newsession_tab,
            R.drawable.view_tab,
            R.drawable.review_tab,
    };
	
	/**
	 * User mode - Determines whether or not to show a dialog showing potential users
	 * @see Constants.java
	 *  PREF_USER_MODE_DEFAULT, PREF_USER_MODE_SINGLE_USER, PREF_USER_MODE_PROVIDER
	 */
	int mUserMode;
	
	/**
	 * Application version info determined by the package manager
	 */
	private String mApplicationVersion = "";	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.main_chooser_activity_layout);
        
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);    
        setTitle("BioZen");
        
        mUserMode = SharedPref.getInt(this, 
        		Constants.PREF_USER_MODE, 
        		Constants.PREF_USER_MODE_DEFAULT);           
        
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
        
		if (mUserMode == Constants.PREF_USER_MODE_PROVIDER) {
			Intent intent2 = new Intent(this, SelectUserActivity.class);
			this.startActivityForResult(intent2, Constants.SELECT_USER_ACTIVITY);		
			
		} else {
	    	SharedPref.putString(this, "SelectedUser", 	"");
		}        

        gridView.setOnItemClickListener(new GridView.OnItemClickListener() 
        {
            public void onItemClick(AdapterView parent, View v, int position, long id) 
            {    
            	Intent intent;            	
            	switch (position)
            	{
            	case ID_LEARN:
            		break;

            	case ID_NEW_SESSION:
        			boolean instructionsOnStart = SharedPref.getBoolean(instance, 
        					Constants.PREF_INSTRUCTIONS_ON_START, 
        					Constants.PREF_INSTRUCTIONS_ON_START_DEFAULT);        

        			if (instructionsOnStart) {
        				Intent intent1 = new Intent(instance, InstructionsActivity.class);
        				instance.startActivityForResult(intent1, Constants.INSTRUCTIONS_USER_ACTIVITY);		
        			} else {
        				intent = new Intent(instance, BuddahActivity.class);
        				instance.startActivity(intent);		
        			}
        			break;
        			
            	case ID_VIEW_ACTIVITY:
        			intent = new Intent(instance, GraphsActivity.class);
        			instance.startActivity(intent);	        			
            		break;
            	case ID_REVIEW:
        			intent = new Intent(instance, FileChooser.class);
        			instance.startActivityForResult(intent, Constants.FILECHOOSER_USER_ACTIVITY);            		
            		break;
            	}
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
				
		    case (Constants.SELECT_USER_ACTIVITY) :  
			      if (resultCode == RESULT_OK) {
			  		if (data == null)
						return;

			    	// We can't write the note yet because we may not have been re-initialized
			    	// since the not dialog put us into pause.
			    	// We'll save the note and write it at restore
			    	String userName = data.getStringExtra(Constants.SELECT_USER_ACTIVITY_RESULT);

			    	if (userName == null) {
			    		userName = "";
			    	}

			    	SharedPref.putString(this, "SelectedUser", 	userName);
			    	  
			      } 
			      break; 	
			      
		    case (Constants.INSTRUCTIONS_USER_ACTIVITY):
				intent = new Intent(this, BuddahActivity.class);
				this.startActivity(intent);		
		    	break;
		    	
		    case (Constants.USER_MODE_ACTIVITY):
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
	
	
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;

	    public ImageAdapter(Context c) {
	        mContext = c;
	    }

	    public int getCount() {
	        return mThumbIds.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
	            imageView.setPadding(1,1,1,1);
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageResource(mThumbIds[position]);
	        return imageView;
	    }
	}	
}