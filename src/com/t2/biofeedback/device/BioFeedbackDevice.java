package com.t2.biofeedback.device;

import com.t2.biofeedback.device.zephyr.ZephyrBH;

public abstract class BioFeedbackDevice extends SerialBTDevice {
	private long linkTimeout = 0;
	
	private int[] capabilities;
	
	public BioFeedbackDevice() {
		super();
	}
	
	@Override
	protected void onDeviceConnected() {
		this.setLinkTimeout(linkTimeout);
	}
	
	@Override
	protected void onConnectedClosed() {}

	public final void setLinkTimeout(long linkTimeout) {
		this.linkTimeout = linkTimeout;
		this.onSetLinkTimeout(linkTimeout);
	}

	public final long getLinkTimeout() {
		return linkTimeout;
	}
	
	
	protected void onSetCollectData(int data, boolean canCollect) {}

	protected abstract void onSetLinkTimeout(long linkTimeout);
	public abstract ModelInfo getModelInfo();
//	public abstract int getDeviceId();
	
	

	
	
	/*public static final BioFeedbackDevice factory(int bioFeedbackDeviceId) {
		BioFeedbackDevice d = null;
		switch(bioFeedbackDeviceId) {
			case Device.ZEPHYR_BIOHARNESS:
				return new ZephyrBH();
		}
		
		return null;
	}*/
	
	

	
	public class ModelInfo {
		public final String name;
		public final String manufacturer;
		
		public ModelInfo(String name, String manuf) {
			this.name = name;
			this.manufacturer = manuf;
		}
	}
	
	public class UnsupportedCapabilityException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4070660360479320363L;

		public UnsupportedCapabilityException(String msg) {
			super(msg);
		}
	}
	
	public static class Capability {

	}
	

}
