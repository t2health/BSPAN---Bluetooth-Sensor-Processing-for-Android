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
 *  This class represents the Functionality entity.
 *  It contains a constructor, a toString and getters methods.
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package spine.datamodel;

import java.io.Serializable;

import spine.SPINEFunctionConstants;

public class Functionality implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private byte functionType;
	private byte functionalityCode;
	
	/**
	 * Constructor of a Functionality object
	 * @param functionType the function type of the functionality
	 * @param functionalityCode the code of this functionality
	 * 
	 * @see spine.SPINEFunctionConstants
	 */
	protected Functionality(byte functionType, byte functionalityCode) {
		this.functionType = functionType;
		this.functionalityCode = functionalityCode; 
	}
	
	/**
	 * Getter method of the function type of this functionality
	 * @return the the function type of this functionality
	 */
	protected byte getFunctionType() {
		return functionType;
	}

	/**
	 * Getter method of the code of this functionality
	 * @return the code of this functionality
	 */
	protected byte getFunctionalityCode() {
		return functionalityCode;
	}
	
	/**
	 * 
	 * Returns a string representation of the Functionality object.
	 * 
	 * @return a string representation of this Functionality
	 * 
	 */
	public String toString() {
		return SPINEFunctionConstants.functionCodeToString(functionType) + " - " + 
			   SPINEFunctionConstants.functionalityCodeToString(functionType, functionalityCode);
	}
}
