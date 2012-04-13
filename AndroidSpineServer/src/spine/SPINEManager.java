/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute
modify it under the terms of the sub-license (below).

*****************************************************************/

/*****************************************************************
BSPAN - BlueTooth Sensor Processing for Android is a framework 
that extends the SPINE framework to work on Android and the 
Android Bluetooth communication services.

Copyright (C) 2011 The National Center for Telehealth and 
Technology

Eclipse Public License 1.0 (EPL-1.0)

This library is free software; you can redistribute it and/or
modify it under the terms of the Eclipse Public License as
published by the Free Software Foundation, version 1.0 of the 
License.

The Eclipse Public License is a reciprocal license, under 
Section 3. REQUIREMENTS iv) states that source code for the 
Program is available from such Contributor, and informs licensees 
how to obtain it in a reasonable manner on or through a medium 
customarily used for software exchange.

Post your updates and modifications to our GitHub or email to 
t2@tee2.org.

This library is distributed WITHOUT ANY WARRANTY; without 
the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the Eclipse Public License 1.0 (EPL-1.0)
for more details.
 
You should have received a copy of the Eclipse Public License
along with this library; if not, 
visit http://www.opensource.org/licenses/EPL-1.0

*****************************************************************/

package spine;

import jade.util.Logger;

import java.io.InterruptedIOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import spine.datamodel.Address;
import spine.datamodel.Node;
import spine.datamodel.functions.ShimmerNonSpineSetupSensor;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineFunctionReq;
import spine.datamodel.functions.SpineObject;
import spine.datamodel.functions.SpineServiceCommand;
import spine.datamodel.functions.SpineSetupFunction;
import spine.datamodel.functions.SpineSetupSensor;
import spine.datamodel.functions.SpineStart;
import spine.datamodel.serviceMessages.ServiceErrorMessage;
import spine.exceptions.MethodNotSupportedException;
import android.content.res.Resources;
import android.os.Handler;

import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.WSNConnection;





public class SPINEManager {
	private static final String TAG = "BFDemo";
	
	/** package scoped as they are used by EventDispatcher **/
	static final byte DISC_COMPL_EVT_COD = 100;
	static final String SPINEDATA_FUNCT_CLASSNAME_SUFFIX = "SpineData";
	static final String SPINE_SERVICE_MESSAGE_CLASSNAME_SUFFIX = "Message";
	static String URL_PREFIX = null;
	static String SPINEDATACODEC_PACKAGE = null;	
	static String SPINE_SERVICE_MESSAGE_CODEC_PACKAGE = null;
	WSNConnection connection;
	Vector activeNodes = new Vector(); // <values:Node>	
	Hashtable htInstance = new Hashtable(); // Hash Table class instance	
	SpineCodec spineCodec = null;	
	boolean discoveryCompleted = false;
	
	// private object to which SPINEMager delegates the event dispatching functionality
	private EventDispatcher eventDispatcher = null; 
	
	private static Properties prop = Properties.getDefaultProperties();
	
	
	private static final String DEF_PROP_MISSING_MSG = 
		"ERROR: unable to load 'defaults.properties' file.";
	
	private final static long DISCOVERY_TIMEOUT = 2000;	
	
	
	
	private long discoveryTimeout = DISCOVERY_TIMEOUT;
	
	private boolean started = false;	
	
	private LocalNodeAdapter nodeAdapter;	
	
	private	static String MOTECOM = null;
	private static String PLATFORM = null;	
	
	private static byte MY_GROUP_ID = 0;
	private static String LOCALNODEADAPTER_CLASSNAME = null;
	
	private static final String SPINEDATACODEC_PACKAGE_PREFIX = "spine.payload.codec.";
	private static final String SPINE_SERVICE_MESSAGE_CODEC_PACKAGE_PREFIX = "spine.payload.codec.";
	
	private static String MESSAGE_CLASSNAME = null;		
	
	private Node baseStation = null;
	
