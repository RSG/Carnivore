package org.rsg.carnivore;

import java.util.Date;

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
}
