package org.rsg.carnivore;

import java.io.File;
import java.util.Date;

import net.sourceforge.jpcap.net.TCPPacket;

/*
 * some static utilities that do stuff
 */

public class CarniUtilities {
	
    public static int normalizeVolume(int volume) {
    	if(volume >= Constants.VOLUME_MAX) {
    		volume = Constants.VOLUME_MAX;
    	}
    	
    	if(volume <= 0) {
    		volume = 0;
    	}
    	return volume;
    }
    
	public static Object[] packetToObjectArray(CarnivorePacket p) {
		return new Object[]{	p.strTransportProtocol, 
				p.dateStamp(), 
				p.senderSocket(),
				p.receiverSocket(),
				p.payload()
			};		
	}

	public static String dateStamp() {
		return Constants.SERIALIZED_DATE_FORMAT.format(new Date());
	}

	public static String copyString(String s, int i) {
		String returnme = "";
		for(int x = 0; x < i; x++) {
			returnme = returnme + s;
		}
		return returnme;
	}

	public static File findSuitableFilenameToSave(String prefix, String suffix) {
		File f = null;
		int ascii_bottom = 10;
		int ascii_top = 36;
		int loop = 1;
		int i = ascii_bottom;

		while(true) {
			String aa = copyString("" + Character.forDigit(i, 36), loop);
			//System.err.println("[Utilities] findSuitableFilenameToSave loop:"+loop+" aa:"+aa);
			String filename = prefix + "_" + CarniUtilities.dateStamp() + aa + suffix;
			f = new File(filename);		
			if(!f.exists()) {
				//System.err.println("[Utilities] findSuitableFilenameToSave f:"+f.toString());
				return f;
			}			
			i++;
			
			if(i >= ascii_top) {
				i = ascii_bottom;
				loop++;
			}
		}
	}	

	public static String tcpFlags(TCPPacket p) {
		String s = "";
		if(p.isUrg()) {
			s = s + "U";
		} else {
			s = s + "-";			
		}

		if(p.isAck()) {
			s = s + "A";
		} else {
			s = s + "-";						
		}

		if(p.isPsh()) {
			s = s + "P";
		} else {
			s = s + "-";						
		}

		if(p.isRst()) {
			s = s + "R";
		} else {
			s = s + "-";						
		}

		if(p.isSyn()) {
			s = s + "S";
		} else {
			s = s + "-";						
		}

		if(p.isFin()) {
			s = s + "F";
		} else {
			s = s + "-";						
		}

		return s;
	}
}
