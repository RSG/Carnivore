package org.rsg.carnivore.net;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.rsg.carnivore.Constants;
import org.rsg.lib.Log;

/**
 * A simple class to store IP addresses and their component numbers. 
 * 
 * @author RSG
 * @version 2.2
 */
public class IPAddress implements Serializable {
	private static final long serialVersionUID = Constants.VERSION;
	public InetAddress ip;
	
	/**
	 * Constructor
	 * @param s IP address in format 123.45.67.89
	 */
	public IPAddress(String s) {
		try {
			this.ip = InetAddress.getByName(s);
		} catch (UnknownHostException e) {
			Log.debug("["+this.getClass().getName()+"] getByName: " + e);
		}
	}
	
	//helper methods -- mostly for P5 clients 
	private String[] getOctetsFromIP() { 
		return ip.getHostAddress().split("\\."); 
	}	
	
	/**
	 * @return host name
	 */
	public String getHostName() {
		return ip.getHostName();
	}
	
	/**
	 * @return The first octet of the IP address.
	 */
	public int octet1() { return (int) Integer.valueOf(getOctetsFromIP()[0]); }
	
	/**
	 * @return The second octet of the IP address.
	 */
	public int octet2() { return (int) Integer.valueOf(getOctetsFromIP()[1]); }
	
	/**
	 * @return The third octet of the IP address.
	 */
	public int octet3() { return (int) Integer.valueOf(getOctetsFromIP()[2]); }
	
	/**
	 * @return The fourth octet of the IP address.
	 */
	public int octet4() { return (int) Integer.valueOf(getOctetsFromIP()[3]); }
	
	public String toString() {
		return ip.getHostAddress();
	}
}
