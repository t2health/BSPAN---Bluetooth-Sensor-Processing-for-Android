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
 * This is the interface representing the SPINE events listener
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package spine;

import java.util.Vector;

import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;

public interface SPINEListener {
	
	/**
	 * This method is invoked by the SPINEManager to its registered listeners
	 * when it receives a ServiceAdvertisement message from a BSN node
	 * 
	 * @param newNode the node discovered
	 * 
	 * @see spine.datamodel.Node
	 */
	public void newNodeDiscovered(Node newNode);

	/**
	 * This method is invoked by the SPINEManager to its registered listeners
	 * when a ServiceMessage is received from a particular node.  
	 * The SPINEManager itself can generate service messages ('nodeID' will be SPINEPacketsConstants.SPINE_BASE_STATION) 
	 * notifying the application of anomalies or other information. 
	 * 
	 * @param msg the service message 
	 */
	public void received(ServiceMessage msg); 

	/**
	 * This method is invoked by the SPINEManager to its registered listeners
	 * when it receives new data from the specified node.
	 * The generic Data object contains the information about the type of data carried.
	 * It's up to the application that handle this event to check what kind of data are in there 
	 * (thru the Data method getFunctionCode); take the actual data (thru the Data method getData) and 
	 * cast it properly (i.e.; if the function generating the data is FEATURE, then the data will be 
	 * a Vector of Feature objects - because this is how the FeatureData class decode the low level packet sent over-the-air 
	 * by the node to the coordinator.) 
	 * 
	 * @param data the data received from the node 'nodeID'
	 * 
	 * @see spine.datamodel.Data
	 */
	public void received(Data data);

	/**
	 * This method is invoked by the SPINEManager to its registered listeners
	 * when the discovery procedure timer fires. 
	 * It provides a Vector of spine.datamodel.Node objects representing the discovered nodes. 
	 * It's possible to change the timer period, before discovering the WSN, 
	 * with the method: setDiscoveryProcedureTimeout. 
	 * 
	 * @param activeNodes the list of discovered nodes.
	 * 
	 * @see spine.datamodel.Node
	 */
	public void discoveryCompleted(Vector activeNodes);
	
}
