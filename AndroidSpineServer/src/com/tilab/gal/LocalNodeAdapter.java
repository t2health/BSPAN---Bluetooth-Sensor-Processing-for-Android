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
