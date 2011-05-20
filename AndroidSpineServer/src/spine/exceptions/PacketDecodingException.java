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
 *  This exception is thrown when the attempt to decode 
 *  a low level SPINE message into an higher level SpineObject fails.
 *  
 *  Known uses: spine.payload.codec.tinyos.BufferedRawDataSpineData
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package spine.exceptions;

public class PacketDecodingException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PacketDecodingException() {
	}

	public PacketDecodingException(String message) {
		super(message);
	}

	public PacketDecodingException(Throwable cause) {
		super(cause);
	}

	public PacketDecodingException(String message, Throwable cause) {
		super(message, cause);
	}

}
