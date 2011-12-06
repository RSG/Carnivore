/*******************************************************************************

	$Id: JD3CarbonFunctions.java,v 1.9 2005/02/25 04:01:33 steve Exp $
	
	File:		JD3CarbonFunctions.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.
	
	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	02/05/03	Created this file - Steve
	03/31/03	Renamed from CarbonFunctions to JD3CarbonFunctions - Steve

*******************************************************************************/

package net.roydesign.mac;

//import com.apple.mrj.jdirect.Linker;

/**
 * A JDirect 3 interface to some Carbon functions we use to implement
 * functionality for the Preferences menu item and the Reopen Application event
 * when running in MRJ 3.0 and MRJ 3.1 on Mac OS X. Those two versions of MRJ
 * didn't have built-in support for this item and this event.
 * 
 * @version MRJ Adapter 1.0.9
 */
class JD3CarbonFunctions
{
	public static final String JDirect_MacOSX =
		"/System/Library/Frameworks/Carbon.framework/Versions/A/Carbon";
	
	//private static final Object linkage = new Linker(JD3CarbonFunctions.class);
	
	public static native void EnableMenuCommand(int menu, int commandID);
	
	public static native void DisableMenuCommand(int menu, int commandID);
	
	public static native int AEInstallEventHandler(int eventClass,
		int eventID, int handler, int refcon, boolean isSysHandler);
}
