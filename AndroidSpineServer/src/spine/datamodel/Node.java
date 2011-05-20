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
 *  This class represents the Node entity.
 *  It contains a constructor, a toString and getters methods.
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package spine.datamodel;

import java.util.Vector;

import spine.Properties;
import spine.datamodel.functions.Function;
import spine.datamodel.functions.SpineObject;

public class Node implements SpineObject {
	
	private static final long serialVersionUID = 1L;
	
	private static final String NEW_LINE = Properties.getDefaultProperties().getProperty(Properties.LINE_SEPARATOR_KEY);
	
	private Address physicalID = null;
	
	private Address logicalID = null;
	
	private Vector sensorsList = new Vector(); // <values:Sensor>
	
	private Vector functionsList = new Vector(); // <values:Function>
	
	
	/**
	 * Constructor of a Node object. ** For in-framework use only. **
	 * Application-level classes should never use this constructor, but rather get a node 
	 * by looking at the activeNode Vector of the spine.SPINEManager
	 */
	public Node(Address physicalID) {
		this.physicalID = physicalID;
	}
	
	/**
	 * Getter method of the node sensors list
	 * @return the sensors list of the node
	 */
	public Vector getSensorsList() {
		return sensorsList;
	}
	
	/**
	 * Setter method of the node sensors list. ** For in-framework use only. **
	 * 
	 * @param sensorsList the Vector containing the various Sensor the node is composed of
	 */
	public void setSensorsList(Vector sensorsList) {
		this.sensorsList = sensorsList;
	}
	
	

	/**
	 * Getter method of the node functionality (function libraries) list
	 * 
	 * @return the functionality list of the node
	 */
	public Vector getFunctionsList() {
		return functionsList;
	}
	
	/**
	 * Setter method of the node functionality (function libraries) list. ** For in-framework use only. **
	 * 
	 * @param functionsList the Vector containing the various Function this node is able to compute
	 */
	public void setFunctionsList(Vector functionsList) {
		this.functionsList = functionsList;
	}
	
	/**
	 * Getter method of the physicalID attribute
	 * 
	 * @return the physicalID of this Node
	 */
	public Address getPhysicalID() {
		return physicalID;
	}

	/**
	 * Setter method of the logicalID attribute
	 * 
	 * @param logicalID the logical ID of this Node
	 */
	public void setLogicalID(Address logicalID) {
		this.logicalID = logicalID;
	}

	/**
	 * Getter method of the logicalID attribute
	 * 
	 * @return the logicalID of this Node
	 */
	public Address getLogicalID() {
		return logicalID;
	}
	
	/**
	 * 
	 * Returns a short string representation of this Node.
	 * 
	 */
	public String toShortString() {
		return "phyID:" + this.physicalID + ", logID:" + this.logicalID;
	}
	
	/**
	 * 
	 * Returns a string representation of this Node.
	 * 
	 */
	public String toString() {
		String s = "Physical Node ID: " + this.physicalID + NEW_LINE;
		s += "Logical Node ID: " + this.logicalID + NEW_LINE;
		
		s += "OnBoard Sensors:" + NEW_LINE;
		for (int i = 0; i<this.sensorsList.size(); i++) 
			s += "  " + (Sensor)this.sensorsList.elementAt(i) + NEW_LINE;
		
		s += "Supported Functions:" + NEW_LINE;
		for (int i = 0; i<this.functionsList.size(); i++) 
			s += "  " + (Function)this.functionsList.elementAt(i) + NEW_LINE;
		
		return s;
		
	}

	
}
