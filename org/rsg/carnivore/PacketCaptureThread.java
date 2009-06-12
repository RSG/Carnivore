package org.rsg.carnivore;

import net.sourceforge.jpcap.capture.CapturePacketException;
import net.sourceforge.jpcap.capture.PacketCapture;

public class PacketCaptureThread extends Thread {
	public PacketCapture packetcapture;

	public void run() { 
		try { 
			packetcapture.capture(); //this has to be in a thread, otherwise it blocks 
		} catch (CapturePacketException e) {
			//e.printStackTrace();  //TODO turned this off because it was only excepting once on quit
									//so.... do we care? 
		}
	} 
	
	public void stopCapture() {
		packetcapture.endCapture();
		packetcapture.close();
	}

	public PacketCaptureThread(PacketCapture packetcapture) {
		this.packetcapture = packetcapture;
	}
	
	public String toString() {
		return "PacketCaptureThread " + packetcapture.toString();
	}
}
