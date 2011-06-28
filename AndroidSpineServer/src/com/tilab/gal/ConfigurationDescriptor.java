package com.tilab.gal;

/**
 * Stub file
 * @author scott.coleman
 *
 */
public abstract interface  ConfigurationDescriptor {
	
	 // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS = 0;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_HOLDING = 0;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_INITIALIZING = 1;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_DISCOVERING = 2;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_JOINING = 3;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_REJOINING = 4;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_FORMING = 5;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_AUTHENTICATING = 6;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_FORMED = 7;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_JOINED = 8;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_ORPHANED = 9;
	  
	  // Field descriptor #6 I
	  public static final int IB_GTW_ZB_NWK_STATUS_UNAUTHENTICATED = 10;
	  
	  // Field descriptor #6 I
	  public static final int IB_APP_FIXME = 192;
	  
	  // Field descriptor #6 I
	  public static final int IB_NWK_FIXME = 128;
	  
	  // Field descriptor #6 I
	  public static final int IB_PHY_FIXME = 255;
	  
//	  public abstract void preSet(com.tilab.gal.ConfigurationDescriptor.Parameter arg0)
//	  {
//		  
//	  }
//	  
//	  // Method descriptor #37 (Lcom/tilab/gal/ConfigurationDescriptor$Parameter;)V
//	  public abstract void get(com.tilab.gal.ConfigurationDescriptor.Parameter arg0)
//	  {
//		  
//	  }
	  
	  // Method descriptor #40 ()V
	  public abstract void commit();	  
	  
	  public static class Parameter {
		  
	  }

}
