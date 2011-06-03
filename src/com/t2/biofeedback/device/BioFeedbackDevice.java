package com.t2.biofeedback.device;

import com.t2.biofeedback.device.zephyr.ZephyrBH;

public abstract class BioFeedbackDevice extends SerialBTDevice {
	private long linkTimeout = 0;
	private OnSpineMessageListener onSpineMessageListener;	
	
	private boolean onSpineMessageListenerIsSet = false;
	private boolean onBatteryLevelListenerIsSet = false;
	private boolean onHeartRateListenerIsSet = false;
	private boolean onRespirationRateListenerIsSet = false;
	private boolean onSkinTemperatureListenerIsSet = false;

	
	
	private int[] capabilities;
	
	public BioFeedbackDevice() {
		super();
	}
	
	@Override
	public void write(byte[] bytes) {
		super.write(bytes);
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
	
	public boolean hasCapability(int cap) {
		if(capabilities == null) {
			this.capabilities = getCapabilities();
		}
		
		for(int i = 0; i < this.capabilities.length; i++) {
			if(this.capabilities[i] == cap) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public void setOnSpineMessageListener(OnSpineMessageListener l) throws UnsupportedCapabilityException {
		if(!this.hasCapability(Capability.SPINE_MESSAGE)) {
			throw new UnsupportedCapabilityException("Device doesn't support this capability.");
		}
		this.onSpineMessageListener = l;
		this.onSpineMessageListenerIsSet= (l != null);
	}
		
	
	protected void onSetCollectData(int data, boolean canCollect) {}

	protected abstract void onSetLinkTimeout(long linkTimeout);
	public abstract int[] getCapabilities();	
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
	
	protected void onSpineMessage(byte[] message) {
		if(this.onSpineMessageListenerIsSet) {
			
			this.onSpineMessageListener.onSpineMessage(this, message);
		}
	}
		
	
	public interface OnSpineMessageListener {
		public void onSpineMessage(BioFeedbackDevice bioFeedbackDevice, byte[] message);
	}
	

	
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

		public static final int SPINE_MESSAGE = 44;
		public static final int BATTERY_LEVEL = 44;
		public static final int HEART_RATE = 45;
		public static final int RESPIRATION_RATE = 46;
		public static final int SKIN_TEMP = 47;
	}
	
	

}
