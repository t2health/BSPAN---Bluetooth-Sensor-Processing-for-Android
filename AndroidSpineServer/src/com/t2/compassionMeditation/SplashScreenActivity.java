package com.t2.compassionMeditation;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;


public class SplashScreenActivity extends Activity implements OnClickListener {
	private TextView startupTipsView;
	private Timer startTimer;
	private Handler startHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			startMainActivity();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		// This needs to happen BEFORE setContentView
        
		int nextTimeout = 2500;
		this.setContentView(R.layout.splash_screen_activity);
		View v = this.findViewById(R.id.splashWrapper);
		this.findViewById(R.id.splashWrapper).setOnClickListener(this);		


		startTimer = new Timer();
		startTimer.schedule(new TimerTask(){
			@Override
			public void run() {
				startHandler.sendEmptyMessage(0);
			}
		}, nextTimeout);
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.splashWrapper:
			startTimer.cancel();
			startMainActivity();
		}
	}

	private void startMainActivity() {
//		Intent i = new Intent(this, MainActivity.class);
		Intent i = new Intent(this, MainChooserActivity.class);		
		this.startActivity(i);
		this.finish();
	}
}
