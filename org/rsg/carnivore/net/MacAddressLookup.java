package org.rsg.carnivore.net;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.rsg.carnivore.CarnivorePacket;

public class MacAddressLookup {
	public File databaseFile = new File("data/etter.finger.os");
	public ArrayList<Fingerprint> database = new ArrayList<Fingerprint>(); 
	public static final int FINGERPRINT_THRESHOLD = 6;

	public static void main (String[] args) {		
		String[] testquery1 = { "0200",null,null,null,"0","0","0","0","S","LT" };
		FingerprintsQueryResults queryresults1 = MacAddressLookup.instance().query(testquery1);
		System.out.println("queryresults1: " + queryresults1.size() + " -- " + queryresults1.toString());
	}
	
	//singleton pattern
	public static MacAddressLookup instance() { return INSTANCE; }
	private static final MacAddressLookup INSTANCE = new MacAddressLookup();
	private MacAddressLookup() {
		System.out.print("[Fingerprints] Loading database...");
		loadDatabase(databaseFile);
		System.out.println("OK");
	}

	public FingerprintsQueryResults query(String[] query) {
		ArrayList<Fingerprint> databaseResults = database;
		return winnowResultsBasedOnQuery(databaseResults, query);
	}
	
//	public String getFingerprint(String[] query, int num){
//		return queryByCount(query,num);
//	}
	
	public String getFingerprint(CarnivorePacket packet, int num){
//		Log.debug("\n---------------------------");
//		Log.debug("["+this.getClass().getName()+"] new CarnivorePacket: " + packet);
        String[] testquery1 = { null,"_MSS",null,"WS","0","0",null,"0",null,null }; //start empty
        testquery1[0] = intToPaddedHexString(packet.getTcpWindowSize(), 4);
        testquery1[8] = (packet.isAck() ? "A" : "S");
		byte[] headers = packet.tcpHeader;
		int[] options = new int[100];
		int numBytes = 0;
		for(int i = 20; i < Array.getLength(headers); i++){
			options[i - 20] = headers[i];
			numBytes++;
			}	
		if(numBytes > 0){
			parseTCPOptions(options, numBytes, testquery1);
			return queryByCount(testquery1,num);
		}
		return "";
	}
	
	public void parseTCPOptions(int[] options, int numBytes, String[] query){
		for(int i = 0; i < numBytes; i++){
			if(options[i] == 1){
				query[5] = "1";
			}
			if((options[i]==2) && (options[i+1] == 4)){
				query[1] = bytesToString(options[i+2],options[i+3]);
			}
			if((options[i]==3) && (options[i+1] == 3)){
				query[3] = intToPaddedHexString(options[i+2],2);
			}
			if((options[i]==4) && (options[i+1] == 2)){
				query[4] = "1";
			}
			if((options[i]==8) && (options[i+1]==10)){
				query[7] = "1";
			}
		}
	}
	
	public String bytesToString(int a, int b){	
		int value = 0;
        value += (a & 0x000000FF);
        value += (b & 0x000000FF) << 8;
		return intToPaddedHexString(value);
	}
	
	public static String intToPaddedHexString(int i) {
	       return intToPaddedHexString(i , 4);
	   }

	public static String intToPaddedHexString(int i, int padToLength) {
		String returnme = Integer.toHexString(i).toUpperCase();
	    while(returnme.length() < padToLength){
	    	returnme = "0" + returnme;
	    }
	    return returnme;
	}
	
	public String queryByCount(String[] query,int num) {
		ArrayList<Fingerprint> databaseResults = database;
		FingerprintsQueryResults databaseToReturn = new FingerprintsQueryResults();
		for(int i = 0; i < databaseResults.size();i++){
			int count = 0;
			Fingerprint fingerprint = (Fingerprint) databaseResults.get(i);
			if(queryIsAHit(fingerprint.getWindowSize(), query[0])){
				count++;
			}
			if(queryIsAHit(fingerprint.getMaximumSegmentSize(), query[1])){
				count++;
			}
			if(queryIsAHit(fingerprint.getTimeToLive(), query[2])){
				count++;
			}
			if(queryIsAHit(fingerprint.getWindowScale(), query[3])){
				count++;
			}
			if(queryIsAHit(fingerprint.getAsStringIsSackPermitted(), query[4])){
				count++;
			}
			if(queryIsAHit(fingerprint.getAsStringIsNOP(), query[5])){
				count++;
			}
			if(queryIsAHit(fingerprint.getAsStringIsDontFragment(), query[6])){
				count++;
			}
			if(queryIsAHit(fingerprint.getAsStringIsTimestamp(), query[7])){
				count++;
			}
			if(queryIsAHit(fingerprint.getIsSynOrAck().toString(), query[8])){
				count++;
			}
			if(queryIsAHit(fingerprint.getLengthOfPacket(), query[9])){
				count++;
			}		
			if(count > num){
				databaseToReturn.add(fingerprint);
			}
		}
		return getMajorOS(databaseToReturn);
//		return winnowResultsBasedOnQuery(databaseToReturn, query).toString();
	}
	
