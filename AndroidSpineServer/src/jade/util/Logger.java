package jade.util;

import java.util.Hashtable;
import java.util.Properties;

import android.util.Log;

/**
 * Wrapper class for using the generic Java logging facility
 * which is used by the majority of SPINE core code
 * 	ex:
 * 			private static Logger l = Logger.getMyLogger(SPINEManager.class.getName());
 *			if (l.isLoggable(Logger.SEVERE)) 
 *				l.log(Logger.SEVERE, "Test");
 *
 *  This is maintained in the core SPINE code for compatibility purposes with other platforms
 *  Classes that are exclusively Android will usually use the android Catalog Logger
 *  ex:
 *  		Log.i("Test);
 *  
 * @author scott.coleman
 *
 */
public class Logger
{
	private static final String TAG = "JavaLogger";
  //SEVERE is a message level indicating a serious failure.
	public static final int SEVERE	=	10;
  //WARNING is a message level indicating a potential problem.
	public static final int WARNING	=	9;
  //INFO is a message level for informational messages
	public static final int INFO	=	8;
  //CONFIG is a message level for static configuration messages.
  	public static final int CONFIG	=	7;
  //FINE is a message level providing tracing information.
	public static final int FINE	=	5;
  //FINER indicates a fairly detailed tracing message.
	public static final int FINER	=	4;
  //FINEST indicates a highly detailed tracing message
	public static final int FINEST	=	3;
  //ALL indicates that all messages should be logged.
	public static final int ALL		=	-2147483648;
  //Special level to be used to turn off logging
	public static final int OFF		=	2147483647;

	private static Properties verbosityLevels = null;
	private static Hashtable loggers = new Hashtable();
	
	private int myLevel = INFO;
	private String myName;
	
	public synchronized static Logger getMyLogger(String name){
		Logger l = (Logger) loggers.get(name);
		if (l == null) {
			StringBuffer sb = new StringBuffer(name.replace('.', '_'));
			sb.append("_loglevel");
			String key = sb.toString();
			int level = INFO;
			if (verbosityLevels != null) {
				try {
					level = getLevel(verbosityLevels.getProperty(key));
				}
				catch (Exception e) {
					// Keep default
				}
			}
			l = new Logger(name, level);
			loggers.put(name, l);
		}
		return l;
	}

	public static void initialize(Properties pp) {

	}

	
	private static int getLevel(String level) {
		if (level != null) {
			try {
				return Integer.parseInt(level);
			}
			catch (Exception e) {				
			 	if (level.equals("severe"))
			 		return SEVERE;
			 	if (level.equals("warning"))
			 		return WARNING;
			 	if (level.equals("info"))
			 		return INFO;
			 	if (level.equals("config"))
			 		return CONFIG;
			 	if (level.equals("fine"))
			 		return FINE;
			 	if (level.equals("finer"))
			 		return FINER;
			 	if (level.equals("finest"))
			 		return FINEST;
			 	if (level.equals("all"))
			 		return ALL;
			 	if (level.equals("off"))
			 		return OFF;
			}
		}
		// If we get here either nothing or a wrong value was specified --> use default
		return INFO;
	}
		
				
	//  Private constructor. The getMyLogger() static method must be used 
	private Logger(String name, int level) {
		myName = name;
		myLevel = level;
	}
	
	// Check if the current level is loggable
	public boolean isLoggable(int level){
		if(level >= myLevel) {
	   return true;
		}
		else {
			return false;
	  }
	}

	public void log(int level, String msg) {
		if (msg != null)
		{
			Log.i(TAG, msg);
		}
	}
}



