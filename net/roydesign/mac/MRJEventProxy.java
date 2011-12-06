/*******************************************************************************

	$Id: MRJEventProxy.java,v 1.11 2005/02/25 04:01:33 steve Exp $
	
	File:		MRJEventProxy.java
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
	09/29/03	Modified addPreferencesListener() and removePreferencesListener()
				to automatically enable and disable the Preferences item - Steve
	11/25/03    Added support for action commands - Steve

*******************************************************************************/

package net.roydesign.mac;

import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractButton;

import net.roydesign.event.ApplicationEvent;

/**
 * Implementation of an abstract application event proxy that is the
 * base class of <code>MRJ4EventProxy</code> and <code>MRJ23EventProxy</code>.
 * The role of the base class is to collect references to the action
 * listeners and to dispatch action events to them. The subclasses are
 * responsible for receiving the events native to MRJ and relaying
 * them to the base class via one of the <code>fireXXX</code> methods.
 * 
 * @see MRJ23EventProcy
 * @see MRJ4EventProcy
 * 
 * @version MRJ Adapter 1.0.9
 */
abstract class MRJEventProxy
{
	/**
	 * The hash key used to identify the About listeners.
	 */
	private final String ABOUT_KEY = "about";

	/**
	 * The hash key used to identify the Preferences listeners.
	 */
	private final String PREFERENCES_KEY = "preferences";

	/**
	 * The hash key used to identify the Open Application listeners.
	 */
	private final String OPEN_APPLICATION_KEY = "open application";

	/**
	 * The hash key used to identify the Quit Application listeners.
	 */
	private final String QUIT_APPLICATION_KEY = "quit application";

	/**
	 * The hash key used to identify the Open Document listeners.
	 */
	private final String OPEN_DOCUMENT_KEY = "open document";

	/**
	 * The hash key used to identify the Print Document listeners.
	 */
	private final String PRINT_DOCUMENT_KEY = "print document";

	/**
	 * The hash key used to identify the Reopen Application listeners.
	 */
	private final String REOPEN_APPLICATION_KEY = "reopen application";

	/**
	 * The various action listeners attached to each event as identified by
	 * the keys above. The hash table contains <code>ListenerInfo</code>
	 * objects.
	 */
	private Hashtable listenerLists = new Hashtable();

	/**
	 * This class encapsulates a listener and the source object to use when
	 * firing the event to that listener.
	 */
	private class ListenerInfo
	{
		ActionListener actionListener;
		Object source;
	}

	/**
	 * Add an About action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to an <code>MRJEvent</code>.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public void addAboutListener(ActionListener l, Object source)
	{
		addListener(l, source, ABOUT_KEY);
	}

	/**
	 * Remove an About action listener.
	 * @param l the action listener
	 */
	public void removeAboutListener(ActionListener l)
	{
		removeListener(l, ABOUT_KEY);
	}

	/**
	 * Add a Preferences action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to an <code>MRJEvent</code>.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public void addPreferencesListener(ActionListener l, Object source)
	{
		// Enable the menu item if this is the first listener
		if (listenerLists.get(PREFERENCES_KEY) == null)
			setPreferencesEnabled(true);

		addListener(l, source, PREFERENCES_KEY);
	}

	/**
	 * Remove a Preferences action listener.
	 * @param l the action listener
	 */
	public void removePreferencesListener(ActionListener l)
	{
		removeListener(l, PREFERENCES_KEY);

		// Disable the menu item if there is no more listener
		if (listenerLists.get(PREFERENCES_KEY) == null)
			setPreferencesEnabled(false);
	}

	/**
	 * Add an Open Application action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to an <code>MRJEvent</code>.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public void addOpenApplicationListener(ActionListener l, Object source)
	{
		addListener(l, source, OPEN_APPLICATION_KEY);
	}

	/**
	 * Remove an Open Application action listener.
	 * @param l the action listener
	 */
	public void removeOpenApplicationListener(ActionListener l)
	{
		removeListener(l, OPEN_APPLICATION_KEY);
	}

	/**
	 * Add a Reopen Application action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to an <code>MRJEvent</code>.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public void addReopenApplicationListener(ActionListener l, Object source)
	{
		addListener(l, source, REOPEN_APPLICATION_KEY);
	}

	/**
	 * Remove a Reopen Application action listener.
	 * @param l the action listener
	 */
	public void removeReopenApplicationListener(ActionListener l)
	{
		removeListener(l, REOPEN_APPLICATION_KEY);
	}

	/**
	 * Add a Quit Application action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to an <code>MRJEvent</code>.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public void addQuitApplicationListener(ActionListener l, Object source)
	{
		addListener(l, source, QUIT_APPLICATION_KEY);
	}

	/**
	 * Remove a Quit Application action listener.
	 * @param l the action listener
	 */
	public void removeQuitApplicationListener(ActionListener l)
	{
		removeListener(l, QUIT_APPLICATION_KEY);
	}

	/**
	 * Add an Open Document action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to an <code>MRJEvent</code>
	 * or to an <code>MRJDocumentEvent</code> which allows to get a reference
	 * to the file associated with the event.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public void addOpenDocumentListener(ActionListener l, Object source)
	{
		addListener(l, source, OPEN_DOCUMENT_KEY);
	}

	/**
	 * Remove an Open Document action listener.
	 * @param l the action listener
	 */
	public void removeOpenDocumentListener(ActionListener l)
	{
		removeListener(l, OPEN_DOCUMENT_KEY);
	}

