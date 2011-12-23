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

import java.io.Serializable;

public class Address implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String address = null;
	
	
	/**
	 * Default constructor of an Address object.
	 * 
	 * @param address the String representation of the address
	 */
	public Address(String address) {
		this.address = address;
	}
	
	/**
	 * This method returns the int representation of this Address.
	 * If the address cannot be directly represented as an int (e.g. for IP addresses),
	 * an hash-code of this address will be returned.
	 * 
	 * @return the int representation of this Address
	 */
	public int getAsInt() {		
		try {
			return Integer.parseInt((this.address.toString()));
		} catch (NumberFormatException e) {
			return this.hashCode();
		}
	}
	
	/**
	 * Returns a hash code value for the String representation of this address
	 * 
	 * @return a hash code value for this object
	 */
	public int hashCode() {
		return this.address.hashCode();
	}
	
	/**
	 * Returns the String representation of this Address
	 * 
	 * @return the String representation of this Address
	 */
	public String toString() {
		return address;
	}
	
	/**
	 * 
	 * @param anAddress the Address to compare this Address against 
	 * @return true whether this address is equals to the given one; false otherwise.
	 */
	public boolean equals(Object anAddress) {
		return this.address.equals(((Address)anAddress).address);
	}
	
}
