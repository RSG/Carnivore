package org.rsg.carnivore.tcpReassembly;

import java.util.ArrayList;
import org.rsg.carnivore.CarnivorePacket;

public class ReassemblyCache {
	private static ArrayList<TcpSessionHost> tcpSessionHosts = new ArrayList<TcpSessionHost>(); 

	public static void newPacket(CarnivorePacket packet) {
		TcpSessionHost tcpSessionHost = findSessionHost(packet);
		
		//no session exists, so create a new one
		if(null==tcpSessionHost) {
			tcpSessionHost = new TcpSessionHost(packet.getTcpSequenceNumber(), packet.toStringTcpSessionHost());
			tcpSessionHosts.add(tcpSessionHost);
		}
		
		tcpSessionHost.newPacket(packet);
//		printCache();
	}
	
	public static void remove(TcpSessionHost tsh){
		tcpSessionHosts.remove(tsh);
	}
	
	private static TcpSessionHost findSessionHost(CarnivorePacket packet) {
		for(TcpSessionHost tcpSessionHost: tcpSessionHosts) {
			if(tcpSessionHost.getSessionId().equals(packet.toStringTcpSessionHost()))
				return tcpSessionHost;
		}		
		return null;
	}	
	
	public static void printCache() {
		System.out.println("\n[ReassemblyCache] "+tcpSessionHosts.size()+" hosts ==============");
//		for(TcpSessionHost tcpSessionHost : tcpSessionHosts) 
//			System.out.println(tcpSessionHost.toString());
	}
}
