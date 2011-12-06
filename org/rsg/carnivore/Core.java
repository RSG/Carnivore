package org.rsg.carnivore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.sourceforge.jpcap.capture.CaptureDeviceOpenException;
import net.sourceforge.jpcap.capture.InvalidFilterException;
import net.sourceforge.jpcap.capture.PacketCapture;
import net.sourceforge.jpcap.capture.PacketListener;
import net.sourceforge.jpcap.net.IPPacket;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.net.UDPPacket;

import org.rsg.carnivore.net.Devices;
import org.rsg.carnivore.net.IPAddress;
import org.rsg.lib.ErrorMessages;
import org.rsg.lib.Log;
//import org.rsg.lib.time.TimeUtilities;

public class Core implements PacketListener {

	//PUBLIC
	public String channel = "carnivore"; 
	public boolean shouldSkipUDP = false;
	
	//PRIVATE
	//private boolean volumeIsUnlimited = false;
	//private int unlimitedVolumeAmount = 100;
	private final int SNAP_LENGTH = 65535;	//from pcap man page: "A value of 65535 should be sufficient, on most 
											//if not all networks, to capture all the data available from the packet"
	//private final int FOREVER = -1; //i just embedded this inside jpcap's PacketCapture
	private String FILTER_TCP_AND_UDP = "ip";
	private String FILTER_TCP_ONLY = "tcp";
	private String filter = FILTER_TCP_AND_UDP;
	private ArrayList<PacketCaptureThread> packetcapturethreads = new ArrayList<PacketCaptureThread>();
	private PacketCapture packetcapture;
	private Devices devices;
	public static Object parent;
    
	public Core(Object parent) {
		System.out.println("["+this.getClass().getName()+"] Running Java version " + System.getProperty("java.version"));
		System.out.println("["+this.getClass().getName()+"] Starting Carnivore Core version " + Constants.VERSION);
		Core.parent = parent;
		
		//INIT DEVICES
		devices = new Devices();
		HashMap<String, String> d = devices.getDevices();
		
		//quit if no devices were found
		if(d.size() < 1) {
			Log.window(ErrorMessages.NO_NETWORK_DEVICES);
		}
		
		//OPEN CAPTURE ON EVERY DEVICE WE FOUND
		openCaptureOnEveryDevice(d);
		
		//START CACHE THREAD
		System.out.println("["+this.getClass().getName()+"] starting PacketCacheThread");
		PacketCacheThread.instance().start();
	}
	
	private void openCaptureOnEveryDevice(HashMap<?, ?> d) {
		Iterator<?> it = d.keySet().iterator();
		//int instance = 0;
		while(it.hasNext()){
			String name = (String)it.next();

			if (!name.substring(0,2).equals("lo")) { //ignore loopback? (lo, lo0)			
				
				//CREATE CAPTURE INSTANCE
				packetcapture = new PacketCapture();
				try {
					packetcapture.open(name, SNAP_LENGTH, true, PacketCapture.DEFAULT_TIMEOUT);
					
				//if we've gotten this far it generally means the machine has network adapters 
				//that we can see, but they are offline for some reason (cable unplugged, etc.)
				} catch (CaptureDeviceOpenException e) {

					//this is generally a windows exception
					System.err.println("["+this.getClass().getName()+"] Warning -- can't open device \"" + name + "\" (CaptureDeviceOpenException)");
					break;
				}
				Log.debug("["+this.getClass().getName()+"] opened capture on device \"" + name + "\"");
				
				//set filter
				try {
					packetcapture.setFilter(filter, true);
				} catch (InvalidFilterException e) {
					e.printStackTrace();
				}
				
				//ADD CALLBACK
				packetcapture.addPacketListener(this);
				packetcapturethreads.add(new PacketCaptureThread(packetcapture));  //save instance in thread
			}
	    } 
	}


	public void stop() {
		for(int i = 0; i < packetcapturethreads.size(); i++){
			PacketCaptureThread pct = (PacketCaptureThread) packetcapturethreads.get(i);
			System.out.print("["+this.getClass().getName()+"] stopping Packet Capture on "+ pct + "...");
			pct.stopCapture();
			System.out.println("OK");
		}
		PacketCacheThread.instance().stop();
	}

	public void start() {
		for(int i = 0; i < packetcapturethreads.size(); i++){
			PacketCaptureThread pct = (PacketCaptureThread) packetcapturethreads.get(i);
			System.out.print("["+this.getClass().getName()+"] starting Packet Capture on "+ pct + "...");
			pct.start();
			System.out.println("OK");
			
			//Log.debug("["+this.getClass().getName()+"] started capture on " + pct.toString() + " -- ready to receive packets");
		}
	}
	
	public void startPcapInstance(PacketCapture p) {
	}
	
