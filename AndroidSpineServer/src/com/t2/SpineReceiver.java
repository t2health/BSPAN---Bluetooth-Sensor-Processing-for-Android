package com.t2;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SpineReceiver extends BroadcastReceiver {
	private static final String TAG = Constants.TAG;
	private OnBioFeedbackMessageRecievedListener messageRecievedListener;
	
	public SpineReceiver(OnBioFeedbackMessageRecievedListener omrl) {
		this.messageRecievedListener = omrl;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("com.t2.biofeedback.service.data.BROADCAST")) {
			if(messageRecievedListener != null) {
				messageRecievedListener.onDataReceived(BioFeedbackData.factory(intent));
			}
		} else if(intent.getAction().equals("com.t2.biofeedback.service.spinedata.BROADCAST")) {
			if(messageRecievedListener != null) {
				messageRecievedListener.onSpineDataReceived(BioFeedbackSpineData.factory(intent));
			}
		} else if(intent.getAction().equals("com.t2.biofeedback.service.status.BROADCAST")) {
			if(messageRecievedListener != null) {
				messageRecievedListener.onStatusReceived(BioFeedbackStatus.factory(intent));
			}
		}
	}

	public boolean isDataMessage(Intent i) {
		return i.getStringExtra("messageId").startsWith("DATA_");
	}
	
	public interface OnBioFeedbackMessageRecievedListener {
		public void onDataReceived(BioFeedbackData bfmd);
		public void onSpineDataReceived(BioFeedbackSpineData bfmd);
		public void onStatusReceived(BioFeedbackStatus bfs);
	}
	
	public abstract static class BioFeedbackMessage {
		public String address;
		public String name;
		public String messageType;
		public String messageId;
		public Double messageValue;
	}
	
	public static class BioFeedbackStatus extends BioFeedbackMessage {
		public static BioFeedbackStatus factory(Intent i) {
			BioFeedbackStatus m = new BioFeedbackStatus();
			m.address = i.getStringExtra("address");
			m.name = i.getStringExtra("name");
			m.messageType = i.getStringExtra("messageType");
			m.messageId = i.getStringExtra("messageId");
			m.messageValue = i.getDoubleExtra("messageValue", -12345678901234567890.12);
			
			if(m.messageValue == -12345678901234567890.12) {
				m.messageValue = null;
			}
			
			return m;
		}
	}
	
	
	public static class BioFeedbackData extends BioFeedbackMessage {
		public double avgValue;
		public double currentValue;
		public double[] sampleValues;
		
		public long currentTimestamp;
		public long[] sampleTimestamps;
		
		public static BioFeedbackData factory(Intent i) {
			BioFeedbackData m = new BioFeedbackData();
			m.address = i.getStringExtra("address");
			m.name = i.getStringExtra("name");
			m.messageType = i.getStringExtra("messageType");
			m.messageId = i.getStringExtra("messageId");
			
			m.avgValue = i.getDoubleExtra("avgValue", 0.00);
			m.currentValue = i.getDoubleExtra("currentValue", 0.00);
			m.sampleValues = i.getDoubleArrayExtra("sampleValues");
			
			m.currentTimestamp = i.getLongExtra("currentTimestamp", 0);
			m.sampleTimestamps = i.getLongArrayExtra("sampleTimestamps");
			
			return m;
		}
	}	
	
	public static class BioFeedbackSpineData extends BioFeedbackMessage {
		public byte[] msgBytes;
		public long currentTimestamp;
		
		public static BioFeedbackSpineData factory(Intent i) {
			BioFeedbackSpineData m = new BioFeedbackSpineData();
			m.address = i.getStringExtra("address");
			m.name = i.getStringExtra("name");
			m.messageType = i.getStringExtra("messageType");
			m.messageId = i.getStringExtra("messageId");
			m.msgBytes = i.getByteArrayExtra("msgBytes");
			m.currentTimestamp = i.getLongExtra("currentTimestamp", 0);
			
			return m;
		}
	}
}
