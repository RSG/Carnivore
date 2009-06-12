package org.rsg.carnivore.net;

import org.rsg.carnivore.Constants;

public class Node {

	public IPAddress ip;
	private int packetsSentTCP;
	private int packetsSentUDP;
	private int packetsReceivedTCP;
	private int packetsReceivedUDP;
	
	public Node(IPAddress ip) {
		this.ip = ip;
	}
	
	public void sentPacket(int type) {
		switch(type) {
		case Constants.intTCP: packetsSentTCP++; break;
		case Constants.intUDP: packetsSentUDP++; break;
		}
	}

	public void receivedPacket(int type) {
		switch(type) {
		case Constants.intTCP: packetsReceivedTCP++; break;
		case Constants.intUDP: packetsReceivedUDP++; break;
		}		
	}
	
	public int getPacketsSentTotal() {
		return packetsSentTCP + packetsSentUDP;
	}
	
	public int getPacketsReceivedTotal() {
		return packetsReceivedTCP + packetsReceivedUDP;
	}

	public int getPacketsReceivedTCP() {
		return packetsReceivedTCP;
	}

	public int getPacketsReceivedUDP() {
		return packetsReceivedUDP;
	}

	public int getPacketsSentTCP() {
		return packetsSentTCP;
	}

	public int getPacketsSentUDP() {
		return packetsSentUDP;
	}

}
