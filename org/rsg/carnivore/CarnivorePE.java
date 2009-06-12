package org.rsg.carnivore;

import org.rsg.carnivore.cache.OfflineCache;
import org.rsg.carnivore.cache.OfflineLogger;
import org.rsg.carnivore.gui.Console;
import org.rsg.carnivore.gui.GUI;
import org.rsg.carnivore.net.DevBPF;
import org.rsg.carnivore.net.Server;
import org.rsg.lib.ErrorMessages;
import org.rsg.lib.JVM;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.Log;

/*
 * this is the "Main" entry point for the CarnivorePE desktop app
 * 
 * it launches the gui and acts as a client for the carnivore core
 */

public class CarnivorePE implements CarnivoreListener {
	public Server server;
	public Core core;
	public Preferences preferences;
	public boolean isSnifferOn = false;

	/*public void guiIsFinishedLaunching(boolean b){
		Log.debug("[CarnivorePE] guiIsFinishedLaunching="+b);
		guiIsFinishedLaunching = b;
	}*/
	
	//MAIN
	public static void main (String[] args) {
				
		//SET DEBUGGING ON IF USER REQUESTED IT 
		if ((args.length > 0) && (args[0].equals("-debug"))) {
		     Log.setDebug(true);
		}

		//HANDLE COMMAND LINE ARGS
        int i = 0;
        String arg;
        String playbackfile = null;
        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            if (arg.equals("-playback")) {
                if (i < args.length) {
                	playbackfile = args[i++];
                	System.out.println("playbackfile file received: " + playbackfile);
                } else {
                    System.err.println("-playback requires a filename");
                }
            }/* else if (arg.equals("-h") || arg.equals("-help")) {
            	System.out.println("Usage: CarnivorePE [-playback filename]");
            }*/
        }
		
		//test
		/*if (args.length > 0) {
			for(int x = 0; x < args.length; x++) {
				System.out.println("args["+x+"]: "+args[x]);
			}
		}*/
		
		//turn on debugging output
		org.rsg.lib.Log.setDebug(true);

		//Log.debug(System.getProperty("java.specification.version"));

		//version check -- does this ever get fired? 
		if (!JVM.is15()) {
			Log.window(ErrorMessages.JAVA_VERSION_NOT_15);
			System.exit(0);
		}
		
		if(playbackfile != null) {
			CarnivorePE.instance().startPlayback(playbackfile);
		} else {
			CarnivorePE.instance();
		}
	}

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final CarnivorePE INSTANCE = new CarnivorePE();
    public static CarnivorePE instance() {
        return INSTANCE;
    }
    
	//CONSTRUCTOR
	private CarnivorePE() {

		//LOAD PREFERENCES
		Preferences.instance();
		
		//start mac specific GUI stuff
		LibUtilities.setOSXSystemProperties("CarnivorePE"); //puts menus into finder bar instead of window
		if (LibUtilities.isMac()){
			MacEventHandler.instance();
		}
		
		//LAUNCH GUI
		GUI.instance().start();
		while(!GUI.instance().isFinishedLaunching) { //wait
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
		
		//START SNIFFER
		Log.debug("["+this.getClass().getName()+"] starting carnivore core");
		checkBPFforMac(); //will quit if Mac machines are not in promiscuous 
		core = new Core(this);
		core.addCarnivoreListener(this);

		GUI.instance().initGUIWithPreferences();

		core.start();

		//SERVER THREAD
		Server.instance().start(); 
	} 
	
	//CALLBACK FROM CORE
	public void newCarnivorePacket(CarnivorePacket packet) {
		//Log.debug("\n---------------------------");
		//Log.debug("["+this.getClass().getName()+"] new CarnivorePacket: " + packet);
		
		OfflineCache.instance().add(packet); 			//send to offline recorder (only actually adds if it's enabled)
		OfflineLogger.instance().add(packet); 			//send to logger (only actually adds if it's enabled)
		Server.instance().sendData(packet.toString());	//send to server for output to clients
		Console.instance().addPacket(packet);			//send to console
	}

	private void startPlayback(String filename) {
		OfflineCache.instance().startPlaybackUsingFilename(filename);
	}
		
	//will quit if Mac machines are not in promiscuous 
	private void checkBPFforMac() {
		if (!LibUtilities.isMac())
			return;

		DevBPF devbpf = new DevBPF();
		if(!devbpf.isPromiscuous) {
			Log.window(ErrorMessages.MAC_NOT_PROMISCUOUS);
		}
	}

	//called by GUI pulldown menu "Quit"
	//this should serve as master method for application shutdown
	public void quit() {		
		OfflineCache.instance().stopRecording(); 
		OfflineLogger.instance().stop();
		Log.debug("["+CarnivorePE.class.getName()+"] quit");
		System.exit(0);
	}
	
	public void newImage(String path) {
		// TODO Auto-generated method stub
		
	}
	
}
