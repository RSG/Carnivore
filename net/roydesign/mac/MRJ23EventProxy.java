/*******************************************************************************

	$Id: MRJ23EventProxy.java,v 1.11 2005/02/25 04:01:33 steve Exp $
	
	File:		MRJ23EventProxy.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>

	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>

	Change History:
	03/31/03	Created this file - Steve
	08/31/03	Added handling of the Reopen Application event - Steve
	09/29/03	Changed constructor not to enable the Preferences item - Steve

*******************************************************************************/

package net.roydesign.mac;

import java.io.File;

import net.roydesign.event.ApplicationEvent;

import com.apple.mrj.MRJAboutHandler;
import com.apple.mrj.MRJApplicationUtils;
import com.apple.mrj.MRJOpenApplicationHandler;
import com.apple.mrj.MRJOpenDocumentHandler;
import com.apple.mrj.MRJPrefsHandler;
import com.apple.mrj.MRJPrintDocumentHandler;
import com.apple.mrj.MRJQuitHandler;

/**
 * Implementation of an application event proxy which supports Mac OS
 * virtual machines that use MRJToolkit as their integration mechanism.
 * This corresponds to MRJ 2 and 3 which implement Java 1.1 and 1.3.1
 * respectively.
 * @see MRJEventProxy
 * 
 * @version MRJ Adapter 1.0.9
 */
class MRJ23EventProxy extends MRJEventProxy
{
	/**
	 * The Apple event class identifying required Apple events that all
	 * applications must support
	 */
	private static final int kCoreEventClass = 0x61657674; // 'aevt'

	/**
	 * The Apple event ID for the Preferences menu item on Mac OS X.
	 */
	private static final int kPreferencesItem = 0x70726566; // 'pref'

	/**
	 * The Apple event ID for the Reopen Application event on Mac OS
	 * and Mac OS X.
	 */
	private static final int kReopenApplicationEvent = 0x72617070; // 'rapp'

	/**
	 * The single instance of MRJ23EventProxy.
	 */
	private static MRJ23EventProxy instance;

	/**
	 * The generic reference to the preferences handler. With MRJ 3.0 and 3.1,
	 * this is a reference to an <code>AppleEventHandlerThunk</code>, to prevent
	 * it from being garbage collected. With MRJ 3.2 and up, this is an
	 * <code>MRJPrefsHandler</code>, and we need it to fake the
	 * <code>setEnabled()</code> method.
	 */
	private Object preferencesHandler;

	/**
	 * The generic reference to the reopen application handler. This is a
	 * reference to an <code>AppleEventHandlerThunk</code>, to prevent
	 * it from being garbage collected.
	 */
	private Object reopenApplicationHandler;

	/**
	 * Whether the Preferences menu item is enabled or not.
	 */
	private boolean preferencesEnabled = false;

	/**
	 * Get the single instance of this class.
	 * @return the single instance of <code>MRJ23EventProxy</code>
	 */
	public static MRJ23EventProxy getInstance()
	{
		if (instance == null)
			instance = new MRJ23EventProxy();
		return instance;
	}

