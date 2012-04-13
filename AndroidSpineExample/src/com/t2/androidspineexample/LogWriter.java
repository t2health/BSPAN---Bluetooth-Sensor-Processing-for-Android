package com.t2.androidspineexample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

public class LogWriter {
	private static final String TAG = "BFDemo";
	
	
	private BufferedWriter mLogWriter = null;
	private String mFileName = "";
	private File mLogFile;	
	public Context mContext;

	public LogWriter(Context context) {
		mContext = context;
	}
	
	public void open(String fileName) {
		
		mFileName = fileName;
		
		try {
		    File root = Environment.getExternalStorageDirectory();
		    if (root.canWrite()){
		        mLogFile = new File(root, fileName);
		        mFileName = mLogFile.getAbsolutePath();
		        
		        FileWriter gpxwriter = new FileWriter(mLogFile, true); // open for append
		        mLogWriter = new BufferedWriter(gpxwriter);

//		        try {
//		        	if (mLogWriter != null) {
//		        		mLogWriter.write(mLogHeader + "\n");
//		        	}
//				} catch (IOException e) {
//					Log.e(TAG, e.toString());
//				}
		        
		        
		    } 
		    else {
    		    Log.e(TAG, "Cannot write to log file" );
    			AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
    			alert.setTitle("ERROR");
    			alert.setMessage("Cannot write to log file");	
    			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int whichButton) {
    				}
   				});    			
    			alert.show();
    			
		    }
		} catch (IOException e) {
		    Log.e(TAG, "Cannot write to log file" + e.getMessage());
			AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
			alert.setTitle("ERROR");
			alert.setMessage("Cannot write to file");
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
			});    			
		
			alert.show();			
		    
		}		
		
		
	}
	
	public void close() {
    	try {
        	if (mLogWriter != null)
        		mLogWriter.close();
		} catch (IOException e) {
			Log.e(TAG, "Exeption closing file " + e.toString());
			e.printStackTrace();
		}    		
		mLogWriter = null;
	}

	public void write(String line) {
        line += "\n";
		try {
        	if (mLogWriter != null)
        		mLogWriter.write(line);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}			
	}
	
}
