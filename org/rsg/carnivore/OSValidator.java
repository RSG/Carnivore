package org.rsg.carnivore;

public class OSValidator {
	 
	private static String OS_NAME = System.getProperty("os.name");
	private static String OS_ARCH = System.getProperty("os.arch");
	private static String OS_VERSION = System.getProperty("os.version");
	private static String OS_BITS = System.getProperty("sun.arch.data.model");	
 
	public static void main(String[] args) {
 
		System.out.println(OSValidator.toMessage());
 
		if (is32bit()) {
			System.out.println("This is 32 bit");
		} else if (is64bit()) {
			System.out.println("This is 64 bit");
		} else {
			System.out.println("bit size unknown");
		}
			
		if (isWindows()) {
			System.out.println("This is Windows");
		} else if (isMac()) {
			System.out.println("This is Mac");
		} else if (isUnix()) {
			System.out.println("This is Unix or Linux");
		} else if (isSolaris()) {
			System.out.println("This is Solaris");
		} else {
			System.out.println("Operating system not supported");
		}
	}

	public static boolean is32bit() {
		return OS_BITS.equals("32");
	}

	public static boolean is64bit() {
		return OS_BITS.equals("64");
	}

	public static boolean isARM() {
		return (OS_ARCH.toLowerCase().indexOf("arm") >= 0);
	}

	public static boolean isWindows() {
		return (OS_NAME.toLowerCase().indexOf("win") >= 0);
	}
 
	public static boolean isMac() {
		return (OS_NAME.toLowerCase().indexOf("mac") >= 0);
	}
 
	public static boolean isUnix() {
		return (OS_NAME.toLowerCase().indexOf("nix") >= 0 || OS_NAME.toLowerCase().indexOf("nux") >= 0 || OS_NAME.toLowerCase().indexOf("aix") > 0 );
	}
 
	public static boolean isSolaris() {
		return (OS_NAME.toLowerCase().indexOf("sunos") >= 0);
	}
	
	public static String toMessage() {
		return OS_NAME + " (" + OS_VERSION + "), " + OS_ARCH + ", " + OS_BITS + " bit";
	}
 
}