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
