package com.t2.biofeedback.device;

/**
 * Base class for sensor devices
 * @author scott.coleman
 *
 */
public abstract class BioFeedbackDevice extends SerialBTDevice {
	private long linkTimeout = 0;

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
	}
	
	@Override
	protected void onConnectedClosed() {}

	public final long getLinkTimeout() {
		return linkTimeout;
	}
	
	
	public abstract ModelInfo getModelInfo();
	
	
	public class ModelInfo {
		public final String name;
		public final String manufacturer;
		
		public ModelInfo(String name, String manuf) {
			this.name = name;
			this.manufacturer = manuf;
		}
	}
}
