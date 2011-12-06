package org.rsg.carnivore;

import java.io.Serializable;
import java.util.Date;
import org.rsg.carnivore.net.IPAddress;
import org.rsg.lib.LibUtilities;

/**
 * @author RSG
 * @version 2.2
 */
public class CarnivorePacket implements Serializable {
	private static final long serialVersionUID = Constants.VERSION;
	
	/**
	 * Packet type as a string, either "TCP" or "UDP".
	 */
	public String strTransportProtocol;
	
	/**
	 * Packet type as an int, either 6 (TCP) or 17 (UDP).
	 */
	public int intTransportProtocol;
	
	private boolean isSyn;
	private boolean isPsh;
	private boolean isFin;
	private boolean isRst;
	private boolean isAck;
	
	
	public boolean isTCP() {
		return intTransportProtocol == 6;
	}

	public boolean isUDP() {
		return intTransportProtocol == 17;
	}

	/**
	 * Sender IP address.
	 */
	public IPAddress senderAddress;
	
	/**
	 * Receiver IP address.
	 */
	public IPAddress receiverAddress;
	
	/**
	 * Sender port.
	 */
	public int senderPort;
	
	/**
	 * Receiver port.
	 */
	public int receiverPort;
	
	/**
	 * date of the packet.
	 */
	public Date date;
	
	/**
	 * The content/payload of the packet as bytes.
	 */
	public byte[] data;

	/**
	 * The ip header of the packet as bytes.
	 */
	public byte[] ipHeader;

	/**
	 * The tcp header (if it exists) of the packet as bytes.
	 */
	public byte[] tcpHeader;

	/**
	 * The udp header (if it exists) of the packet as bytes.
	 */
	public byte[] udpHeader;

	/**
	 * The identification number from the IP header.
	 */
	public int ipIdentification;
	
	/**
	 * The sequence number from the TCP header.
	 */
	private long tcpSequenceNumber;

	/**
	 * The acknowledgement number from the TCP header.
	 */
	private long tcpAcknowledgementNumber;
	
	private int tcpWindowSize;
	
	private int ipLength;
	private int ipHeaderLength;
	private int tcpHeaderLength;
	private int tcpPayloadDataLength;
	
	private String senderMacAddress; 
	private String receiverMacAddress; 
	
//	public TCPPacket jpTCPPacket; 

//	public UDPPacket jpUDPPacket;
	
	public String getSenderMacAddress() {
		return senderMacAddress;
	}

	public void setSenderMacAddress(String senderMacAddress) {
		this.senderMacAddress = senderMacAddress;
	}

	public String getReceiverMacAddress() {
		return receiverMacAddress;
	}

	public void setReceiverMacAddress(String receiverMacAddress) {
		this.receiverMacAddress = receiverMacAddress;
	}

	/**
	 * Constructor -- does nothing. 
	 */
	public CarnivorePacket () {}

	/**
	 * @return The content of the packet converted into ASCII characters. 
	 * This is handy if you actually want to read the packets. 
	 * (Note: any bytes outside of the simple ASCII range [greater than 
	 * 31 and less than 127] are printed as whitespace.)
	 */
	public String ascii() {
		return LibUtilities.bytesToAsciiCharString(data);
	}

	/**
	 * @return The content of the packet converted into a string of formated hex values.
	 */
	public String hex() {
		return LibUtilities.bytesToHexString(data);
	}
	
	/**
	 * @return a string in the format: dateStamp senderSocket > receiverSocket
	 */
	public String header() {
		String sender  		= senderSocket();
		String receiver		= receiverSocket();
	    String timestamp 	= dateStamp();
	    return printHeader(timestamp, sender, receiver); 
	}

	/**
	 * @return Sender formated as an "IPaddress:Port" string.
	 */
	public String senderSocket() {
		return senderAddress + Constants.SOCKET_DELIMITER + senderPort;
	}

	/**
	 * @return Receiver formated as an "IPaddress:Port" string.
	 */
	public String receiverSocket() {
		return receiverAddress + Constants.SOCKET_DELIMITER + receiverPort;
	}