	//CALLBACK (implemented from net.sourceforge.jpcap.capture.PacketListener) -- CALLED WHENEVER A PACKET ARRIVES
	public void packetArrived(Packet packet) {		
		CarnivorePacket carnipacket = new CarnivorePacket();
		
		//Log.debug("["+this.getClass().getName()+"] Packet:" + packet.toString());
//		System.out.println("[Core] packetArrived "+ TimeUtilities.dateStampSimplerPrecise());
//		System.out.println("[Core] packetArrived");
		
		try {
			//handle TCP packets
			if(packet instanceof TCPPacket) {
				//Log.debug("["+this.getClass().getName()+"] packet is a TCPPacket");
				carnipacket.strTransportProtocol = Constants.strTCP;
				carnipacket.intTransportProtocol = Constants.intTCP;

				TCPPacket tcpPacket = (TCPPacket)packet;		
				//carnipacket.senderAddress = (IPAddress) IPAddress.getByName(tcpPacket.getSourceAddress());
				//carnipacket.receiverAddress = (IPAddress) IPAddress.getByName(tcpPacket.getDestinationAddress());
				carnipacket.senderAddress = new IPAddress(tcpPacket.getSourceAddress());
				carnipacket.receiverAddress = new IPAddress(tcpPacket.getDestinationAddress());
				carnipacket.senderPort = tcpPacket.getSourcePort();
				carnipacket.receiverPort = tcpPacket.getDestinationPort();
				carnipacket.data = packet.getData();  
				carnipacket.ipHeader = ((IPPacket)packet).getIPHeader();  
				carnipacket.tcpHeader = packet.getHeader();
				carnipacket.date = packet.getTimeval().getDate();
				carnipacket.ipIdentification = ((IPPacket)packet).getId();
//				System.out.println("[Core] Sec: "+ tcpPacket.getSequenceNumber() + 
//						" (rSec: "+ (tcpPacket.getSequenceNumber() + tcpPacket.getPayloadDataLength()) + ")" +
//						" Ack: "+tcpPacket.getAcknowledgementNumber() +
//						" Len: "+tcpPacket.getPayloadDataLength());
//				carnipacket.jpTCPPacket = tcpPacket;
				
				carnipacket.setTcpSequenceNumber(tcpPacket.getSequenceNumber());				
				carnipacket.setTcpAcknowledgementNumber(tcpPacket.getAcknowledgementNumber());				
				carnipacket.setSyn(tcpPacket.isSyn());
				carnipacket.setAck(tcpPacket.isAck());
				carnipacket.setPsh(tcpPacket.isPsh());
				carnipacket.setRst(tcpPacket.isRst());
				carnipacket.setFin(tcpPacket.isFin());				
				carnipacket.setIpLength(((IPPacket) tcpPacket).getLength());
				carnipacket.setIpHeaderLength(((IPPacket) tcpPacket).getIPHeaderLength());
				carnipacket.setTcpHeaderLength(tcpPacket.getHeaderLength());
				carnipacket.setTcpWindowSize(tcpPacket.getWindowSize());
				carnipacket.setTcpPayloadDataLength(tcpPacket.getPayloadDataLength());
				carnipacket.setReceiverMacAddress(tcpPacket.getDestinationHwAddress());
				carnipacket.setSenderMacAddress(tcpPacket.getSourceHwAddress());

				PacketCacheThread.instance().addPacket(carnipacket);
				
			//handle UDP packets
			} else if(packet instanceof UDPPacket) {
				//Log.debug("["+this.getClass().getName()+"] packet is a UDPPacket");
				carnipacket.strTransportProtocol = Constants.strUDP;
				carnipacket.intTransportProtocol = Constants.intUDP;

				UDPPacket udpPacket = (UDPPacket)packet;
				//carnipacket.senderAddress = (IPAddress) IPAddress.getByName(udpPacket.getSourceAddress());
				//carnipacket.receiverAddress = (IPAddress) IPAddress.getByName(udpPacket.getDestinationAddress());
				carnipacket.senderAddress = new IPAddress(udpPacket.getSourceAddress());
				carnipacket.receiverAddress = new IPAddress(udpPacket.getDestinationAddress());
				carnipacket.senderPort = udpPacket.getSourcePort();
				carnipacket.receiverPort = udpPacket.getDestinationPort();
				carnipacket.data = packet.getData();  
				carnipacket.ipHeader = ((IPPacket)packet).getIPHeader();  
				carnipacket.tcpHeader = packet.getHeader();
				carnipacket.date = packet.getTimeval().getDate();
				carnipacket.ipIdentification = ((IPPacket)packet).getId();
//				carnipacket.jpUDPPacket = udpPacket;

				if(!Preferences.instance().getBoolean(Constants.SHOULD_SKIP_UDP)) {
					PacketCacheThread.instance().addPacket(carnipacket);
				}
			}

		} catch( Exception e ) {
			e.printStackTrace();
		}
		//Log.debug("["+this.getClass().getName()+"] carnipacket:" + carnipacket.toString());
	}


	////////////////////////////////////////////////////////////////////////////////
	//DISPATCHER
	static HashSet<CarnivoreListener> listeners = new HashSet<CarnivoreListener>();
	
	public void addCarnivoreListener(CarnivoreListener objListener) { 
		Log.debug("["+this.getClass().getName()+"] addCarnivoreListener: " + objListener);
		listeners.add(objListener); 
	}
	
	public void removeCarnivoreListener(CarnivoreListener objListener) { 
		Log.debug("["+this.getClass().getName()+"] removeCarnivoreListener: " + objListener);
		listeners.remove(objListener); 
	}
	
	public static void dispatch(CarnivorePacket p) {
	    Iterator<CarnivoreListener> i = listeners.iterator();
	    while(i.hasNext()) {
	    	CarnivoreListener l = (CarnivoreListener)i.next();
	    	l.newCarnivorePacket(p);
	    }
    }
	
	public void setFilter(){
		
		//SET FILTER STRING
		if(shouldSkipUDP){
			filter = FILTER_TCP_ONLY;
		} else {
			filter = FILTER_TCP_AND_UDP;
		}	

		for(int i = 0; i < packetcapturethreads.size(); i++){
			PacketCaptureThread pct = (PacketCaptureThread) packetcapturethreads.get(i);

		//Iterator i = packetcaptures.iterator();
		//while(i.hasNext()){
			//PacketCapture p = (PacketCaptureThread)i.next();			

			//SET FILTER
			try {
				pct.packetcapture.setFilter(filter, true);
			} catch (InvalidFilterException e) {
				e.printStackTrace();
			}
			Log.debug("["+this.getClass().getName()+"] setFilter: "+filter);
		}
	}
}
