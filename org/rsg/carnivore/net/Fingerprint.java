package org.rsg.carnivore.net;

public class Fingerprint { 
	/*
	#  ettercap -- etter.finger.os -- passive OS fingerprint database          #
	#                                                                          #
	# The fingerprint database has the following structure:                    #
	#                                                                          #
	# WWWW:MSS:TTL:WS:S:N:D:T:F:LEN:OS                                         #
	#                                                                          #
	# WWWW: 4 digit hex field indicating the TCP Window Size                   #
	# MSS : 4 digit hex field indicating the TCP Option Maximum Segment Size   #
	#       if omitted in the packet or unknown it is "_MSS"                   #
	# TTL : 2 digit hex field indicating the IP Time To Live                   #
	# WS  : 2 digit hex field indicating the TCP Option Window Scale           #
	#       if omitted in the packet or unknown it is "WS"                     #
	# S   : 1 digit field indicating if the TCP Option SACK permitted is true  #
	# N   : 1 digit field indicating if the TCP Options contain a NOP          #
	# D   : 1 digit field indicating if the IP Don't Fragment flag is set      #
	# T   : 1 digit field indicating if the TCP Timestamp is present           #
	# F   : 1 digit ascii field indicating the flag of the packet              #
	#       S = SYN                                                            #
	#       A = SYN + ACK                                                      #
	# LEN : 2 digit hex field indicating the length of the packet              #
	#       if irrilevant or unknown it is "LT"                                #
	# OS  : an ascii string representing the OS                                #
	*/
	public static enum SYNORACK { 
		SYN, ACK; 
		public String toString() {
			return (this == SYN) ? "S" : "A";
		}
	}	
	private String windowSize;
	private String maximumSegmentSize;
	private String timeToLive;
	private String windowScale;
	private boolean isSackPermitted;
	private boolean isNOP;
	private boolean isDontFragment;
	private boolean isTimestamp;
	private SYNORACK isSynOrAck;
	private String lengthOfPacket;
	private String operatingSystem;

	public Fingerprint(String line) {
		super();
		String[] lineArray = 	line.split(":");	
		this.windowSize = 		lineArray[0];
		this.maximumSegmentSize = lineArray[1];
		this.timeToLive = 		lineArray[2];
		this.windowScale = 		lineArray[3];
		this.isSackPermitted = 	lineArray[4].equals("1") ? true : false;
		this.isNOP = 			lineArray[5].equals("1") ? true : false;
		this.isDontFragment = 	lineArray[6].equals("1") ? true : false;
		this.isTimestamp = 		lineArray[7].equals("1") ? true : false;
		this.isSynOrAck = 		lineArray[8].equals("S") ? SYNORACK.SYN : SYNORACK.ACK;
		this.lengthOfPacket = 	lineArray[9];
		this.operatingSystem = 	lineArray[10];
	}
	
	public String toString() {
		return 
		windowSize + ":" +
		maximumSegmentSize + ":" +
		timeToLive + ":" +
		windowScale + ":" +
		(isSackPermitted ? "1" : "0") + ":" +
		(isNOP ? "1" : "0") + ":" +
		(isDontFragment ? "1" : "0") + ":" +
		(isTimestamp ? "1" : "0") + ":" +
		isSynOrAck + ":" +
		lengthOfPacket + ":" +
		operatingSystem;
	}

	public String getWindowSize() {
		return windowSize;
	}
	public void setWindowSize(String windowSize) {
		this.windowSize = windowSize;
	}
	public String getMaximumSegmentSize() {
		return maximumSegmentSize;
	}
	public void setMaximumSegmentSize(String maximumSegmentSize) {
		this.maximumSegmentSize = maximumSegmentSize;
	}
	public String getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(String timeToLive) {
		this.timeToLive = timeToLive;
	}
	public String getWindowScale() {
		return windowScale;
	}
	public void setWindowScale(String windowScale) {
		this.windowScale = windowScale;
	}
	public boolean isSackPermitted() {
		return isSackPermitted;
	}
	public String getAsStringIsSackPermitted() {
		return isSackPermitted ? "1" : "0";
	}
	public void setSackPermitted(boolean isSackPermitted) {
		this.isSackPermitted = isSackPermitted;
	}
	public boolean isNOP() {
		return isNOP;
	}
	public String getAsStringIsNOP() {
		return isNOP ? "1" : "0";
	}
	public void setNOP(boolean isNOP) {
		this.isNOP = isNOP;
	}
	public boolean isDontFragment() {
		return isDontFragment;
	}
	public String getAsStringIsDontFragment() {
		return isDontFragment ? "1" : "0";
	}
	public void setDontFragment(boolean isDontFragment) {
		this.isDontFragment = isDontFragment;
	}
	public boolean isTimestamp() {
		return isTimestamp;
	}
	public String getAsStringIsTimestamp() {
		return isTimestamp ? "1" : "0";
	}
	public void setTimestamp(boolean isTimestamp) {
		this.isTimestamp = isTimestamp;
	}
	public SYNORACK getIsSynOrAck() {
		return isSynOrAck;
	}
	public void setIsSynOrAck(SYNORACK isSynOrAck) {
		this.isSynOrAck = isSynOrAck;
	}
	public String getLengthOfPacket() {
		return lengthOfPacket;
	}
	public void setLengthOfPacket(String lengthOfPacket) {
		this.lengthOfPacket = lengthOfPacket;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
}
