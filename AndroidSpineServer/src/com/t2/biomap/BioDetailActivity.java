package com.t2.biomap;


import android.app.Activity;
import android.os.Bundle;
//import android.widget.FrameLayout.LayoutParams;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;

public class BioDetailActivity extends Activity  {
    private static final String TAG = "BioDetail";
	
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biodetail_layout);

		
    }
    
    
    @Override
    protected void onResume()
    {
        super.onResume();
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    
}