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
