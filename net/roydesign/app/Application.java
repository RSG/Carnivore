/*******************************************************************************

	$Id: Application.java,v 1.10 2005/02/25 04:01:33 steve Exp $
	
	File:		Application.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.
	
	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	01/31/03	Created this file - Steve
	02/04/03	Made getInstance() synchronized, removed the single
				argument version of fireApplicationEvent(), added exception
				comment for Swing related methods - Steve
	02/05/03	Made the default constructor throw an IllegalStateException - Steve
	06/13/03	Added the four frameless menu bar methods - Steve
	08/08/03	Changed implementation to use the MRJAdapter class - Steve
	08/24/03	Made multiple instances returned by the get***MenuItem methods
				when running on a platform other than the Mac - Steve
	08/31/03	Added the addReopenApplicationListener() and
				removeReopenApplicationListener() methods - Steve

*******************************************************************************/

package net.roydesign.app;

import java.awt.MenuBar;
import java.awt.event.ActionListener;

import javax.swing.JMenuBar;

import net.roydesign.mac.MRJAdapter;

/**
 * This is a wrapper for an application instance. It provides a unified API to
 * the different mechanisms used by different platforms to handle menu items
 * provided by the OS as well as some interapplication messages. The About,
 * Preferences and Quit menu items are managed by this class, so that your
 * application doesn't have to go into ugly if/else statements to add proper
 * support for Mac OS platforms. The application messages also managed by this
 * API are Open Application, Open Document and Print Document. See the
 * <code>ApplicationEvent</code> class for more details as to the meaning
 * of these messages.
 * <p>
 * To use this class, simply subclass it or use the
 * <code>Application.getInstance()</code> method. Once you have an application
 * instance, you can get a reference to the About, Preferences or Quit menu
 * item and add event handling just as you normally would with any other menu
 * item. Both Swing and AWT menus are supported. See the <code>MenuItem</code>
 * or <code>JMenuItem</code> subclasses starting with <code>About*</code>,
 * <code>Preferences*</code> or <code>Quit*</code> for more detailed
 * information.
 * <p>
 * Note that since the About, Preferences and Quit menu items are provided by
 * the OS in some cases and not in others, each <code>MenuItem</code> or
 * <code>JMenuItem</code> subclass includes an
 * <code>isAutomaticallyPresent()</code> method to allow clean cross-platform
 * development.
 * <p>
 * To handle application messages, simply implement the <code>ActionListener</code>
 * interface and attach your implementation to your application instance with the
 * appropriate add***Listener() method. When the action listener is called, the
 * event received can be cast to an <code>ApplicationEvent</code> to retrieve more
 * information about the event, including the file object associated with it, if any.
 *
 * @see AboutJMenuItem
 * @see AboutMenuItem
 * @see PreferencesJMenuItem
 * @see PreferencesMenuItem
 * @see QuitJMenuItem
 * @see QuitMenuItem
 * @see net.roydesign.event.ApplicationEvent
 * 
 * @version MRJ Adapter 1.0.9
 */
public class Application
{
	/**
	 * The application instance.
	 */
	private static Application instance;
	
	/**
	 * The name of the application.
	 */
	private String name;
	
	/**
	 * The single instance of About Swing menu item on Mac OS.
	 */
	private AboutJMenuItem macAboutJMenuItem;
	
	/**
	 * The single instance of About AWT menu item on Mac OS.
	 */
	private AboutMenuItem macAboutMenuItem;
	
	/**
	 * The single instance of Preferences Swing menu item on Mac OS.
	 */
	private PreferencesJMenuItem macPreferencesJMenuItem;
	
	/**
	 * The single instance of Preferences AWT menu item on Mac OS.
	 */
	private PreferencesMenuItem macPreferencesMenuItem;
	
	/**
	 * The single instance of Quit Swing menu item on Mac OS.
	 */
	private QuitJMenuItem macQuitJMenuItem;
	
	/**
	 * The single instance of Quit AWT menu item on Mac OS.
	 */
	private QuitMenuItem macQuitMenuItem;
	
