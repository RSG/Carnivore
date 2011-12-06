package org.rsg.carnivore;

import java.lang.reflect.Method;

import org.rsg.carnivore.net.DevBPF;
//import org.rsg.carnivore.net.IPAddress;
//import org.rsg.lib.ErrorMessages;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.Log;

import processing.core.PApplet;

/**
 * This class is the entry point for the Carnivore Processing library.
 * 
 * @author RSG
 * @version 2.2
 * 
 */
public class CarnivoreP5 implements CarnivoreListener {
	PApplet parent;
	Method packetEventMethod;
	Core core;
	public boolean isMacAndPromiscuousModeFailed = false; 	

	
	/**
	 * Example: <tt>CarnivoreP5 c = new CarnivoreP5(this);<tt>
	 * 
	 * @param parent the Processing sketch that is instantiating Carnivore. It is typically "this".
	 */
	public CarnivoreP5(PApplet parent) {

		//REGISTER "DISPOSE" CALLBACK
		this.parent = parent;
		parent.registerDispose(this);

		try {
			packetEventMethod = parent.getClass().getMethod("packetEvent", new Class[] { CarnivorePacket.class });
		} catch (Exception e) {
			e.printStackTrace(); // no such method, or an error.. which is fine, just ignore
		}	    

		Log.debug("["+this.getClass().getName()+"] starting carnivore core");
		checkBPFforMac(); //will quit if Mac machines are not in promiscuous 

		if(!isMacAndPromiscuousModeFailed) {
			core = new Core(this);
			core.addCarnivoreListener(this);
			core.start();

			setVolumeLimit(Constants.VOLUME_MAX); //start w/ high limit
		}
	}

	//////////////////////////////////////////////////////////////////////
	//API for P5 code 
	/**
	 * Buffer the output of the carnivore core to a given number of packets per second. 
	 * This is useful for high volume networks, or if the Processing sketch responds better 
	 * to a low volume of incoming packets. 
	 * 
	 * @param i volume limit in packets per second
	 */
	public void setVolumeLimit(int i) {
		Preferences.instance().put(Constants.MAXIMUM_VOLUME, CarniUtilities.normalizeVolume(i));	
	}

	/**
	 * Determines if carnivore core should print TCP and UDP packets, or only TCP packets. 
	 */
	public void setShouldSkipUDP(boolean b) {
		Preferences.instance().put(Constants.SHOULD_SKIP_UDP, b);	
	}
	
	//////////////////////////////////////////////////////////////////////
	//CarnivoreListener interface 
	/** 
	 * The callback from carnivore core. 
	 * Each time a new packet arrives it invokes the <tt>packetEvent</tt> method in the Processing sketch. 
	 * The Processing sketch should not call this method directly. 
	 * 
	 * @see org.rsg.carnivore.CarnivoreListener#newCarnivorePacket(org.rsg.carnivore.CarnivorePacket)
	 */
	public void newCarnivorePacket(CarnivorePacket packet) {
//		Log.debug("[CarnivoreP5] newCarnivorePacket from " + packet.senderAddress);

		if (packetEventMethod != null) {
			try {
				packetEventMethod.invoke(parent, new Object[] { packet } );
			} catch (Exception e) {
				e.printStackTrace();
				//System.err.println("Disabling packetEvent() because of an error.");
				//packetEventMethod = null;
			}
		}
	}

	/**
	 * Part of the coding convention for making Processing libraries. 
	 * Anything in here will be called automatically when the parent applet shuts down. 
	 * The Processing sketch should not call this method directly. 
	 */
	public void dispose() {
		core.stop();
		//Nodes.save(); //serialize nodes object
		System.out.println("[CarnivoreP5] dispose()"); //hmm why doesn't this ever get called? 
	}

	/**
	 * will quit if Mac machines are not in promiscuous
	 */
	private void checkBPFforMac() {
		if (!LibUtilities.isMac()) return;	//return if not a mac 
		
		DevBPF devbpf = new DevBPF();
		isMacAndPromiscuousModeFailed = !devbpf.isPromiscuous;
		
//		if(!devbpf.isPromiscuous) {
//			System.err.println(ErrorMessages.MAC_NOT_PROMISCUOUS);
//			System.exit(0);
//			return false; 
//		}
	}

}
