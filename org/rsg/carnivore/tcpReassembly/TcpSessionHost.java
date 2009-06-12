package org.rsg.carnivore.tcpReassembly;

import java.util.ArrayList;
import org.rsg.carnivore.CarnivorePacket;
import org.rsg.carnivore.tcpReassembly.proxyHttp.HttpDatagram;

public class TcpSessionHost {
	private ArrayList<DatagramPart> cache = new ArrayList<DatagramPart>(); 
	private long initialSequenceNumber; 
	private String sessionId = "";

	public TcpSessionHost(long initialSequenceNumber, String sessionId) {
		super();
		this.initialSequenceNumber = initialSequenceNumber;
		this.sessionId = sessionId;
	}

	public void newPacket(CarnivorePacket packet) {
		DatagramPart datagrampart = new DatagramPart(packet, this.initialSequenceNumber);
		cache.add(datagrampart);
//		enchainOrphans();
//		cullForCompletedChains();
		
		//quick and dirty -- we assume that any FIN or PSH is enough to flush the cache
		//TODO: make this compliant with tcp sequencing and out of sequence packet arrivals etc
		boolean shouldFlush = false;
		
		//FIN always flush
		if(packet.isFin()) {
			shouldFlush = true;
			
		//PSH flush if we have everything
		} else if(packet.isPsh()) {

			switch(packet.portIndicatingService()) {
			case 80: 
				HttpDatagram httpDatagram = new HttpDatagram(peek());
				shouldFlush = httpDatagram.shouldFlush();
//				if(!httpDatagram.probablyHasAnInterestingPayload())
//					shouldFlush = true;					
//					
//				if(httpDatagram.doesContentMatchExpectedLength())
//					shouldFlush = true;
				break; 
			default: 
				shouldFlush = true;
				break;
			}
			
		}

		if(shouldFlush) {
			Parser.parse(this, packet.portIndicatingService(), flush());
			ReassemblyCache.remove(this);
		}
	}
	
	private int cachePayloadTotalLength() {
		int length = 0;
		for(int i = 0; i < cache.size(); i++) 
			length += ((DatagramPart) cache.get(i)).len();		
		return length;
	}

	private byte[] peek() {		
//		System.out.println("[TcpSessionHost] peek: "+ toString());

		//create a byte buffer with the correct length
		byte[] buffer = new byte[cachePayloadTotalLength()];

		//pop each DatagramPart and copy the bytes into the buffer
		int marker = 0;
		for(int i = 0; i < cache.size(); i++) {
//		while(cache.size() > 0) {
			DatagramPart d = (DatagramPart) cache.get(i);
			System.arraycopy(d.getPacket().data, 0, buffer, marker, d.getPacket().data.length); 
			marker += d.getPacket().data.length;
		}
//		System.out.println(LibUtilities.bytesToCharString(buffer));
		return buffer;
	}
	
	private byte[] flush() {		
//		System.out.println("[TcpSessionHost] flush: "+ toString());

		//create a byte buffer with the correct length
		byte[] buffer = new byte[cachePayloadTotalLength()];

		//pop each DatagramPart and copy the bytes into the buffer
		int marker = 0;
		while(cache.size() > 0) {
			DatagramPart d = (DatagramPart) cache.remove(0);
			System.arraycopy(d.getPacket().data, 0, buffer, marker, d.getPacket().data.length); 
			marker += d.getPacket().data.length;
		}
//		System.out.println(LibUtilities.bytesToCharString(buffer));
		return buffer;
	}
	
//	private boolean isOrphan(DatagramPart datagrampart) {
//		return null == datagrampart.getParent();
//	}

//	private void enchainOrphans() {
//		for(int i = 0; i < cache.size(); i++) {
//			DatagramPart child = (DatagramPart) cache.get(i);
//			if(isOrphan(child)) {
//				DatagramPart parent = findParent(child);
//				child.setParent(parent);
//				if(null!=parent)
//					parent.addChild(child);
//			}
//		}		
//	}
//	
//	private void cullForCompletedChains() {
////		System.out.println("\n[ReassemblyCache] cullForCompletedChains ============");
//		for(int i = 0; i < cache.size(); i++) {
//			DatagramPart d = (DatagramPart) cache.get(i);
//
////			if(d.isFirstParent()) {
////				try {
////					System.out.println("\n");
////					d.toStringChain();
////				} catch (StackOverflowError e) {
////					System.out.println("StackOverflowError");
////				}
////				
//////				System.out.println("\n"+d.toStringChain());
////////				System.out.print(d.sequenceNumber() + "->" + d.nextSequenceNumber());
////////				DatagramPart child = d.getChild();
////////				while(null!=child) {
////////					System.out.print(", " + child.sequenceNumber() + "->" + child.nextSequenceNumber());					
////////					child = d.getChild();
////////				}
////////				System.out.println("");					
////			} 
//		}		
		
//		System.out.println("\n[ReassemblyCache] fullcache-------------");
//		for(int i = 0; i < cache.size(); i++) {
//			DatagramPart d = (DatagramPart) cache.get(i);
//			
////			if(!d.didprint) {
//				try {
//					System.out.println(d.toStringVerboseProtocolInfo());
////					d.toStringChain();
//				} catch (StackOverflowError e) {
//					System.out.println("StackOverflowError");
//				}				
////			}
////			d.didprint = false;
//		}
		
//	}
	
//	private DatagramPart findParent(DatagramPart datagrampart) {
//		if(datagrampart.getPacket().previous() == -1) return null;
//		for(int i = 0; i < cache.size(); i++) {
//			DatagramPart d = (DatagramPart) cache.get(i);
//			if(d.isSameSocketConnectionPair(datagrampart)) {					 //only connect ip:port pairs				
//				if(d.getPacket().next() == datagrampart.getPacket().previous()) { //hook my expected parent number to its expected child number
//					return d;
//				}
//			}
//		}
//		return null;
//	}

	public long getInitialSequenceNumber() {
		return initialSequenceNumber;
	}

	public void setInitialSequenceNumber(long initialSequenceNumber) {
		this.initialSequenceNumber = initialSequenceNumber;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String toString() {
		String s = "\n[TcpSessionHost] ISN:"+ initialSequenceNumber + " " + sessionId + " "+ cache.size()+" datagramparts\n";
		for(DatagramPart d : cache) 
			s += d.toStringVerboseProtocolInfo() + "\n";
		return s;
	}
}
