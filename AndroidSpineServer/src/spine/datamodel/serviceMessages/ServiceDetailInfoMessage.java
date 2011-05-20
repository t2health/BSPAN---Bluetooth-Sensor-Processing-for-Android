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
