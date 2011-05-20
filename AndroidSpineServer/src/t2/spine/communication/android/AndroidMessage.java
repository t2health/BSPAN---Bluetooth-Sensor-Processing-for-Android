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

public class AndroidMessage extends com.tilab.gal.Message {

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

	private static final long serialVersionUID = 1L;

	public void setSourceURL(String sourceID) {
		this.sourceURL = sourceID;
	}

	public String toString() {
		short[] payload = this.getPayload();
		String valPayload = "";
		if (payload == null || payload.length == 0) {
			valPayload = "empty payload";
		} else {
			for (int i = 0; i < payload.length; i++) {
				short b = payload[i];
				if (b < 0)
					b += 256;
				valPayload = valPayload + Integer.toHexString(b) + " ";
			}
		}
		return "From node: " + this.getSourceURL() + " " + "pktType(clusterId)=" + this.clusterId + " - " + valPayload;
	}
}