	public String getMajorOS(FingerprintsQueryResults databaseResults){
		String osBlank = "???";
		String windows = "Windows";
		String linux = "Linux";
		String mac = "Mac";
		if(databaseResults.size() < 20){
			return osBlank;
		}
		int windowsCount = 0;
		int linuxCount = 0;
		int macCount = 0;
		int windowsMax = 561;
		int linuxMax = 319;
		int macMax = 82;
		float windowsScale = 0.0f;
		float linuxScale = 0.0f;
		float macScale = 0.0f;
		for(int i = 0; i < databaseResults.size(); i++){
			Fingerprint fingerprint = (Fingerprint) databaseResults.get(i);
			if(fingerprint.getOperatingSystem().indexOf(windows) >= 0){
				windowsCount++;
			}
			if(fingerprint.getOperatingSystem().indexOf(mac) >= 0){
				macCount++;
			}
			if(fingerprint.getOperatingSystem().indexOf(linux) >= 0){
				linuxCount++;
			}
		}
		windowsScale = (float)windowsCount/(float)windowsMax;
		linuxScale = (float)linuxCount/(float)linuxMax;
		macScale = (float)macCount/(float)macMax;
		if(windowsScale > linuxScale && windowsScale > macScale){
			return windows;
		}
		if(linuxScale > windowsScale && linuxScale > macScale){
			return linux;
		}
		if(macScale > windowsScale && macScale > linuxScale){
			return mac;
		}
		return osBlank;
	}
	
	private FingerprintsQueryResults winnowResultsBasedOnQuery(ArrayList<Fingerprint> databaseResults, String[] query) {		
		FingerprintsQueryResults newDatabase = new FingerprintsQueryResults();
		for(int i = 0; i < databaseResults.size(); i++) {
			Fingerprint fingerprint = (Fingerprint) databaseResults.get(i);
			
			if(
					queryIsAHit(fingerprint.getWindowSize(), query[0]) &&
					queryIsAHit(fingerprint.getMaximumSegmentSize(), query[1]) &&
					queryIsAHit(fingerprint.getTimeToLive(), query[2]) &&
					queryIsAHit(fingerprint.getWindowScale(), query[3]) &&
					queryIsAHit(fingerprint.getAsStringIsSackPermitted(), query[4]) &&
					queryIsAHit(fingerprint.getAsStringIsNOP(), query[5]) &&
					queryIsAHit(fingerprint.getAsStringIsDontFragment(), query[6]) &&
					queryIsAHit(fingerprint.getAsStringIsTimestamp(), query[7]) &&
					queryIsAHit(fingerprint.getIsSynOrAck().toString(), query[8]) &&
					queryIsAHit(fingerprint.getLengthOfPacket(), query[9])
					) {
				newDatabase.add(fingerprint);
			}
		}		
		return newDatabase;
	}
	
	private boolean queryIsAHit(String origin, String match) {
		if(null == match) return true;
		return origin.equals(match);
	}
	
   private void loadDatabase(File aFile) {
    try {
      BufferedReader input =  new BufferedReader(new FileReader(aFile));
      try {
        String line = null;
        while (( line = input.readLine()) != null){
        	if(!isLineEmpty(line) && !isThisACommentLine(line)) {
        		database.add(new Fingerprint(line));
        	}
        }
      } finally {
        input.close();
      }
    } catch (IOException ex){
      ex.printStackTrace();
    }
  }

  public String toString() {
	  String returnme = "";
	  returnme += "Fingerprints" + "\n";
	  for(int i = 0; i < database.size(); i++) {
		  returnme += database.get(i).toString() + "\n";
	  }
	  returnme += "(" + database.size() + " total)" + "\n";
	  return returnme;
  }
	
  private boolean isThisACommentLine(String line) {
	  if (line.length() < 1) return false; 
	  return line.substring(0, 1).equals("#");
  }

  private boolean isLineEmpty(String line) {
	  return line.trim().length() < 1;
  }

} 