	/**
	 * Construct an application instance. Note that only one can ever
	 * be created. Attempting to instantiate more will result in an
	 * <code>IllegalStateException</code> being thrown.
	 */
	protected Application()
	{
		if (instance != null)
			throw new IllegalStateException();
		instance = this;
	}
	
	/**
	 * Get the unique application instance. If it doesn't exist,
	 * this method creates it.
	 * @return the unique application instance
	 */
	public static synchronized Application getInstance()
	{
		if (instance == null)
			new Application();
		return instance;
	}
	
	/**
	 * Set the name of the application.
	 * @param name the name of the application
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Get the name of the application.
	 * @return the name of the application
	 */
	public String getName()
	{
		if (name == null)
			name = System.getProperty("com.apple.mrj.application.apple.menu.about.name");
		return name;
	}
	
	/**
	 * Set the AWT frameless menu bar of the application. This menu bar is
	 * shown when no frame is visible, which is a normal state for a Mac
	 * application. This method has no visible effect on other platforms.
	 * @param menuBar the AWT menu bar to use
	 * @see net.roydesign.mac.MRJAdapter#setFramelessMenuBar
	 */
	public void setFramelessMenuBar(MenuBar menuBar)
	{
		MRJAdapter.setFramelessMenuBar(menuBar);
	}
	
	/**
	 * Get the AWT frameless menu bar of the application.
	 * @return the AWT menu bar
	 * @see net.roydesign.mac.MRJAdapter#getFramelessMenuBar
	 */
	public MenuBar getFramelessMenuBar()
	{
		return MRJAdapter.getFramelessMenuBar();
	}
	
	/**
	 * Set the Swing frameless menu bar of the application. This menu bar is
	 * shown when no frame is visible, which is a normal state for a Mac
	 * application. This method has no visible effect on other platforms.
	 * @param menuBar the Swing menu bar to use
	 * @see net.roydesign.mac.MRJAdapter#setFramelessJMenuBar
	 */
	public void setFramelessJMenuBar(JMenuBar menuBar)
	{
		MRJAdapter.setFramelessJMenuBar(menuBar);
	}
	
	/**
	 * Get the Swing frameless menu bar of the application.
	 * @return the Swing menu bar
	 * @see net.roydesign.mac.MRJAdapter#getFramelessJMenuBar
	 */
	public JMenuBar getFramelessJMenuBar()
	{
		return MRJAdapter.getFramelessJMenuBar();
	}
	
	/**
	 * Get the About item as a Swing menu item.
	 * @return the About Swing menu item
	 * @exception ClassNotFoundException if Swing is not available
	 */
	public AboutJMenuItem getAboutJMenuItem()
	{
		if (MRJAdapter.mrjVersion != -1)
		{
			if (macAboutJMenuItem == null)
				macAboutJMenuItem = new AboutJMenuItem(this);
			return macAboutJMenuItem;
		}
		else
		{
			return new AboutJMenuItem(this);
		}
	}
	
	/**
	 * Get the About item as a AWT menu item.
	 * @return the About AWT menu item
	 */
	public AboutMenuItem getAboutMenuItem()
	{
		if (MRJAdapter.mrjVersion != -1)
		{
			if (macAboutMenuItem == null)
				macAboutMenuItem = new AboutMenuItem(this);
			return macAboutMenuItem;
		}
		else
		{
			return new AboutMenuItem(this);
		}
	}
	
	/**
	 * Get the Preferences item as a Swing menu item.
	 * @return the Preferences Swing menu item
	 * @exception ClassNotFoundException if Swing is not available
	 */
	public PreferencesJMenuItem getPreferencesJMenuItem()
	{
		if (MRJAdapter.mrjVersion >= 3.0)
		{
			if (macPreferencesJMenuItem == null)
				macPreferencesJMenuItem = new PreferencesJMenuItem();
			return macPreferencesJMenuItem;
		}
		else
		{
			return new PreferencesJMenuItem();
		}
	}
	
