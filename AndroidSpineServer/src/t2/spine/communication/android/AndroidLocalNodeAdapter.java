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
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.â€  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 *****************************************************************/

/**
 * Implementation of the GAL LocalNodeAdapter.
 * 
 * This class is responsible to receive and transmit packets from-to Android. 
 * 
 * Note that this class is only used internally at the framework. 
 *
 * @author Alessia Salmeri
 * @author Scott Coleman
 *
 * @version 1.0
 * 
 * @see LocalNodeAdapter
 * @see SocketMessageListener
 */

package t2.spine.communication.android;

import jade.util.Logger;

import java.util.Vector;
import java.io.*;

import spine.Properties;
import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;

import android.content.Intent;
import android.util.Log;

//import com.t2.AndroidSocketThrdServer;
import com.t2.biofeedback.Constants;
import com.tilab.gal.ConfigurationDescriptor;
import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.Message;
import com.tilab.gal.WSNConnection;

import java.util.Hashtable;
import java.util.Enumeration;

public class AndroidLocalNodeAdapter extends LocalNodeAdapter implements AndroidSocketMessageListener {

	// Node Information Hash Table
	Hashtable nodeInfo = new Hashtable();

	int nodeId;

	int nodeConnected = 0;

	/**
	 * This method is called by the nodeCoordinator (AndroidMessageServer) when a
	 * new message is received by the nodeCoordinator.
	 */
	public void messageReceived(int srcID, Message msg) {

		String sourceURL = "";
		if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
			StringBuffer str = new StringBuffer();
			str.append("Msg Received from nodeCoordinator (AndroidSocketThrdServer) -> ");
			str.append(msg);
			SPINEManager.getLogger().log(Logger.INFO, str.toString());
		}

		// Case "Service Advertisement Message" (ProfileId != 0) => set nodeInfo
		// HashTable and pass - thru to connections
		if ((msg.getProfileId() != 0)) {
			// nodeId from sourceURL
			sourceURL = msg.getSourceURL();
			String urlPrefix = Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.ANDROID + "_" + Properties.URL_PREFIX_KEY);
			nodeId = Integer.parseInt(sourceURL.substring(urlPrefix.length()));

			// Add "nodeId"/"node info msg" to nodeInfo
			nodeInfo.put(new Integer(nodeId), msg);

			// pass - thru connections
			for (int i = 0; i < connections.size(); i++)
				((AndroidWSNConnection) connections.elementAt(i)).messageReceived(msg);
		} else {
			// Case "Data Message" (ProfileId = 0) => pass - thru to connections
			for (int i = 0; i < connections.size(); i++)
				((AndroidWSNConnection) connections.elementAt(i)).messageReceived(msg);
		}
	}

	private Vector connections = new Vector();

	protected String motecom = null;

	protected String port = null;

	protected String speed = null;

	public WSNConnection createAPSConnection() {
		WSNConnection newConnection = new AndroidWSNConnection(this);
		connections.add(newConnection);
		return newConnection;
	}

	public ConfigurationDescriptor getConfigurationDescriptor() {
		return null;
	}

	public void init(Vector parms) {
	}

	public void reset() {
	}

