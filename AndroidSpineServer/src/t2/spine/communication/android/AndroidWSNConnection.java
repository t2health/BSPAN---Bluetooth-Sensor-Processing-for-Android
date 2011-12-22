/**
 * Implementation of the GAL WSNConnection.
 * This class, together with AndroidLocalNodeAdapter, implements the generic API to accessing
 * Android from the upper layers. 
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Alessia Salmeri
 * @author Scott Coleman
 *
 * @version 1.0
 */

package t2.spine.communication.android;

import jade.util.Logger;

import java.io.InterruptedIOException;

import com.tilab.gal.WSNConnection;

import spine.Properties;
import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;

/**
 * Encapsulates the data communication interface between the event dispatcheer 
 * and the local node adapter
 * 
 * @author scott.coleman
 *
 */
public class AndroidWSNConnection implements WSNConnection {

	private byte sequenceNumber = 0; 
	
	private WSNConnection.Listener listener = null;	
	
	private AndroidLocalNodeAdapter adapter = null;
	
	
	protected AndroidWSNConnection (AndroidLocalNodeAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * Passes message received from sensor to the lister (Event handler)
	 * @param msg
	 */
	public void messageReceived(com.tilab.gal.Message msg) {
		listener.messageReceived(msg);
	}
	
	public void close() {
	}

	public com.tilab.gal.Message poll() {
		return null;
	}

	public com.tilab.gal.Message receive() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tilab.gal.WSNConnection#send(com.tilab.gal.Message)
	 */
	public void send(com.tilab.gal.Message msg) throws InterruptedIOException, UnsupportedOperationException {
		
		byte fragmentNr = 1;
		byte totalFragments = 1;
		
		byte[] compressedPayload = new byte[0];
		try {
			// create a SPINE Android dependent message from a high level Message object
			int destNodeID = Integer.parseInt(msg.getDestinationURL().substring(Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.ANDROID + "_" + Properties.URL_PREFIX_KEY).length()));
			
			try {
				short[] compressedPayloadShort = msg.getPayload();
				compressedPayload = new byte[compressedPayloadShort.length];
				for (int i = 0; i<compressedPayloadShort.length; i++)
					compressedPayload[i] = (byte)compressedPayloadShort[i];
			} catch (Exception e) {}
			
			AndroidMessage tosmsg = new AndroidMessage((byte)msg.getClusterId(), (byte)msg.getProfileId(),
														 SPINEPacketsConstants.SPINE_BASE_STATION, destNodeID, 
														 this.sequenceNumber++, fragmentNr, totalFragments, compressedPayload);
			
			// sends the platform dependent message using the local node adapter
			adapter.send(destNodeID, tosmsg);
			
			if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
				StringBuffer str = new StringBuffer();
				str.append("SENT -> ");
				str.append(tosmsg);
				SPINEManager.getLogger().log(Logger.INFO, str.toString());
			}	
			
			if ((byte)msg.getClusterId() == SPINEPacketsConstants.RESET)
				this.sequenceNumber = 0;
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.INFO, e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.INFO, e.getMessage());
		} 
		
	}

	/* (non-Javadoc)
	 * @see com.tilab.gal.WSNConnection#setListener(com.tilab.gal.WSNConnection.Listener)
	 */
	public void setListener(WSNConnection.Listener l) {
		this.listener = l;		
	}
}
