// $Id: PacketCapture.java,v 1.16 2004/09/28 17:27:11 pcharles Exp $

/***************************************************************************
 * Copyright (C) 2001, Patrick Charles and Jonas Lehmann                   *
 * Distributed under the Mozilla Public License                            *
 *   http://www.mozilla.org/NPL/MPL-1.1.txt                                *
 ***************************************************************************/
package net.sourceforge.jpcap.capture;

import java.util.Locale;

import org.rsg.carnivore.OSValidator;

/**
 * This class is the core of packet capture in jpcap. It provides a 
 * high-level interface for capturing network packets by encapsulating 
 * libpcap.
 * <p>
 * If you want to capture network packets, implement PacketListener
 * and register with an instance of this class. When packets arrive, 
 * the object will call you back via packetArrived().
 * <p>
 * Examples can be found in net.sourceforge.jpcap.tutorial.
 * <p>
 * For more documentation on this class's methods, see PacketCaptureCapable;
 * Javadoc is 'inherited' from this interface.
 * <p>
 * PacketCapture utilizes libpcap's pcap_loop().
 * See SyncPacketCapture for pcap_dispatch()-type behavior.
 *
 * @author Patrick Charles and Jonas Lehmann
 * @version $Revision: 1.16 $
 * @lastModifiedBy $Author: pcharles $
 * @lastModifiedAt $Date: 2004/09/28 17:27:11 $
 */
public class PacketCapture extends PacketCaptureBase implements PacketCaptureCapable {

	//Create a new packet capture instance.
	public PacketCapture() {
		if(nextInstance >= INSTANCE_MAX) {
			throw new Error("Too many instances, exceeds " + INSTANCE_MAX);
		}
		instanceNum = nextInstance ++;
	}

	////////////////////////////////////////////////////////////////////////////////
	//open
	public void open(String device, boolean promiscuous) throws CaptureDeviceOpenException {
		//System.out.println("[net.sourceforge.jpcap.capture.PacketCapture] open(): device=" + device + " promiscuous=" + promiscuous);
		open(instanceNum, device, DEFAULT_SNAPLEN, promiscuous, DEFAULT_TIMEOUT);
	}

	public void open(String device, int snaplen, boolean promiscuous, int timeout) throws CaptureDeviceOpenException {
		//System.out.print("["+this.getClass().getName()+"] calling native open(): device=" + device + " promiscuous=" + promiscuous + "... ");
		open(instanceNum, device, snaplen, promiscuous, timeout); //calls native open method below
		//System.out.println("ok");
	}

	public void openOffline(String fileName) throws CaptureFileOpenException {
		openOffline(instanceNum, fileName); //calls native open method below
	}

	//native calls
	public native void open(int instance, String device, int snaplen, boolean promiscuous, int timeout) throws CaptureDeviceOpenException;
	public native void openOffline(int instance, String fileName) throws CaptureFileOpenException;

	////////////////////////////////////////////////////////////////////////////////
	//set filter 
	public native void setFilter(int instance, String filterExpression, boolean optimize) throws InvalidFilterException;
	public void setFilter(String filterExpression, boolean optimize) throws InvalidFilterException {
		//System.out.print("["+this.getClass().getName()+"] calling native setFilter(): filterExpression=" + filterExpression + " optimize=" + optimize + "... ");
		setFilter(instanceNum, filterExpression, optimize);
		//System.out.println("ok");
	}

	////////////////////////////////////////////////////////////////////////////////
	//capture 
	public native void capture(int instance, int count) throws CapturePacketException;
	public void capture(int count) throws CapturePacketException {
		//System.out.print("["+this.getClass().getName()+"] calling native capture(): instanceNum=" +instanceNum+ " count=" + count + "... ");
		capture(instanceNum, count);
		//System.out.println("ok");
	}
	public void capture() throws CapturePacketException {
		//System.err.println("["+this.getClass().getName()+"] calling native capture(): instanceNum=" +instanceNum + "... ");
		capture(instanceNum, -1); //-1 means forever
		//System.err.println("["+this.getClass().getName()+"] ok");
	}
	
