package org.rsg.carnivore;

import java.util.Stack;

//import org.rsg.carnivore.cache.OfflineCache;
//import org.rsg.carnivore.cache.OfflineLogger;
import org.rsg.carnivore.cache.SpeedometerCache;
//import org.rsg.carnivore.cache.Stats;
//import org.rsg.carnivore.gui.GUI;
//import org.rsg.carnivore.net.Server;
import org.rsg.lib.time.TimeUtilities;

class PacketCacheThread extends Thread {
	public boolean continueRunning = true;
	private final int MILLISECS_PER_THREAD_LOOP = 50;
	private final int MILLISECS_PER_SEC = 1000;
	private int milliseconds = 0;
	private int seconds = 0;
	private int packetsPerSecDispatched = 0;
	private static SpeedometerCache speedometer = new SpeedometerCache(); 
	private static Stack<CarnivorePacket> cacheForClient = new Stack<CarnivorePacket>(); 

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    public static PacketCacheThread instance() { return INSTANCE; }
    private static final PacketCacheThread INSTANCE = new PacketCacheThread();
	private PacketCacheThread() { }
    
	public void run() {
		while (continueRunning) {     
//			System.out.println("[PacketCacheThread] run "+ TimeUtilities.dateStampSimplerPrecise());
			
			speedometer.pruneToFrame();
			
			//each 10 millisecs... dispatch the cache, but don't go over volume limit			
			while(	(cacheForClient.size() > 0) && (!isOverLimit())) {
				Core.dispatch(cacheForClient.remove(0)); //shift
				packetsPerSecDispatched++;
			}
			
			//each second
			if(milliseconds >= MILLISECS_PER_SEC) {

//				Log.debug(	"[PacketCacheThread] uptime=" + seconds + " secs, " +
//						LibUtilities.formatFloat(stats.packetsPerSecTotal) + " packets/sec " + 
//						"(" + LibUtilities.formatFloat(stats.packetsPerSecTCP) + " TCP, "+ 
//						LibUtilities.formatFloat(stats.packetsPerSecUDP) +" UDP). Number dispatched to client: "+packetsPerSecDispatched);
//				
//				Log.debug("\n================================");
				seconds++;
				reset();		
			}
			
			try {
				milliseconds += MILLISECS_PER_THREAD_LOOP;
				Thread.sleep(MILLISECS_PER_THREAD_LOOP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isOverLimit() {
		int sliderVolume = Preferences.instance().getInt(Constants.MAXIMUM_VOLUME);
		if(sliderVolume >= Constants.VOLUME_MAX) { 			 //infinity state so always return false
			return false;
		} else {
			return (packetsPerSecDispatched > sliderVolume); //otherwise pin to slider value
		}
	}
	
	private void reset() {
		milliseconds = 0;
		packetsPerSecDispatched = 0;
		cacheForClient.clear(); //clear leftovers		
	}
	
	public void addPacket(CarnivorePacket p) {
		System.out.println("[PacketCacheThread] addPacket "+ TimeUtilities.dateStampSimplerPrecise());
		cacheForClient.add(p);
		speedometer.add(p);
	}
}

