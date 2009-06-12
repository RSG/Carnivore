package org.rsg.carnivore.net;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import net.sourceforge.jpcap.capture.CaptureDeviceLookupException;
import net.sourceforge.jpcap.capture.PacketCapture;

import org.rsg.lib.LibUtilities;
import org.rsg.lib.Log;

//open devices: chmod 777 /dev/bpf*
//reset them:   chmod 600 /dev/bpf*

public class Devices { 
    private static String _current_device;
	public static HashMap<String, String> _devices = 			new HashMap<String, String>();
	public static HashMap<String, String> _interface_lookup = 	new HashMap<String, String>();
	
	public Devices() {

		//"write once, compile anywhere"?? :P
		//WINDOWS CODE
		if(LibUtilities.isWindows()) {
			try {
				String[] devlist = PacketCapture.lookupDevices();

				for(int i = 0; i < devlist.length; i++) {
					String iface = devlist[i];
					String name, displayName;

					//split string up into interface name and display name (same on *nix platforms)
					if (iface.indexOf('\n') != -1) {
						//windows, most likely -- split on new line
						name = iface.substring(0, iface.indexOf('\n'));
						displayName = iface.substring(iface.indexOf('\n') + 1);
					} else {
						//other platforms...theoretically will be covered by interface_lookup
						name = displayName = iface;
					}

					//BUILD HASH OF DEVICE NAMES AND LONG NAMES				
					String prefix = name.substring(0,2);
					if (!prefix.equals("lo")) { //ignore loopback? (lo, lo0)

						//USE LOOKUP NAME FOR CERTAIN INTERFACES
						if(_interface_lookup.containsKey(name)){
							_devices.put(name, (String) _interface_lookup.get(name));

							//OTHERWISE USE WHATEVER THE DEVICE TOLD US
						} else {
							_devices.put(name,displayName);
						}
					}
				}	
			} catch (UnsatisfiedLinkError e) {
				Log.debug("Error--can't load native library.\n\n" +

						"Make sure you have installed winpcap (http://www.winpcap.org)\n" +
						"and that jpcap.dll is in the same folder as this application.\n\n" +

						"["+this.getClass().getName()+"] UnsatisfiedLinkError: " + e.toString());

			} catch (CaptureDeviceLookupException e) {
				e.printStackTrace();
			}

			//MAC CODE
			//TODO: check to see if this works on linux
		} else {
			//SET UP KNOWN INTERFACE LONG NAME LOOK UP TABLE
			_interface_lookup.put("lo0", "loopback");
			_interface_lookup.put("en0", "Built-in Ethernet");
			_interface_lookup.put("en1", "AirPort");

			Enumeration<NetworkInterface> list;
			try {
				list = NetworkInterface.getNetworkInterfaces();
				while(list.hasMoreElements()){
					NetworkInterface iface = (NetworkInterface) list.nextElement();

					//BUILD HASH OF DEVICE NAMES AND LONG NAMES				
					String prefix = iface.getName().toString().substring(0,2);
					if (!prefix.equals("lo")) { //ignore loopback? (lo, lo0)

						//USE LOOKUP NAME FOR CERTAIN INTERFACES
						if(_interface_lookup.containsKey(iface.getName())){
							_devices.put(iface.getName(), (String) _interface_lookup.get(iface.getName()));

							//OTHERWISE USE WHATEVER THE DEVICE TOLD US
						} else {
							_devices.put(iface.getName(),iface.getDisplayName());
						}
					}
				}	
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		Log.debug("["+this.getClass().getName()+"] Found network devices: " + this.toString());

	}

    public String toString() {
    	return _devices.toString();
    }

    public String getDeviceCurrent() {
    	return _current_device;
    }

    public HashMap<String, String> getDevices() {
    	return _devices;
    }
}