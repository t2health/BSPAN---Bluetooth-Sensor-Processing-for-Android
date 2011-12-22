package com.tilab.gal;

/**
 * Interface class defining the Wireless Sensor Network communication
 * @author scott.coleman
 *
 */
public abstract interface WSNConnection {
	
	/**
	 * Sets up listener for messages received from the local node adapter
	 * @param arg0
	 */
	public abstract void setListener(com.tilab.gal.WSNConnection.Listener arg0);
	  
	/**
	 * Sends a message to the local node adapter (to be forwarded to the sensor node)
	 * @param arg0
	 * @throws java.io.InterruptedIOException
	 * @throws java.lang.UnsupportedOperationException
	 */
	public abstract void send(com.tilab.gal.Message arg0) throws java.io.InterruptedIOException, java.lang.UnsupportedOperationException;
	  
	public abstract com.tilab.gal.Message receive();
	  
	public abstract com.tilab.gal.Message poll();
	  
	public abstract void close();	
	  
	public interface Listener
	{
		public void messageReceived(com.tilab.gal.Message msg);
	}
}
