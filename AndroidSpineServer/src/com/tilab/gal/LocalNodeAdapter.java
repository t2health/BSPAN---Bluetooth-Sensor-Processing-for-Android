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

package com.tilab.gal;


import spine.Properties;

/**
 * This static class encapsulates the Spine Manager's interface to 
 * lower level sensor interfaces.
 * The LocalNodeAdapter is dynamically loaded based on system property files
 * (So a different LocalNodeAdapter is loaded based on the platfor specified
 * in SPINETestApp.properties
 *  
 * @author scott.coleman
 *
 */
public abstract class LocalNodeAdapter {
	
	private static final String APP_PROP_MISSING_MSG = "Application Property Missing";
	protected static com.tilab.gal.LocalNodeAdapter instance;
	private static Properties prop = Properties.getDefaultProperties();
	private static String LOCALNODEADAPTER_CLASSNAME = null;	
	
	public LocalNodeAdapter()
	{

	}
	
	/**
	 * Dynamically loads an instance of LocalNodeAdapter which is specified by the 
	 * platform specified by SPINETestApp.properties
	 * 
	 * @return	Instance of the LocalNodeAdapter
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static LocalNodeAdapter getLocalNodeAdapter() throws IllegalAccessException, InstantiationException, ClassNotFoundException
	{
		
		LOCALNODEADAPTER_CLASSNAME = prop.getProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY);		
		LOCALNODEADAPTER_CLASSNAME = System.getProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY);
		
		ClassLoader classLoader = LocalNodeAdapter.class.getClassLoader();		
	    	Class aClass = classLoader.loadClass(LOCALNODEADAPTER_CLASSNAME);
	    	instance = (LocalNodeAdapter) aClass.newInstance();
	        //System.out.println("aClass.getName() = " + aClass.getName());

		return instance;
		
	}	
	
	 // Method descriptor #54 (Ljava/util/Vector;)V
	  public abstract void init(java.util.Vector arg0);
	  
	  // Method descriptor #10 ()V
	  public abstract void start();
	  
	  // Method descriptor #10 ()V
	  public abstract void stop();
	  
	  // Method descriptor #10 ()V
	  public abstract void reset();
	  
	  // Method descriptor #59 ()Lcom/tilab/gal/ConfigurationDescriptor;
	  //  public abstract com.tilab.gal.ConfigurationDescriptor getConfigurationDescriptor();
	  
	  // Method descriptor #61 ()Lcom/tilab/gal/WSNConnection;
	/**
	 * Creates a new connection
	 *  The connection serves as the link between the event dispatcher and the local node adapter
	 * @return Newly created AndroidWSNConnection
	 */
	public abstract com.tilab.gal.WSNConnection createAPSConnection();
	 

}
