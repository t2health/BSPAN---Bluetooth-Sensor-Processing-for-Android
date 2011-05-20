package com.tilab.gal;

import java.util.Vector;

public abstract class LocalNodeAdapter {
	
	protected static com.tilab.gal.LocalNodeAdapter instance;
	
	public LocalNodeAdapter()
	{
		
	}
	
	public static LocalNodeAdapter getLocalNodeAdapter()
	{
		
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
