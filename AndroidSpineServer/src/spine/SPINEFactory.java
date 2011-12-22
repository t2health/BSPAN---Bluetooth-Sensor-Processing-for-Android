/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Â  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MAÂ  02111-1307, USA.
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
