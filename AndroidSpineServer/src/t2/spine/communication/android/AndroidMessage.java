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
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
  
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 *****************************************************************/

/**
 *
 * Implementation for Android Message interface.
 * 
 * Note that this class is only used internally at the framework. 
 *
 * @author Alessia Salmeri
 * @author Scott Coleman
 *
 * @version 1.0
 */

package t2.spine.communication.android;

import spine.Properties;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;

public class AndroidMessage extends com.tilab.gal.Message {

//	private static final String TINYOS_URL_PREFIX = Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.TINYOS + "_" + Properties.URL_PREFIX_KEY);
	private static final String TINYOS_URL_PREFIX = Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.ANDROID + "_" + Properties.URL_PREFIX_KEY);
	
	private static final int DEFAULT_MESSAGE_SIZE = 0; 	// it represents a variable-size array and 
	// does not check the corresponding array index

	private static final int AM_TYPE = SPINEPacketsConstants.AM_SPINE;

// TODO: When we switch to real shimmer hardware there will be a 8 byte AM header
	protected static final int AM_HEADER_SIZE = 0;
//	protected static final int AM_HEADER_SIZE = 8;

	protected SPINEHeader header = null;

	protected byte[] payloadBuf = null;	
	
	public AndroidMessage() {
		super();
	}

		
	
	public AndroidMessage(short dstAddr, short destEndpoint, short srcEndpoint,
			short clusterId, short transId, short options, short radius,
			short len, short[] data) {
		super(dstAddr, destEndpoint, srcEndpoint, clusterId, transId, options, radius,
				len, data);
		// TODO Auto-generated constructor stub
	}

	protected AndroidMessage(byte pktType, byte groupID, int sourceID, int destID, byte sequenceNumber, byte fragmentNr, byte totalFragments, byte[] payload) {

		//    	super(SPINEPacketsConstants.SPINE_HEADER_SIZE + payload.length);
    	// Since we're not actually iniheriting from tinyOS message we'll need create the backing data buffer 
		
		
    	this.amTypeSet(AM_TYPE); 
    	
    	this.header = new SPINEHeader(SPINEPacketsConstants.CURRENT_SPINE_VERSION, false, pktType, 
    										 groupID, sourceID, destID, sequenceNumber, fragmentNr, totalFragments);   
    	
    	this.payloadBuf = payload;
    	
    	byte[] msgBuf = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE + payload.length];
    	System.arraycopy(header.build(), 0, msgBuf, 0, SPINEPacketsConstants.SPINE_HEADER_SIZE);
    	System.arraycopy(payload, 0, msgBuf, SPINEPacketsConstants.SPINE_HEADER_SIZE, payload.length);
    	
    	this.dataSet(msgBuf);        
	}	
	
	
	private static final long serialVersionUID = 1L;

	public void setSourceURL(String sourceID) {
		this.sourceURL = sourceID;
	}

	
	
	public static AndroidMessage Construct(byte[] rawsfmessage) {
		byte[] payload;
		
		SPINEHeader h;
		
		byte[] sh_bytes = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];
		System.arraycopy(rawsfmessage, AM_HEADER_SIZE, sh_bytes, 0, sh_bytes.length);
		try {
			h = new SPINEHeader(sh_bytes);
		}
		catch (IllegalSpineHeaderSizeException ishse) {
			return null;
		}
		
		payload = new byte[rawsfmessage.length - SPINEPacketsConstants.SPINE_HEADER_SIZE - AM_HEADER_SIZE];
		System.arraycopy(rawsfmessage, AM_HEADER_SIZE+SPINEPacketsConstants.SPINE_HEADER_SIZE,
						 payload, 0, payload.length);
		
		
		return new AndroidMessage(h.getPktType(), h.getGroupID(), h.getSourceID(), h.getDestID(), h.getSequenceNumber(), h.getFragmentNumber(), h.getTotalFragments(), payload);
	}	
	
	
	
	protected SPINEHeader getHeader() throws IllegalSpineHeaderSizeException {
    	byte[] msgBuf = this.dataGet();
    	
    	if (msgBuf.length < SPINEPacketsConstants.SPINE_HEADER_SIZE) 
    		throw new IllegalSpineHeaderSizeException(SPINEPacketsConstants.SPINE_HEADER_SIZE, msgBuf.length);
    	
		byte[] headerBuf = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];
		System.arraycopy(msgBuf, 0, headerBuf, 0, SPINEPacketsConstants.SPINE_HEADER_SIZE);		
		
		return new SPINEHeader(headerBuf); 
    }	
	
	
	protected byte[] getRawPayload() {
    	if (this.payloadBuf == null) {
	    	this.payloadBuf = new byte[this.dataGet().length - SPINEPacketsConstants.SPINE_HEADER_SIZE];
			System.arraycopy(this.dataGet(), SPINEPacketsConstants.SPINE_HEADER_SIZE, this.payloadBuf, 0, this.payloadBuf.length);
    	}				
		return this.payloadBuf;
    }
    
	protected void setRawPayload(byte[] payload) {
    	this.payloadBuf = payload;
    }	
	
	
	protected TOSMessage parse() throws IllegalSpineHeaderSizeException {
		TOSMessage msg = new TOSMessage();
		
		byte[] msgBuf = this.dataGet();
		
		byte[] headerBuf = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];
		System.arraycopy(msgBuf, 0, headerBuf, 0, SPINEPacketsConstants.SPINE_HEADER_SIZE);		
		
		if(this.payloadBuf == null) {
			this.payloadBuf = new byte[msgBuf.length - SPINEPacketsConstants.SPINE_HEADER_SIZE];
			System.arraycopy(msgBuf, SPINEPacketsConstants.SPINE_HEADER_SIZE, this.payloadBuf, 0, this.payloadBuf.length);
		}
		
		SPINEHeader header = new SPINEHeader(headerBuf); 
		
		msg.setClusterId(header.getPktType());
		msg.setProfileId(header.getGroupID());
		msg.setSourceURL(TINYOS_URL_PREFIX + header.getSourceID()); 
		msg.setDestinationURL(TINYOS_URL_PREFIX + header.getDestID());
		msg.setSeqNo(header.getSequenceNumber());
		
		short[] payloadBufShort = new short[this.payloadBuf.length];
		for (int i = 0; i<this.payloadBuf.length; i++)
			payloadBufShort[i] = payloadBuf[i]; 
		
		msg.setPayload(payloadBufShort);
		
		return msg;
	}	
	
	
	
