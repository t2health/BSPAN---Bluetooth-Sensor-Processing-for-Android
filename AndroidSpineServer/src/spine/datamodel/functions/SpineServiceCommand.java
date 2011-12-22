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
 * This class represents the SPINE Setup Sensor request.
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 * 
 * @see spine.SPINESensorConstants
 */

package spine.datamodel.functions;


public class SpineServiceCommand  implements SpineObject {
	
	private static final long serialVersionUID = 1L;

	public static final byte COMMAND_STOPPED = 0;
	public static final byte COMMAND_RUNNING = 1;
	
	public static final byte COMMAND_ENABLED = 2;
	public static final byte COMMAND_DISABLED = 3;
	
	private byte[] btAddress = new byte[6];
	private byte[] btName = new byte[255];
	private int samplingTime = -1;
	private byte command = 0;   // 0 = stopped, 1 = running, 2 enabled, 3 disabled


	public byte[] getBtAddress() {
		return btAddress;
	}

	public void setBtAddress(byte[] btAddress) {
		this.btAddress = btAddress;
	}

	public void setBtAddress(String strBtAddress) {

		Boolean formatOk = true;
		// First make sure the string is in proper format
		// B1:B2:B3:B4:B6:B6
		if (strBtAddress.length() != 17 ){
			formatOk = false;
		}
		
		if (
				strBtAddress.charAt(2) != ':' || 
				strBtAddress.charAt(5) != ':' || 
				strBtAddress.charAt(8) != ':' || 
				strBtAddress.charAt(11) != ':' || 
				strBtAddress.charAt(14) != ':'
				) {
			formatOk = false;
		}
		try {
			
			int j = 0;
			for (int i = 0; i < 6; i++) {
				btAddress[i] = (byte) (Byte.parseByte(strBtAddress.substring(j,j+1), 16) << 4);	
				btAddress[i] += (byte) (Byte.parseByte(strBtAddress.substring(j+1,j+2), 16));
				j+= 3;
				
			}
	
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	
	public static String stringToHex(String base)
    {
     StringBuffer buffer = new StringBuffer();
     int intValue;
     for(int x = 0; x < base.length(); x++)
         {
         int cursor = 0;
         intValue = base.charAt(x);
         String binaryChar = new String(Integer.toBinaryString(base.charAt(x)));
         for(int i = 0; i < binaryChar.length(); i++)
             {
             if(binaryChar.charAt(i) == '1')
                 {
                 cursor += 1;
             }
         }
         if((cursor % 2) > 0)
             {
             intValue += 128;
         }
         buffer.append(Integer.toHexString(intValue) + " ");
     }
     return buffer.toString();
}
	
	public byte[] getBtName() {
		return btName;
	}

	public void setBtName(String strBtName) {

		if (strBtName.length() <= 254) {
			btName = strBtName.getBytes();	
		}
		
	}


	public byte getCommand() {
		return command;
	}

	public void setCommand(byte command) {
		this.command = command;
	}


	
	/**
	 * Returns a string representation of this SpineSetupSensor object.
	 * 
	 * @return the String representation of this SpineSetupSensor object
	 */
	public String toString() {
		String s = "SpineServiceCommand {";
		

		
		return s;
	}
	
}
