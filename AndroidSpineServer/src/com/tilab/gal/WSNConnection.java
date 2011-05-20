package com.tilab.gal;

public abstract interface WSNConnection {
	
	
	
	  // Method descriptor #6 (Lcom/tilab/gal/WSNConnection$Listener;)V
	  public abstract void setListener(com.tilab.gal.WSNConnection.Listener arg0);
	  
	  // Method descriptor #8 (Lcom/tilab/gal/Message;)V
	  public abstract void send(com.tilab.gal.Message arg0) throws java.io.InterruptedIOException, java.lang.UnsupportedOperationException;
	  
	  // Method descriptor #15 ()Lcom/tilab/gal/Message;
	  public abstract com.tilab.gal.Message receive();
	  
	  // Method descriptor #15 ()Lcom/tilab/gal/Message;
	  public abstract com.tilab.gal.Message poll();
	  
	  // Method descriptor #18 ()V
	  public abstract void close();	
	  
//		inner name: #24 Listener, accessflags: 1545 public abstract static]	

	  public interface Listener
	  {
		  public void messageReceived(com.tilab.gal.Message msg);
		  
	  }

}
