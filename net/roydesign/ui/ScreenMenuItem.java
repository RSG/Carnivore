/*******************************************************************************

	$Id: ScreenMenuItem.java,v 1.10 2005/02/25 04:01:34 steve Exp $
	
	File:		ScreenMenuItem.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2002-2004 Steve Roy <sroy@roydesign.net>
	
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	02/23/03	Created this file - Steve
	02/26/04    Merged into MRJ Adapter, added support for Swing actions - Steve
	04/13/04    Made configurePropertiesFromAction() use reflection because the
				KeyStroke class extends AWTKeyStroke in 1.4, which makes it
				incompatible at runtime with the 1.3 KeyStroke class - Steve
	04/16/04    Renamed from MenuItem to ScreenMenuItem - Steve

*******************************************************************************/

package net.roydesign.ui;

import java.awt.Frame;
import java.awt.MenuShortcut;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.Vector;

import javax.swing.Action;

/**
 * <p>A subclass of <code>java.awt.MenuItem</code> that adds the logistics
 * needed to make menu bars conform to the Mac OS screen menu bar requirements
 * without sacrificing the usual way of presenting menu bars on other platforms.
 * See the class <code>ScreenMenuBar</code> for more details.</p>
 *
 * @see ScreenMenuBar
 * 
 * @version MRJ Adapter 1.0.9
 */
public class ScreenMenuItem extends java.awt.MenuItem
{
	/**
	 * The action to be executed by this menu item.
	 */
	private Action action;
	
