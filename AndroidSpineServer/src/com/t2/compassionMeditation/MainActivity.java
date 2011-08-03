package com.t2.compassionMeditation;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

import com.t2.AndroidSpineServerMainActivity;
import com.t2.R;
import com.t2.biomap.SharedPref;
import com.t2.filechooser.FileChooser;


public class MainActivity extends ListActivity {
	
	
	private static final int BLUETOOTH_SETTINGS = 987;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // For now we'll set the prefs manually (until we get a dialog for it
        setPrefs();
        

        View header = getLayoutInflater().inflate(R.layout.layout_header, null);
        ListView listView = getListView();
        listView.addHeaderView(header);
        
        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));
        
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.main1,R.id.label, mStrings));        
        getListView().setTextFilterEnabled(true);
    }

    void setPrefs() {
		 SharedPref.putInt(this, Constants.PREF_SESSION_LENGTH, 	35);

    }
    
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent intent;
    	
    	
		super.onListItemClick(l, v, position, id);
		
		
		
		// Get the item that was clicked
		Object o = this.getListAdapter().getItem(position - 1);
		String keyword = o.toString();
		if (keyword.equalsIgnoreCase("new session")) {
			intent = new Intent(this, MeditationActivity.class);
			this.startActivity(intent);		
			
		}
		if (keyword.equalsIgnoreCase("view eeg activity")) {
			intent = new Intent(this, CompassionActivity.class);
			this.startActivity(intent);		
			
		}
		if (keyword.equalsIgnoreCase("View Previous Session")) {
			intent = new Intent(this, FileChooser.class);
			
			this.startActivityForResult(intent, Constants.fileChooserRequestCode);

//			this.startActivity(intent);		
			
		}
		
		
	}




	private String[] mStrings = {
            "New Session", 
            "View EEG Activity", 
            "View Previous Session", 
            };	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
			case Constants.fileChooserRequestCode:
				String LogMarkerNote = data.getStringExtra(Constants.FILE_CHOOSER_EXTRA);
		    	Toast.makeText(this, "File Clicked: " + LogMarkerNote, Toast.LENGTH_SHORT).show();

				break;
		}
	}
	
	
	
	
	
	
}
