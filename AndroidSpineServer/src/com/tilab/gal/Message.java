package com.tilab.gal;

public class Message {
	
	  // Field descriptor #8 S
	  protected short srcAddr;
	  
	  // Field descriptor #8 S
	  protected short srcEndpoint;
	  
	  // Field descriptor #8 S
	  protected short dstAddr;
	  
	  // Field descriptor #8 S
	  protected short destEndpoint;
	  
	  // Field descriptor #13 Ljava/lang/String;
	  protected java.lang.String sourceURL;
	  
	  // Field descriptor #13 Ljava/lang/String;
	  protected java.lang.String destinationURL;
	  
	  // Field descriptor #13 Ljava/lang/String;
	  protected java.lang.String connectionURL;
	  
	  // Field descriptor #8 S
	  protected short wasBroadcast;
	  
	  // Field descriptor #8 S
	  protected short linkQuality;
	  
	  // Field descriptor #8 S
	  protected short securityUse;
	  
	  // Field descriptor #20 J
	  protected long timestamp;
	  
	  // Field descriptor #8 S
	  protected short transSeqNumber;
	  
	  // Field descriptor #23 B
	  protected byte txSettings;
	  
	  // Field descriptor #8 S
	  protected short options;
	  
	  // Field descriptor #8 S
	  protected short radius;
	  
	  // Field descriptor #27 I
	  protected int maxHopsNumber;
	  
	  // Field descriptor #8 S
	  protected short len;
	  
	  // Field descriptor #30 [S
	  protected short[] data;
	  
	  // Field descriptor #8 S
	  protected short clusterId;
	  
	  // Field descriptor #8 S
	  protected short profileId;
	  
	  // Field descriptor #8 S
	  protected short groupId;
	  
	  // Field descriptor #27 I
	  static final int SECURITYSTATUS_UNSECURE = 0;
	  
	  // Field descriptor #27 I
	  static final int SECURITYSTATUS_SECUREDNETWORKKEY = 1;
	  
	  // Field descriptor #27 I
	  static final int SECURITYSTATUS_SECUREDLINKKEY = 2;
	  
	  // Field descriptor #23 B
	  static final byte TXSETTING_SECURITYENABLEDTRANSMISSION = 1;
	  
	  // Field descriptor #23 B
	  static final byte TXSETTING_USENETWORKKEY = 2;
	  
	  // Field descriptor #23 B
	  static final byte TXSETTING_ACKNOWLEDGEDTRANSMISSION = 4;
	  
	  // Field descriptor #23 B
	  static final byte TXSETTING_FRAGMENTATIONPERMITTED = 8;
	  
	  public Message(short dstAddr, short destEndpoint, short srcEndpoint, short clusterId, short transId, short options, short radius, short len, short[] data)
	  {
		  
	  }

	  public Message()
	  {
		  
	  }

	  public short getSrcAddr()
	  {
		  return 0;
	  }


	  public short getSrcEndpoint()
	  {
		  return 0;
	  }

	  public short getDstAddr()
	  {
		  return 0;
	  }

	  public short getDestEndpoint()
	  {
		  return 0;
	  }

	  public java.lang.String getSourceURL()
	  {
		  return "";
	  }

	  public java.lang.String getDestinationURL()
	  {
		  return "";
	  }

	  
	  public void setDestinationURL(java.lang.String destinationURL)
	  {
		  
	  }

	  public java.lang.String getConnectionURL()
	  {
		  return "";
	  }

	  public boolean isBroadcast()
	  {
		  return false;
	  }

	  public short getLinkQuality()
	  {
		  return 0;
	  }

	  public void setLinkQuality(short linkQuality)
	  {
		  
	  }

	  public short getSecurityStatus()
	  {
		  return 0;
	  }
	  
	  // Method descriptor #110 (S)V
	  // Stack: 2, Locals: 2
	  public void setSecurityStatus(short securityStatus)
	  {
		  
	  }

	  public long getTimestamp()
	  {
		  return 0;
	  }

	  public short getTransNo()
	  {
		  return 0;
	  }

	  public byte getTxSettings()
	  {
		  return 0;
	  }

	  public void setTxSettings(byte txSettings)
	  {
		  
	  }

	  public short getOptions()
	  {
		  return 0;
	  }
	  public short getRadius()
	  {
		  return 0;
	  }
	  public int getMaxHopsNumber()
	  {
		  return 0;
	  }
	  public void setMaxHopsNumber(int maxHopsNumber)
	  {
		  
	  }
	  public short getLen()
	  {
		  return 0;
	  }
	  public short[] getPayload()
	  {
		  short[] f = {0};
		  
		  return f;
	  }
	  public void setPayload(short[] payload)
	  {
		  
	  }
	  public short getClusterId()
	  {
		  return 0;
	  }
	  public void setClusterId(short clusterId)
	  {
		  
	  }
	  public short getProfileId()
	  {
		  return 0;
	  }
	  public void setProfileId(short profileId)
	  {
		  
	  }
	  public short getGroupId()
	  {
		  return 0;
	  }
}
