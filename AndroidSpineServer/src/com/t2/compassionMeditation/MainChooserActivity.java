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

	
}
