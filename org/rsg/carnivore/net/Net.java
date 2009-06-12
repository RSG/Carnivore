package org.rsg.carnivore.net;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class Net {

	final static String HOST = "www.google.com";
	final static int PORT = 80;
	final static int TIMEOUT = 2000; //milliseconds

	//PERFORMANCE = BEST
	//if everything is up: 		works (fast)
	//if Airport turned off: 	works (fast)
	//if router down: 			works (short timeout) 
	public static boolean canPingUsingURLConnection() {
		try {
			URL url = new URL("http://" + HOST);
			URLConnection urlc = url.openConnection();
			urlc.setConnectTimeout(TIMEOUT);
			if(urlc.getContent() != null) {
				return true;
			}
		} catch (Exception e) {} //do nothing
		return false;
	}
	
	//PERFORMANCE = MEDIUM 
	//if everything is up: 		works (fast)
	//if Airport turned off: 	works (fast)
	//if router down: 			works (loooooong timeout) 
	public static boolean canPingUsingHTTPGet() {
		try {
			Socket socket = new Socket(HOST, PORT);
			socket.setSoTimeout(TIMEOUT); //is this doing anything??

			InputStream from_server = socket.getInputStream();
			PrintWriter to_server = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			to_server.println("GET /index.html HTTP/1.1\n\n");
			to_server.flush();
			
			byte[] buffer = new byte[4096];
			if(from_server.read(buffer) > -1) {
				socket.close();
				return true;
			}
		} catch (Exception e) {} //do nothing		      
		return false;
	}

	//PERFORMANCE = WORST
	//doesn't work
	public static boolean canPingUsingInetIsReachable() {
		try {
			InetAddress address = InetAddress.getByName(HOST);
			if(address.isReachable(TIMEOUT)) {
				return true;				
			}
		} catch (Exception e) {} //do nothing
		return false;
	}
	
	public static String getInetLocalHost() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.toString();			
		} catch (Exception e) {} //do nothing
		return "unknown";
	}
}