	/**
	 * The object that listens for property change events coming from
	 * the action attached to the menu item.
	 */
	private PropertyChangeListener actionPropertyChangeListener =
		new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String prop = e.getPropertyName();
				if (prop.equals("action"))
					configurePropertiesFromAction((Action)e.getNewValue());
				else if (prop.equals(Action.NAME))
					setLabel((String)e.getNewValue());
				else if (prop.equals("enabled"))
					setEnabled(((Boolean)e.getNewValue()).booleanValue());
				else if (prop.equals(Action.ACTION_COMMAND_KEY))
					setActionCommand((String)e.getNewValue());
			}
		};
	
	/**
	 * The property change handler.
	 */
	private PropertyChangeSupport propertiesHandler =
		new PropertyChangeSupport(this);
	
	/**
	 * The user frames of this menu item.
	 */
	private Vector userFrames;
	
	/**
	 * Construct a menu item with no set text.
	 */
	public ScreenMenuItem()
	{
		super();
	}
	
	/**
	 * Construct a menu item with text.
	 * @param text the text of the menu item
	 */
	public ScreenMenuItem(String text)
	{
		super(text);
	}
	
	/**
	 * Construct a menu item with the specified text and
	 * keyboard mnemonic.
	 * @param text the text of the menu item
	 * @param shortcut the keyboard shortcut for the menu item
	 */
	public ScreenMenuItem(String text, MenuShortcut shortcut)
	{
		super(text, shortcut);
	}
	
	/**
	* Construct a menu item whose properties are taken from the 
	* specified <code>Action</code>.
	* @param action the action of the menu item
	*/
	public ScreenMenuItem(Action action)
	{
		super();
		setAction(action);
	}
	
	/**
	 * Set the label for this menu item to the given string. This
	 * method is overriden to fire a property change event.
	 * @param label the new label or <code>null</code> for no label
	 */
	public synchronized void setLabel(String label)
	{
		String oldLabel = getLabel();
		super.setLabel(label);
		if (!label.equals(oldLabel))
			propertiesHandler.firePropertyChange("label", oldLabel, label);
    }
	
	/**
	 * Set the state of the menu item. This method is overriden to
	 * fire a property change event.
	 * @param enabled whether to enable or disable the menu item
	 */
	public synchronized void setEnabled(boolean enabled)
	{
		boolean oldEnabled = isEnabled();
		super.setEnabled(enabled);
		if (enabled != oldEnabled)
		{
			propertiesHandler.firePropertyChange("enabled",
				new Boolean(oldEnabled), new Boolean(enabled));
		}
	}
	
	/**
	 * Set the keyboard shortcut associated with this menu item.
	 * If a menu shortcut is already associated with this menu
	 * item, it is replaced. This method is overriden to fire a
	 * property change event.
	 * @param shortcut the menu shortcut to associate with this item
	 */
	public void setShortcut(MenuShortcut shortcut)
	{
		MenuShortcut oldShortcut = getShortcut();
		super.setShortcut(shortcut);
		if (shortcut != oldShortcut)
		{
			propertiesHandler.firePropertyChange("shortcut",
				oldShortcut, shortcut);
		}
	}
	
	/**
	 * Get the <code>Action</code> for the <code>ActionEvent</code> source.
	 * @return the action to be performed by this menu item
	 */
	public Action getAction()
	{
		return action;
	}
	
	/**
	 * Set the <code>Action</code> for the <code>ActionEvent</code> source.
	 * @param action the action to be performed by this menu item
	 */
	public void setAction(Action action)
	{
		Action oldAction = this.action;
		if (oldAction == null || !oldAction.equals(action))
		{
			this.action = action;
			if (oldAction != null)
			{
				removeActionListener(oldAction);
				oldAction.removePropertyChangeListener(actionPropertyChangeListener);
			}
			configurePropertiesFromAction(this.action);
			if (this.action != null)
			{		
				addActionListener(this.action);
				this.action.addPropertyChangeListener(actionPropertyChangeListener);
			}
		//	firePropertyChange("action", oldAction, this.action);
		//	revalidate();
		//	repaint();
		}
	}
	
	/**
	 * Factory method which sets the ActionEvent source's properties
	 * according to values from the Action instance.
	 * @param action the assigned action
	 */
	protected void configurePropertiesFromAction(Action action)
	{
		if (action != null)
		{
			setLabel((String)action.getValue(Action.NAME));
			setEnabled(action.isEnabled());
			Object ks = action.getValue(Action.ACCELERATOR_KEY);
			if (ks != null)
			{
				try
				{
					Method met = ks.getClass().getMethod("getModifiers", null);
					Object obj = met.invoke(ks, null);
					int mdfrs = ((Number)obj).intValue();
					if ((mdfrs & KeyEvent.META_MASK) != 0)
					{
						boolean shft = ((mdfrs & KeyEvent.SHIFT_MASK) != 0);
						met = ks.getClass().getMethod("getKeyCode", null);
						obj = met.invoke(ks, null);
					    int code = ((Number)obj).intValue();
						setShortcut(new MenuShortcut(code, shft));
					}
				}
				catch (Exception e)
				{
					// Should never happen
					e.printStackTrace();
				}
			}
			else
			{
				setShortcut(null);
			}
		}
		else
		{
			setLabel(null);
			setEnabled(true);
			setShortcut(null);
		}
	}
	
	/**
	 * Add the given <code>Frame</code> subclass as a user
	 * of the menu item. When a menu item has no user frames, then all
	 * frames get the menu item.
	 * @param frameClass the <code>Frame</code> subclass
	 */
	public synchronized void addUserFrame(Class frameClass)
	{
		if (userFrames == null)
			userFrames = new Vector();
		userFrames.addElement(frameClass);
	}
	
	/**
	 * Remove the given <code>Frame</code> subclass from the user
	 * frames of the menu item.
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
	 * Get whether the menu item is used by the given frame instance.
	 * @return whether the menu item is used by the given frame
	 */
	public boolean isUsedBy(Frame frame)
	{
		return userFrames == null || userFrames.contains(frame.getClass());
	}
	
	/**
	 * Add the given property change listener to the notification list.
	 * @param l the property change listener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		propertiesHandler.addPropertyChangeListener(l);
	}
	
	/**
	 * Remove the given property change listener from the notification list.
	 * @param l the property change listener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		propertiesHandler.removePropertyChangeListener(l);
	}
}
