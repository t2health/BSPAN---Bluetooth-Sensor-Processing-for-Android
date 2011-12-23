/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute
modify it under the terms of the sub-license (below).

*****************************************************************/

/*****************************************************************
BSPAN - BlueTooth Sensor Processing for Android is a framework 
that extends the SPINE framework to work on Android and the 
Android Bluetooth communication services.

Copyright (C) 2011 The National Center for Telehealth and 
Technology

Eclipse Public License 1.0 (EPL-1.0)

This library is free software; you can redistribute it and/or
modify it under the terms of the Eclipse Public License as
published by the Free Software Foundation, version 1.0 of the 
License.

The Eclipse Public License is a reciprocal license, under 
Section 3. REQUIREMENTS iv) states that source code for the 
Program is available from such Contributor, and informs licensees 
how to obtain it in a reasonable manner on or through a medium 
customarily used for software exchange.

Post your updates and modifications to our GitHub or email to 
t2@tee2.org.

This library is distributed WITHOUT ANY WARRANTY; without 
the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the Eclipse Public License 1.0 (EPL-1.0)
for more details.
 
You should have received a copy of the Eclipse Public License
along with this library; if not, 
visit http://www.opensource.org/licenses/EPL-1.0

*****************************************************************/


package spine;

import jade.util.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.res.AssetManager;

/**
 * 
 * @see spine.Properties
 */
public class PropertiesImpl extends spine.Properties {	
	
	private final static String DEFAULT_COMMENT = "Created by the PropertiesImpl J2SE";
	
	
	private Properties p;
	
	private String propertiesFileName = null;
	
	private boolean loaded = false;
	
	
	
	PropertiesImpl() {
		this.propertiesFileName = DEFAULT_PROPERTIES_FILE;
		p = new Properties();				
	}
	
	PropertiesImpl(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
		p = new Properties();
	}
	
	
	public void load() throws IOException {
		if (!loaded) 			
			loadPropFile();		
	}
	
	private void loadPropFile() throws IOException {

		if (loaded)
			return;
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFileName); 
		if (is == null)
		{
		//	is = new FileInputStream(propertiesFileName);
			if (resources == null) {
//				throw new RuntimeException( "Spine has not been initialized!" );
			}
			else {
		        AssetManager assetManager = resources.getAssets();
				is = assetManager.open(propertiesFileName);				
			}
		}
		if (is != null) {
			p.load(is);
			loaded = true;
			
		}
	}
	
	public void store() {
		try {
			FileOutputStream fos = new FileOutputStream(propertiesFileName);
			p.store(fos, DEFAULT_COMMENT);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
		}
	}
	
	public String getProperty(String key) {
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			return null;
		}
		return p.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		p.setProperty(key, value);
	}
	
	public Object remove(String key) {
		return p.remove(key);
	}	
}
