/*******************************************************************************

	$Id: MRJ4EventProxy.java,v 1.9 2005/02/25 04:01:33 steve Exp $
	
	File:		MRJ4EventProxy.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.
	
	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	03/31/03	Created this file - Steve
	08/27/03	Added handling of the new Reopen Application event - Steve
	09/11/03	Added the setHandled(true) calls in the event handlers - Steve
	09/29/03	Removed the setHandled(true) call in Handler.handleQuit()
				because it was causing the VM to call System.exit(), changed
				constructor not to enable the Preferences item - Steve

*******************************************************************************/

package net.roydesign.mac;

import java.io.File;

import net.roydesign.event.ApplicationEvent;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;

/**
 * Implementation of an application event proxy which supports Mac OS
 * virtual machines that use com.apple.eawt as their integration mechanism.
 * This corresponds to MRJ 4 and above which implements Java 1.4.1 and above.
 * @see MRJEventProxy
 * 
 * @version MRJ Adapter 1.0.9
 */
class MRJ4EventProxy extends MRJEventProxy
{
	/**
	 * The single instance of MRJ4EventProxy.
	 */
	private static MRJ4EventProxy instance;
	
	/**
	 * The <code>com.apple.eawt.Application</code> that we get the
	 * native MRJ events from.
	 */
	private Application application;
	
	/**
	 * Get the single instance of this class.
	 * @return the single instance of <code>MRJ4EventProxy</code>
	 */
	public static MRJ4EventProxy getInstance()
	{
		if (instance == null)
			instance = new MRJ4EventProxy();
		return instance;
	}
	
	/**
	 * Construct an MRJ 4 event proxy.
	 */
	private MRJ4EventProxy()
	{
		application = new Application();
		application.addApplicationListener(new Handler());
	}
	
	/**
	 * Get whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS, this method always returns false.
	 * @return whether the Preferences menu item is enabled
	 */
	public boolean isPreferencesEnabled()
	{
		return application.getEnabledPreferencesMenu();
	}
	
	/**
	 * Set whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS, this method does nothing.
	 * @param enabled whether the menu item is enabled
	 */
	public void setPreferencesEnabled(boolean enabled)
	{
		if (enabled != application.getEnabledPreferencesMenu())
			application.setEnabledPreferencesMenu(enabled);
	}
	
	/**
	 * This class implements the listener that handles native events
	 * which it then relays to MRJ Adapter using our unified interface.
	 */
	private class Handler extends ApplicationAdapter
	{
		public void handleAbout(com.apple.eawt.ApplicationEvent e)
		{
			fireMenuEvent(ApplicationEvent.ABOUT);
			e.setHandled(true);
		}
		
		public void handlePreferences(com.apple.eawt.ApplicationEvent e)
		{
			fireMenuEvent(ApplicationEvent.PREFERENCES);
			e.setHandled(true);
		}
		
		public void handleOpenApplication(com.apple.eawt.ApplicationEvent e)
		{
			fireApplicationEvent(ApplicationEvent.OPEN_APPLICATION);
			e.setHandled(true);
		}
		
		public void handleReOpenApplication(com.apple.eawt.ApplicationEvent e)
		{
			fireApplicationEvent(ApplicationEvent.REOPEN_APPLICATION);
			e.setHandled(true);
		}
		
		public void handleQuit(com.apple.eawt.ApplicationEvent e)
		{
			fireApplicationEvent(ApplicationEvent.QUIT_APPLICATION);
		}
		
		public void handleOpenFile(com.apple.eawt.ApplicationEvent e)
		{
			fireDocumentEvent(ApplicationEvent.OPEN_DOCUMENT, new File(e.getFilename()));
			e.setHandled(true);
		}
		
		public void handlePrintFile(com.apple.eawt.ApplicationEvent e)
		{
			fireDocumentEvent(ApplicationEvent.PRINT_DOCUMENT, new File(e.getFilename()));
			e.setHandled(true);
		}
	}
}
