package jade.util;
/**
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * Copyright (C) 2001,2002 TILab S.p.A.
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

import java.io.ObjectStreamException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.t2.biofeedback.Constants;

import android.util.Log;

/*#MIDP_INCLUDE_BEGIN
import javax.microedition.rms.RecordStore;
#MIDP_INCLUDE_END*/

/**
 * This class provides a uniform API to produce logs
 * over a set of different and device-dependent logging mechanisms. 
 * Different implementations of this class are
 * provided according to the target environment (J2SE, pJava, MIDP), but all
 * of them offer the same API. Finally the MIDP implementation redirects 
 * logging printouts on a MIDP RecordStore where they can be later inspected 
 * by means of the OutputViewer MIDlet distributed with the LEAP add-on.
 * <br>
 * See also this
 * <a href="../../../tutorials/logging/JADELoggingService.html"> tutorial </a>
 * for an overview of the JADE Logging Service.
 * <br>
 * Logging levels can be used to control logging output.
 * According to java logging philosophy, several logging levels can be set.
 * The levels in descending order are: <p>
 * SEVERE (highest value) <br>
 * WARNING <br>
 * INFO <br>
 * CONFIG <br>
 * FINE <br>
 * FINER <br>
 * FINEST (lowest value)
 * <p> In addition, there is a level OFF that can be used to turn off logging, and a level ALL that can be used to enable logging of all messages.
 * <p>
 * Notice that re-definition of logging levels was necessary in order to allow
 * portability of calling code in PersonalJava e MIDP environments.
 * <p>
 * For instance, in order to log the warning message  "Attention!", the
 * following code should be used, independently of the target device: <br><br>
 *
 * <code>Logger logger = Logger.getMyLogger(this.getClass().getName());</code><br>
 * <code>if (logger.isLoggable(logger.WARNING)) </code>
 * <br> <code>logger.log(Logger.WARNING,"Attention!"); </code>
 * <p>
 * Notice that the test <code>isLoggable</code> allows just to improve performance, but
 * it has no side-effect.
 * <p> <b>J2SE</b><br>
 * The J2SE implementation is a pure
 * extension of the <code>java.util.logging.Logger</code> class and
 * it provides the whole set of
 * functionalities of java.util.logging.
 * <p> In the J2SE environment, the logging configuration can be initialized by using a logging
 * configuration file that will be read at startup. This file is in standard
 * java.util.Properties format. The default logging configuration,
 * that is part of the JRE distribution,
 * can be overridden by setting the java.util.logging.config.file
 * system property, like the following example: <br>
 * <code>java -Djava.util.logging.config.file=mylogging.properties jade.Boot </code>
 *
 * <p><b>PersonaJava</b><br>
 * In the PJava implementation of the <code>Logger</code> class (available in 
 * the LEAP add-on) calls to the 
 * <code>log()</code> method result in calls to <code>System.out.println()</code>.
 * Alternatively it is possible to redirect logging printouts to a text file 
 * by setting the <code>-jade_util_Logger_logfile</code> option. Note that, 
 * in order to face resource limitations, it is not possible to redirect 
 * logging printouts produced by different Logger objects to different files.
 * 
 * <p><b>MIDP</b><br>
 * In the MIDP implementation of the <code>Logger</code> class (available in 
 * the LEAP add-on) logging printouts are redirected to a MIDP RecordStore so 
 * that they can be later viewed
 * by means of the <code>jade.util.leap.OutputViewer</code> MIDlet included
 * in the LEAP add-on.<br>
 * <br>
 * The default level for logging is set to INFO, all messages of higher level 
 * will be logged by default.
 * In MIDP and PJava the logging level for a Logger object registererd with 
 * name x.y.z can be configured by setting the configuration option
 * <code>x_y_z_loglevel</code> to one of <code>severe, warning, info, config,
 * fine, finer, finest, all</code>. See the LEAP user guide for details about 
 * how to set JADE configuration options in MIDP and PJava.
 *
 * @author Rosalba Bochicchio - TILAB
 * @author Nicolas Lhuillier - Motorola (MIDP version)
 */
public class Logger
//#J2ME_EXCLUDE_BEGIN
		extends java.util.logging.Logger
