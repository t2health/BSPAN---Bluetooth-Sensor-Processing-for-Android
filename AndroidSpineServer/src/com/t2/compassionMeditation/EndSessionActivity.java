package com.t2.compassionMeditation;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

//Need the following import to get access to the app resources, since thisclass is in a sub-package.
import com.t2.R;


public class EndSessionActivity extends Activity{
	private static final String TAG = "BFDemo";
	private static final String mActivityVersion = "1.0";
	static private EndSessionActivity instance;
	private static final String KEY_NAME = "categories_";	

	/**
	 * Currently selected category
	 */
	private String mCurrentCategory = "";

	/**
	 * Index of currently selected category
	 */	
	private int mSelectedCategory;

	
	/**
	 * UI ListView for list
	 */
	private ListView mListView;

	/**
	 * Ordered list of available BioUser 
	 */
	private ArrayList<String> mCurrentCategories;	
	
	private ArrayList<RadioButton> categoryButtons = new ArrayList<RadioButton>();;
	
	
	protected SharedPreferences sharedPref;
	
    private Button mQuitButton;
    private Button mSaveButton;
    private Button mRestartButton;
	private EditText mDetailLog;
    

	
	public void onButtonClick(View v)
	{
		Intent resultIntent = new Intent();
		resultIntent.putExtra(BioZenConstants.END_SESSION_ACTIVITY_CATEGORY, mCurrentCategory);		
		resultIntent.putExtra(BioZenConstants.END_SESSION_ACTIVITY_NOTES, mDetailLog.getText().toString());		

		final int id = v.getId();
		    switch (id) {
		    
		    case R.id.buttonQuitSession:
				resultIntent.putExtra(BioZenConstants.END_SESSION_ACTIVITY_RESULT, BioZenConstants.END_SESSION_QUIT);		
				setResult(RESULT_OK, resultIntent);
				finish();		    	
		    	break;

		    case R.id.buttonSaveSession:
				resultIntent.putExtra(BioZenConstants.END_SESSION_ACTIVITY_RESULT, BioZenConstants.END_SESSION_SAVE);	
				
				setResult(RESULT_OK, resultIntent);
				finish();		    	
		    	break;

		    case R.id.buttonRestartSession:
				resultIntent.putExtra(BioZenConstants.END_SESSION_ACTIVITY_RESULT, BioZenConstants.END_SESSION_RESTART);		
				setResult(RESULT_OK, resultIntent);
				finish();		    	
		    	break;

		    
		    case R.id.buttonAddUser:
				AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

				alert1.setMessage("Enter new user name");

				// Set an EditText view to get user input 
				final EditText input = new EditText(this);
				alert1.setView(input);

				alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String newUserName = input.getText().toString();
					
					boolean found = false;
					if (mCurrentCategories != null) {
						for (String category: mCurrentCategories) {
							if (category.equalsIgnoreCase(newUserName))
								found = true;							
						}
					}
					
					if (found) {
						AlertDialog.Builder alert2 = new AlertDialog.Builder(instance);

						alert2.setMessage("Category Already exists");
						alert2.show();	
					}
					else {

						mCurrentCategories.add(newUserName);		
						setCategories("categories", mCurrentCategories);
						
						
					}
				  }
				});

				alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				  }
				});

				alert1.show();		    	
				break;
		    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		this.setContentView(R.layout.end_session_activity_layout);
		
		
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());   
        mQuitButton = (Button) findViewById(R.id.buttonQuitSession);
        mSaveButton = (Button) findViewById(R.id.buttonSaveSession);
        mRestartButton = (Button) findViewById(R.id.buttonRestartSession);
        mDetailLog = (EditText) findViewById(R.id.editTextNotes);  
        
		
		mListView = (ListView)findViewById(R.id.listViewUsers);
		
