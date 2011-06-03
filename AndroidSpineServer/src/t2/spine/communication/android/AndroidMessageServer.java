package t2.spine.communication.android;


import com.t2.AndroidSpineServerMainActivity;

import jade.util.Logger;
import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

	public class AndroidMessageServer extends BroadcastReceiver {
		static int msgCount = 0;

		// TODO: should share these with service so we know we're using the same labels!
		public static final String ACTION_STATUS_BROADCAST = "com.t2.biofeedback.service.status.BROADCAST";
		public static final String ACTION_SERVER_DATA_BROADCAST = "com.t2.biofeedback.server.data.BROADCAST";
		public static final String EXTRA_ADDRESS = "address";
		public static final String EXTRA_NAME = "name";
		public static final String EXTRA_MESSAGE_TYPE = "messageType";
		public static final String EXTRA_MESSAGE_ID = "messageId";
		public static final String EXTRA_MESSAGE_VALUE = "messageValue";
		public static final String EXTRA_TIMESTAMP = "timestamp";
		
		public static final String EXTRA_MSG_BYTES = "msgBytes";
		
		
		private static AndroidSocketMessageListener androidLocalNodeAdapter;		

		@Override
		public void onReceive(Context context, Intent intent) {
			int srcID = 99;
			short sSPort = 0;
			int destNodeID = 0;
			String sourceURL = "http://android:1";			
			
			if(intent.getAction().equals("com.t2.biofeedback.service.spinedata.BROADCAST")) {
				if(androidLocalNodeAdapter != null) {
	
					AndroidMessage msg = new AndroidMessage();
					
					// TODO: Eventually we'll actually receive all of the message parameters in the intent'
					// (right now we're getting only the payload).
					// For now we'll poke in some dummy message data so the mesage get's to it's intended recipient.
					msg.setSourceURL(sourceURL);
//					msg.setClusterId((short)4);
					
					
					// Real hack alert - fake a service discovery message so the stub messages coming in get registered as a node
					if (msgCount++ == 0)
					{
						msg.setProfileId((short)1);
					}
					
					
					// TODO: Note: either find a better way to do this, or better yet,
					// change the intent array to shorts
					byte[] bytes = intent.getByteArrayExtra("msgBytes");
					int len = bytes.length;
					short[] shorts = new short[len];
					
					int i = 0;
					int ignoreHeaderCount = SPINEPacketsConstants.SPINE_HEADER_SIZE;
					for (Byte b : bytes)
					{
						if (ignoreHeaderCount-- <= 0)
							shorts[i++] = b;
					}
					msg.setPayload(shorts);

					// Grab the message type (cluster id) from the first byte of the header
					byte msgType = bytes[0];
					msgType &= 0x1f;
					msg.setClusterId((short)msgType);

					
					//				
	//				m.address = intent.getStringExtra("address");
	//				m.name = intent.getStringExtra("name");
	//				m.messageType =intent.getStringExtra("messageType");
	//				m.messageId = intent.getStringExtra("messageId");
	//				m.msgBytes = intent.getByteArrayExtra("msgBytes");
	//				m.currentTimestamp = intent.getLongExtra("currentTimestamp", 0);				
					
					androidLocalNodeAdapter.messageReceived(srcID, msg);
				}
			}
		}
		
		// EMULocalNodeAdapter is a SocketMessage listener
		public void registerListener(AndroidSocketMessageListener arg) {
			if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
				StringBuffer str = new StringBuffer();
				str.append("registered SocketMessageListener: ");
				str.append(arg);
				SPINEManager.getLogger().log(Logger.INFO, str.toString());
			}
			androidLocalNodeAdapter = arg;
		}
		
		public abstract static class BioFeedbackMessage {
			public String address;
			public String name;
			public String messageType;
			public String messageId;
			public Double messageValue;
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
		public void sendCommand(int destNodeID, AndroidMessage emumsg) {
			if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
				StringBuffer str = new StringBuffer();
				str.append("Send cmd: ");
				str.append(emumsg.toString());
				str.append(" to node: ");
				str.append(destNodeID);
				SPINEManager.getLogger().log(Constants.ANDROIDLOGTEMP, str.toString());
			}

			Intent intent = new Intent();
			intent.setAction(ACTION_SERVER_DATA_BROADCAST);
			intent.putExtra(EXTRA_MESSAGE_TYPE, emumsg.getClusterId());
			AndroidSpineServerMainActivity.getInstance().sendBroadcast(intent);			
			
			
			
			//AndroidSpineServerMainActivity.getInstance().sendBroadcast(new Intent("com.t2.biofeedback.service.STOP"));			

			
			
			
		}		

}
	