	/**
	 * Construct an MRJ 2/3 event proxy.
	 */
	private MRJ23EventProxy()
	{
		Handler h = new Handler();
		MRJApplicationUtils.registerAboutHandler(h);
		MRJApplicationUtils.registerOpenApplicationHandler(h);
		MRJApplicationUtils.registerOpenDocumentHandler(h);
		MRJApplicationUtils.registerPrintDocumentHandler(h);
		MRJApplicationUtils.registerQuitHandler(h);
		if (MRJAdapter.mrjVersion >= 3.2f)
		{
			preferencesHandler = new MRJPrefsHandler()
				{
					public void handlePrefs()
					{
						fireMenuEvent(ApplicationEvent.PREFERENCES);
					}
				};
		}
		/*else if (MRJAdapter.mrjVersion >= 3.0f)
		{
			preferencesHandler = new JD3AppleEventHandlerThunk(new AppleEventHandler()
				{
					public short handleEvent(int event, int reply, int refcon)
					{
						new Thread()
							{
								public void run()
								{
									fireMenuEvent(ApplicationEvent.PREFERENCES);
								}
							}.start();
						return 0;
					}
				});
			JD3CarbonFunctions.AEInstallEventHandler(kCoreEventClass, kPreferencesItem,
				((JD3AppleEventHandlerThunk)preferencesHandler).getProc(), 0, false);
		}*/
		if (MRJAdapter.mrjVersion >= 2.1f)
		{
			AppleEventHandler aeh = new AppleEventHandler()
				{
					public short handleEvent(int event, int reply, int refcon)
					{
						new Thread()
							{
								public void run()
								{
									fireApplicationEvent(ApplicationEvent.REOPEN_APPLICATION);
								}
							}.start();
						return 0;
					}
				};
			/*if (MRJAdapter.mrjVersion >= 3.0f)
			{
				reopenApplicationHandler = new JD3AppleEventHandlerThunk(aeh);
				JD3CarbonFunctions.AEInstallEventHandler(kCoreEventClass, kReopenApplicationEvent,
					((JD3AppleEventHandlerThunk)reopenApplicationHandler).getProc(), 0, false);
			}
			else
			{
				reopenApplicationHandler = new JD2AppleEventHandlerThunk(aeh);
				JD2AppleEventFunctions.AEInstallEventHandler(kCoreEventClass, kReopenApplicationEvent,
					((JD2AppleEventHandlerThunk)reopenApplicationHandler).getProc(), 0, false);
			}*/
		}
		else
		{
			/** @todo Did the OS support this at all with MRJ 1.5 and 2.0? */
		}
	}

	/**
	 * Get whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS, this method always returns false.
	 * @return whether the Preferences menu item is enabled
	 */
	public boolean isPreferencesEnabled()
	{
		return preferencesEnabled;
	}

	/**
	 * Set whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS, this method does nothing.
	 * @param enabled whether the menu item is enabled
	 */
	public void setPreferencesEnabled(boolean enabled)
	{
		if (enabled != preferencesEnabled)
		{
			if (MRJAdapter.mrjVersion >= 3.2f)
			{
				// This can be simulated because it's the register() call
				// that enables the menu item and passing null disables it
				if (enabled)
					MRJApplicationUtils.registerPrefsHandler((MRJPrefsHandler)preferencesHandler);
				else
					MRJApplicationUtils.registerPrefsHandler(null);
			}
			else if (MRJAdapter.mrjVersion >= 3.0f)
			{
				if (enabled)
					JD3CarbonFunctions.EnableMenuCommand(0, kPreferencesItem);
				else
					JD3CarbonFunctions.DisableMenuCommand(0, kPreferencesItem);
			}
			preferencesEnabled = enabled;
		}
	}

	/**
	 * This class implements the listener that handles native events
	 * which it then relays to our application using our unified
	 * interface.
	 */
	private class Handler implements MRJAboutHandler, MRJOpenApplicationHandler,
		MRJOpenDocumentHandler, MRJPrintDocumentHandler, MRJQuitHandler
	{
		public void handleAbout()
		{
			fireMenuEvent(ApplicationEvent.ABOUT);
		}

		public void handleOpenApplication()
		{
			fireApplicationEvent(ApplicationEvent.OPEN_APPLICATION);
		}

		public void handleQuit()
		{
			fireApplicationEvent(ApplicationEvent.QUIT_APPLICATION);

			// MRJ 3 automatically quits, contrary to MRJ 2 and 4, so let's
			// apply the workaround, per Apple:
			// http://developer.apple.com/qa/qa2001/qa1187.html
			if (MRJAdapter.mrjVersion >= 3.0f)
				throw new IllegalStateException();
		}

		public void handleOpenFile(File file)
		{
			fireDocumentEvent(ApplicationEvent.OPEN_DOCUMENT, file);
		}

		public void handlePrintFile(File file)
		{
			fireDocumentEvent(ApplicationEvent.PRINT_DOCUMENT, file);
		}
	}
}
