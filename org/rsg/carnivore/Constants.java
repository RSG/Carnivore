package org.rsg.carnivore;

import java.awt.Color;
import java.awt.Font;
import java.text.Format;
import java.text.SimpleDateFormat;

public class Constants {
	public final static long VERSION = (long) 2.2;
	public final static Format PACKET_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
	//public final static Format SERIALIZED_DATE_FORMAT = new SimpleDateFormat("yyMMMd_HH-mm-ss");
	public final static Format SERIALIZED_DATE_FORMAT = new SimpleDateFormat("yyMMMd");
	public final static String SERIALIZED_SUFFIX = ".cpe";
	public final static String SERIALIZED_SUFFIX_SHORT = "cpe";
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

	public static final String DATA 			= "CPEresources" + FILE_SEPARATOR;

	public static final String FONTS_SEVENET7 	= DATA + "font" + FILE_SEPARATOR + "SEVENET7.TTF";

	public static final String IMAGES 			= DATA + "image" + FILE_SEPARATOR;
	public static final String IMAGES_ABOUT 	= IMAGES + "about.jpg";
	public static final String IMAGES_ICON 		= IMAGES + "icon.gif";
	public static final String IMAGES_LABEL1on 	= IMAGES + "label_client_on.jpg";
	public static final String IMAGES_LABEL1off	= IMAGES + "label_client_off.jpg";
	public static final String IMAGES_CHECKBOXon 	= IMAGES + "checkbox_on.gif";
	public static final String IMAGES_CHECKBOXoff	= IMAGES + "checkbox_off.gif";
	public static final String IMAGES_LABEL2on	= IMAGES + "label_network_on.jpg";
	public static final String IMAGES_LABEL2off	= IMAGES + "label_network_off.jpg";
	public static final String IMAGES_LABEL3  	= IMAGES + "label_security.jpg";
	public static final String IMAGES_HEAD_OFF			= IMAGES + "head_off.jpg";
	public static final String IMAGES_HEAD_RECORDING	= IMAGES + "head_recording.jpg";
	public static final String IMAGES_HEAD_PLAYBACK		= IMAGES + "head_playback.jpg";
	public static final String IMAGES_HEAD_LOGGING		= IMAGES + "head_logging.jpg";
	public static final String IMAGES_KNOB		= IMAGES + "knob.gif";

	public static final String MENU_FILE = "File";
	public static final String MENU_PLAYBACK_START 	= "Playback Offline Session...";
	public static final String MENU_PLAYBACK_STOP 	= "Stop Playback";
	public static final String MENU_RECORD_START 	= "Record Offline Session...";
	public static final String MENU_RECORD_STOP 	= "Stop Recording";
	public static final String MENU_LOG_START 		= "Log to Text File...";
	public static final String MENU_LOG_STOP 		= "Stop Logging";
	public static final String MENU_ABOUT = "About";
	public static final String MENU_SHOW_CONSOLE = "Show Console";
	public static final String MENU_EXIT = "Quit";

	public static final String FILENAME_PROPS 			= ".carnivore_preferences.txt";
	//public static final String FILENAME_SERIAL_NODES	= "serialized-nodes.dat";
	public static final String FILENAME_SERIAL_OFFLINE	= "OfflineCache.serialized";
	public static final String PROPS_COMMENT 			= "Carnivore Preferences File";

	public static final String 	CHANNEL 			= "channel";
	public static final int 	CHANNEL_MINIVORE 	= 0;
	public static final int 	CHANNEL_CARNIVORE 	= 1;
	public static final int 	CHANNEL_HEXIVORE 	= 2;
	
	public static final String SHOULD_SKIP_UDP = "shouldSkipUDP";
	public static final String SHOULD_SHOW_CONSOLE = "shouldShowConsole";
	public static final String SHOULD_ALLOW_EXTERNAL_CLIENTS = "shouldAllowExternalClients";
	
	public static final String 	MAXIMUM_VOLUME = "maximumVolume";
	public static final int 	VOLUME_MAX = 20;

	public static final String SERVER_PORT = "serverPort";

	//DEFAULTS FOR PREFERENCES
	public static final int DEFAULT_CHANNEL = CHANNEL_CARNIVORE;
	public static final boolean DEFAULT_SHOULD_SKIP_UDP = false;
	public static final boolean DEFAULT_SHOULD_ALLOW_EXTERNAL_CLIENTS = false;
	public static final boolean DEFAULT_SHOW_CONSOLE = false;
	public static final int DEFAULT_MAXIMUM_VOLUME = 5;
	public static final int DEFAULT_SERVER_PORT = 6667;

	public static final Font CONSOLE_FONT = new Font("Dialog", Font.PLAIN, 10);

	public static final int THRESHOLD_FOR_MAGNETIC_WINDOWS 	= 40; //pixels
	
	public static final String[] CONSOLE_COLUMN_HEADERS = {"Type","Time","Sender","Receiver","Payload"};
	public static final int CONSOLE_MAX_ROWS = 500;
	public static final int CONSOLE_COLUMN_WIDTH_0 = 32;
	public static final int CONSOLE_COLUMN_WIDTH_1 = 80;
	public static final int CONSOLE_COLUMN_WIDTH_2 = 110;	
	public static final int CONSOLE_COLUMN_WIDTH_3 = CONSOLE_COLUMN_WIDTH_2;	
	public static final int CONSOLE_COLUMN_WIDTH_4 = 1200;	
	
	public static final Color grey1 = new Color(250,250,250);
	public static final Color grey2 = new Color(240,240,240);

	//connection states (from rfc 793)
	public final static int LISTEN = 0;
	public final static int SYN_SENT = 1;
	public final static int SYN_RECEIVED = 2;
	public final static int ESTABLISHED = 3;
	public final static int FIN_WAIT_1 = 4;
	public final static int FIN_WAIT_2 = 5;
	public final static int CLOSE_WAIT = 6;
	public final static int CLOSING = 7;
	public final static int LAST_ACK = 8;
	public final static int TIME_WAIT = 9;
	public final static int CLOSED = 10;
	  
}