//	AndroidSocketThrdServer nodeCoordinator;
	AndroidMessageServer nodeCoordinator;

	public void start() {
		nodeCoordinator = new AndroidMessageServer();
		if (SPINEManager.getLogger().isLoggable(Logger.INFO)) 
		SPINEManager.getLogger().log(Logger.INFO, "AndroidLLocalNodeAdapter in wainting ...");

		nodeCoordinator.registerListener(this);
		
//		if (SPINEManager.getLogger().isLoggable(Logger.INFO)) 
//			SPINEManager.getLogger().log(Logger.INFO, "AndroidLocalNodeAdapter nodeAdapter.start()");
//		nodeCoordinator.setTitle("WSN Emulator: Collector node");
//		WindowListener l = new WindowAdapter() {
//			public void windowClosing(WindowEvent e) {
//				System.exit(0);
//			}
//		};
//		nodeCoordinator.addWindowListener(l);
//		// AndroidLocalNodeAdapter is a SocketMessage listener
//		nodeCoordinator.pack();
//		nodeCoordinator.setVisible(true);
//		Thread t = new Thread(nodeCoordinator);
//		t.start();
	}

	public void stop() {
	}

	// send from AndroidLocalNodeAdapter to AndroidSocketThrdServer .... from
	// AndroidSocketThrdServer to Server Socket Node
	protected synchronized void send(int destNodeID, AndroidMessage emumsg) {
		int nodeId = -1;

		try {
			switch ((byte) emumsg.getClusterId()) {
			case SPINEPacketsConstants.START:
				if (SPINEManager.getLogger().isLoggable(Logger.INFO)) 
					SPINEManager.getLogger().log(Logger.INFO, "AndroidLocalNodeAdapter nodeAdapter.send() --> Cmd manager.start or manager.startWsn");
				// nodeCoordinator.sendCommand(destNodeID,"START");
				for (Enumeration e = nodeInfo.keys(); e.hasMoreElements();) {
					Integer key = (Integer) e.nextElement();
					
					if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
						StringBuffer str = new StringBuffer();
						str.append(key);
						str.append(":");
						str.append(nodeInfo.get(key));
						str.append("\nCase START --> emumsg:");
						str.append(emumsg.toString());
						str.append(" pktType=");
						str.append(emumsg.getClusterId());
						SPINEManager.getLogger().log(Logger.INFO, str.toString());
					}
					
					nodeId = key.intValue();
//					nodeCoordinator.sendCommand(key.intValue(), emumsg);
				}
				break;
			case SPINEPacketsConstants.RESET:
				if (SPINEManager.getLogger().isLoggable(Logger.INFO))
					SPINEManager.getLogger().log(Logger.INFO, "AndroidLocalNodeAdapter nodeAdapter.send() --> Cmd manager.resetWsn");				
				// nodeCoordinator.sendCommand(destNodeID, "RESET");
				nodeId = Integer.parseInt(emumsg.getDestinationURL());
//				nodeCoordinator.sendCommand(Integer.parseInt(emumsg.getDestinationURL()), emumsg);
				break;
			case SPINEPacketsConstants.SYNCR:
				if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
					StringBuffer str = new StringBuffer();
					str.append("AndroidLocalNodeAdapter nodeAdapter.send() --> Cmd manager.syncWsn or manager.synchrWsn\nCase SYNCR --> emumsg:");
					str.append(emumsg.toString());
					SPINEManager.getLogger().log(Logger.INFO, str.toString());
				
				}
				// nodeCoordinator.sendCommand(destNodeID, "SYNCR");
				break;
			case SPINEPacketsConstants.SERVICE_DISCOVERY:
				// Cmd discoveryWsn:execute from AndroidLocalNodeAdapter
				// for each nodo in nodeInfo pass-thru to connections
				if (SPINEManager.getLogger().isLoggable(Logger.INFO)) 
					SPINEManager.getLogger().log(Logger.INFO, "AndroidLocalNodeAdapter nodeAdapter.send() --> Cmd manager.discoveryWsn");
				for (Enumeration e = nodeInfo.keys(); e.hasMoreElements();) {
					Integer key = (Integer) e.nextElement();
					
					if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
						StringBuffer str = new StringBuffer();
						str.append(key);
						str.append(":");
						str.append(nodeInfo.get(key));
						str.append("\nCase SERVICE_DISCOVERY --> emumsg:");
						str.append(emumsg.toString());
						SPINEManager.getLogger().log(Constants.ANDROIDLOGTEMP, str.toString());
					}
					nodeId = key.intValue();
//					nodeCoordinator.sendCommand(key.intValue(), emumsg);
				}
				break;
			case SPINEPacketsConstants.SETUP_SENSOR:
				if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
					StringBuffer str = new StringBuffer();
					str.append("AndroidLocalNodeAdapter nodeAdapter.send() --> Cmd manager.setupSensor() or manager.setup(..., SpineSetupSensor)\nCase SETUP_SENSOR --> emumsg:");
					str.append(emumsg.toString());
					SPINEManager.getLogger().log(Logger.INFO, str.toString());
				}
				// nodeCoordinator.sendCommand(destNodeID, "SETUP_SENSOR");
				nodeId = Integer.parseInt(emumsg.getDestinationURL());
	//			nodeCoordinator.sendCommand(Integer.parseInt(emumsg.getDestinationURL()), emumsg);
				break;
			case SPINEPacketsConstants.SETUP_FUNCTION:
				if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
					StringBuffer str = new StringBuffer();
					str.append("AndroidLocalNodeAdapter nodeAdapter.send() --> Cmd manager.setupFunction() or manager.setup(..., SpineSetupFunction)\nCase SETUP_FUNCTION --> emumsg:");
					str.append(emumsg.toString());
					SPINEManager.getLogger().log(Logger.INFO, str.toString());
				}
				// nodeCoordinator.sendCommand(destNodeID, "SETUP_FUNCTION");
				nodeId = Integer.parseInt(emumsg.getDestinationURL());
	//			nodeCoordinator.sendCommand(Integer.parseInt(emumsg.getDestinationURL()), emumsg);
				break;
			case SPINEPacketsConstants.FUNCTION_REQ:
				if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
					StringBuffer str = new StringBuffer();
					str.append("AndroidLocalNodeAdapter nodeAdapter.send() --> FUNCTION_REQ\nCase FUNTION_REQ --> emumsg:");
					str.append(emumsg.toString());
					SPINEManager.getLogger().log(Logger.INFO, str.toString());
				}
				// nodeCoordinator.sendCommand(destNodeID, "FUNCTION_REQ");
				nodeId = Integer.parseInt(emumsg.getDestinationURL());
//				nodeCoordinator.sendCommand(Integer.parseInt(emumsg.getDestinationURL()), emumsg);
				break;
			default:
				if (SPINEManager.getLogger().isLoggable(Logger.WARNING))
					SPINEManager.getLogger().log(Logger.WARNING, "ERROR PktType");
			}
//		} 
//		catch (IOException e1) {
//			nodeInfo.remove(new Integer(nodeId));
		} catch (Exception e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
		}
	}
}
