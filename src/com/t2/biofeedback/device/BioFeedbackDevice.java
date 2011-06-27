package com.t2.biofeedback.device;

import com.t2.biofeedback.device.Spine.SpineDevice;
import com.t2.biofeedback.device.zephyr.ZephyrDevice;



public abstract class BioFeedbackDevice extends SerialBTDevice {
	private long linkTimeout = 0;
	private OnDeviceDataMessageListener onSpineMessageListener;	
	private OnDeviceDataMessageListener onDeviceDataMessageListener;	
	
	private boolean onSpineMessageListenerIsSet = false;
	private boolean onDeviceDataMessageListenerIsSet = false;

	public BioFeedbackDevice() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.SerialBTDevice#write(byte[])
	 */
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
	
	
	public void setOnDeviceDataMessageListener(OnDeviceDataMessageListener l, BioFeedbackDevice device){

		if (device instanceof SpineDevice)
		{
			this.onSpineMessageListener = l;
			this.onSpineMessageListenerIsSet= (l != null);
		}
		else if (device instanceof ZephyrDevice)
		{

			this.onDeviceDataMessageListener = l;
			this.onDeviceDataMessageListenerIsSet= (l != null);

			// We also need to do this since device messages are now treated as Spine messages
			this.onSpineMessageListener = l;
			this.onSpineMessageListenerIsSet= (l != null);

			
			
		}
	}

	
	protected void onSetCollectData(int data, boolean canCollect) {}

	protected abstract void onSetLinkTimeout(long linkTimeout);
	public abstract ModelInfo getModelInfo();
	
	
	protected void onSpineMessage(byte[] message) {
		if(this.onSpineMessageListenerIsSet) {
			
			this.onSpineMessageListener.onSpineMessage(this, message);
		}
	}
	
	protected void onDeviceMessage(byte[] message) {
		if(this.onDeviceDataMessageListenerIsSet) {
			
			this.onDeviceDataMessageListener.onDeviceMessage(this, message);
		}
	}
		
	
	public interface OnDeviceDataMessageListener {
		public void onSpineMessage(BioFeedbackDevice bioFeedbackDevice, byte[] message);
		public void onDeviceMessage(BioFeedbackDevice bioFeedbackDevice, byte[] message);
	}
	

	
	public class ModelInfo {
		public final String name;
		public final String manufacturer;
		
		public ModelInfo(String name, String manuf) {
			this.name = name;
			this.manufacturer = manuf;
		}
	}
	

	

}
