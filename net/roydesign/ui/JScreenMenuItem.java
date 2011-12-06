/*******************************************************************************

	$Id: JScreenMenuItem.java,v 1.8 2005/02/25 04:01:33 steve Exp $
	
	File:		JScreenMenuItem.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2002-2004 Steve Roy <sroy@roydesign.net>
	
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	11/26/02	Created this file - Steve
	02/26/04    Merged into MRJ Adapter - Steve
	04/16/04    Renamed from JMenuItem to JScreenMenuItem - Steve

*******************************************************************************/

package net.roydesign.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import net.roydesign.mac.MRJAdapter;

/**
 * <p>A subclass of <code>javax.swing.JMenuItem</code> that adds the logistics
 * needed to make menu bars conform to the Mac OS screen menu bar requirements
 * without sacrificing the usual way of presenting menu bars on other platforms.
 * See the class <code>JScreenMenuBar</code> for more details.</p>
 *
 * <p>As a convenience, this class implements support for the
 * <code>setAction()<code> method for versions of Swing that didn't include it.</p>
 *
 * @see JScreenMenuBar
 * 
 * @version MRJ Adapter 1.0.9
 */
public class JScreenMenuItem extends javax.swing.JMenuItem
{
	/**
	 * The action to be executed by this menu item. This is only used when
	 * running in a version of Java prior to 1.3, where this feature didn't
	 * exist. In 1.3 and up, this functionality is built-in and this is not used.
	 */
	private Action actionBefore13;
	
	/**
	 * The object that listens for property change events coming from
	 * the action attached to the menu item. This is only used when
	 * running in a version of Java prior to 1.3.
	 */
	private PropertyChangeListener actionPropertyChangeListener =
		new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				if (e.getPropertyName().equals("action"))
					configurePropertiesFromAction((Action)e.getNewValue());
			}
		};
	
	/**
	 * The user frames of this menu item.
	 */
	private Vector userFrames;
	
	/**
	 * Construct a menuItem with no set text or icon.
	 */
	public JScreenMenuItem()
	{
		super();
	}
	
	/**
	 * Construct a menuItem with an icon.
	 * @param icon the icon of the menu item
	 */
	public JScreenMenuItem(Icon icon)
	{
		super(icon);
	}
	
	/**
	 * Construct a menuItem with text.
	 * @param text the text of the menu item
	 */
	public JScreenMenuItem(String text)
	{
		super(text);
	}
	
	/**
	 * Construct a menu item whose properties are taken from the 
	 * given action.
	 * @param action the assigned action
	 */
	public JScreenMenuItem(Action action)
	{
		super();
		setAction(action);
	}
	
	/**
	 * Construct a menu item with the supplied text and icon.
	 * @param text the text of the menu item
	 * @param icon the icon of the menu item
	 */
	public JScreenMenuItem(String text, Icon icon)
	{
		super(text, icon);
	}
	
	/**
	 * Construct a menuItem with the specified text and
	 * keyboard mnemonic.
	 * @param text the text of the menu item
	 * @param mnemonic the keyboard mnemonic for the menu item
	 */
	public JScreenMenuItem(String text, int mnemonic)
	{
		super(text, mnemonic);
	}
	
	/**
	 * This method is overriden to add support for actions in
	 * versions of Java prior to 1.3.
	 * @return the action to be performed by this menu item
	 */
	public Action getAction()
	{
		if (MRJAdapter.javaVersion < 1.3f)
			return actionBefore13;
		return super.getAction();
	}
	
	/**
	 * This method is overriden to add support for actions in
	 * versions of Java prior to 1.3.
	 * @param action the action to be performed by this menu item
	 */
	public void setAction(Action action)
	{
		if (MRJAdapter.javaVersion < 1.3f)
			setActionBefore13(action);
		else
			super.setAction(action);
	}
	
	/**
	 * Sets the action to be performed by the menu item. This method
	 * is only used when running in a version of Java prior to 1.3.
	 * @param action the action to be performed by this menu item
	 */
	private void setActionBefore13(Action action)
	{
		Action oldAction = this.actionBefore13;
		if (oldAction == null || !oldAction.equals(action))
		{
			this.actionBefore13 = action;
			if (oldAction != null)
			{
				removeActionListener(oldAction);
				oldAction.removePropertyChangeListener(actionPropertyChangeListener);
			}
			configurePropertiesFromAction(this.actionBefore13);
			if (this.actionBefore13 != null)
			{		
				addActionListener(this.actionBefore13);
				this.actionBefore13.addPropertyChangeListener(actionPropertyChangeListener);
			}
			firePropertyChange("action", oldAction, this.actionBefore13);
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Factory method which sets the ActionEvent source's properties
	 * according to values from the Action instance. This method is
	 * overriden to fix a bug in JDK 1.3.x where the accelerator is
	 * not set.
	 * @param action the assigned action
	 */
	protected void configurePropertiesFromAction(Action action)
	{
		if (MRJAdapter.javaVersion >= 1.3f)
		{
			super.configurePropertiesFromAction(action);
			if (MRJAdapter.javaVersion == 1.3f)
				setAccelerator((action != null ? (KeyStroke)action.getValue(Action.ACCELERATOR_KEY) : null));
		}
		else
		{
			setText((action != null ? (String)action.getValue(Action.NAME) : null));
			setIcon((action != null ? (Icon)action.getValue(Action.SMALL_ICON) : null));
			setAccelerator((action != null ? (KeyStroke)action.getValue(Action.ACCELERATOR_KEY) : null));
			setEnabled((action != null ? action.isEnabled() : true));
			setToolTipText((action != null ? (String)action.getValue(Action.SHORT_DESCRIPTION) : null));	
			if (action != null)
			{
				Integer i = (Integer)action.getValue(Action.MNEMONIC_KEY);
				if (i != null)
					setMnemonic(i.intValue());
			}
		}
	}
	
	/**
	 * Add the given <code>JFrame</code> subclass as a user
	 * of the menu item. When a menu item has no user frames, then all
	 * frames get the menu item.
	 * @param frameClass the <code>JFrame</code> subclass
	 */
	public synchronized void addUserFrame(Class frameClass)
	{
		if (userFrames == null)
			userFrames = new Vector();
		userFrames.addElement(frameClass);
	}
	
	/**
	 * Remove the given <code>JFrame</code> subclass from the user
	 * frames of the menu item.
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
	 * Get whether the menu item is used by the given frame instance.
	 * @return whether the menu item is used by the given frame
	 */
	public boolean isUsedBy(JFrame frame)
	{
		return userFrames == null || userFrames.contains(frame.getClass());
	}
}