	/**
	 * Add a Print Document action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to an <code>MRJEvent</code>
	 * or to an <code>MRJDocumentEvent</code> which allows to get a reference
	 * to the file associated with the event.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public void addPrintDocumentListener(ActionListener l, Object source)
	{
		addListener(l, source, PRINT_DOCUMENT_KEY);
	}

	/**
	 * Remove a Print Document action listener.
	 * @param l the action listener
	 */
	public void removePrintDocumentListener(ActionListener l)
	{
		removeListener(l, PRINT_DOCUMENT_KEY);
	}

	/**
	 * Register the given action listener to receive action events from
	 * the given source. They are added to the hash table and associated
	 * with the given key.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 * @param key the key to associate the listener with
	 */
	private void addListener(ActionListener l, Object source, String key)
	{
		// Get the hash table containing the listeners for the given key
		Hashtable ht = (Hashtable)listenerLists.get(key);
		if (ht == null)
		{
			// If there is none yet, create it
			ht = new Hashtable(1); // In most cases, 1 will be enough
			listenerLists.put(key, ht);
		}

		// Don't allow the same listener to be added twice
		String name = l.getClass().getName();
		if (ht.containsKey(name))
			return;

		// Encapsulate the listener info and add it to the hash table
		ListenerInfo li = new ListenerInfo();
		li.actionListener = l;
		li.source = (source != null ? source : this);
		ht.put(name, li);
	}

	/**
	 * Deregister the given action listener to receive action events
	 * of the kind associated with the given key.
	 * @param l the action listener
	 * @param key the key to dissociate the listener with
	 */
	private void removeListener(ActionListener l, String key)
	{
		Hashtable ht = (Hashtable)listenerLists.get(key);
		String name = l.getClass().getName();
		if (ht != null && ht.remove(name) != null && ht.isEmpty())
			listenerLists.remove(key);
	}

	/**
	 * Get whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS, this method always returns false.
	 * @return whether the Preferences menu item is enabled
	 */
	public abstract boolean isPreferencesEnabled();

	/**
	 * Set whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS, this method does nothing.
	 * @param enabled whether the menu item is enabled
	 */
	public abstract void setPreferencesEnabled(boolean enabled);

	/**
	 * Fire a menu event of the given type, as designated by the
	 * types <code>ApplicationEvent.ABOUT</code> or
	 * <code>ApplicationEvent.PREFERENCES</code>.
	 * @param type the type of the event
	 */
	protected void fireMenuEvent(int type)
	{
		Hashtable ht = null;
		switch (type)
		{
			case ApplicationEvent.ABOUT:
				ht = (Hashtable)listenerLists.get(ABOUT_KEY);
				break;
			case ApplicationEvent.PREFERENCES:
				ht = (Hashtable)listenerLists.get(PREFERENCES_KEY);
				break;
			default:
				throw new Error("unknown event type");
		}
		if (ht == null)
			return;
		Enumeration enumm = ht.elements();
		while (enumm.hasMoreElements())
		{
			ListenerInfo li = (ListenerInfo)enumm.nextElement();
			String cmd = null;
			if (li.source instanceof MenuItem)
				cmd = ((MenuItem)li.source).getActionCommand();
			else if (li.source instanceof AbstractButton)
				cmd = ((AbstractButton)li.source).getActionCommand();
			ApplicationEvent e = new ApplicationEvent(li.source, type, cmd);
			li.actionListener.actionPerformed(e);
		}
	}

	/**
	 * Fire a document event of the given type, as designated by the
	 * types <code>ApplicationEvent.OPEN_DOCUMENT</code> or
	 * <code>ApplicationEvent.PRINT_DOCUMENT</code>.
	 * @param type the type of the event
	 * @param file the file to associate with the event
	 */
	protected void fireDocumentEvent(int type, File file)
	{
		Hashtable ht = null;
		switch (type)
		{
			case ApplicationEvent.OPEN_DOCUMENT:
				ht = (Hashtable)listenerLists.get(OPEN_DOCUMENT_KEY);
				break;
			case ApplicationEvent.PRINT_DOCUMENT:
				ht = (Hashtable)listenerLists.get(PRINT_DOCUMENT_KEY);
				break;
			default:
				throw new Error("unknown event type");
		}
		if (ht == null)
			return;
		Enumeration enumm = ht.elements();
		while (enumm.hasMoreElements())
		{
			ListenerInfo li = (ListenerInfo)enumm.nextElement();
			ApplicationEvent e = new ApplicationEvent(li.source, type, file);
			li.actionListener.actionPerformed(e);
		}
	}

	/**
	 * Fire an application event of the given type, as designated by the
	 * types <code>ApplicationEvent.OPEN_APPLICATION</code>,
	 * <code>ApplicationEvent.REOPEN_APPLICATION</code> or
	 * <code>ApplicationEvent.QUIT_APPLICATION</code>.
	 * @param type the type of the event
	 */
	protected void fireApplicationEvent(int type)
	{
		Hashtable ht = null;
		switch (type)
		{
			case ApplicationEvent.OPEN_APPLICATION:
				ht = (Hashtable)listenerLists.get(OPEN_APPLICATION_KEY);
				break;
			case ApplicationEvent.REOPEN_APPLICATION:
				ht = (Hashtable)listenerLists.get(REOPEN_APPLICATION_KEY);
				break;
			case ApplicationEvent.QUIT_APPLICATION:
				ht = (Hashtable)listenerLists.get(QUIT_APPLICATION_KEY);
				if (ht == null)
				{
					System.exit(0);
					return;
				}
				break;
			default:
				throw new Error("unknown event type");
		}
		if (ht == null)
			return;
		Enumeration enumm = ht.elements();
		while (enumm.hasMoreElements())
		{
			ListenerInfo li = (ListenerInfo)enumm.nextElement();
			ApplicationEvent e = new ApplicationEvent(li.source, type);
			li.actionListener.actionPerformed(e);
		}
	}
}
