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

import java.io.IOException;

import android.content.res.Resources;

public abstract class Properties {
	
//	public final static String DEFAULT_PROPERTIES_FILE = "resources/defaults.properties";
	public final static String DEFAULT_PROPERTIES_FILE = "defaults.properties";
	
	public static final String MOTECOM_KEY = "MOTECOM";
	public static final String PLATFORM_KEY = "PLATFORM";
	public static final String SPINE_HOME_KEY = "SPINE_HOME";
	
	public static final String GROUP_ID_KEY = "group_id";
	public static final String LINE_SEPARATOR_KEY = "line_separator";
	
	public static final String LOCALNODEADAPTER_CLASSNAME_KEY = "LocalNodeAdapter_ClassName";
	public static final String URL_PREFIX_KEY = "url_prefix";
	public static final String SPINEDATACODEC_PACKAGE_SUFFIX_KEY = "spineDataCodec_Package_Suffix";
	public static final String MESSAGE_CLASSNAME_KEY = "message_className";
	
	public static final String VIRTUAL_WSN_SIZE_KEY = "virtualWSNSize";
	public static Resources resources;

	
	public static void setResources(Resources _resources)
	{
		resources = _resources;
	}
	/**
	 * Returns an implementation of the Properties abstract class 
	 * which is binded to the default properties file "default.properties"
	 * 
	 * @return an implementation of the Properties abstract class binded to the "default.properties" file
	 */
	public static Properties getDefaultProperties() {
		return new PropertiesImpl();
	}
	
	/**
	 * Returns an implementation of the Properties abstract class 
	 * which is binded to the given 'propertiesFileName'  
	 * 
	 * @return an implementation of the Properties abstract class binded to the given 'propertiesFileName'
	 */
	public static Properties getProperties(String propertiesFileName) {
		return new PropertiesImpl(propertiesFileName);
	}
	
	/**
	 * Loads the properties set from a properties source (i.e, from a predefined .properties file or a Manifest)
	 *  
	 */
	public abstract void load() throws IOException;
	
	/**
	 * Stores (permanently if possible) a loaded properties set
	 * 
	 */
	public abstract void store();
	
	/**
	 * Returns the value of the given property key 'key'
	 * 
	 * @param key the property key to be returned
	 * @return the property value or null if the property 'key' doesn't exist
	 */
	public abstract String getProperty(String key);
	
	/**
	 * Sets the property key 'key' with the value 'value'
	 * 
	 * @param key the property key to be set
	 * @param value the property value
	 */
	public abstract void setProperty(String key, String value);
	
	/**
	 * Remove the given property key 'key' from the set
	 * 
	 * @param key the property key to be removed
	 * @return the value mapped to 'key'
	 */
	public abstract Object remove(String key);
}
