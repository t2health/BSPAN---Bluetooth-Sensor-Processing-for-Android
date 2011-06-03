package com.tilab.gal;


import spine.Properties;

public abstract class LocalNodeAdapter {
	
	private static final String APP_PROP_MISSING_MSG = "Application Property Missing";
	protected static com.tilab.gal.LocalNodeAdapter instance;
	private static Properties prop = Properties.getDefaultProperties();
	private static String LOCALNODEADAPTER_CLASSNAME = null;	
	
	public LocalNodeAdapter()
	{

	}
	
//	public static LocalNodeAdapter getLocalNodeAdapter()
//	{
//		LOCALNODEADAPTER_CLASSNAME = prop.getProperty(PLATFORM + "_" + Properties.LOCALNODEADAPTER_CLASSNAME_KEY);
//		
//		return instance;
//		
//	}
	
	public static LocalNodeAdapter getLocalNodeAdapter() throws IllegalAccessException, InstantiationException, ClassNotFoundException
	{
		
//		Properties appProp = Properties.getProperties("defaults.properties");
//
//		String MOTECOM = System.getProperty(Properties.MOTECOM_KEY);
//		MOTECOM = (MOTECOM != null) ? MOTECOM : appProp.getProperty(Properties.MOTECOM_KEY);
//
//		String PLATFORM = System.getProperty(Properties.PLATFORM_KEY);
//		PLATFORM = (PLATFORM != null) ? PLATFORM : appProp.getProperty(Properties.PLATFORM_KEY);
//		
//		LOCALNODEADAPTER_CLASSNAME = prop.getProperty(PLATFORM + "_" + Properties.LOCALNODEADAPTER_CLASSNAME_KEY);		
		LOCALNODEADAPTER_CLASSNAME = prop.getProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY);		
		LOCALNODEADAPTER_CLASSNAME = System.getProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY);
		
//		if (MOTECOM == null || PLATFORM == null)
//			throw new InstantiationException(APP_PROP_MISSING_MSG);		
		
		ClassLoader classLoader = LocalNodeAdapter.class.getClassLoader();		
	    	Class aClass = classLoader.loadClass(LOCALNODEADAPTER_CLASSNAME);
	    	instance = (LocalNodeAdapter) aClass.newInstance();
	    	//instance = (LocalNodeAdapter) Class.forName(s, true, classLoader);
//	        Class aClass = classLoader.loadClass(s);
	        System.out.println("aClass.getName() = " + aClass.getName());

		return instance;
		
	}	
	
	
	
	public static LocalNodeAdapter getLocalNodeAdapter(String s) throws IllegalAccessException, InstantiationException
	{
		
		ClassLoader classLoader = LocalNodeAdapter.class.getClassLoader();		
	    try {
	
	    	Class aClass = classLoader.loadClass(s);
	    	instance = (LocalNodeAdapter) aClass.newInstance();
	    	//instance = (LocalNodeAdapter) Class.forName(s, true, classLoader);
//	        Class aClass = classLoader.loadClass(s);
	        System.out.println("aClass.getName() = " + aClass.getName());
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }		
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
	  public abstract com.tilab.gal.WSNConnection createAPSConnection();
	 

}