	private static Logger l = Logger.getMyLogger(SPINEManager.class.getName());
	

	/** package-scoped method to get a reference to baseStation **/
	final Node getBaseStation() {
		return baseStation;
	}
	
	/** package-scoped method to know if discovery is completed **/
	final boolean isDiscoveryCompleted() {
		return discoveryCompleted;
	}
	
	/** package-scoped method called by SPINEFactory.
	 * The caller must guarantee that moteCom and platform are not null. **/
	SPINEManager(String moteCom, String platform) {
		try {
			MOTECOM = moteCom;
			PLATFORM = platform;			
			
			MY_GROUP_ID = (byte)Short.parseShort(prop.getProperty(Properties.GROUP_ID_KEY), 16);
			LOCALNODEADAPTER_CLASSNAME = prop.getProperty(PLATFORM + "_" + Properties.LOCALNODEADAPTER_CLASSNAME_KEY);
			URL_PREFIX = prop.getProperty(PLATFORM + "_" + Properties.URL_PREFIX_KEY);
			SPINEDATACODEC_PACKAGE = SPINEDATACODEC_PACKAGE_PREFIX + 
									prop.getProperty(PLATFORM + "_" + Properties.SPINEDATACODEC_PACKAGE_SUFFIX_KEY) + ".";
			MESSAGE_CLASSNAME = prop.getProperty(PLATFORM + "_" + Properties.MESSAGE_CLASSNAME_KEY);
			SPINE_SERVICE_MESSAGE_CODEC_PACKAGE = SPINE_SERVICE_MESSAGE_CODEC_PACKAGE_PREFIX + 
													prop.getProperty(PLATFORM + "_" + Properties.SPINEDATACODEC_PACKAGE_SUFFIX_KEY) + ".";
			
			System.setProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY, LOCALNODEADAPTER_CLASSNAME);
			nodeAdapter = LocalNodeAdapter.getLocalNodeAdapter();	

			Vector params = new Vector();
			params.addElement(MOTECOM);
			nodeAdapter.init(params);

			java.util.logging.LogManager mng = java.util.logging.LogManager.getLogManager();
			Enumeration e = mng.getLoggerNames();
			
			nodeAdapter.start();

			connection = nodeAdapter.createAPSConnection();	
			
			baseStation = new Node(new Address(""+SPINEPacketsConstants.SPINE_BASE_STATION));
			baseStation.setLogicalID(new Address(SPINEPacketsConstants.SPINE_BASE_STATION_LABEL));
			
			eventDispatcher = new EventDispatcher(this);

		} catch (NumberFormatException e) {
			exit(DEF_PROP_MISSING_MSG);
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, e.getMessage());
		} 
		catch (InstantiationException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, e.getMessage());
		} 
	}
	
	
	
	/** package-scoped method called by SPINEFactory.
	 * The caller must guarantee that moteCom and platform are not null. **/
	SPINEManager(String moteCom, String platform, Resources resources) {
		try {
			MOTECOM = moteCom;
			PLATFORM = platform;	
			
			MY_GROUP_ID = (byte)Short.parseShort(prop.getProperty(Properties.GROUP_ID_KEY), 16);
			LOCALNODEADAPTER_CLASSNAME = prop.getProperty(PLATFORM + "_" + Properties.LOCALNODEADAPTER_CLASSNAME_KEY);
			URL_PREFIX = prop.getProperty(PLATFORM + "_" + Properties.URL_PREFIX_KEY);
			SPINEDATACODEC_PACKAGE = SPINEDATACODEC_PACKAGE_PREFIX + 
									prop.getProperty(PLATFORM + "_" + Properties.SPINEDATACODEC_PACKAGE_SUFFIX_KEY) + ".";
			MESSAGE_CLASSNAME = prop.getProperty(PLATFORM + "_" + Properties.MESSAGE_CLASSNAME_KEY);
			SPINE_SERVICE_MESSAGE_CODEC_PACKAGE = SPINE_SERVICE_MESSAGE_CODEC_PACKAGE_PREFIX + 
													prop.getProperty(PLATFORM + "_" + Properties.SPINEDATACODEC_PACKAGE_SUFFIX_KEY) + ".";
			
			System.setProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY, LOCALNODEADAPTER_CLASSNAME);
			nodeAdapter = LocalNodeAdapter.getLocalNodeAdapter();	
			
			Vector params = new Vector();
			params.addElement(MOTECOM);
			nodeAdapter.init(params);

			nodeAdapter.start();

			connection = nodeAdapter.createAPSConnection();	
			
			baseStation = new Node(new Address(""+SPINEPacketsConstants.SPINE_BASE_STATION));
			baseStation.setLogicalID(new Address(SPINEPacketsConstants.SPINE_BASE_STATION_LABEL));
			
			eventDispatcher = new EventDispatcher(this);

		} catch (NumberFormatException e) {
			exit(DEF_PROP_MISSING_MSG);
		} 
