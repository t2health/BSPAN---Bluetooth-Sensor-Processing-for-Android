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
 *  This class represents the generic Address entity.
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

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
