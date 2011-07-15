package com.t2.biomap;

import com.t2.AndroidSpineServerMainActivity;
import com.t2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class LogNoteActivity extends Activity implements OnKeyListener, OnClickListener{

	private EditText mLogNoteEditText;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.log_marker_kayout);

        this.findViewById(R.id.buttonSave).setOnClickListener(this);
        mLogNoteEditText = (EditText)this.findViewById(R.id.edit_text_logmarker);
        
        
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		Intent resultIntent;
		switch(v.getId()){
		case R.id.buttonSave:
			String note = mLogNoteEditText.getText().toString().trim();

			resultIntent = new Intent();
			resultIntent.putExtra(AndroidSpineServerMainActivity.ANDROID_SPINE_SERVER_ACTIVITY_RESULT, note);
			setResult(RESULT_OK, resultIntent);
			finish();			
			break;
		case R.id.buttonCancel:
			finish();			
			break;
	}
		
	}

}
