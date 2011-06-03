package jade.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import android.util.Log;

//public class AndroidLogger extends Logger{
	public class AndroidLogger {
	
	private static AndroidLogger loggerInstance;	
	static private FileHandler fileHTML;
	static private Formatter formatterHTML;

	//This custom formatter formats parts of a log record to a single line
	class MyHtmlFormatter extends Formatter
	{
		// This method is called for every log records
		public String format(LogRecord rec)
		{
			StringBuffer buf = new StringBuffer(1000);
			return buf.toString();
		}
		public MyHtmlFormatter(int i)
		{
			
		}
	}	
	
	
	public AndroidLogger()
	{
//		super("","");
	}

	public static AndroidLogger getLogger()
	{
		if (loggerInstance == null)
		{
			loggerInstance = new AndroidLogger();
		}
		
		return loggerInstance;
		
	}
	
	static public void setup()
	{
		
		
		try {
			fileHTML = new FileHandler("Logging.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
//	public static void log(int level, String msg) 
	public void log(Level severe, String msg) 
	{
		Log.i("tag", msg);		
		//  log(level, msg, null);
	}
	
	public static boolean isLoggable(int level)
	{
		return true;
	}
	
//	public static AndroidLogger getMyLogger(String str)
//	{
//		return AndroidLogger;
//	}

}
