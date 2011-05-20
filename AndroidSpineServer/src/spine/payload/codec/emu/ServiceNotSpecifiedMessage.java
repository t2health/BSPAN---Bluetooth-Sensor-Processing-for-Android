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
* This class contains the static method to parse (decode) a 
* NodeEmulator SPINE Service Not Specified Message  packet payload into a platform independent one.
*
* Note that this class is only used internally at the framework. 
*
* @author Buondonno Luigi
*
* @version 1.3
*/

package spine.payload.codec.emu;

import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;

public class ServiceNotSpecifiedMessage extends SpineCodec {
	
	public byte[] encode(SpineObject payload) throws MethodNotSupportedException{
		throw new MethodNotSupportedException("encode");
	};
	
	public SpineObject decode(Node node, byte[] payload)throws MethodNotSupportedException {
		spine.datamodel.serviceMessages.ServiceNotSpecifiedMessage snsm=new spine.datamodel.serviceMessages.ServiceNotSpecifiedMessage();
		snsm.setNode(node);
		snsm.setMessageDetail(payload[1]);
		return snsm;
	}

}
