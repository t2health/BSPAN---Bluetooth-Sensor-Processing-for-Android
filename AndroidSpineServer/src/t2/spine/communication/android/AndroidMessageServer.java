package t2.spine.communication.android;


import net.tinyos.message.MessageListener;

import com.t2.AndroidSpineServerMainActivity;
import com.t2.Constants;
import com.t2.Util;



import jade.util.Logger;
import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

	public class AndroidMessageServer extends BroadcastReceiver {
		static int msgCount = 0;
		private static final String TAG = Constants.TAG;

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
		
		private static MessageListener androidLocalNodeAdapter;		

		public AndroidMessageServer()
		{
			doBindService();
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int srcID = 99;
			
			if(intent.getAction().equals("com.t2.biofeedback.service.spinedata.BROADCAST")) {
				if(androidLocalNodeAdapter != null) {
					//	For legacy devices only	
	//				m.address = intent.getStringExtra("address");
	//				m.name = intent.getStringExtra("name");
	//				m.messageType =intent.getStringExtra("messageType");
	//				m.messageId = intent.getStringExtra("messageId");
	//				m.msgBytes = intent.getByteArrayExtra("msgBytes");
	//				m.currentTimestamp = intent.getLongExtra("currentTimestamp", 0);				

					AndroidMessage tosmsg = null;
					// change the intent array to shorts
					byte[] bytes = intent.getByteArrayExtra(EXTRA_MSG_BYTES);
					tosmsg = AndroidMessage.Construct(bytes);					
					androidLocalNodeAdapter.messageReceived(srcID, tosmsg);
				}
			}
		}
		
		public void registerListener(MessageListener arg) {
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
				SPINEManager.getLogger().log(Logger.INFO, str.toString());
			}

			Intent intent = new Intent();
			intent.setAction(ACTION_SERVER_DATA_BROADCAST);

			// TODO: Fix this!!!!			
			//intent.putExtra(EXTRA_MESSAGE_TYPE, emumsg.getClusterId());
			AndroidSpineServerMainActivity.getInstance().sendBroadcast(intent);			
		}		

		public void sendMessage(int destNodeID, AndroidMessage emumsg) {
			if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
				StringBuffer str = new StringBuffer();
				str.append("Send cmd: ");
				str.append(emumsg.toString());
				str.append(" to node: ");
				str.append(destNodeID);
				SPINEManager.getLogger().log(Logger.INFO, str.toString());
			}

			Intent intent = new Intent();
			intent.setAction(ACTION_SERVER_DATA_BROADCAST);

			// TODO: Fix this - put all packet pieces in		
			short s = emumsg.header.getPktType();
			intent.putExtra(EXTRA_MESSAGE_TYPE, s);
			AndroidSpineServerMainActivity.getInstance().sendBroadcast(intent);			
		}		
		
	    /**
	     * Command to the service to register a client, receiving callbacks
	     * from the service.  The Message's replyTo field must be a Messenger of
	     * the client where callbacks should be sent.
	     */
	    static final int MSG_REGISTER_CLIENT = 1;

	    /**
	     * Command to the service to unregister a client, ot stop receiving callbacks
	     * from the service.  The Message's replyTo field must be a Messenger of
	     * the client as previously given with MSG_REGISTER_CLIENT.
	     */
	    static final int MSG_UNREGISTER_CLIENT = 2;

	    /**
	     * Command to service to set a new value.  This can be sent to the
	     * service to supply a new value, and will be sent by the service to
	     * any registered clients with the new value.
	     */
	    static final int MSG_SET_VALUE = 3;	
		
		/** Messenger for communicating with service. */
		Messenger mService = null;
		/** Flag indicating whether we have called bind on the service. */
		boolean mIsBound;
		/** Some text view we are using to show state information. */

		private static final int MSG_SET_ARRAY_VALUE = 5;
		/**
		 * Handler of incoming messages from service.
		 */
		class IncomingHandler extends Handler {
		    @Override
		    public void handleMessage(Message msg) {
		    	
				int srcID = 99;		    	
		        switch (msg.what) {
	            case MSG_SET_ARRAY_VALUE:
	    			byte[] bytes = msg.getData().getByteArray("message");
	    			if (bytes != null)
	    			{
//	    	    		Util.logHexByteString(TAG, "xxxxxxxMain Found message:", bytes);
						AndroidMessage tosmsg = null;
						// change the intent array to shorts
						
						tosmsg = AndroidMessage.Construct(bytes);					
						androidLocalNodeAdapter.messageReceived(srcID, tosmsg);
	    	    		
	    				
	    			}
		                break;
		            default:
		                super.handleMessage(msg);
		        }
		    }
		}

		/**
		 * Target we publish for clients to send messages to IncomingHandler.
		 */
		final Messenger mMessenger = new Messenger(new IncomingHandler());

		/**
		 * Class for interacting with the main interface of the service.
		 */
		private ServiceConnection mConnection = new ServiceConnection() {

			public void onServiceConnected(ComponentName className,IBinder service) {
		        // This is called when the connection with the service has been
		        // established, giving us the service object we can use to
		        // interact with the service.  We are communicating with our
		        // service through an IDL interface, so get a client-side
		        // representation of that from the raw service object.
		        mService = new Messenger(service);
		        Log.i(TAG,"Connecting");
		        
//		        mCallbackText.setText("Attached.");

		        // We want to monitor the service for as long as we are
		        // connected to it.
		        try {
		            Message msg = Message.obtain(null,MSG_REGISTER_CLIENT);
		            msg.replyTo = mMessenger;
		            mService.send(msg);

		            // Give it some value as an example.
		            msg = Message.obtain(null,MSG_SET_VALUE, this.hashCode(), 0);
		            mService.send(msg);
		            
		        } catch (RemoteException e) {
		            // In this case the service has crashed before we could even
		            // do anything with it; we can count on soon being
		            // disconnected (and then reconnected if it can be restarted)
		            // so there is no need to do anything here.
		        }


		    }

		    public void onServiceDisconnected(ComponentName className) {
		        // This is called when the connection with the service has been
		        // unexpectedly disconnected -- that is, its process crashed.
		        mService = null;
//		        mCallbackText.setText("Disconnected.");

		    }
		};

		void doBindService() {
			Log.i(TAG, "*****************binding **************************");

			try {
				Intent intent2 = new Intent("com.t2.biofeedback.IBioFeedbackService");
				AndroidSpineServerMainActivity.getInstance().bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
				Log.i(TAG, "*****************binding SUCCESS**************************");
				
				mIsBound = true;
			} catch (Exception e) {
				Log.i(TAG, "*****************binding FAIL**************************");
				Log.e(TAG, e.toString());
				
			}

		}

		void doUnbindService() {
		    if (mIsBound) {
		        // If we have received the service, and hence registered with
		        // it, then now is the time to unregister.
		        if (mService != null) {
		            try {
		                Message msg = Message.obtain(null,MSG_UNREGISTER_CLIENT);
		                msg.replyTo = mMessenger;
		                mService.send(msg);
		            } catch (RemoteException e) {
		                // There is nothing special we need to do if the service
		                // has crashed.
		            }
		        }

		        // Detach our existing connection.
		        AndroidSpineServerMainActivity.getInstance().unbindService(mConnection);
		        mIsBound = false;
//		        mCallbackText.setText("Unbinding.");
		    }
		}	

		

		

}
	

