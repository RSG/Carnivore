/*******************************************************************************

	$Id: JScreenMenu.java,v 1.10 2005/02/25 04:01:33 steve Exp $
	
	File:		JScreenMenu.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2002-2004 Steve Roy <sroy@roydesign.net>
	
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	12/30/02	Created this file - Steve
	02/26/04    Merged into MRJ Adapter - Steve
	04/16/04    Renamed from JMenu to JScreenMenu - Steve

*******************************************************************************/

package net.roydesign.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JSeparator;

import net.roydesign.mac.MRJAdapter;

/**
 * <p>A subclass of <code>javax.swing.JMenu</code> that adds the logistics
 * needed to make menu bars conform to the Mac OS screen menu bar requirements
 * without sacrificing the usual way of presenting menu bars on other platforms.
 * See the class <code>JScreenMenuBar</code> for more details.</p>
 *
 * <p>Additionally, if an instance of this class is populated with instances
 * of <code>JScreenMenuItem</code>, or simply other <code>JScreenMenus</code>,
 * then the menu will automatically disable itself when all its items are
 * disabled, and automatically enable itself back when one of the items becomes
 * enabled. This is the kind of behavior expected by users from a quality
 * desktop application and this class takes care of the work for you.</p>
 *
 * <p>Finally, this class fixes a bug in the Aqua L&F of the Java 1.3.1 VM
 * from Apple where the disabling the menu before the parent frame is shown
 * has no effect for Swing menus when using the screen menu bar.</p>
 *
 * @see JScreenMenuBar
 * 
 * @version MRJ Adapter 1.0.9
 */
