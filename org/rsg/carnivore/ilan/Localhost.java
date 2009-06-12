package org.rsg.carnivore.ilan;

import java.net.*;
import java.util.*;

public class Localhost {
	ArrayList<String> hostlist = new ArrayList<String>();
	Localhost() {
       findLocalhostNames();
	}

	void findLocalhostNames(){
	  try {
			NetworkInterface iface = null;
			for(Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();ifaces.hasMoreElements();){
				iface = (NetworkInterface)ifaces.nextElement();
				//System.out.println("Interface:"+ iface.getDisplayName());
				InetAddress ia = null;
				for(Enumeration<InetAddress> ips = iface.getInetAddresses();ips.hasMoreElements();){
					ia = (InetAddress)ips.nextElement();
                                     hostlist.add(ia.getCanonicalHostName()+"");
                                     hostlist.add(ia.getHostAddress()+"");
					//System.out.println(ia.getCanonicalHostName()+" "+ia.getHostAddress());
				}
			}
	  } catch (java.net.SocketException e) {}
	}
	
    public boolean isLocal(String ip, String host){
       for(int i=0; i<hostlist.size(); i++) {
         if((ip.equals(hostlist.get(i))) || (ip.equals(hostlist.get(i)))) { return true; }
       }
       return false;
     }

	public String toString() {
		return hostlist.toString();
	}
}