//		
//		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//				String seletedItem = (String) mListView.getAdapter().getItem(i);
//				Intent resultIntent;
//				resultIntent = new Intent();
//				resultIntent.putExtra(Constants.SELECT_USER_ACTIVITY_RESULT, seletedItem);
//				setResult(RESULT_OK, resultIntent);
//				finish();				
//				
//			}
//		});		
		
//		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//				mCurrentCategory = (String) mListView.getAdapter().getItem(i);
//				mSelectedCategory = i;
//				
//				
//				AlertDialog.Builder alert = new AlertDialog.Builder(instance);
//				alert.setTitle("Choose Activity");
//				
//		    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//		            public void onClick(DialogInterface dialog, int whichButton) {
//		            }
//		        });				
//				
//		    	alert.setNeutralButton("Delete Category", new DialogInterface.OnClickListener() {
//		            public void onClick(DialogInterface dialog, int whichButton) {
//		            	
//						AlertDialog.Builder alert2 = new AlertDialog.Builder(instance);
//						alert2.setMessage("Are you sure?");
//
//						alert2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//
//						}
//						});
//
//						alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//						  public void onClick(DialogInterface dialog, int whichButton) {
//						  }
//						});
//
//						alert2.show();
//		            }
//		        });				
//				
//				alert.show();					
//				return true;
//			}
//		});		

		updateListView();		
	}

	/**
	 * Populates the UI list view with current available users
	 */
	void updateListView() {
		
		
		mCurrentCategories = getCategories("categories");
		// Make sure there are at least the base categories
		if (mCurrentCategories.size() < 4) {
			mCurrentCategories.clear();
			mCurrentCategories.add("Meditation");
			mCurrentCategories.add("Breathing");
			mCurrentCategories.add("Entertainment");
			mCurrentCategories.add("Working");
			
			setCategories("categories", mCurrentCategories);
			
		}
		
		
		ArrayList<String>  strUsers = new ArrayList<String>();
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mCurrentCategories);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
//		mListView.setAdapter(adapter);
		
        GridView gridView = (GridView) findViewById(R.id.gridViewCategories);
        gridView.setNumColumns(2);
        gridView.setAdapter(new ImageAdapter(this));		
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	private ArrayList<String> getCategories(String keySuffix) {
		String[] idsStrArr = SharedPref.getValues(
				sharedPref, 
				KEY_NAME+keySuffix, 
				",",
				new String[0]
		);
		
		return new ArrayList<String>(Arrays.asList(idsStrArr));
		
	}	

	private void setCategories(String keySuffix, ArrayList<String> categories) {
		Object[] alt = categories.toArray();	
		String[] ss = ArraysExtra.toStringArray(alt);		
		SharedPref.setValues(
				sharedPref, 
				KEY_NAME+keySuffix, 
				",", 
				ss);
//				ArraysExtra.toStringArray((Object[]categories));
	}		
	

	
    // references to our images - The order MUST match the ID index variables above
    private Integer[] mThumbIds = {
            R.drawable.learn_tab, 
            R.drawable.newsession_tab,
            R.drawable.view_tab,
            R.drawable.review_tab,
            R.drawable.files_tab,
    };	
	
	public class ImageAdapter extends BaseAdapter implements View.OnClickListener{
	    private Context mContext;

	    public ImageAdapter(Context c) {
	        mContext = c;
	    }

	    public int getCount() {
	        return mCurrentCategories.size();
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {

	    	RadioButton b = new RadioButton(instance);
	    	b.setText(mCurrentCategories.get(position));
	    	b.setOnClickListener(this);
	    	
	        categoryButtons.add(b);
	    	
	    	return b;
	    	
	    }

		@Override
		public void onClick(View v) {
			RadioButton b = (RadioButton) v;
			
			// For some reason radioGroup is not working here so we'll do the group action manually.
			for (RadioButton button: categoryButtons) {
				button.setChecked(false);
			}
			b.setChecked(true);
			mCurrentCategory = b.getText().toString();
			
		}
	}

	
}