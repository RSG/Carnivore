package org.rsg.carnivore;

import java.text.Format;
import java.text.SimpleDateFormat;

public class Constants {
	public final static long VERSION = (long) 5; // the version number is stored in three places 
												 // (change them all for a new version roll out):
												 // 		--here in Constants.java
												 //			--in build.xml
												 //			--in library.properties
	public final static Format PACKET_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
	public final static Format SERIALIZED_DATE_FORMAT = new SimpleDateFormat("yyMMMd");
	public final static String LOG_SUFFIX = ".txt";
	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");

	public final static int intTCP = 6; 	//standard protocol numbers
	public final static int intUDP = 17; 	//for these two
	
	public final static String JPCAP_i386 = "jpcap-i386";
	public final static String JPCAP_PPC = "jpcap-ppc";
	
	public final static String strTCP = "TCP";	
	public final static String strUDP = "UDP";	
	public final static String SOCKET_DELIMITER = ":";

	public static final String FILENAME_PROPS 			= ".carnivore_preferences.txt";
	public static final String PROPS_COMMENT 			= "Carnivore Preferences File";

	public static final String 	CHANNEL 			= "channel";
	public static final int 	CHANNEL_MINIVORE 	= 0;
	public static final int 	CHANNEL_CARNIVORE 	= 1;
	public static final int 	CHANNEL_HEXIVORE 	= 2;
	
	public static final String SHOULD_SKIP_UDP = "shouldSkipUDP";
	
	public static final String 	MAXIMUM_VOLUME = "maximumVolume";
	public static final int 	VOLUME_MAX = 20;

	//DEFAULTS FOR PREFERENCES
	public static final int DEFAULT_CHANNEL = CHANNEL_CARNIVORE;
	public static final boolean DEFAULT_SHOULD_SKIP_UDP = false;
	public static final int DEFAULT_MAXIMUM_VOLUME = 5;
	  
}
