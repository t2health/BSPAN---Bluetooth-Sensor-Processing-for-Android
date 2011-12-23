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


import android.content.res.Resources;
import android.util.Log;


/**
 * This class is responsible for creating and configuring the SPINEManager
 * 
 * @author Fabio Bellifemine, Telecom Italia
 * @since 1.3
 */
public class SPINEFactory {

	private static final String APP_PROP_MISSING_MSG = "ERROR: 'app.properties' file is missing, not properly specified or 'MOTECOM' and/or 'PLATFORM' properties not defined!";

	private static SPINEManager managerInstance = null;

	/**
	 * Initializes the SPINE Manager. The SPINEManager instance is connected to
	 * the base-station and platform obtained transparently from the
	 * app.properties file. This method should be called just once in the
	 * application life-time, creating more than one SPINEManager at the moment
	 * has an undefined behaviour.
	 * 
	 * @param appPropertiesFile
	 *            the application properties file where at least the 'MOTECOM'
	 *            and 'PLATFORM' variables are defined
	 * @return the created SPINEManager ready to be used
	 * @see spine.SPINESupportedPlatforms
	 * @throws InstantiationException
	 *             if the SPINEManager has already been initialized or MOTECOM
	 *             and PLATFORM variables have not been defined.
	 **/
	public static SPINEManager createSPINEManager(String appPropertiesFile, Resources resources) throws InstantiationException {
		if (managerInstance != null)
		{
			Log.d("BFDemo", "SPINEManager createSPINEManager(EXISTING), managerInstance = " + managerInstance);				
//			Log.i("BFDemo", "	gOT HERE 2, managerInstance = " + managerInstance);			
			
		}
//			throw new InstantiationException("SPINEManager already initialized");
		else {
			Properties.setResources(resources);
			Properties appProp = Properties.getProperties(appPropertiesFile);

			String MOTECOM = System.getProperty(Properties.MOTECOM_KEY);
			MOTECOM = (MOTECOM != null) ? MOTECOM : appProp.getProperty(Properties.MOTECOM_KEY);

			String PLATFORM = System.getProperty(Properties.PLATFORM_KEY);
			PLATFORM = (PLATFORM != null) ? PLATFORM : appProp.getProperty(Properties.PLATFORM_KEY);
			
			if (MOTECOM == null || PLATFORM == null)
				throw new InstantiationException(APP_PROP_MISSING_MSG);
			
			managerInstance = new SPINEManager(MOTECOM, PLATFORM);
			Log.d("BFDemo", "SPINEManager createSPINEManager(NEW), managerInstance = " + managerInstance);				
			
//			Log.i("BFDemo", "	gOT HERE 1, managerInstance = " + managerInstance);			
			
		}
		return managerInstance;

	}
	
	public static void killSPINEManager() throws InstantiationException {
		managerInstance = null;
	}
	
}
