package t2.spine.communication.android;

import net.tinyos.message.MessageListener;

import com.t2.AndroidSpineServerMainActivity;
//import com.t2.AndroidSpineServerMainActivity;
import com.t2.Constants;



import jade.util.Logger;
import spine.SPINEManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class encapsulates the lowest level of interfacing messaging between
 * the androidServer and the AndroidBTService (Which acts as a serial forwarder)
 * @author scott.coleman
 *
 */
public class AndroidMessageServer extends BroadcastReceiver {
	static int msgCount = 0;
	private static final String TAG = Constants.TAG;

	// TODO: should share these with service so we know we're using the same labels!
	public static final String ACTION_STATUS_BROADCAST = "com.t2.biofeedback.service.status.BROADCAST";
	public static final String ACTION_SERVER_DATA_BROADCAST = "com.t2.biofeedback.server.data.BROADCAST";
	public static final String EXTRA_ADDRESS = "address";
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_MESSAGE_TYPE = "messageType";
	public static final String EXTRA_MESSAGE_PAYLOAD = "messagePayload";
	public static final String EXTRA_MESSAGE_ID = "messageId";
	public static final String EXTRA_MESSAGE_VALUE = "messageValue";
	public static final String EXTRA_TIMESTAMP = "timestamp";
	
	public static final String EXTRA_MSG_BYTES = "msgBytes";
	
	private static MessageListener androidLocalNodeAdapter;		

	public AndroidMessageServer()
	{
		// Bind the the AndroidBTService (serial forwarder)
		doBindService();
	}
	
	// The following is legacy code for when we used to send sensor data using
	// broadcast intents:
	
		@Override
		public void onReceive(Context context, Intent intent) {
			// This interface code is left in for the off chance we might want to uset the old mechanism 
//			int srcID = 99;
//			
//			if(intent.getAction().equals("com.t2.biofeedback.service.spinedata.BROADCAST")) {
//				if(androidLocalNodeAdapter != null) {
//	
//					AndroidMessage tosmsg = null;
//					// change the intent array to shorts
//					byte[] bytes = intent.getByteArrayExtra(EXTRA_MSG_BYTES);
//					tosmsg = AndroidMessage.Construct(bytes);					
//					androidLocalNodeAdapter.messageReceived(srcID, tosmsg);
//				}
//			}
		}
	
	/**
	 * Registers a listener for data messages
	 * @param arg	Listener to send received messages to
	 */
	public void registerListener(MessageListener arg) {
		if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
			StringBuffer str = new StringBuffer();
			str.append("registered SocketMessageListener: ");
			str.append(arg);
			SPINEManager.getLogger().log(Logger.INFO, str.toString());
		}
		androidLocalNodeAdapter = arg;
	}
	
	/**
	 * Message used for Spine commands
	 * @author scott.coleman
	 *
	 */
	public abstract static class BioFeedbackMessage {
		public String address;
		public String name;
		public String messageType;
		public String messageId;
		public Double messageValue;
	}		

	/**
	 * Sends a command (in an AndroidMessage) to a sensor node
	 * @param destNodeID	Address of destination node
	 * @param msg			Message to send
	 */
	public void sendCommand(int destNodeID, AndroidMessage andMsg) {
		if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
			StringBuffer str = new StringBuffer();
			str.append("Send cmd: ");
			str.append(andMsg.toString());
			str.append(" to node: ");
			str.append(destNodeID);
			SPINEManager.getLogger().log(Logger.INFO, str.toString());
		}

		// Send the command via handlers instead
        try {
            short pktType = andMsg.header.getPktType();            
            Message msg = Message.obtain(null,MSG_SPINE_COMMAND, pktType, 0);            
            
            // Check to see if there is a payload, of so add it to the message
			if (andMsg.payloadBuf.length > 0) {
				Bundle b = new Bundle();
				b.putByteArray("EXTRA_MESSAGE_PAYLOAD", andMsg.payloadBuf);      
				msg.setData(b);			
			}			
            mService.send(msg);
        } 
        catch (RemoteException e) {
	        Log.e(TAG,"Error sending SPINE command to service");
        }		
        catch (NullPointerException e) {
	        Log.e(TAG,"Error sending SPINE command to service - NULL POINTER");
        }		
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
    
    /**
     * Command to the service to send a spine command
     */
    static final int MSG_SPINE_COMMAND = 8888;    
	
	/** Messenger for communicating with service. */
	Messenger mService = null;

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
					AndroidMessage tosmsg = null;
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
	        AndroidSpineServerMainActivity.getInstance().setmService(mService);
//	        AndroidSpineConnector.getInstance().setmService(mService);
	        Log.i(TAG,"Service Connected");
	        
	        // We want to monitor the service for as long as we are
	        // connected to it.
	        try {
	            Message msg = Message.obtain(null,MSG_REGISTER_CLIENT);
	            msg.replyTo = mMessenger;
	            mService.send(msg);

	            // Give it some value as an example.
//	            msg = Message.obtain(null,MSG_SET_VALUE, this.hashCode(), 0);
//	            mService.send(msg);
	            
	        } catch (RemoteException e) {
		        Log.e(TAG,"Remove exception " + e.toString());
	            // In this case the service has crashed before we could even
	            // do anything with it; we can count on soon being
	            // disconnected (and then reconnected if it can be restarted)
	            // so there is no need to do anything here.
	        }
	    }

	    /** 
	     * Called when the service is disconnected
	     * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
	     */
	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        mService = null;
	        AndroidSpineServerMainActivity.getInstance().setmService(mService);
//	        AndroidSpineConnector.getInstance().setmService(mService);
	        
	        Log.i(TAG,"Service Disconnected");
	    }
	};

	/**
	 * Calls the main activity to bind us to the sdrvice
 	 *  Note: we have to do this in the main activity so it knows about our connection
	 *		so that it can un-bind on destroy time.
	 * 		This class doesn't know about the destroy event	 
	 */
	void doBindService() {
		AndroidSpineServerMainActivity.getInstance().doBindService(mConnection);
//		AndroidSpineConnector.getInstance().doBindService(mConnection);
		
	}

	// The following is legacy code for when we used to send sensor data using
	// broadcast intents:
	
	
	//		public static class BioFeedbackSpineData extends BioFeedbackMessage {
	//			public byte[] msgBytes;
	//			public long currentTimestamp;
	//			
	//			public static BioFeedbackSpineData factory(Intent i) {
	//				BioFeedbackSpineData m = new BioFeedbackSpineData();
	//				m.address = i.getStringExtra("address");
	//				m.name = i.getStringExtra("name");
	//				m.messageType = i.getStringExtra("messageType");
	//				m.messageId = i.getStringExtra("messageId");
	//				m.msgBytes = i.getByteArrayExtra("msgBytes");
	//				m.currentTimestamp = i.getLongExtra("currentTimestamp", 0);
	//				
	//				return m;
	//			}
	//		}		

}