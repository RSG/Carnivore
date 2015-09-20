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
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class DevBPF {
	public static final String CHMOD_777 = "rwxrwxrwx";
	public static final String CHMOD_600 = "rw-------";
	
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
	
	public static void main(String[] args) throws IOException {
		new DevBPF();
	}
	
	public DevBPF() {
		System.out.print("[DevBPF] Checking network adaptors for promiscuous mode...");
		scanDevices();
		
		//check for promiscuous
		try {
			checkDevicesForPromiscuous();
		} catch (IOException e) {
			System.err.println("[DevBPF.checkForPromiscuous] IOException");
			e.printStackTrace();
		}
		System.out.println((isPromiscuous) ? "YES" : "NO");
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
	private void checkDevicesForPromiscuous() throws IOException {
		for(int x = 0; x < bpf.length; x++){
			if(!isPromiscuous(bpf[x])) {
				isPromiscuous = false;
				return; // if at least one is closed, it's not promiscuous 
			}
		}	
		isPromiscuous = true; //if we got this far it must be true
	}

/*
 * setPosixFilePermissions works but not for sudo/root authentication (?).. so useless for us
 */
//	private void setDevicesForPromiscuous() {
//		final Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(CHMOD_777);
////		final Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(CHMOD_600);
//
////		for(int x = 0; x < bpf.length; x++){
////			Files.setPosixFilePermissions(Paths.get(bpf[x]), permissions);
////		}	
//
//		String target = "/Users/alex/Desktop/test_permissions";
//		try {
//			Files.setPosixFilePermissions(Paths.get(target), permissions);
////		} catch (SecurityException e) {
////			System.err.println("[DevBPF.checkForPromiscuous] SecurityException -- Accessed denied for chmod on " + target);
////			e.printStackTrace();			
//		} catch (IOException e) {
//			System.err.println("[DevBPF.checkForPromiscuous] IOException");
//			e.printStackTrace();
//		}
//	}
	
	
//	//This worked fine, but depricated in favor of using java.nio.file.attribute.PosixFilePermission
//	//checks if file is readable and writable
//	private boolean canReadWrite(String s) {
//		File f = new File(s);
//		if((f.canRead()) && (f.canWrite())) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	//checks if file is in `chmod 777` mode
	private static boolean isPromiscuous(String path) throws IOException {
		Path p = Paths.get(path);
		Set<PosixFilePermission> set = Files.getPosixFilePermissions(p);
		return isPromiscuous(set);
//		System.out.println(path + " : " + (isPromiscuous(set) ? "Promiscuous" : "Not Promiscuous"));			
	}
	
	private static boolean isPromiscuous(Set<PosixFilePermission> set) {
		return PosixFilePermissions.toString(set).equals(CHMOD_777);
	}
	

//	public static void sudoChmod777devbpf(){
//		String[] commandarray = {"open", "sudoChmod777devbpf.app"};
//		try {
//			Runtime.getRuntime().exec(commandarray);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

