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
 * This class represents a SPINE Start command.
 * It contains the logic for encoding a start command from an high level Start object.
 * 
 * Note that this class is only used internally at the framework.   
 *  
 *
 * @author Raffaele Gravina
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine.payload.codec.emu;

import spine.datamodel.Node;
import spine.datamodel.functions.*;

import spine.exceptions.*;


public class SpineStart extends SpineCodec {
	
	private final static int PARAM_LENGTH = 4;
	
	public SpineObject decode(Node node, byte[] payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("decode");
	};    

	public byte[] encode(SpineObject payload) {
		
		spine.datamodel.functions.SpineStart workPayLoad = (spine.datamodel.functions.SpineStart)payload;
		
		byte[] data = new byte[PARAM_LENGTH];
				
		data[0] = (byte)(workPayLoad.getActiveNodesCount()>>8);
		data[1] = (byte)workPayLoad.getActiveNodesCount();
		data[2] = (workPayLoad.getRadioAlwaysOn())? (byte)1: 0;
		data[3] = (workPayLoad.getEnableTDMA())? (byte)1: 0;
		return data;
	}	
}