public class JScreenMenu extends javax.swing.JMenu implements PropertyChangeListener
{
	/**
	 * The component listener that works around the bug on Mac OS X with
	 * Java 1.3.1 where disabling the menu before the parent frame is shown
	 * has no effect for Swing menus when using the screen menu bar.
	 */
	private static ComponentAdapter initialStateSetterMRJ3 = new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				Component comp = e.getComponent();
				comp.removeComponentListener(this);
				if (!comp.isEnabled())
				{
					comp.setEnabled(true);
					comp.setEnabled(false);
				}
			}
		};
	
	/**
	 * The user frames of this menu.
	 */
	private Vector userFrames;
	
	/**
	 * Construct a menu.
	 */
	public JScreenMenu()
	{
		super("");
		init();
	}
	
	/**
	 * Construct a menu with the given text as the title.
	 * @param text the menu title
	 */
	public JScreenMenu(String text)
	{
		super(text);
		init();
	}
	
	/**
	 * Initialize the menu. We use this method to work around the
	 * Mac OS X problem with Swing menus in the screen menu bar.
	 */
	private void init()
	{
		if (MRJAdapter.isSwingUsingScreenMenuBar() &&
			MRJAdapter.mrjVersion >= 3.0f && MRJAdapter.mrjVersion < 4.0f)
		{
			// This is a workaround for a problem on Mac OS X where
			// setEnabled(false) has no effect when called before the
			// menubar has been shown if the value of
			// com.apple.macos.useScreenMenuBar is set to true
			addComponentListener(initialStateSetterMRJ3);
		}
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 * @param menuItem the menu item to be added
	 * @return the menu item added
	 */
	public javax.swing.JMenuItem add(javax.swing.JMenuItem menuItem)
	{
		menuItem.addPropertyChangeListener(this);
		return super.add(menuItem);
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 * @param comp the component to be added
	 * @return the component added
	 */
	public Component add(Component comp)
	{
		comp.addPropertyChangeListener(this);
		return super.add(comp);
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 * @param comp the component to be added
	 * @param index the index where to insert the component
	 * @return the component added
	 */
	public Component add(Component comp, int index)
	{
		comp.addPropertyChangeListener(this);
		return super.add(comp, index);
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 * @param menuItem the menu item to be removed
	 */
	public void remove(javax.swing.JMenuItem menuItem)
	{
		menuItem.removePropertyChangeListener(this);
		super.remove(menuItem);
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 * @param index the index of the menu item to be removed
	 */
	public void remove(int index)
	{
		getItem(index).removePropertyChangeListener(this);
		super.remove(index);
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 * @param comp the component to be removed
	 */
	public void remove(Component comp)
	{
		comp.removePropertyChangeListener(this);
		super.remove(comp);
	}
	
	/**
	 * This method is overriden to support automatic enabling and
	 * disabling.
	 */
	public void removeAll()
	{
		int n = getMenuComponentCount();
		for (int i = 0; i < n; i++)
			getMenuComponent(i).removePropertyChangeListener(this);
		super.removeAll();
	}
	
	/**
	 * This method is overriden to disable or hide the items that don't
	 * belong in the menu for the parent frame.
	 */
	public void addNotify()
	{
		// Get the parent frame
		JFrame f = getParentFrame();
		
		// Loop through all menu items and check if the frame
		// makes any use of each one
		boolean enabled = false;
		boolean hasSeparator = true;
		int n = getMenuComponentCount();
		for (int i = n - 1; i >= 0; i--)
		{
			Component comp = getMenuComponent(i);
			if (comp instanceof JSeparator)
			{
				if (hasSeparator)
				{
					comp.setVisible(false); /** @todo With MRJ 2.x (Swing 1.1.1), the space is not reclaimed */
					continue;
				}
				hasSeparator = true;
			}
			else if (comp instanceof JScreenMenuItem)
			{
				JScreenMenuItem mi = (JScreenMenuItem)comp;
				Action a = mi.getAction();
				if ((a != null && a instanceof AbstractScreenAction && !((AbstractScreenAction)a).isUsedBy(f)) ||
					!mi.isUsedBy(f))
				{
					if (MRJAdapter.isSwingUsingScreenMenuBar())
					{
						mi.setEnabled(false);
						hasSeparator = false;
					}
					else
					{
						mi.setVisible(false); /** @todo With MRJ 2.x (Swing 1.1.1), the space is not reclaimed */
					}
				}
				else
				{
					hasSeparator = false;
				}
			}
			else if (comp instanceof JScreenMenu)
			{
				JScreenMenu m = (JScreenMenu)comp;
				if (!m.isUsedBy(f))
				{
					if (MRJAdapter.isSwingUsingScreenMenuBar())
						hasSeparator = false;
				}
				else
				{
					hasSeparator = false;
				}
				m.addNotify();
			}
			else
			{
				hasSeparator = false;
			}
			if (comp.isVisible() && comp.isEnabled() && !(comp instanceof JSeparator))
				enabled = true;
		}
		
		// Disable the menu if all its items are invisible or disabled
		if (!enabled)
			setEnabled(false);
		
		super.addNotify();
	}
	
	/**
	 * Add the given <code>JFrame</code> subclass as a user
	 * of the menu. When a menu has no user frames, then all
	 * frames get the menu.
	 * @param frameClass the <code>JFrame</code> subclass
	 */
	public synchronized void addUserFrame(Class frameClass)
	{
		if (userFrames == null)
			userFrames = new Vector();
		userFrames.addElement(frameClass);
	}
	
	/**
	 * Remove the given <code>JFrame</code> subclass from the users
	 * of the menu.
	 * @param frameClass the <code>JFrame</code> subclass
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
	public boolean isUsedBy(JFrame frame)
	{
		return userFrames == null || userFrames.contains(frame.getClass());
	}
	
	/**
	 * Get the parent frame of the menu.
	 * @return the parent <code>JFrame</code>
	 */
	protected JFrame getParentFrame()
	{
		Component comp = getParent();
		while (comp != null && !(comp instanceof JFrame))
			comp = ((Container)comp).getParent();
		return (JFrame)comp;
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
				int n = getMenuComponentCount();
				for (int i = 0; i < n; i++)
				{
					Component comp = getMenuComponent(i);
					if (comp.isVisible() && comp.isEnabled() && !(comp instanceof JSeparator))
						return;
				}
				setEnabled(false); /** @todo With MRJ 3.x, the UI is not repainted */
			}
		}
	}
}
