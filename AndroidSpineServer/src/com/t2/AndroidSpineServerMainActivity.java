package com.t2;




import spine.SPINEFactory;
import spine.SPINEManager;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class AndroidSpineServerMainActivity extends Activity {
	
    EditText mEditText;
    private static SPINEManager manager;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
        
		// Initialize SPINE by passing the fileName with the configuration properties
		try {
			manager = SPINEFactory.createSPINEManager("SPINETestApp.properties", resources);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        
        
                
        
        
        
        
        
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.settings:
				startActivity(new Intent("com.t2.biofeedback.MANAGER"));
				return true;
		
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
}