	/**
	 * @return Port corresponding to service (i.e. the lower of sender and receiver ports) 
	 */
	public int portIndicatingService() {
		return (senderPort < receiverPort) ? senderPort : receiverPort;
	}

	/**
	 * @return Date of the packet in the format hour:minute:second:millisecond.
	 */
	public String dateStamp() {
		return Constants.PACKET_DATE_FORMAT.format(date);
	}

	/**
	 * @param timestamp typically from dateStamp()
	 * @param sender typically from senderSocket()
	 * @param receiver typically from receiverSocket()
	 * @return formated string representing simple packet information
	 */
	private String printHeader(String timestamp, String sender, String receiver) {
		return timestamp + " " + sender + " > " + receiver;
	}

	/**
	 * @return the payload of the packet, based on default channel
	 */
	public String payload() {
		return payload(Preferences.instance().getInt(Constants.CHANNEL));
	}

	/**
	 * @param channel a constant representing the channel number
	 * @return the payload of the packet, based on channel in param
	 */
	public String payload(int channel) {
		if(channel == Constants.CHANNEL_CARNIVORE) {		
			return ascii();
		} else if(channel == Constants.CHANNEL_HEXIVORE) {
			return hex();
		} else {
			return "";
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		int channel = Preferences.instance().getInt(Constants.CHANNEL);
		if(channel == Constants.CHANNEL_CARNIVORE) {		
			return header() + " == " + payload(channel);
		} else if(channel == Constants.CHANNEL_HEXIVORE) {
			return header() + " == " + payload(channel);
		} else {
			return header();		
		}
	}
	
	public String toStringTcpSessionHost() {
		return this.senderSocket() + "-" + this.receiverSocket();
	}
	
	public String flagsToString() {
		String s = "[";
		s += (isSyn()) ? "SYN " : "    ";
		s += (isPsh()) ? "PSH " : "    ";
		s += (isFin()) ? "FIN " : "    ";
		s += (isRst()) ? "RST " : "    ";
		s += (isAck()) ? "ACK " : "    ";
		return s.trim() + "]";
	}

	public boolean isSyn() {
		return isSyn;
	}

	public void setSyn(boolean isSyn) {
		this.isSyn = isSyn;
	}

	public boolean isPsh() {
		return isPsh;
	}

	public void setPsh(boolean isPsh) {
		this.isPsh = isPsh;
	}

	public boolean isFin() {
		return isFin;
	}

	public void setFin(boolean isFin) {
		this.isFin = isFin;
	}

	public boolean isRst() {
		return isRst;
	}

	public void setRst(boolean isRst) {
		this.isRst = isRst;
	}

	public boolean isAck() {
		return isAck;
	}

	public void setAck(boolean isAck) {
		this.isAck = isAck;
	}

	public long getTcpSequenceNumber() {
		return tcpSequenceNumber;
	}

	public void setTcpSequenceNumber(long tcpSequenceNumber) {
		this.tcpSequenceNumber = tcpSequenceNumber;
	}

	public long getTcpAcknowledgementNumber() {
		return tcpAcknowledgementNumber;
	}

	public void setTcpAcknowledgementNumber(long tcpAcknowledgementNumber) {
		this.tcpAcknowledgementNumber = tcpAcknowledgementNumber;
	}

	public int getTcpWindowSize() {
		return tcpWindowSize;
	}

	public void setTcpWindowSize(int tcpWindowSize) {
		this.tcpWindowSize = tcpWindowSize;
	}

	public int getIpLength() {
		return ipLength;
	}

	public void setIpLength(int ipLength) {
		this.ipLength = ipLength;
	}

	public int getIpHeaderLength() {
		return ipHeaderLength;
	}

	public void setIpHeaderLength(int ipHeaderLength) {
		this.ipHeaderLength = ipHeaderLength;
	}

	public int getTcpHeaderLength() {
		return tcpHeaderLength;
	}

	public void setTcpHeaderLength(int tcpHeaderLength) {
		this.tcpHeaderLength = tcpHeaderLength;
	}

	public int getTcpPayloadDataLength() {
		return tcpPayloadDataLength;
	}

	public void setTcpPayloadDataLength(int tcpPayloadDataLength) {
		this.tcpPayloadDataLength = tcpPayloadDataLength;
	}
}
