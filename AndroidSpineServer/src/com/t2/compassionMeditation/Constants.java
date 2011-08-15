package com.t2.compassionMeditation;

import spine.datamodel.MindsetData;

public class Constants {
	
	public static final String PREF_SESSION_LENGTH = "SessionLength";
	public static final int PREF_SESSION_LENGTH_DEFAULT = 1800;

	public static final String PREF_ALPHA_GAIN = "AlphaGain";
	public static final float PREF_ALPHA_GAIN_DEFAULT = 5;

	public static final String PREF_INSTRUCTIONS_ON_START = "InstructionsOnStart";
	public static final boolean PREF_INSTRUCTIONS_ON_START_DEFAULT = true;

	public static final String PREF_MULTIPLE_USERS = "Allow Multiple Users";
	public static final boolean PREF_MULTIPLE_USERS_DEFAULT = false;

	public static final String PREF_COMMENTS = "Allow Comments";
	public static final boolean PREF_COMMENTS_DEFAULT = false;

	public static final String PREF_SAVE_RAW_WAVE = "Save Raw Wave";
	public static final boolean PREF_SAVE_RAW_WAVE_DEFAULT = false;

	public static final String PREF_SHOW_A_GAIN = "ShowAGain";
	public static final boolean PREF_SHOW_A_GAIN_DEFAULT = true;

	public static final String PREF_BAND_OF_INTEREST = "BandOfInterest";
	public static final int PREF_BAND_OF_INTEREST_DEFAULT = MindsetData.THETA_ID;;

	
	
	public static final String EXTRA_SESSION_NAME = "SessionName";
	
	// Intent constants for StartActivityForResult
	
	public static final int SELECT_USER_ACTIVITY = 0x301;	
	public static final String SELECT_USER_ACTIVITY_RESULT = "SelectUserActivityResult";	

	public static final int INSTRUCTIONS_USER_ACTIVITY = 0x302;	
	public static final String INSTRUCTIONS_USER_ACTIVITY_RESULT = "InstructionsActivityResult";	

	public static final int FILECHOOSER_USER_ACTIVITY = 0x303;
	public static final String FILECHOOSER_USER_ACTIVITY_RESULT = "File";
	
}
