package t2.spine.communication.android;

import spine.SPINEPacketsConstants;

class SPINEHeader {
	
	private byte headerBuf[] = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];
	
	private boolean canParse = false;
	private boolean canBuild = false;
	

	private byte vers;      // 2 bits
	private boolean ext;    // 1 bit
	private byte pktT;      // 5 bits

	private byte grpID;     // 8 bits

	private int srcID;    	// 16 bits

	private int dstID;    	// 16 bits

	private byte seqNr;     // 8 bits

	private byte fragNr;    // 8 bits
	private byte totFrags;  // 8 bits
	
	protected SPINEHeader (byte version, boolean extension, byte pktType, byte groupID, int sourceID, int destID, 
						byte sequenceNumber, byte fragmentNr, byte totalFragments) {
		
		this.vers = version;
		this.ext = extension;       
		this.pktT = pktType;      
		this.grpID = groupID;
		this.srcID = sourceID;
		this.dstID = destID; 
		this.seqNr = sequenceNumber; 
		this.fragNr = fragmentNr;    
		this.totFrags = totalFragments;
		
		this.canBuild = true;
	}
	
	protected SPINEHeader(byte[] header) throws IllegalSpineHeaderSizeException {
		if (header.length != SPINEPacketsConstants.SPINE_HEADER_SIZE) 
			throw new IllegalSpineHeaderSizeException(SPINEPacketsConstants.SPINE_HEADER_SIZE, header.length);
		else {
			this.headerBuf = header;
			this.canParse = true;
			parse();
		}
	}
	
	protected byte[] build() {
		
		if (!canBuild)
			return null;
		
		byte e = (this.ext)? (byte)1: (byte)0;    	
		headerBuf[0] = (byte)((this.vers<<6) | (e<<5) | this.pktT);
		
		headerBuf[1] = this.grpID;
		
		headerBuf[2] = (byte)(this.srcID>>8);
		headerBuf[3] = (byte)this.srcID;
		
		headerBuf[4] = (byte)(this.dstID>>8);
		headerBuf[5] = (byte)this.dstID;
		
		headerBuf[6] = this.seqNr;
		
		headerBuf[7] = this.fragNr;
		
		headerBuf[8] = this.totFrags;
		
		return headerBuf;
	}

	private boolean parse() {       
		if (!canParse)
			return false;
		
		vers = (byte)((headerBuf[0] & 0xC0)>>6);    		//  0xC0 = 11000000 binary
//		ext = ((byte)((headerBuf[0] & 0x20)>>5) == 1);     	//  0x20 = 00100000 binary For some reason android doesn't like this line
		byte b = (byte)(headerBuf[0] & 0x20);				// In fact, I can't even use a trinary operator!!
		if (b == 0x20)
			ext = true;
		else
			ext = false;
		
		pktT = (byte)(headerBuf[0] & 0x1F);       			//  0x1F = 00011111 binary
		grpID = headerBuf[1];
	   
		srcID = headerBuf[2];                  // check
		srcID = ((srcID<<8) | headerBuf[3]);

		dstID = headerBuf[4];  	              // check
		dstID = ((dstID<<8) | headerBuf[5]);

		seqNr = headerBuf[6];
	   
		fragNr = headerBuf[7];

		totFrags = headerBuf[8];

		return true;
	}

	protected byte getVersion() {
	   return vers;
	}

	protected boolean isExtended() {
	   return ext;
	}

	protected byte getPktType() {
	   return pktT;
	}

	protected byte getGroupID() {
	   return grpID;
	}

	protected int getSourceID() {
	   return srcID;
	}

	protected int getDestID() {
	   return dstID;
	}
	
	protected byte getSequenceNumber() {
	  return seqNr;
	}

	protected byte getFragmentNumber() {
	   return fragNr;
	}

	protected byte getTotalFragments() {
	   return totFrags;
	}
	
	protected byte[] getHeaderBuf() {
		return headerBuf;
	}
	
	public String toString() {
		
		String grp = (grpID<0)? Integer.toHexString(grpID+256): Integer.toHexString(grpID);		
		String seq = (seqNr<0)? ""+(seqNr+256): ""+seqNr;
		String dst = (dstID==-1)? "BROADCAST": (dstID==0)? "BASESTATION": ""+dstID;
		
		String s = "Spine Header {";
		
		s += "ver: 1." + vers + ", ext:" + ext + 
			 ", pktType:" + SPINEPacketsConstants.packetTypeToString(pktT).toUpperCase() + 
			 ", groupID:" + grp.toUpperCase() + ", srcID:" + srcID + ", dstID:" + dst + 
			 ", seqNr:" + seq + ", fragNr:" + fragNr + ", totFrags:" + totFrags + "}";
		
		return s;
	}
	
}