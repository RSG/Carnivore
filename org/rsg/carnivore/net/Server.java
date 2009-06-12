package org.rsg.carnivore.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import org.rsg.carnivore.Constants;
import org.rsg.carnivore.Preferences;
import org.rsg.lib.Log;

public class Server extends Thread {
	private int port;
	private Vector<Client> clients = new Vector<Client>();	//list of connections
    private boolean listenLoop = true;

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final Server INSTANCE = new Server();
    public static Server instance() {
        return INSTANCE;
    }
    
	//CONSTRUCTOR
	private Server() {
		port = Preferences.instance().getInt(Constants.SERVER_PORT);
	}							
	
	public int getNumberOfClients() {
		return clients.size();
	}
	
	public void run() {		
		ServerSocket s = null;    //our server
		Socket socket; 	          //client socket 
		try {
			s = new ServerSocket(port);	//startup the socket listener
            Log.debug("["+this.getClass().getName()+"] ServerSocket started on port " + port);
			
			//WAIT FOR NEW CONNECTIONS
			while (listenLoop) {
                	socket = s.accept();    //blocks until new connection detected
                	String ip = socket.getInetAddress().getHostAddress();
                
				//PROCESS NEW CONNECTION				
				if(shouldAllowClient(socket.getInetAddress())) {
					Log.debug("["+this.getClass().getName()+"] new connection accepted from: " + ip);
					Client client = new Client(socket);
					client.start();
					clients.add(client);
				} else {
					Log.debug("["+this.getClass().getName()+"] new connection rejected from: " + ip + " (blocked by security preference)");					
				}
			}
			
		} catch (IOException e) {
			Log.debug("["+this.getClass().getName()+"] ServerSocket Error" + e);
		} finally {
        
            //for shutdown, should call shutdown() to allow polite closings for clients
            //only reached if listenLoop is set to false or there's a ServerSocket error
            Log.debug("["+this.getClass().getName()+"] Closing client connections....");
            for (int i = 0; i < clients.size(); i++) {
                ((Client)clients.get(i)).closeConnection();
            }
            
            //no need to deal with exception, either it's null, it's closed or we're closing it
            try {
                if (s != null)
                    s.close();
            } catch(IOException e) { }
        }
	}

	public void checkSecurityStatusOfAllClients() {
		for (int i = 0; i < clients.size(); i++) {
			Client client = (Client)clients.get(i);
			
			if(!shouldAllowClient(client.inetaddress)){
				removeClient(client);
			}
		}
	}
	
	public boolean shouldAllowClient(InetAddress inetaddress) {
		if(Preferences.instance().getBoolean(Constants.SHOULD_ALLOW_EXTERNAL_CLIENTS) ||	//allow if security is turned off
		  (inetaddress.isLoopbackAddress())) {									//or if client is localhost
			return true;
		}
		return false;
	}

	public void printClients() {
		String s = "";
		for (int i = 0; i < clients.size(); i++) {
			Client client = (Client)clients.get(i);
			s = s + " " + client.toString();
		}
		Log.debug("["+this.getClass().getName()+"] printClients: " +s);
	}

	public void sendData(String s) {
		checkSecurityStatusOfAllClients();
		
		for (int i = 0; i < clients.size(); i++) {
			Client client = (Client)clients.get(i);
			
			try {
				client.sendData(s);
				
			//remove client if received i/o error 
			} catch (IOException e) {
				removeClient(client);
			}
		}
	}
	
	public void removeClient(Client c) {
		Log.debug("["+this.getClass().getName()+"] removeClient: " + c.toString());
        c.closeConnection();
		clients.remove(c);
	}
    
    //shutsdown client connections and serverSocket
    public void shutdown() {
        listenLoop = false;
    }
}
