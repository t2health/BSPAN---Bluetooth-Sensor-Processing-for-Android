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
 * @see AndroidMessageServer
 */
package t2.spine.communication.android;

import jade.util.Logger;

import java.util.Vector;

import spine.SPINEManager;
import spine.SPINEPacketsConstants;


//import com.t2.AndroidSocketThrdServer;
import com.tilab.gal.ConfigurationDescriptor;
import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.WSNConnection;


import net.tinyos.message.MessageListener;

public final class AndroidLocalNodeAdapter extends LocalNodeAdapter implements MessageListener {
	
	private Vector connections = new Vector(); // <values: WSNConnection>
	
	private String host = null;
	private String port = null;
	
	/**
	 * Vector for holding message fragments sent from a sensor before they are re-assembled and forwarded 
	 */
	private Vector partials = new Vector(); // <values: Partial>
	
	/**
	 * Vector for holding outbound messages in the case that we are in low power mode
	 * (indicated by sendImmediately = false)
	 * @see sendImmediately
	 */
	private Vector messagesQueue = new Vector(); // <values: Msg>
	
	/**
	 * Boolean saying whether to send messages to the sendor right away
	 * or hold them until the next message is received from the sensor
	 * (after which they are sent as a batch)
	 */
	private boolean sendImmediately = true;

	/**
	 * The node coordinator is responsible for the low level details transmitting/receiving messages
	 */
	private AndroidMessageServer nodeCoordinator = null;	
	
	
	public  void init (Vector parms) {
	}
	
	/* (non-Javadoc)
	 * @see com.tilab.gal.LocalNodeAdapter#start()
	 */
	public void start () {
		if (nodeCoordinator == null) {
			nodeCoordinator = new AndroidMessageServer();
			if (SPINEManager.getLogger().isLoggable(Logger.INFO)) 
			SPINEManager.getLogger().log(Logger.INFO, "AndroidLLocalNodeAdapter in wainting ...");

			nodeCoordinator.registerListener(this);			
		}
	}

	/* (non-Javadoc)
	 * @see com.tilab.gal.LocalNodeAdapter#stop()
	 */
	public void stop() {
		nodeCoordinator = null;
	}
	
	/* (non-Javadoc)
	 * @see com.tilab.gal.LocalNodeAdapter#reset()
	 */
	public void reset() {
		this.stop();
		this.start();
	}
	
	/**
	 * Initiates sending (to the sensor) of messages built up in the messagesQueue
	 *  @see messageReceived
	 *  @see send
	 * 
	 * @param nodeID	Source id of node sending the message
	 */
	protected void sendMessages(int nodeID) {		
		Msg curr = null;	
		for (int i = 0; i<this.messagesQueue.size(); i++) {
			curr = (Msg)this.messagesQueue.elementAt(i);
			if (curr.destNodeID == nodeID || curr.destNodeID == SPINEPacketsConstants.SPINE_BROADCAST) {
				nodeCoordinator.sendCommand(curr.destNodeID, curr.tosmsg);
				this.messagesQueue.removeElementAt(i);
				try { Thread.sleep(2); } catch (InterruptedException e) {}
			}
		}		
	}

