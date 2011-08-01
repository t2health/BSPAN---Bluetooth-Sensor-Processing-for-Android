package com.t2.compassionMeditation;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

import com.t2.R;


public class MainActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View header = getLayoutInflater().inflate(R.layout.layout_header, null);
        ListView listView = getListView();
        listView.addHeaderView(header);
        
        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));
        
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.main1,R.id.label, mStrings));        
        getListView().setTextFilterEnabled(true);
    }

    
    
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent intent;
    	
    	
		super.onListItemClick(l, v, position, id);
		
		
		
		// Get the item that was clicked
		Object o = this.getListAdapter().getItem(position - 1);
		String keyword = o.toString();
		Toast.makeText(this, "You selected: " + keyword, Toast.LENGTH_LONG).show();		
		if (keyword.equalsIgnoreCase("new session")) {
			intent = new Intent(this, MeditationActivity.class);
			this.startActivity(intent);		
			
		}
		if (keyword.equalsIgnoreCase("view eeg activity")) {
			intent = new Intent(this, CompassionActivity.class);
			this.startActivity(intent);		
			
		}
		
		
	}




	private String[] mStrings = {
            "New Session", 
            "View EEG Activity", 
            };	
	
	
}
