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