//#J2ME_EXCLUDE_END
		implements Serializable
{
	

	private static final String TAG = Constants.TAG;
	//#J2ME_EXCLUDE_BEGIN
	/**
	 * SEVERE is a message level indicating a serious failure.
	 **/
	public static final Level SEVERE	=	Level.SEVERE;
	/**
	 * WARNING is a message level indicating a potential problem.
	 **/
	public static final Level WARNING	=	Level.WARNING;
	/**
	 * INFO is a message level for informational messages.
	 **/
	public static final Level INFO		=	Level.INFO;
	/**
	 * CONFIG is a message level for static configuration messages.
	 **/
	public static final Level CONFIG	=	Level.CONFIG;
	/**
	 * FINE is a message level providing tracing information.
	 **/
	public static final Level FINE		=	Level.FINE;
	/**
	 * FINER indicates a fairly detailed tracing message.
	 **/
	public static final Level FINER		=	Level.FINER;
	/**
	 * FINEST indicates a highly detailed tracing message
	 **/
	public static final Level FINEST	=	Level.FINEST;
	/**
	 *ALL indicates that all messages should be logged.
	 **/
	public static final Level ALL		=	Level.ALL;
	/**
	 * Special level to be used to turn off logging
	 **/
	public static final Level OFF		=	Level.OFF;


	private static Map wrappers = new HashMap();

    /**
     * Private method to construct a logger for a named subsystem.
     * @param name A name for the logger
     * @param resourceBundleName  Name of ResourceBundle to be used for localizing messages for this logger. May be null if none of the messages require localization.
    */
//	private Logger(String name,String resourceBundleName){
		public Logger(String name,String resourceBundleName){
		super(name,resourceBundleName);
	}

	//////////////////////////////////////////////
	// This section is for serialization purposes
	//////////////////////////////////////////////
	private Object writeReplace() throws ObjectStreamException {
		return new DummyLogger(getName());
	}

			
	private static class DummyLogger implements Serializable {
		private static final long serialVersionUID = 1L;
		private String name;

		public DummyLogger(String n) {
			name = n;
		}

		private Object readResolve() throws ObjectStreamException {
			return new Logger(name, null);
		}
	}
	//////////////////////////////////////////////
	
	/**
	   Find or create a logger for a named subsystem.
	   @param name The name of the logger.
       @return the instance of the Logger.
	 */
	public synchronized static Logger getMyLogger(String name) {
		java.util.logging.LogManager mng = java.util.logging.LogManager.getLogManager(); 
		java.util.logging.Logger lg = mng.getLogger(name);
		if (lg == null) {
			lg = new Logger(name, (String)null);
			mng.addLogger(lg);
			lg = mng.getLogger(name);
		}
		else if (!(lg instanceof Logger)) {
			// Someone created a java logger for the named subsystem before this method is invoked
			lg = getWrapper(lg);		
		}
		
		return (Logger) lg;
	}
	
	private static Logger getWrapper(java.util.logging.Logger lg) {
		Logger jadeLogger = (Logger) wrappers.get(lg.getName());
		if (jadeLogger == null) {
			jadeLogger = new LoggerWrapper(lg);
			wrappers.put(lg.getName(), jadeLogger);
		}
		return jadeLogger;
	}
	
	public void log(int level, String str)
	{
		Log.i(TAG, str);
	}
	public boolean isLoggable(int level)
	{
		return true;
	}
	/**
	 * Inner class LoggerWrapper
	 */
	private static class LoggerWrapper extends Logger {
		private java.util.logging.Logger realLogger;
		
		private LoggerWrapper(java.util.logging.Logger lg) {
			super(lg.getName(), (String) null);
			realLogger = lg;
		}
		
		public void log(LogRecord r) {
			realLogger.log(r);
		}
	}
	
	/**
	   Initialize the logging mechanism.
	   This method makes sense only in a PJAVA or MIDP environment,
     but is available in J2SE too (where it does nothing) to provide
     a uniform interface over the different Java environments.
   */
	public static void initialize(Properties pp) {
	}
	//#J2ME_EXCLUDE_END
	
  private static PrintStream logStream = System.out;

	public static void println(String log) {
		logStream.println(log);
		/*#MIDP_INCLUDE_BEGIN
		try {
			write(log);
		}
		catch (Throwable t){
			// Maybe the record-store has been closed from the outside. Retry.
            theRecordStore = null;
            try {
      	      write(log);
			}
			catch (Throwable t1) {
              t.printStackTrace();
      	      theRecordStore = null;
			}
		}		
		#MIDP_INCLUDE_END*/
	}
		
	
	/*#J2ME_INCLUDE_BEGIN
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
			if (pp != null) {
				PrintStream ps = initLogStream(pp);
				if (ps != null) {
				  logStream = ps;
        }
				verbosityLevels = pp;
			}
			else {
				verbosityLevels = new Properties();
			}
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
		  log(level, msg, null);
		}
		
		public void log(int level, String msg, Throwable t) {
	      if(level >= myLevel){
	        StringBuffer sb = new StringBuffer(myName);
	        sb.append(": ");
	        sb.append(msg);
	        if (t != null) {
	          sb.append('[');
	          sb.append(t);
	          sb.append(']');
	        }
		    println(sb.toString());
	      }
		}		
	#J2ME_INCLUDE_END*/


  /*#PJAVA_INCLUDE_BEGIN
    private static PrintStream initLogStream(Properties pp) {
      String logprefix = pp.getProperty("jade_util_Logger_logfile");
      if (logprefix != null) {
        try {
		      java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("ddMMyyyy");
		      String logfile = logprefix + sdf.format(new java.util.Date()) + ".txt";
          return new PrintStream(new FileOutputStream(logfile, true)) {
            private java.text.SimpleDateFormat hourFormatter = new java.text.SimpleDateFormat("HH:mm:ss");
            
            public void println(String s) {
            s = hourFormatter.format(new java.util.Date()) + " " + s;
              super.println(s);
            }
          };
        }
        catch (Exception e) {
          println("Cannot initialize log stream. "+e);
        }
      }
      return null;
    }
  #PJAVA_INCLUDE_END*/
	
	
  /*#MIDP_INCLUDE_BEGIN
	private static final String OUTPUT = "OUTPUT";
	private static RecordStore theRecordStore;
	private static volatile int cnt = 0;

	static {
		try {
			RecordStore.deleteRecordStore(OUTPUT);
		}
		catch (Exception e) {
			// The RS does not exist yet --> No need to reset it
		}
	}

	private static void write(String msg) throws Throwable {
		if (theRecordStore == null) {
			theRecordStore = RecordStore.openRecordStore(OUTPUT, true);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(cnt);
        cnt++;
		sb.append(") ");
		sb.append(msg);
        byte[] bb = sb.toString().getBytes();
        theRecordStore.addRecord(bb,0,bb.length);
	}
		
    private static PrintStream initLogStream(Properties pp) {
      return null;
    }
  #MIDP_INCLUDE_END*/
}



