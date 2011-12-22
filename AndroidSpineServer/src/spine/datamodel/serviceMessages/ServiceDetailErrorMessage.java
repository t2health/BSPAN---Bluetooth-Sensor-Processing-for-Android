/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

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
*
* @author Luigi Buondonno
* @author Antonio Giordano
*
* @version 1.3
*/

package spine.datamodel.serviceMessages;

import spine.SPINESensorConstants;
import spine.datamodel.serviceMessages.ServiceErrorMessage;

public class ServiceDetailErrorMessage extends ServiceErrorMessage {
	
	private static final long serialVersionUID = 1L;
	
	protected byte sensorCode=0,channelMask=0;
	protected String description="";
	
	public ServiceDetailErrorMessage() {
		super();
	}
	
	public void setChannelMask(byte channelMask) {
		this.channelMask = channelMask;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setSensorCode(byte sensorCode) {
		this.sensorCode = sensorCode;
	}
	
	public byte getChannelMask() {
		return channelMask;
	}
	
	public byte getSensorCode() {
		return sensorCode;
	}
	
	public String getDescription() {
		return description;
	}

	public String toString() {
		String g = (sensorCode!=0)?(SPINESensorConstants.sensorCodeToString(sensorCode) + " " +
				((channelMask!=0)?SPINESensorConstants.channelBitmaskToString(channelMask):"")):"";
		return super.toString() + "detail: " + getDescription() + " " + g;
	}
}