//	public String toString() {
//		short[] payload = this.getPayload();
//		String valPayload = "";
//		if (payload == null || payload.length == 0) {
//			valPayload = "empty payload";
//		} else {
//			for (int i = 0; i < payload.length; i++) {
//				short b = payload[i];
//				if (b < 0)
//					b += 256;
//				valPayload = valPayload + Integer.toHexString(b) + " ";
//			}
//		}
//		return "From node: " + this.getSourceURL() + " " + "pktType(clusterId)=" + this.clusterId + " - " + valPayload;
//	}
	public String toString() {
		String s = null;
		
		try {
			s = getHeader() + " (hex: ";
			int len = 0;
			for (int i = 0; i<getHeader().getHeaderBuf().length; i++) {
				short b =  getHeader().getHeaderBuf()[i];
				if (b<0) b += 256;
				s += Integer.toHexString(b).toUpperCase() + " ";
				len++;
			}
			
			s += ") - Payload (hex) { ";
			//int len = 0;
			if(payloadBuf != null && payloadBuf.length > 0) 
				for (int i = 0; i<payloadBuf.length; i++) {
					short b =  payloadBuf[i];
					if (b<0) b += 256;
					s += Integer.toHexString(b).toUpperCase() + " ";
					len++;
				}
			else if(this.dataGet() != null && this.dataGet().length > SPINEPacketsConstants.SPINE_HEADER_SIZE) 
				for (int i = SPINEPacketsConstants.SPINE_HEADER_SIZE; i<this.dataGet().length; i++) {
					short b =  this.dataGet()[i];
					if (b<0) b += 256;
					s += Integer.toHexString(b).toUpperCase() + " ";
					len++;
				}
			else 
				s += "empty payload ";
			
			s += "} [msg len=" + len + "]";
			
		} catch (IllegalSpineHeaderSizeException e) {
			return e.getMessage();
		}
		
		return s;
	}	
	
}