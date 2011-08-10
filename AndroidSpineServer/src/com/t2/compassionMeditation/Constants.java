package com.t2.compassionMeditation;

import spine.datamodel.MindsetData;

public class Constants {
	public static final String FILE_CHOOSER_EXTRA = "File";
	
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

	public static final int fileChooserRequestCode = 0x300;
}
