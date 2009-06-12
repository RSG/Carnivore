package org.rsg.carnivore.tcpReassembly;

import java.util.ArrayList;

import net.sourceforge.jpcap.net.IPPacket;
import org.rsg.carnivore.CarnivorePacket;

public class DatagramPart {
	private DatagramPart parent;	
	private ArrayList<DatagramPart> children = new ArrayList<DatagramPart>(); 
	private CarnivorePacket packet;
	private long initialSequenceNumber;

	public DatagramPart(CarnivorePacket packet, long initialSequenceNumber) {
		super();
		this.parent = null;
		this.packet = packet;
		this.initialSequenceNumber = initialSequenceNumber;
//		System.out.println("[DatagramPart] constructor -- " + sequenceNumber());
	}
		
	public boolean isSameSocketConnectionPair(DatagramPart datagrampart) { 
		CarnivorePacket p1 = datagrampart.getPacket();
		CarnivorePacket p2 = this.packet;
		String p1s = p1.senderSocket();
		String p1r = p1.receiverSocket();
		String p2s = p2.senderSocket();
		String p2r = p2.receiverSocket();
		return  (p1s.equals(p2s) && p1r.equals(p2r)) || 
				(p1s.equals(p2r) && p1r.equals(p2s));
	}
	
	public long sequenceNumber() {
		return packet.getTcpSequenceNumber();
	}

	public long acknowledgementNumber() {
		return packet.getTcpAcknowledgementNumber();
	}

	public long len() {
		return 	packet.getIpLength() - 
				packet.getIpHeaderLength() - 
				packet.getTcpHeaderLength();
	}

	public long nextSequenceNumber() {
		return sequenceNumber() + len();
	}

	public DatagramPart getParent() {
		return parent;
	}

	public void setParent(DatagramPart parent) {
		this.parent = parent;
	}

	public CarnivorePacket getPacket() {
		return packet;
	}

	public void setPacket(CarnivorePacket packet) {
		this.packet = packet;
	}

	public ArrayList<DatagramPart> getChildren() {
		return children;
	}

	public void addChild(DatagramPart d) {
		if(!children.contains(d))
			children.add(d);
	}
	
	public boolean isFirstParent() {
		return null==getParent();
	}

	public boolean isLastChild() {
		return children.size() < 1;
	}
	
	public String toStringVerboseProtocolInfo() throws StackOverflowError {
		String s = "";
//		s += this.senderSocket() + " --> " + this.receiverSocket();
		
		if(this.packet.senderAddress.toString().equals("192.168.1.2"))
			s = packet.senderPort + " " + s;

		if(this.packet.receiverAddress.toString().equals("192.168.1.2"))
			s = packet.receiverPort + " " + s;

		if(this.packet.isTCP()){
			s = "TCP " + s;
			s += packet.flagsToString();
			
//			s += " prev:"+ packet.previous();
//			s += "  next:"+ packet.next();

			s += "  Sec: "+ (packet.getTcpSequenceNumber() - initialSequenceNumber) ;
//			s += "  nSec:"+ (packet.jpTCPPacket.getSequenceNumber() + packet.jpTCPPacket.getPayloadDataLength());
//			s += "  Ack: "+packet.jpTCPPacket.getAcknowledgementNumber();
			s += "  Len: "+packet.getTcpPayloadDataLength();		
		} else if(this.packet.isUDP()) {
			s = "UDP " + s;
		}
		
		s += " " + packet.ascii();
		return s;
	}

	public void toStringChain() throws StackOverflowError {
		if(isLastChild()) {
//			return sequenceNumber() + "->" + nextSequenceNumber();
			System.out.println(toStringVerboseProtocolInfo());
		} else {
//			return sequenceNumber() + "->" + nextSequenceNumber() + ", " + getChild().toStringChain();
//			String s = "";
			System.out.println(toStringVerboseProtocolInfo());
			for(DatagramPart d : getChildren()) {
				d.toStringChain();							
			}
//			return s;
		}
	}
}
