package org.rsg.carnivore.net;

/*
 * finds /dev/bpf* devices and checks if they are in promiscuous mode 
 * (i.e. if they are readable and writable)
 * 
 * note: this is only for Mac OSX version (and linux?)
 */

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class DevBPF {
	private String strDevDir = "/dev/";
	private String strStartsWith = "bpf";
	private File fileDevDir = new File(strDevDir);
	public boolean isPromiscuous = false;
	String[] bpf;
	FilenameFilter filter = new FilenameFilter() { 
		public boolean accept(File dir, String name) { 
			return name.startsWith(strStartsWith); 
		} 
	};
	
	public DevBPF() {
		scanDevices();
		checkForPromiscuous();
	}
    
    //returns a string array of all /dev/bpf* 
	private void scanDevices() {
		//apply filter
		bpf = fileDevDir.list(filter); 

		//add directory as prefix
		for(int x = 0; x < bpf.length; x++){
			bpf[x] = strDevDir + bpf[x];
		}
	}
	
	//do a canRead/canWrite on all bpf devices and set isPromiscuous flag
	private void checkForPromiscuous() {
		for(int x = 0; x < bpf.length; x++){
			if(!canReadWrite(bpf[x])) {
				isPromiscuous = false;
				return; // if at least one is closed, it's not promiscuous 
			}
		}	
		isPromiscuous = true; //if we got this far it must be true
	}
	
	//checks if file is readable and writable
	private boolean canReadWrite(String s) {
		File f = new File(s);
		if((f.canRead()) && (f.canWrite())) {
			return true;
		} else {
			return false;
		}
	}

	public static void sudoChmod777devbpf(){
		String[] commandarray = {"open", "sudoChmod777devbpf.app"};
		try {
			Runtime.getRuntime().exec(commandarray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