	/**
	 * Sends the specified message to the specified destination node
	 *   Note that if sendImmediately is true the message is send immediately
	 *   to the destination node.
	 *   If sendImmediately is false (which indicates power saving mode) the message
	 *   goes into messagesQueue. Messages in messagesQueue are send after the next reception
	 *   of a message from the node.
	 *  @see sendMessages
	 *  @see messageReceived
	 *   
	 * @param destNodeID	ID of the destination
	 * @param tosmsg		Message to send
	 */
	protected synchronized void send(int destNodeID, AndroidMessage tosmsg) {
		if(this.sendImmediately) {
			nodeCoordinator.sendCommand(destNodeID, tosmsg);
			try {
			 // check if the flag radioAlwaysOn flag is false
				if(tosmsg.getHeader().getPktType() == SPINEPacketsConstants.START && tosmsg.getRawPayload()[2] == 0)
					this.sendImmediately = false;

			}  catch (IllegalSpineHeaderSizeException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.WARNING)) 
					SPINEManager.getLogger().log(Logger.WARNING, "[SPINE1.3-MALFORMED-HEADER]... discarded!");
			}			
		}
		else 
			this.messagesQueue.addElement(new Msg(destNodeID, tosmsg));
	}
	
	
	// BELOW FROM TOS MOTEIF VERSION
	
	public void messageReceived(int srcID, net.tinyos.message.Message tosmsg) {
		if (tosmsg instanceof AndroidMessage) {			
			try {
				SPINEHeader h = ((AndroidMessage)tosmsg).getHeader();
				int sourceNodeID = h.getSourceID();
				
				// some controls for reducing the risk of start elaborating erroneous received messages 
				if(sourceNodeID == SPINEPacketsConstants.SPINE_BASE_STATION || 
				   sourceNodeID == SPINEPacketsConstants.SPINE_BROADCAST || 
				   h.getVersion() != SPINEPacketsConstants.CURRENT_SPINE_VERSION || 
				   h.getDestID() != SPINEPacketsConstants.SPINE_BASE_STATION || 
				   h.getGroupID() != SPINEManager.getMyGroupID()) 
					return;

				// Since we know the radio is now on, go ahead and send
				// any outgoing messages queued up.
				sendMessages(sourceNodeID);
				
				// re-assembly of fragments into complete messages 
				if (h.getTotalFragments() != 1) {
					int index = inPartials(sourceNodeID, h.getSequenceNumber());
					if (index == -1) {
						if (h.getFragmentNumber() != 1)
							return;
						else {
							partials.addElement(new Partial(sourceNodeID, h.getSequenceNumber(), 
															h.getTotalFragments(), 
															((AndroidMessage)tosmsg).getRawPayload()));
							return;
						}
					}
					else {
						if (h.getFragmentNumber() != ( ((Partial)partials.elementAt(index)).lastFragmentNr + 1 ) ) {
							partials.removeElementAt(index); // no need to keep a partial if a fragment is lost
							return;
						}
						else {
							if (h.getFragmentNumber() < ((Partial)partials.elementAt(index)).totFragments) {
								((Partial)partials.elementAt(index)).addToPayload( ((AndroidMessage)tosmsg).getRawPayload() );
								return;
							}
							else {
								Partial complete = ((Partial)partials.elementAt(index));
								complete.addToPayload( ((AndroidMessage)tosmsg).getRawPayload() );
								((AndroidMessage)tosmsg).setRawPayload(complete.partialPayload);
								partials.removeElementAt(index);								
							}
						}
					}
				}
				
				// notification to upper layer of a message reception
				com.tilab.gal.Message msg = ((AndroidMessage)tosmsg).parse();					
				for (int i = 0; i<connections.size(); i++)
					((AndroidWSNConnection)connections.elementAt(i)).messageReceived(msg);				
				
			} catch (IllegalSpineHeaderSizeException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
					SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			}			
		}
	}
	
	/**
	 * Searches for message in partials array
	 * 
	 * @param sourceID			Source ID of message to search
	 * @param sequenceNumber	Sequence number of message to search
	 * @return		Returns index of partial message in partials if it is found
	 */
	private int inPartials(int sourceID, byte sequenceNumber) {
		for (int i = 0; i<this.partials.size(); i++)
			if (((Partial)partials.elementAt(i)).equal(sourceID, sequenceNumber)) return i;
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see com.tilab.gal.LocalNodeAdapter#createAPSConnection()
	 */
	public WSNConnection createAPSConnection() {
		WSNConnection newConnection = new AndroidWSNConnection(this);
		connections.add(newConnection);
		return newConnection;
	}
	
	public ConfigurationDescriptor getConfigurationDescriptor() {
		return null;
	}
			
	/**
	 * Sensors may break up large message packets into fragments.
	 * This class is used to store these fragments before they are re-assembled
	 * and send to the event dispatcher
	 * 
	 * @author scott.coleman
	 *
	 */
	private class Partial {
		int nodeID;
		byte seqNr;
		byte lastFragmentNr;
		byte totFragments;
		byte[] partialPayload;
		
		private Partial(int nodeID, byte seqNr, byte totFragments, byte[] partialPayload) {
			this.nodeID = nodeID;
			this.seqNr = seqNr;
			this.lastFragmentNr = 1;
			this.totFragments = totFragments;
			this.partialPayload = partialPayload;
		}
		
		private void addToPayload(byte[] newPartial) {
			byte[] newPartialPayload = new byte[partialPayload.length + newPartial.length];
			System.arraycopy(partialPayload, 0, newPartialPayload, 0, partialPayload.length);
			System.arraycopy(newPartial, 0, newPartialPayload, partialPayload.length, newPartial.length);
			
			partialPayload = newPartialPayload;
			
			lastFragmentNr++;
		}
		
		private boolean equal(int nodeID, byte seqNr) {
			return (this.nodeID == nodeID && this.seqNr == seqNr);
		}
		
	}

	/**
	 * Generic base class for partial messages stored in the partials array
	 * @author scott.coleman
	 *
	 */
	private class Msg {
		int destNodeID;
		AndroidMessage tosmsg;
		
		private Msg(int destNodeID, AndroidMessage tosmsg) {
			this.destNodeID = destNodeID;
			this.tosmsg = tosmsg;
		}
	}	
	
}