package com.t2.biofeedback.device;

import com.t2.biofeedback.device.zephyr.ZephyrBH;

public abstract class BioFeedbackDevice extends SerialBTDevice {
	private long linkTimeout = 0;
	
	private OnBatteryLevelListener onBatteryLevelListener;
	private OnHeartRateListener onHeartRateListener;
	private OnRespirationRateListener onRespirationRateListener;
	private OnSkinTemperatureListener onSkinTemperatureListener;
	
	private boolean onBatteryLevelListenerIsSet = false;
	private boolean onHeartRateListenerIsSet = false;
	private boolean onRespirationRateListenerIsSet = false;
	private boolean onSkinTemperatureListenerIsSet = false;
	
	private DeviceValue batteryLevel = new DeviceValue();
	private HeartRate heartRate = new HeartRate();
	private RespirationRate respirationRate = new RespirationRate();
	private SkinTemperature skinTemperature = new SkinTemperature();
	
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
	
	public void setOnBatteryLevelListener(OnBatteryLevelListener l) throws UnsupportedCapabilityException {
		if(!this.hasCapability(Capability.BATTERY_LEVEL)) {
			throw new UnsupportedCapabilityException("Device doesn't support this capability.");
		}
		
		this.onBatteryLevelListener = l;
		this.onBatteryLevelListenerIsSet = (l != null);
	}
	
	public void setOnHeartRateListener(OnHeartRateListener l) throws UnsupportedCapabilityException {
		if(!this.hasCapability(Capability.HEART_RATE)) {
			throw new UnsupportedCapabilityException("Device doesn't support this capability.");
		}
		this.onHeartRateListener = l;
		this.onHeartRateListenerIsSet = (l != null);
	}
	
	public void setOnRespirationRateListener(OnRespirationRateListener l) throws UnsupportedCapabilityException {
		if(!this.hasCapability(Capability.RESPIRATION_RATE)) {
			throw new UnsupportedCapabilityException("Device doesn't support this capability.");
		}
		this.onRespirationRateListener = l;
		this.onRespirationRateListenerIsSet = (l != null);
	}
	
	public void setOnSkinTemperatureListener(OnSkinTemperatureListener l) throws UnsupportedCapabilityException {
		if(!this.hasCapability(Capability.SKIN_TEMP)) {
			throw new UnsupportedCapabilityException("Device doesn't support this capability.");
		}
		this.onSkinTemperatureListener = l;
		this.onSkinTemperatureListenerIsSet = (l != null);
	}
	
	
	protected void onSetCollectData(int data, boolean canCollect) {}
	public abstract int[] getCapabilities();
	protected abstract void onSetLinkTimeout(long linkTimeout);
	public abstract ModelInfo getModelInfo();
//	public abstract int getDeviceId();
	
	
	protected void onBatteryLevel(long timestamp, double value) {
		if(this.onBatteryLevelListenerIsSet) {
			this.batteryLevel.currentTimetamp = timestamp;
			this.batteryLevel.currentValue = value;
			this.onBatteryLevelListener.onBatteryLevel(this, this.batteryLevel);
		}
	}
	
	protected void onHeartRate(long timestamp, double value) {
		if(this.onHeartRateListenerIsSet) {
			this.heartRate.addValue(timestamp, value);
			this.onHeartRateListener.onHeartRate(this, this.heartRate);
		}
	}
	
	protected void onRespirationRate(long timestamp, double value) {
		if(this.onRespirationRateListenerIsSet) {
			this.respirationRate.addValue(timestamp, value);
			this.onRespirationRateListener.onRespirationRate(this, this.respirationRate);
		}		
	}
	
	protected void onSkinTemperature(long timestamp, double value) {
		if(this.onSkinTemperatureListenerIsSet) {
			this.skinTemperature.addValue(timestamp, value);
			this.onSkinTemperatureListener.onSkinTemperature(this, this.skinTemperature);
		}
	}
	
	
	/*public static final BioFeedbackDevice factory(int bioFeedbackDeviceId) {
		BioFeedbackDevice d = null;
		switch(bioFeedbackDeviceId) {
			case Device.ZEPHYR_BIOHARNESS:
				return new ZephyrBH();
		}
		
		return null;
	}*/
	
	
	public interface OnBatteryLevelListener {
		public void onBatteryLevel(BioFeedbackDevice bioFeedbackDevice, DeviceValue dv);
	}
	
	public interface OnHeartRateListener {
		public void onHeartRate(BioFeedbackDevice bioFeedbackDevice, HeartRate hr);
	}
	
	public interface OnRespirationRateListener {
		public void onRespirationRate(BioFeedbackDevice bioFeedbackDevice, RespirationRate rr);
	}
	
	public interface OnSkinTemperatureListener {
		public void onSkinTemperature(BioFeedbackDevice bioFeedbackDevice, SkinTemperature st);
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
		public static final int BATTERY_LEVEL = 44;
		public static final int HEART_RATE = 45;
		public static final int RESPIRATION_RATE = 46;
		public static final int SKIN_TEMP = 47;
	}
	
	/*public static class Device {
		public static final int ZEPHYR_BIOHARNESS = 63;
//		public static final int ZEPHYR_HXM = 64;
		
		public static final int[] ALL_DEVICES = new int[] {
			ZEPHYR_BIOHARNESS,
//			ZEPHYR_HXM
		};
	}*/
}
