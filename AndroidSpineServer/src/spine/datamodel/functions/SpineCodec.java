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
 * Abstract class that any data packet payload codec must extends.
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Alessia Salmeri
 *
 * @version 1.3
 */


package spine.datamodel.functions;

import spine.datamodel.Node;
import spine.exceptions.*;

public abstract class SpineCodec {
	
	/**
	 * Converting an high level data packet payload into 
	 * an actual SPINE Ota message, in terms of a byte[] array
	 * 
	 * @param payload the platform independent data packet payload
	 * @return the actual SPINE Ota message 
	 */
	public abstract byte[] encode (SpineObject payload)throws MethodNotSupportedException;

	/**
	 * Decompress data packet payload into a platform independent packet payload
	 * 
	 * @param node the Node that issued this data packet
	 * @param payload the low level byte array containing the payload of the Data packet to parse (decompress)
	 * 
	 * @return a byte array representing the platform independent data packet payload.
	 */
	public abstract SpineObject decode(Node node, byte[] payload) throws MethodNotSupportedException, PacketDecodingException;
	
}
	

