
package t2.spine.communication.android;

import jade.util.Logger;

import spine.Properties;
import spine.SPINEManager;
import spine.SPINESupportedPlatforms;

import java.io.*;
import java.net.*;
import java.util.*;

// There is a ClientWorker for each Virtual Node
class ClientWorker implements Runnable {

	private Socket client;


	private AndroidSocketMessageListener emuLocalNodeAdapter;

	private AndroidSocketThrdServer serverSocket;

	ClientWorker(Socket client, AndroidSocketMessageListener emuLocalNodeAdapter, AndroidSocketThrdServer serverSocket) {
		this.client = client;
		this.emuLocalNodeAdapter = emuLocalNodeAdapter;
		this.serverSocket = serverSocket;
	}

	public void run() {
		int srcID = 99;
		short sSPort = 0;
		int destNodeID = 0;
		String sourceURL = "";

		AndroidMessage msg = new AndroidMessage();

		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(client.getInputStream());
			new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) {
				StringBuffer str = new StringBuffer();
				str.append(e.getMessage());
				str.append(": In or out failed");
				SPINEManager.getLogger().log(Logger.SEVERE, str.toString());
			}
		}
		while (true) {
			try {
				msg = (AndroidMessage) ois.readObject();
				// In msg type Node Information the ProfileId contains
				// Server Socket Node port number (otherwise 0)
				sSPort = msg.getProfileId();
				sourceURL = msg.getSourceURL();
				String urlPrefix = Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.EMULATOR + "_" + Properties.URL_PREFIX_KEY);
				destNodeID = Integer.parseInt(sourceURL.substring(urlPrefix.length()));

				if (sSPort != 0) {
					serverSocket.connectToSocketServerNode(destNodeID, sSPort);
				}
				emuLocalNodeAdapter.messageReceived(srcID, msg);
//				textArea.append(msg.toString() + "\n");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
					SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) {
					StringBuffer str = new StringBuffer();
					str.append(e.getMessage());
					str.append(": Read failed");
					SPINEManager.getLogger().log(Logger.SEVERE, str.toString());
				}
				break;
			}
		}
	}
}

class AndroidSocketThrdServer implements Runnable {
	
	private static final long serialVersionUID = 1L;

	AndroidSocketMessageListener emulAdap;


	ServerSocket server = null;

	private static final int NODE_COMMUNICATION_PORT = Integer.parseInt(SPINEManager.getMoteCom());

	AndroidSocketThrdServer() {
	}

	// EMULocalNodeAdapter is a SocketMessage listener
	public void registerListener(AndroidSocketMessageListener arg) {
		if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
			StringBuffer str = new StringBuffer();
			str.append("registered AndroidSocketMessageListener: ");
			str.append(arg);
			SPINEManager.getLogger().log(Logger.INFO, str.toString());
		}
		emulAdap = arg;
	}


	Hashtable oisClient = new Hashtable();
	Hashtable oosClient = new Hashtable();

	Socket socket = null;

	public void run() {
		try {
			server = new ServerSocket(NODE_COMMUNICATION_PORT);
		} catch (IOException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) {
				StringBuffer str = new StringBuffer();
				str.append(e.getMessage());
				str.append(": Could not listen on port ");
				str.append(NODE_COMMUNICATION_PORT);
				SPINEManager.getLogger().log(Logger.SEVERE, str.toString());
			}
		}
		while (true) {
			ClientWorker w;
			try {
				w = new ClientWorker(server.accept(), emulAdap, this);
				Thread t = new Thread(w);
				t.start();

			} catch (IOException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) {
					StringBuffer str = new StringBuffer();
					str.append(e.getMessage());
					str.append(": Accept failed");
					SPINEManager.getLogger().log(Logger.SEVERE, str.toString());
				}
			}
		}
	}

	// Create socket connection to Server Socket Node
	public void connectToSocketServerNode(int destNodeID, short sSPort) {
		try {
			socket = new Socket("localhost", sSPort);
			if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
				StringBuffer str = new StringBuffer();
				str.append("Connection successful to Server Socket - Node ");
				str.append(destNodeID);
				str.append(" on port ");
				str.append(sSPort);
				SPINEManager.getLogger().log(Logger.INFO, str.toString());
			}
			oosClient.put(new Integer(destNodeID), new ObjectOutputStream(socket.getOutputStream()));
			oisClient.put(new Integer(destNodeID), new ObjectInputStream(socket.getInputStream()));

		} catch (UnknownHostException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());

		} catch (IOException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
		}
	}

	public void sendCommand(int destNodeID, AndroidMessage emumsg) throws IOException {
		if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
			StringBuffer str = new StringBuffer();
			str.append("Send cmd: ");
			str.append(emumsg.toString());
			str.append(" to node: ");
			str.append(destNodeID);
			SPINEManager.getLogger().log(Logger.INFO, str.toString());
		}
		
		ObjectOutputStream oosC = (ObjectOutputStream) (oosClient.get(new Integer(destNodeID)));
		oosC.writeObject(emumsg);
		oosC.flush();
	}

	protected void finalize() {
		// Objects created in run method are finalized when
		// program terminates and thread exits
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) {
				StringBuffer str = new StringBuffer();
				str.append(e.getMessage());
				str.append(": Could not close socket");
				SPINEManager.getLogger().log(Logger.SEVERE, str.toString());
			}
		}
	}

}