//		catch (IOException e) {
//			exit(DEF_PROP_MISSING_MSG);
//		}  			

		catch (ClassNotFoundException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, e.getMessage());
		} 
		catch (InstantiationException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, e.getMessage());
		} 
	}

	
	/**
	 * Adds a SPINEListener to the manager instance
	 * 
	 * @param listener the listener to register
	 */
	public void addListener(SPINEListener listener) {
		eventDispatcher.addListener(listener);
	}
	
	
	/**
	 * Remove a SPINEListener from the manager instance
	 * 
	 * @param listener the listener to deregister
	 */
	public void removeListener(SPINEListener listener) {
		eventDispatcher.removeListener(listener);
	}
	

	/**
	 * Returns the list of the discovered nodes as a Vector of spine.datamodel.Node objects
	 * 
	 * @return the discovered nodes
	 * 
	 * @see spine.datamodel.Node
	 */
	public Vector getActiveNodes() {
		return activeNodes;
	}
	
	
	/**
	 * Returns true if the manager has been asked to start the processing in the wsn; false otherwise
	 * 
	 * @return true if the manager has been asked to start the processing in the wsn; false otherwise  
	 */
	public boolean isStarted() {
		return this.started;
	}
	
	
	/**
	 * Commands the SPINEManager to discovery the surrounding WSN nodes
	 * A default timeout of 2s, is used
	 */
	public void discoveryWsn() {
		discoveryCompleted = false;
		
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.SERVICE_DISCOVERY, null);
		
		
		if(this.discoveryTimeout > 0) {
			   Handler handler = new Handler(); 
			    handler.postDelayed(new Runnable() { 
			         public void run() { 
			 			// if no nodes has been discovered, it's the symptom of some radio connection problem;
			 			// the SPINEManager notifies the SPINEListener of that situation by issuing an appropriate service message
			 			if (activeNodes.size()==0) {
			 				ServiceErrorMessage serviceErrorMessage=new ServiceErrorMessage();
			 				serviceErrorMessage.setNode(baseStation);
			 				serviceErrorMessage.setMessageDetail(SPINEServiceMessageConstants.CONNECTION_FAIL);
			 				eventDispatcher.notifyListeners(SPINEPacketsConstants.SVC_MSG,serviceErrorMessage);
			 			}		
			 			discoveryCompleted = true;			
			 			eventDispatcher.notifyListeners(DISC_COMPL_EVT_COD, activeNodes);			        	 
			             
			         } 
			    }, this.discoveryTimeout); 		
		}
		
	}
	

	/**
	 * 
	 * Commands the SPINEManager to discovery the surrounding WSN nodes
	 * This method sets the timeout for the discovery procedure.
	 * 
	 * A timeout <= 0 will disable the Discovery Timer; 
	 * this way a 'discovery complete' event will never be signaled and at any time an 
	 * announcing node is added to the active-nodes list and signaled to the SPINE listeners. 
	 * 
	 * @param discoveryTimeout the timeout for the discovery procedure
	 */
	public void discoveryWsn(long discoveryTimeout) {
		this.discoveryTimeout = discoveryTimeout;
		
		discoveryWsn();		
	}
	
	public void pollBluetoothDevices() {
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.POLL_BLUETOOTH_DEVICES, null);
	}
	
	public void setBluetoothDeviceEnabled(String address, Boolean enabled) {
	}
	
	
	
	/**
	 * 
	 * Currently, it does nothing!
	 * 
	 */
	public void bootUpWsn() {			
	}

	
	/**
	 * Setups a specific sensor of the given node.
	 * Currently, a sensor is setup by providing a sampling time value and a time scale factor 
	 * 
	 * @param node the destination of the request
	 * @param setupSensor the object containing the setup parameters
	 */
	public void setup(Node node, SpineSetupSensor setupSensor) {
		if (node == null) 
			throw new RuntimeException("Can't setup the sensor: node is null");
		if (setupSensor == null)
			throw new RuntimeException("Can't setup the sensor: setupSensor is null");
		
		send(node.getPhysicalID(), SPINEPacketsConstants.SETUP_SENSOR, setupSensor);
	}

	public void setup(Node node, ShimmerNonSpineSetupSensor setupSensor) {
		if (node == null) 
			throw new RuntimeException("Can't setup the sensor: node is null");
		if (setupSensor == null)
			throw new RuntimeException("Can't setup the sensor: setupSensor is null");
		
		send(node.getPhysicalID(), SPINEPacketsConstants.SETUP_SENSOR, setupSensor);
		
	}
	
	public void serviceCommand(SpineServiceCommand serviceCommand) {
		if (serviceCommand == null)
			throw new RuntimeException("Can't send service command: setupSensor is null");
		
		send(new Address("0"), SPINEPacketsConstants.SERVICE_COMMAND, serviceCommand);
		
	}
	
	
	
	/**
	 * Setups a specific function of the given node.
	 * The parameters involved are 'function dependent' and are specified by providing a proper
	 * SpineSetupFunction instantiation object
	 * 
	 * @param node the destination of the request
	 * @param setupFunction the object containing the setup parameters
	 */
	public void setup(Node node, SpineSetupFunction setupFunction) {
		if (node == null) 
			throw new RuntimeException("Can't setup the function: node is null");
		if (setupFunction == null)
			throw new RuntimeException("Can't setup the function: setupFunction is null");
		
		send(node.getPhysicalID(), SPINEPacketsConstants.SETUP_FUNCTION, setupFunction);
	}
	

	/**
	 * Activates a function (or even only function sub-routines) on the given sensor.
	 * The content of the actual request is 'function dependent' and it's embedded into the
	 * 'SpineFunctionReq' instantiations.
	 * 
	 * @param node the destination of the request
	 * @param functionReq the specific function activation request
	 */
	public void activate(Node node, SpineFunctionReq functionReq) {
		if (node == null) 
			throw new RuntimeException("Can't activate the function: node is null");
		if (functionReq == null)
			throw new RuntimeException("Can't activate the function: functionReq is null");
		
		// function activation requests are differentiated by the deactivation requests by setting the appropriate flag 
		functionReq.setActivationFlag(true);

		send(node.getPhysicalID(), SPINEPacketsConstants.FUNCTION_REQ, functionReq);
	}	
	
	/**
	 * Deactivates a function (or even only function sub-routines) on the given sensor.
	 * The content of the actual request is 'function dependent' and it's embedded into the
	 * 'SpineFunctionReq' instantiations.
	 * 
	 * @param node the destination of the request
	 * @param functionReq the specific function deactivation request
	 */
	public void deactivate(Node node, SpineFunctionReq functionReq) {
		if (node == null) 
			throw new RuntimeException("Can't deactivate the function: node is null");
		if (functionReq == null)
			throw new RuntimeException("Can't deactivate the function: functionReq is null");
		
		// function activation requests are differentiated by the deactivation requests by setting the appropriate flag
		functionReq.setActivationFlag(false);

		send(node.getPhysicalID(), SPINEPacketsConstants.FUNCTION_REQ, functionReq);
	}	
	
	/**
	 * Commands the given node to do a 'immediate one-shot' sampling on the given sensor.
	 * The method won't have any effects if the node is not provided with the given sensor.
	 * 
	 * @param node the destination of the request
	 * @param sensorCode the sensor to be sampled
	 * 
	 * @see spine.SPINESensorConstants
	 */
	public void getOneShotData(Node node, byte sensorCode) {
		SpineSetupSensor sss = new SpineSetupSensor();
		sss.setSensor(sensorCode);
		sss.setTimeScale(SPINESensorConstants.NOW);
		sss.setSamplingTime(0);
		setup(node, sss);	
	}
	
	
	/**
	 * Starts the WSN sensing and computing the previously requested functions. 
	 * This is done thru a broadcast SPINE Synchr message.
	 * This simple start method will let the nodes use their default radio access scheme.
	 * 
	 * @param radioAlwaysOn low-power radio mode control flag; set it 'true' to disable the radio low-power mode;
	 * 'false' to allow radio module turn off during 'idle' periods 
	 */
	public void startWsn(boolean radioAlwaysOn) {
		startWsn(radioAlwaysOn, false);
	}	
	
	/**
	 * Starts the WSN sensing and computing the previously requested functions. 
	 * This is done thru a broadcast SPINE Synchr message.
	 * 
	 * @param radioAlwaysOn low-power radio mode control flag; set it 'true' to disable the radio low-power mode;
	 * 'false' to allow radio module turn off during 'idle' periods  
	 * 
	 * @param enableTDMA TDMA transmission scheme control flag; set it 'true' to enable the TDMA on the nodes;
	 * 'false' to keep using the default radio access scheme. 
	 */
	public void startWsn(boolean radioAlwaysOn, boolean enableTDMA) {
		SpineStart ss = new SpineStart();
		ss.setActiveNodesCount(activeNodes.size());
		ss.setRadioAlwaysOn(radioAlwaysOn);
		ss.setEnableTDMA(enableTDMA);
		
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.START, ss);
		
		started = true;
	}	
	
	/**
	 * Commands a software 'on node local clock' synchronization of the whole WSN.
	 * This is done thru a broadcast SPINE Synchr message.
	 */
	public void syncWsn() {		
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.SYNCR, null);
	}
	
	
	/**
	 * Commands a software reset of the whole WSN.
	 * This is done thru a broadcast SPINE Reset message.
	 */
	public void resetWsn() {		
		//send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.RESET, null);
		
		// broadcast reset is translated into multiple unicast reset as a workaround in the case node are 
		// communicating in radio low power mode. 
		for(int i = 0; i<activeNodes.size(); i++) 
			send(((Node)activeNodes.elementAt(i)).getPhysicalID(), SPINEPacketsConstants.RESET, null);
		
		started = false;
	}	
	
	
	/*
	 * Private utility method containing the actual message send code 
	 */
	private void send(Address destination, byte pktType, SpineObject payload) {
		try {
			
			//	dynamic class loading of the proper SpineCodec implementation
			if (payload != null){
				    String payloadClassName = payload.getClass().getSimpleName();
				    spineCodec = (SpineCodec)htInstance.get (payloadClassName);
					
				    if (spineCodec==null){
				    	Class p =  Class.forName(SPINEDATACODEC_PACKAGE + payloadClassName + "_codec");
				    	spineCodec = (SpineCodec)p.newInstance();
				    	htInstance.put (payload.getClass().getSimpleName(), spineCodec);	
				    } 
			}
			
			
			// dynamic class loading of the proper Message implementation
			// String messageClassName = prop.getProperty(Properties.MESSAGE_CLASSNAME_KEY);
			Class c = (Class)htInstance.get (MESSAGE_CLASSNAME);
			if (c == null){
				c = Class.forName(MESSAGE_CLASSNAME);
				htInstance.put (MESSAGE_CLASSNAME, c);
			} 
			com.tilab.gal.Message msg = (com.tilab.gal.Message)c.newInstance();
			// construction of the message 
			msg.setDestinationURL(URL_PREFIX + destination.getAsInt());
			msg.setClusterId(pktType); // the clusterId is treated as the 'packet type' field
			msg.setProfileId(MY_GROUP_ID); // the profileId is treated as the 'group id' field
			if (payload != null) {
				try {
					
					byte[] payloadArray = spineCodec.encode(payload);
					short[] payloadShort = new short[payloadArray.length];
        			for (int i = 0; i<payloadShort.length; i++)
        				payloadShort[i] = payloadArray[i];					
					msg.setPayload(payloadShort);
					
				}catch (MethodNotSupportedException e){
					e.printStackTrace();
					if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
						SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
				}	      
			}
			
			// message sending
			connection.send(msg);
			
		} catch (InstantiationException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, "InstantiationException" + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, "IllegalAccessException" + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, "ClassNotFoundException" + e.getMessage());
		} catch (InterruptedIOException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, "InterruptedIOException" + e.getMessage());
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			if (l.isLoggable(Logger.SEVERE)) 
				l.log(Logger.SEVERE, "UnsupportedOperationException" + e.getMessage());
		}			
	}
	
	/**
	 * Returns the already discovered Node corresponding to the given Address
	 * 
	 * @param id the physical address of the Node to obtain
	 * @return the already discovered Node corresponding to the given Address 
	 * or null if doesn't exist any Node with the given Address.  
	 */
	public Node getNodeByPhysicalID(Address id) {
		for (int i = 0; i<activeNodes.size(); i++) {
			Node curr = (Node)activeNodes.elementAt(i);
			if(curr.getPhysicalID().equals(id))
				return curr;
		}
		l.log(Logger.INFO, "Could not find sensor in active nodes" );
		return null;
	}
	
	/**
	 * Returns the already discovered Node corresponding to the given Address
	 * 
	 * @param id the logical address of the Node to obtain
	 * @return the already discovered Node corresponding to the given Address 
	 * or null if doesn't exist any Node with the given Address.  
	 */
	public Node getNodeByLogicalID(Address id) {
		for (int i = 0; i<activeNodes.size(); i++) {
			Node curr = (Node)activeNodes.elementAt(i);
			if(curr.getLogicalID().equals(id))
				return curr;
		}
		return null;
	}
	
	/**
	 * Returns the MOTECOM property value specified in the given app.properties file
	 * 
	 * @return the MOTECOM property value specified in the given app.properties file 
	 * or the empty string if the MOTECOM property hasn't been specified  
	 */
	public static String getMoteCom() {
		return MOTECOM;
	}
	
	/**
	 * Returns the PLATFORM property value specified in the given app.properties file
	 * 
	 * @return the PLATFORM property value specified in the given app.properties file 
	 * or the empty string if the PLATFORM property hasn't been specified  
	 */
	public static String getPlatform() {
		return PLATFORM;
	}
	
	/**
	 * Returns the Logger to be used by all the SPINE core classes
	 * 
	 * @return the Logger to be used by all the SPINE core classes  
	 */
	public static Logger getLogger() {
		return l;
	}
	
	/**
	 * Returns the MY_GROUP_ID property value specified in the given app.properties file
	 * 
	 * @return the MY_GROUP_ID property value specified in the dafaults.properties file
	 */
	public static byte getMyGroupID() {
		return MY_GROUP_ID;
	}
	
	private static void exit(String msg) {
		if (l.isLoggable(Logger.SEVERE)) {
			StringBuffer str = new StringBuffer();
			str.append(msg);
			str.append("\r\n");
			str.append("Will exit now!");
			l.log(Logger.SEVERE, str.toString());
		}
		System.exit(-1);
	}

	
}
