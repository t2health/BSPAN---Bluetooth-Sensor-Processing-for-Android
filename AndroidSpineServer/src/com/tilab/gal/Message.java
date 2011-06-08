package com.tilab.gal;


public class Message {
	
	// This next set is from TinyOS Message------------------------------
    /**
     * The maximum number of characters read from an 8-bit array field
     * being converted into a Java String.
     */
    public static final int MAX_CONVERTED_STRING_LENGTH = 512;

    /** 
     * The underlying byte array storing the data for this message. 
     * This is private to enforce access to the data through the accessor
     * methods in this class, which do bounds checking and manage the
     * base_offset for embedded messages.
     */
    private byte[] tinyOSdata;

    /** 
     * The base offset into the data. This allows the message data to
     * exist at some non-zero offset into the actual data.
     */
    protected int base_offset;

    /**
     * The actual length of the message data. Must be less than or
     * equal to (data.length - base_offset).
     */
    protected int data_length;

    /**
     * The AM type corresponding to this object. Set to -1 if no AM type
     * is known.
     */
    protected int am_type;	
	
    
    /**
     * Return the length of the data (in bytes) contained in this message.
     */
    public int dataLength() {
        return data_length;
    }

    /**
     * Return the active message type of this message (-1 if unknown)
     */
    public int amType() {
	return am_type;
    }    
    
    /**
     * Set the active message type of this message
     */
    public void amTypeSet(int type) {
      this.am_type = type;
    }    
    
    public void dataSet(byte[] data) {
    	this.tinyOSdata = new byte[data.length];
    	System.arraycopy(data, 0,
		       this.tinyOSdata, 0,
		       data.length);	
    }    
	
    /**
     * Return the raw byte array representing the data of this message.
     * Note that only indices in the range
     * (this.baseOffset(), this.baseOffset()+this.dataLength()) are
     * valid. 
     */
    public byte[] dataGet() {
	return this.tinyOSdata;
    }    
	// This next set is from TinyOS Message------------------------------
    
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
		  this.dstAddr = dstAddr;
		  this.destEndpoint = destEndpoint;
		  this.srcEndpoint = srcEndpoint;
		  this.clusterId = clusterId;
		  this.transSeqNumber = transId;
		  this.options = options;
		  this.radius = radius;
		  this.len = len;
		  this.data = data;
		  
	  }

	  public Message()
	  {
		  
	  }

	  public short getSrcAddr()
	  {
		  return this.srcAddr;
	  }


	  public short getSrcEndpoint()
	  {
		  return this.srcEndpoint;
	  }

	  public short getDstAddr()
	  {
		  return this.dstAddr;
	  }

	  public short getDestEndpoint()
	  {
		  return this.destEndpoint;
	  }

	  public java.lang.String getSourceURL()
	  {
		  return this.sourceURL;
	  }

	  public java.lang.String getDestinationURL()
	  {
		  return this.destinationURL;
	  }

	  
	  public void setDestinationURL(java.lang.String destinationURL)
	  {
		  this.destinationURL = destinationURL;
	  }

	  public java.lang.String getConnectionURL()
	  {
		  return this.connectionURL;
	  }

	  public boolean isBroadcast()
	  {
		  return (wasBroadcast != 0) ? true:false;
	  }

	  public short getLinkQuality()
	  {
		  return this.linkQuality;
	  }

	  public void setLinkQuality(short linkQuality)
	  {
		  this.linkQuality = linkQuality;
	  }

	  public short getSecurityStatus()
	  {
		  return this.securityUse;
	  }
	  
	  // Method descriptor #110 (S)V
	  // Stack: 2, Locals: 2
	  public void setSecurityStatus(short securityStatus)
	  {
		  this.securityUse = securityStatus;
	  }

	  public long getTimestamp()
	  {
		  return this.timestamp;
	  }

	  public short getTransNo()
	  {
		  return this.transSeqNumber;
	  }

	  public byte getTxSettings()
	  {
		  return this.txSettings;
	  }

	  public void setTxSettings(byte txSettings)
	  {
		  this.txSettings =  txSettings;
	  }

	  public short getOptions()
	  {
		  return this.options;
	  }
	  public short getRadius()
	  {
		  return this.radius;
	  }
	  public int getMaxHopsNumber()
	  {
		  return this.maxHopsNumber;
	  }
	  public void setMaxHopsNumber(int maxHopsNumber)
	  {
		  this.maxHopsNumber = maxHopsNumber;
	  }
	  public short getLen()
	  {
		  return this.len;
	  }
	  public short[] getPayload()
	  {
		  return this.data;
	  }
	  public void setPayload(short[] payload)
	  {
		  this.data = payload;
	  }
	  public short getClusterId()
	  {
		  return this.clusterId;
	  }
	  public void setClusterId(short clusterId)
	  {
		  this.clusterId = clusterId;
	  }
	  public short getProfileId()
	  {
		  return this.profileId;
	  }
	  public void setProfileId(short profileId)
	  {
		  this.profileId = profileId;
	  }
	  public short getGroupId()
	  {
		  return this.groupId;
	  }
}
