/*******************************************************************************

	$Id: ScreenMenu.java,v 1.10 2005/02/25 04:01:33 steve Exp $
	
	File:		ScreenMenu.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2002-2004 Steve Roy <sroy@roydesign.net>
	
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	02/23/03	Created this file - Steve
	02/26/04    Merged into MRJ Adapter - Steve
	04/13/04	Added support for MenuItem Actions in addNotify() - Steve
	04/16/04    Renamed from Menu to ScreenMenu - Steve

*******************************************************************************/

package net.roydesign.ui;

import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.Action;

import net.roydesign.mac.MRJAdapter;

/**
 * <p>A subclass of <code>Menu</code> that adds the logistics needed to make menu
 * bars conform to the Mac OS screen menu bar requirements without sacrificing
 * the usual way of presenting menu bars on other platforms. See the class
 * <code>ScreenMenuBar</code> for more details.</p>
 *
 * <p>Additionally, if an instance of this class is populated with instances
 * of <code>ScreenMenuItem</code>, or simply other <code>ScreenMenus</code>,
 * then the menu will automatically disable itself when all its items are
 * disabled, and automatically enable itself back when one of the items becomes
 * enabled. This is the kind of behavior expected by users from a quality
 * desktop application and this class takes care of the work for you.</p>
 *
 * @see ScreenMenuBar
 * 
 * @version MRJ Adapter 1.0.9
 */
public class ScreenMenu extends java.awt.Menu implements PropertyChangeListener
{
	/**
	 * The user frames of this menu.
	 */
	private Vector userFrames;
	
	/**
	 * Construct a menu.
	 */
	public ScreenMenu()
	{
		super("");
	}
	
	/**
	 * Construct a menu with the given text as the title.
	 * @param text the menu title
	 */
	public ScreenMenu(String text)
	{
		super(text);
	}
	
	/**
	 * Construct a menu with the given text as the title.
	 * @param text the menu title
	 * @param tearOff whether the menu can be torn off or not
	 */
	public ScreenMenu(String text, boolean tearOff)
	{
		super(text, tearOff);
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 * @param menuItem the menu item to be added
	 * @return the menu item added
	 */
	public java.awt.MenuItem add(java.awt.MenuItem menuItem)
	{
		if (menuItem instanceof ScreenMenuItem)
			((ScreenMenuItem)menuItem).addPropertyChangeListener(this);
		return super.add(menuItem);
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 * @param menuItem the menu item to be added
	 * @param index the index where to insert the menu item
	 * @return the component added
	 */
	public void insert(java.awt.MenuItem menuItem, int index)
	{
		if (menuItem instanceof ScreenMenuItem)
			((ScreenMenuItem)menuItem).addPropertyChangeListener(this);
		super.insert(menuItem, index);
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 * @param index the index of the menu item to be removed
	 */
	public void remove(int index)
	{
		java.awt.MenuItem it = getItem(index);
		if (it instanceof ScreenMenuItem)
			((ScreenMenuItem)it).removePropertyChangeListener(this);
		super.remove(index);
	}
	
	/**
	 * This method is overriden to disable or hide the items that don't
	 * belong in the menu for the parent frame.
	 */
	public void addNotify()
	{
		// Get the parent frame
		Frame f = getParentFrame();
		
		// Loop through all menu items and check if the frame
		// makes any use of each one
		boolean enabled = false;
		boolean hasSeparator = true;
		int n = getItemCount();
		for (int i = n - 1; i >= 0; i--)
		{
			java.awt.MenuItem it = getItem(i);
			if (it.getLabel().equals("-"))
			{
				if (hasSeparator)
				{
					remove(i);
					continue;
				}
				hasSeparator = true;
			}
			else if (it instanceof ScreenMenuItem)
			{
				ScreenMenuItem mi = (ScreenMenuItem)it;
				Action a = mi.getAction();
				if ((a != null && a instanceof AbstractScreenAction && !((AbstractScreenAction)a).isUsedBy(f)) ||
					!mi.isUsedBy(f))
				{
					if (MRJAdapter.isAWTUsingScreenMenuBar())
					{
						mi.setEnabled(false);
						hasSeparator = false;
					}
					else
					{
						remove(i);
					}
				}
				else
				{
					hasSeparator = false;
				}
			}
			else if (it instanceof ScreenMenu)
			{
				ScreenMenu m = (ScreenMenu)it;
				if (!m.isUsedBy(f))
				{
					if (MRJAdapter.isAWTUsingScreenMenuBar())
					{
						m.setEnabled(false);
						hasSeparator = false;
					}
					else
					{
						remove(i);
					}
				}
				else
				{
					hasSeparator = false;
				}
			//	m.addNotify(); /** @todo Why was this here? Causes missing menu in 1.4.2 on OS X */
			}
			else
			{
				hasSeparator = false;
			}
			if (it.getParent() != null && it.isEnabled() && !it.getLabel().equals("-"))
				enabled = true;
		}
		
		// Disable the menu if all its items are invisible or disabled
		if (!enabled)
			setEnabled(false);
		
		super.addNotify();
	}
	
	/**
	 * Add the given <code>Frame</code> subclass as a user
	 * of the menu. When a menu has no user frames, then all
	 * frames get the menu.
	 * @param frameClass the <code>Frame</code> subclass
	 */
	public synchronized void addUserFrame(Class frameClass)
	{
		if (userFrames == null)
			userFrames = new Vector();
		userFrames.addElement(frameClass);
	}
	
	/**
	 * Remove the given <code>Frame</code> subclass from the users
	 * of the menu.
	 * @param frameClass the <code>Frame</code> subclass
	 */
	public synchronized void removeUserFrame(Class frameClass)
	{
		if (userFrames == null)
			return;
		userFrames.removeElement(frameClass);
		if (userFrames.size() == 0)
			userFrames = null;
	}
	
	/**
	 * Get whether the menu is used by the given frame instance.
	 * @return whether the menu is used by the given frame
	 */
	public boolean isUsedBy(Frame frame)
	{
		return userFrames == null || userFrames.contains(frame.getClass());
	}
	
	/**
	 * Get the parent frame of the menu.
	 * @return the parent <code>Frame</code>
	 */
	protected Frame getParentFrame()
	{
		MenuContainer cont = getParent();
		while (cont != null && !(cont instanceof Frame))
			cont = ((MenuComponent)cont).getParent();
		return (Frame)cont;
	}
	
	/**
	 * Implementation of the property listener interface. This method
	 * disables the menu when all its items are disabled. Conversely,
	 * it enables the menu when at least one of its items is enabled.
	 * @param e the property change event
	 */
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getPropertyName().equals("enabled"))
		{
			if (((Boolean)e.getNewValue()).booleanValue() == true)
			{
				setEnabled(true); /** @todo With MRJ 3.x, the UI is not repainted */
			}
			else
			{
				int n = getItemCount();
				for (int i = 0; i < n; i++)
				{
					java.awt.MenuItem it = getItem(i);
					if (it.isEnabled() && !it.getLabel().equals("-"))
						return;
				}
				setEnabled(false); /** @todo With MRJ 3.x, the UI is not repainted */
			}
		}
	}
}
