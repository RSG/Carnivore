/*******************************************************************************

	$Id: QuitJMenuItem.java,v 1.9 2005/02/25 04:01:33 steve Exp $
	
	File:		QuitJMenuItem.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.
	
	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	01/31/03	Created this file - Steve

*******************************************************************************/

package net.roydesign.app;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.roydesign.mac.MRJAdapter;

/**
 * This is the Swing implementation of the Quit menu item.
 * <p>
 * On Mac OS X, this menu item is always automatically included in the menu
 * bar of the application. On other platforms, it never is. You can find out
 * at runtime if the menu item is automatically included with the
 * <code>isAutomaticallyPresent()</code> method and then add it yourself if
 * it isn't. This will make your code cross-platform while letting the
 * application do the right thing for the current platform.
 * <p>
 * In the case where the Quit menu item is automatically included, this menu
 * item is really just a placeholder for the actual native menu item, passing
 * off operations to and from the native menu item where possible. Of course,
 * when this is the case, not all methods of this class will be functional.
 * However, there is no harm in calling dysfunctional methods, other than
 * your user interface not matching your requests.
 * <p>
 * The methods that work on all platforms are the following.
 * <ul>
 * <li>addActionListener</li>
 * <li>removeActionListener</li>
 * <li>setAction (only making the action the listener will actually work on
 * all platforms)</li>
 * </ul>
 * 
 * @version MRJ Adapter 1.0.9
 */
public class QuitJMenuItem extends JMenuItem
{
	/**
	 * Construct a Quit menu item. This method is package private so only
	 * the <code>Application</code> class can create a Quit menu item.
	 * @param application the application instance using this item
	 */
	QuitJMenuItem(Application application)
	{
		super("Quit");
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		String appName = application.getName();
		if (MRJAdapter.mrjVersion >= 3.0f && appName != null)
			setText("Quit " + appName);
	}
	
	
	/**
	 * Add an action listener to the menu item.
	 * @param l the action listener to be added
	 */
	public void addActionListener(ActionListener l)
	{
		MRJAdapter.addQuitApplicationListener(l, this);
		super.addActionListener(l);
	}
	
	/**
	 * Remove an action listener from the menu item.
	 * @param l the action listener to be removed
	 */
	public void removeActionListener(ActionListener l)
	{
		MRJAdapter.removeQuitApplicationListener(l);
		super.removeActionListener(l);
	}
	
	/**
	 * Get whether this menu item is automatically present in the menu bar
	 * of the current underlying platform.
	 * @return whether this menu item is automatically present
	 */
	public static boolean isAutomaticallyPresent()
	{
		return MRJAdapter.isQuitAutomaticallyPresent();
	}
}
