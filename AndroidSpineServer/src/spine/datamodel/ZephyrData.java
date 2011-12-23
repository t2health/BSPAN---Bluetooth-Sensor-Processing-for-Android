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

package spine.datamodel;


public class ZephyrData extends Data {

	private static final long serialVersionUID = 1L;
	
	public int heartRate;
	public int respRate;
	public int skinTemp;
	
	public ZephyrData() {
	}

	public ZephyrData(int heartRate, int respRate, int skinTemp) {
		this.heartRate = heartRate;
		this.respRate = respRate;
		this.skinTemp = skinTemp;
	}
	
	public String getParameterName(int index) {
		switch (index) {
		case MindsetData.NUM_BANDS + 2:
			return "HeartRate";
			
		case MindsetData.NUM_BANDS + 2 + 1:
			return "RespRate";
			
		case MindsetData.NUM_BANDS + 2 + 2:
			return "SkinTemp";
		}
		return "";
	}
	
	public int getFeatureValue(int index) {
		switch (index) {
		case MindsetData.NUM_BANDS + 2:
			return heartRate;
			
		case MindsetData.NUM_BANDS + 2 + 1:
			return respRate;
			
		case MindsetData.NUM_BANDS + 2 + 2:
			return skinTemp;
		}
		return 0;
	}	
	
	public String getLogDataLine() {
		String line = "";							// Comment
		line += heartRate + ", " + respRate + ", " + skinTemp + ", ";
		return line;
	}
	
	public String getLogDataLineHeader() {
		String line = "HeartRate, RespRate, SkinTemp,";							// Comment

		return line;
	}
	
	
	
}
