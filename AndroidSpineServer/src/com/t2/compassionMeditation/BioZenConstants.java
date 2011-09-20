package com.t2.compassionMeditation;


import spine.datamodel.MindsetData;

public class BioZenConstants {
	
	public static final int MAX_KEY_ITEMS = 	MindsetData.NUM_BANDS 
						+ 2 											// eattention, eMEditation
						+ 3;												// HeartRate, RespRate, SkinTemp;
	
	public static final String PREF_SESSION_LENGTH = "session_length";
	public static final int PREF_SESSION_LENGTH_DEFAULT = 1800;

	public static final String PREF_ALPHA_GAIN = "alpha_gain";
	public static final float PREF_ALPHA_GAIN_DEFAULT = 5;

	public static final String PREF_INSTRUCTIONS_ON_START = "instructions_on_start";
	public static final boolean PREF_INSTRUCTIONS_ON_START_DEFAULT = false;

	public static final String PREF_COMMENTS = "Allow Comments";
	public static final boolean PREF_COMMENTS_DEFAULT = false;

	public static final String PREF_SAVE_RAW_WAVE = "save_raw_wave";
	public static final boolean PREF_SAVE_RAW_WAVE_DEFAULT = false;

	public static final String PREF_SHOW_A_GAIN = "show_a_gain";
	public static final boolean PREF_SHOW_A_GAIN_DEFAULT = true;

	public static final int PREF_BIOHARNESS_PHEARTRATE = 0;
	public static final int PREF_BIOHARNESS_PRESPRATE = 1;
	public static final int PREF_BIOHARNESS_PSKINTEMP = 2;
	public static final int PREF_BIOHARNESS_PNONE = 3;
	// Note - until this gets fixed, the above names MUST match the strings in R.array.bioharness_parameters_array

	public static final String PREF_BIOHARNESS_PARAMETER_OF_INTEREST = "parameter_of_interest";
	public static final String PREF_BIOHARNESS_PARAMETER_OF_INTEREST_DEFAULT = "0";
	
	public static final String PREF_BAND_OF_INTEREST = "band_of_interest";

	public static final String PREF_BAND_OF_INTEREST_REVIEW = "BandOfInterestReview";
	public static final int PREF_BAND_OF_INTEREST_DEFAULT_REVIEW = MindsetData.THETA_ID;


	public static final String PREF_USER_MODE = "user_mode";
	public static final String PREF_USER_MODE_DEFAULT = "1";

	public static final int PREF_USER_MODE_SINGLE_USER = 1;
	public static final int PREF_USER_MODE_PROVIDER = 2;

	
	
	public static final String EXTRA_SESSION_NAME = "SessionName";
	
	// Intent constants for StartActivityForResult
	
	public static final int SELECT_USER_ACTIVITY = 0x301;	
	public static final String SELECT_USER_ACTIVITY_RESULT = "SelectUserActivityResult";	

	public static final int INSTRUCTIONS_USER_ACTIVITY = 0x302;	
	public static final String INSTRUCTIONS_USER_ACTIVITY_RESULT = "InstructionsActivityResult";	

	public static final int FILECHOOSER_USER_ACTIVITY = 0x303;
	public static final String FILECHOOSER_USER_ACTIVITY_RESULT = "File";
	
	public static final int VIEW_SESSIONS_ACTIVITY = 0x304;
	public static final String VIEW_SESSIONS_ACTIVITY_RESULT = "ViewSessions";
	
	public static final int USER_MODE_ACTIVITY = 0x305;	
	public static final String USER_MODE_ACTIVITY_RESULT = "UserModeResult";	

	public static final int END_SESSION_ACTIVITY = 0x306;	
	public static final String END_SESSION_ACTIVITY_RESULT = "EndSessionResult";	
	public static final String END_SESSION_ACTIVITY_CATEGORY = "EndSessionCategory";	
	public static final String END_SESSION_ACTIVITY_NOTES = "EndSessionNotes";	
	public static final int END_SESSION_QUIT = 0; 
	public static final int END_SESSION_SAVE = 1; 
	public static final int END_SESSION_RESTART = 2; 

	public static final int SPINE_MAINSERVER_ACTIVITY = 0x307;	
	public static final String SPINE_MAINSERVER_ACTIVITY_RESULT = "SpineMainServerActivityResult";	
	
	
}



