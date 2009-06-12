package org.rsg.carnivore;

import org.rsg.carnivore.cache.OfflineCache;
import org.rsg.lib.Log;

public class OfflineThread extends Thread {
	private boolean continueRunning = false;
	private final boolean alwaysTrue = true;
	private final int MILLISECS_PER_THREAD_LOOP = 10;
	public int milliseconds = 0;

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final OfflineThread INSTANCE = new OfflineThread();
	private OfflineThread() {start();}
    public static OfflineThread instance() {
        return INSTANCE;
    }
    
    public void startMe() {
    	Log.debug("["+this.getClass().getName()+"] starting...");
    	continueRunning = true;
    	milliseconds = 0;
    }

    public void stopMe() {
    	Log.debug("["+this.getClass().getName()+"] stopping...");
    	continueRunning = false;
    }

	public void run() {
		while (alwaysTrue) {   
			//Log.debug("["+this.getClass().getName()+"] run()");
			while (continueRunning) {   

				//loop back to beginning if at end
				if(OfflineCache.instance().isAtEnd()) {
					OfflineCache.instance().resetToBeginning();
					milliseconds = 0;
				}

				/*if((milliseconds % 1000) == 0){
					System.out.println(OfflineCache.instance().getMarked().timestamp + " < " + milliseconds);
				}*/

				if(OfflineCache.instance().getMarked().timestamp < milliseconds) {		//peek at time
					CarnivorePacket p = (CarnivorePacket) OfflineCache.instance().getMarked().object;				
					PacketCacheThread.instance().addPacket(p);
					OfflineCache.instance().marker++;
					//System.out.println(	"\n=======\n[OfflineThread] thread timecode:"+milliseconds+
					//		" marker:" + OfflineCache.instance().marker + " packet:" + p);
				}

				//moderation for "continueRunning" loop
				try {
					milliseconds += MILLISECS_PER_THREAD_LOOP;
					Thread.sleep(MILLISECS_PER_THREAD_LOOP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//moderation for "alwaysTrue" loop
			try {
				Thread.sleep(MILLISECS_PER_THREAD_LOOP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