	////////////////////////////////////////////////////////////////////////////////
	public CaptureStatistics getStatistics() {
		setupStatistics();

		// transfer the values setup by the native method into
		// the container and return to the caller
		return new CaptureStatistics(receivedCount, droppedCount);
	}

	/**
	 * Close cleans up after a packet capture session.
	 * It does _not_ terminate a packet capture. 
	 * capture() does not return control until 'count' packets are captured.
	 * <p>
	 * If you are looking for a way to signal an end to a capture session 
	 * before a set number of packets are received, check out the class
	 * SyncPacketCapture.
	 */
	public native void close(int instance);

	public void close() {
		close(instanceNum);
	}

	public native void endCapture(int instance);

	public void endCapture() {
		endCapture(instanceNum);
	}


	/**
	 * Get Interface List
	 * @return Network device interface names.
	 */
	public static native String[] lookupDevices() 
	throws CaptureDeviceLookupException;

	// the following methods could be static, but aren't so that they 
	// can be included in the PacketCaptureCapable interface.
	public native String findDevice() throws CaptureDeviceNotFoundException;

	public native int getNetwork(String device) throws CaptureConfigurationException;

	public native int getNetmask(String device) throws CaptureConfigurationException;

	public int getLinkLayerType() throws CaptureConfigurationException {
		return getLinkLayerType(instanceNum);
	}

	public native int getLinkLayerType(int instance) throws CaptureConfigurationException;

	public int getSnapshotLength() {
		return getSnapshotLength(instanceNum);
	}

	public native int getSnapshotLength(int instance);


	/**
	 * The packet capture library sets up the statistic counter members
	 * when this method is invoked internally.
	 */
	private native void setupStatistics(int instance);

	private void setupStatistics() {
		setupStatistics(instanceNum);
	}

	// static initialization
	static {
//		System.out.println("[net.sourceforge.jpcap.capture.PacketCapture] " + OSValidator.toMessage());
		System.out.print("[net.sourceforge.jpcap.capture.PacketCapture] loading native library jpcap... ");
		//public static String LIB_PCAP_WRAPPER = "jpcap";
		String s = "";
		
		//win
		if(OSValidator.isWindows()) {
//			System.out.print("Windows...") ;
			
			s = "jpcap-win64bit";
			
			if(OSValidator.is32bit()) {
//				System.out.print("32 bit");
				s = "jpcap-win32bit";
//				System.loadLibrary("jpcap-win32bit");
			} 	
			
		//mac
		} else if(OSValidator.isMac()) {
//		if(System.getProperty("os.name").toLowerCase(Locale.US).indexOf("mac") != -1) {
//			System.out.print("Mac...");
			//this chooses the libjpcap*.jnilib library file for jpcap (Mac only)
			
			s = "jpcap-i386";
			
			if(System.getProperty("os.arch").toLowerCase(Locale.US).indexOf("ppc") != -1) {
//				System.out.print("i386");
//				System.loadLibrary("jpcap-i386");
				s = "jpcap-ppc";
			}   

		//Linux
		} else if (OSValidator.isUnix()){
//			System.out.print("Linux...");
			
			s = "jpcap-linux64bit";
			
			if(OSValidator.is32bit()) {
//				System.out.print("32 bit");
//				System.loadLibrary("jpcap-linux32bit");
				s = "jpcap-linux32bit";
			} 	
		} else {
			s = "(warning: OS not supported)";
		}
		
		System.out.println(s);		
		System.loadLibrary(s);
	}

	private int instanceNum = 0; // the index of this instance
	private static int nextInstance = 0; // static instance counter
	private static int INSTANCE_MAX = 10;
	private String _rcsid = 
		"$Id: PacketCapture.java,v 1.16 2004/09/28 17:27:11 pcharles Exp $";
}

