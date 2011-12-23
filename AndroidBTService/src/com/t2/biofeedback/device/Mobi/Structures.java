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

package com.t2.biofeedback.device.Mobi;

public class Structures {
	
	public class FrontEndInfo {
		
		
		public static final int TMSBLOCKSYNC    	= 0xaaaa;
		public static final int TMSACKNOWLEDGE    	= 0x00;
		public static final int TMSCHANNELDATA      = 0x01;
		public static final int TMSFRONTENDINFO     = 0x02;
		public static final int TMSFRONTENDINFOREQ  = 0x03;
		public static final int TMSRTCREADREQ       = 0x06;
		public static final int TMSRTCDATA          = 0x07;
		public static final int TMSRTCTIMEREADREQ   = 0x1E;
		public static final int TMSRTCTIMEDATA      = 0x1F;
		public static final int TMSIDREADREQ        = 0x22;
		public static final int TMSIDDATA           = 0x23;
		public static final int TMSKEEPALIVEREQ     = 0x27;
		public static final int TMSVLDELTADATA      = 0x2F;
		public static final int TMSVLDELTAINFOREQ   = 0x30;
		public static final int TMSVLDELTAINFO      = 0x31;

		
		
		
		/**
		 * # of channels set by host (<=nrofswchannels and >0)
		 * 		first 'nrofuserchannels' channels of system
		 * 		will be sent by frontend (only when supported by frontend software!)
		 */
		public int nrOfUserChannels;
		
		/**
		 * When imp.mode, then only effect when stopping the impedance mode (changing to other mode)
		 * 		0 = base sample rate (when supported by hardware)    
		 * 		1 = base sample rate /2 (when supported by hardware) 
		 * 		2 = base sample rate /4 (when supported by hardware) 
		 * 		3 = base sample rate /8 (when supported by hardware) 
		 * 		4 = base sample rate /16 (when supported by hardware)
		 */
		public int currentSampleRateSetting;
		
		/**
		 * bit 0.. 7 is status bits active low
		 * 		bit 8..15 is mask bits active low
		 * 		bit 0 = datamode    0 = normal, Channel data send enabled
		 * 							1 = nodata, Channel data send disabled
		 * 		bit 1 = storagemode	0 = storage on  (only if supported by frontend hardware/software)
		 * 							1 = storage off 
		 * 
		 */
		public int mode;
		
	    /** last 13 uint16_t have valid values only from frontend to PC */
		public int  maxRS232;        /**< Maximum RS232 send frequentie in Hz */              		
		public int serialnumber;     /**< System serial number, low uint16_t first */         
		public int nrEXG;            /**< nr of EXG (unipolar) channels */                    
		public int nrAUX;            /**< nr of BIP and AUX channels */                       
		public int hwversion;        /**< frontend hardware version number                    
		                        * hundreds is major part, ones is minor */              
		public int swversion;        /**< frontend software version number                    
		                        * hundreds is major part, ones is minor */              
		public int cmdbufsize;       /**< number of uint16_ts in frontend receive buffer */   
		public int sendbufsize;      /**< number of uint16_ts in frontend send buffer */      
		public int nrofswchannels;   /**< total nr of channels in frontend */                 
		public int basesamplerate;   /**< base sample frequency (in Hz) */
		public int power; 				// power and  hardwarecheck not implemented yet, for future use, send 0xFFFF
		public int hardwarecheck;

		
	}
	
	
	

}
