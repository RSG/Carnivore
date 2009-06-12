package org.rsg.carnivore.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.rsg.carnivore.gui.GUI;
import org.rsg.lib.Log;
import org.rsg.lib.SafeBufferedReader;

public class Client extends Thread {
	// streams for data input and output
	BufferedWriter out;
	SafeBufferedReader in;
	public Socket socket;
	public InetAddress inetaddress;
    private String charEnc = "US-ASCII";
	
	Client (Socket socket) throws IOException {
		this.socket = socket;
		this.inetaddress = socket.getInetAddress();
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), charEnc));
	}
	
	public void run() {
		String userInput;
		try {
			Log.debug("["+this.getClass().getName()+"] thread run()");
			
			in  = new SafeBufferedReader(new InputStreamReader(socket.getInputStream()));
		    	while ((userInput = in.readLine()) != null) {
		        Log.debug("["+this.getClass().getName()+"] command from client: " + userInput);
		        processClientCommand(userInput);
		    	}	
		} catch (IOException e) {
			Log.debug("["+this.getClass().getName()+"] IOException: " + e);
		}
	}
	
	private void processClientCommand(String command) throws IOException {
		
		try {
			//USER command
			if(command.substring(0,4).toUpperCase().equals("USER")) {
				sendData("PING :11111111111");
	
		    //PONG command
			} else if(command.substring(0,4).toUpperCase().equals("PONG")) {
				sendData("Welcome to Carnivore");
	
		   	//JOIN #carnivore command
			} else if(command.substring(0,7).toUpperCase().equals("JOIN #C")) {
				sendData("JOINED");
				GUI.instance().checkbox_ascii.doClick();
				Log.debug("["+this.getClass().getName()+"] GUI.checkbox_ascii.doClick();");
		   	//JOIN #hexivore command
			} else if(command.substring(0,7).toUpperCase().equals("JOIN #H")) {
				sendData("JOINED");
				GUI.instance().checkbox_hex.doClick();
				Log.debug("["+this.getClass().getName()+"] GUI.checkbox_hex.doClick();");
		   	//JOIN #minivore command
			} else if(command.substring(0,7).toUpperCase().equals("JOIN #M")) {
				sendData("JOINED");
				GUI.instance().checkbox_header.doClick();
				Log.debug("["+this.getClass().getName()+"] GUI.checkbox_header.doClick();");
		    }
		} catch(StringIndexOutOfBoundsException e) {} //command from client is malformed
		
	}
	
	//helper method
	public String toString() {
		return inetaddress.getHostAddress();
	}
	
	public void sendData(String s) throws IOException {
		// send the data to the client
		Log.debug("["+this.getClass().getName()+"] sending packet to " + this.toString());
		out.write(s + "\r\n\0");
		out.flush();
	}
    
    //if IOException is thrown, most likely already closed
    public void closeConnection() {
        try {
            out.close();
            in.close();
        } catch(IOException e) { }
    }
}
