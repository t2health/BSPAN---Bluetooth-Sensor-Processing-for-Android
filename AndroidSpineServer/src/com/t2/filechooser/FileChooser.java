package com.t2.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;
import com.t2.compassionMeditation.BioZenConstants;



public class FileChooser extends ListActivity {
	
    private File currentDir;
    private FileArrayAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //currentDir = new File("/sdcard/");
        currentDir = Environment.getExternalStorageDirectory();
        
        fill(currentDir);
        
        // Set a listener for long click to email file
        ListView lv = getListView();         
        lv.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener () { 
        	@Override 
            public boolean onItemLongClick(AdapterView<?> av, View v, int  pos, long id) { 

        		Option o = adapter.getItem(pos);
        		if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
        		}
        		else {
        			 Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), o.getName()));        		
            		
            		Intent i = new Intent(Intent.ACTION_SEND);
            		i.setType("text/plain");
            		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"scott.coleman@tee2.org"});
            		i.putExtra(Intent.EXTRA_SUBJECT, "session results: " + o.getName());
//            		i.putExtra(Intent.EXTRA_TEXT   , o.getName());
            		i.putExtra(Intent.EXTRA_STREAM, uri);        		
            		try {
            		    startActivity(Intent.createChooser(i, "Send mail..."));
            		} catch (android.content.ActivityNotFoundException ex) {
            		    Toast.makeText(FileChooser.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            		}        		
        		}        		
        		
        		
        		
        		
        		return true; 
            } 
        });         
        
    }
    private void fill(File f)
    {
    	File[]dirs = f.listFiles();
		 this.setTitle("Current Dir: "+f.getName());
		 List<Option>dir = new ArrayList<Option>();
		 List<Option>fls = new ArrayList<Option>();
		 try{
			 for(File ff: dirs)
			 {
				if(ff.isDirectory()) {
//					dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
				}
				else
				{
					String fileName = ff.toString();
					if (fileName.endsWith(".log"))
						fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
				}
			 }
		 }catch(Exception e)
		 {
			 
		 }
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new Option("..","Parent Directory",f.getParent()));
		 adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
		 this.setListAdapter(adapter);
    }
    
    
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
				currentDir = new File(o.getPath());
				fill(currentDir);
		}
		else
		{
			if (!o.getName().contains("Logcat"))
				onFileClick(o);
		}
	}
    private void onFileClick(Option o)
    {
		Intent resultIntent;
		resultIntent = new Intent();
		resultIntent.putExtra(BioZenConstants.FILECHOOSER_USER_ACTIVITY_RESULT, o.getName());		
		setResult(RESULT_OK, resultIntent);
		finish();
    }
}