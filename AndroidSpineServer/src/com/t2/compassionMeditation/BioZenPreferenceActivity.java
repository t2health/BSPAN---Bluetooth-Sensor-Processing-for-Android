/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t2.compassionMeditation;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class BioZenPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
    public static final String KEY_PREFERENCE = "change_user_mode_preference";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.bio_zen_preferences);
    }

    
    @Override
    protected void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }    

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {
      if (key.equals("audio_track")) {
      Toast.makeText(this, "Thanks! You increased my count to ", Toast.LENGTH_SHORT).show();
  }
		
	}
}
