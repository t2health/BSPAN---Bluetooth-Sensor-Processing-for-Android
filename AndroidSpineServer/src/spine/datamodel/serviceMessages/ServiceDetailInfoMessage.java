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

package spine.datamodel.serviceMessages;



public class ServiceDetailInfoMessage extends ServiceInfoMessage {
	
	private static final long serialVersionUID = 1L;
	
	public static final int HIGH_LEVEL = 2, LOW_LEVEL = 1;
	protected int computationType;
	protected double statistics;
	protected int featureCode;
	protected byte channelMask;
	protected int numElemeForChannel;
	protected String description="description not available for this type";
	
	public ServiceDetailInfoMessage() {
		super();
	}

	public int getComputationType() {
		return computationType;
	}

	public void setComputationType(int computationType) {
		this.computationType = computationType;
	}

	public double getStatistics() {
		return statistics;
	}

	public void setStatistics(double statistics) {
		this.statistics = statistics;
	}

	public int getFeatureCode() {
		return featureCode;
	}

	public void setFeatureCode(int codiceFeature) {
		this.featureCode = codiceFeature;
	}

	public byte getChannelMask() {
		return channelMask;
	}

	public void setChannelMask(byte channelMask) {
		this.channelMask = channelMask;
	}

	public int getNumElemeForChannel() {
		return numElemeForChannel;
	}

	public void setNumElemeForChannel(int numElemeForChannel) {
		this.numElemeForChannel = numElemeForChannel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		return super.toString()+ description +"type :"+computationType+"  statValue="+statistics;
	}	

}
