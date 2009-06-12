package org.rsg.carnivore.net;

import java.util.ArrayList;

public class FingerprintsQueryResults extends ArrayList<Fingerprint> {
	private static final long serialVersionUID = 1L;

	public String toString() {
		String returnme = "";
		
		switch(size()) {
		case 0: 
			returnme += "Unknown";
			break;			
			
		case 1:
			returnme += get(0).getOperatingSystem();
			break;			

		default:
			returnme += size() + " results (";
			for(int i = 0; i < this.size(); i++) {
				String os = get(i).getOperatingSystem();
				if(os.indexOf(" ") > 0)
					os = os.substring(0, os.indexOf(" "));
				returnme += os + ", ";				
			}				
			returnme += ")";
		}
		return returnme;
	}
}