	/**
	 * Get the Preferences item as a AWT menu item.
	 * @return the Preferences AWT menu item
	 */
	public PreferencesMenuItem getPreferencesMenuItem()
	{
		if (MRJAdapter.mrjVersion >= 3.0)
		{
			if (macPreferencesMenuItem == null)
				macPreferencesMenuItem = new PreferencesMenuItem();
			return macPreferencesMenuItem;
		}
		else
		{
			return new PreferencesMenuItem();
		}
	}
	
	/**
	 * Get the Quit item as a Swing menu item.
	 * @return the Quit Swing menu item
	 * @exception ClassNotFoundException if Swing is not available
	 */
	public QuitJMenuItem getQuitJMenuItem()
	{
		if (MRJAdapter.mrjVersion >= 3.0)
		{
			if (macQuitJMenuItem == null)
				macQuitJMenuItem = new QuitJMenuItem(this);
			return macQuitJMenuItem;
		}
		else
		{
			return new QuitJMenuItem(this);
		}
	}
	
	/**
	 * Get the Quit item as a AWT menu item.
	 * @return the Quit AWT menu item
	 */
	public QuitMenuItem getQuitMenuItem()
	{
		if (MRJAdapter.mrjVersion >= 3.0)
		{
			if (macQuitMenuItem == null)
				macQuitMenuItem = new QuitMenuItem(this);
			return macQuitMenuItem;
		}
		else
		{
			return new QuitMenuItem(this);
		}
	}
	
	/**
	 * Add an Open Application action listener.
	 * @param l the action listener
	 * @see net.roydesign.mac.MRJAdapter#addOpenApplicationListener
	 */
	public void addOpenApplicationListener(ActionListener l)
	{
		MRJAdapter.addOpenApplicationListener(l, this);
	}
	
	/**
	 * Remove an Open Application action listener.
	 * @param l the action listener
	 * @see net.roydesign.mac.MRJAdapter#removeOpenApplicationListener
	 */
	public void removeOpenApplicationListener(ActionListener l)
	{
		MRJAdapter.removeOpenApplicationListener(l);
	}
	
	/**
	 * Add a Reopen Application action listener.
	 * @param l the action listener
	 * @see net.roydesign.mac.MRJAdapter#addReopenApplicationListener
	 */
	public void addReopenApplicationListener(ActionListener l)
	{
		MRJAdapter.addReopenApplicationListener(l, this);
	}
	
	/**
	 * Remove a Reopen Application action listener.
	 * @param l the action listener
	 * @see net.roydesign.mac.MRJAdapter#removeReopenApplicationListener
	 */
	public void removeReopenApplicationListener(ActionListener l)
	{
		MRJAdapter.removeReopenApplicationListener(l);
	}
	
	/**
	 * Add an Open Document action listener.
	 * @param l the action listener
	 * @see net.roydesign.mac.MRJAdapter#addOpenDocumentListener
	 */
	public void addOpenDocumentListener(ActionListener l)
	{
		MRJAdapter.addOpenDocumentListener(l, this);
	}
	
	/**
	 * Remove an Open Document action listener.
	 * @param l the action listener
	 * @see net.roydesign.mac.MRJAdapter#removeOpenDocumentListener
	 */
	public void removeOpenDocumentListener(ActionListener l)
	{
		MRJAdapter.removeOpenDocumentListener(l);
	}
	
	/**
	 * Add a Print Document action listener.
	 * @param l the action listener
	 * @see net.roydesign.mac.MRJAdapter#addPrintDocumentListener
	 */
	public void addPrintDocumentListener(ActionListener l)
	{
		MRJAdapter.addPrintDocumentListener(l, this);
	}
	
	/**
	 * Remove a Print Document action listener.
	 * @param l the action listener
	 * @see net.roydesign.mac.MRJAdapter#removePrintDocumentListener
	 */
	public void removePrintDocumentListener(ActionListener l)
	{
		MRJAdapter.removePrintDocumentListener(l);
	}
}
