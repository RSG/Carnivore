/*******************************************************************************

	$Id: AbstractScreenAction.java,v 1.12 2005/02/25 04:01:33 steve Exp $
	
	File:		AbstractScreenAction.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2002-2004 Steve Roy <sroy@roydesign.net>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	11/27/02	Created this file - Steve
	02/26/04    Added support to AWT frames to getSourceFrame() - Steve
	03/12/04	Added support for components in getSourceFrame() - Steve
	04/13/04    Made isUsedBy() use Frame instead of JFrame, since our own
				MenuItem class can now use Actions - Steve
	04/16/04    Renamed from AbstractAction to AbstractScreenAction - Steve

*******************************************************************************/

package net.roydesign.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * <p>A subclass of <code>javax.swing.AbstractAction</code> that adds the
 * logistics needed to make menu bars conform to the Mac OS screen menu bar
 * requirements without sacrificing the usual way of presenting menu bars on
 * other platforms. See the <code>ScreenMenuBar</code> and
 * <code>JScreenMenuBar</code> classes for more details.</p>
 *
 * @see ScreenMenuBar
 * @see JScreenMenuBar
 * 
 * @version MRJ Adapter 1.0.9
 */
public abstract class AbstractScreenAction extends javax.swing.AbstractAction
{
	/**
	 * The key used for storing a large icon for the action,
	 * used for toolbar buttons.
	 */
	public static final String LARGE_ICON = "LargeIcon";
	
	/**
	 * The user frames of this action.
	 */
	private Vector userFrames;
	
	/**
	 * Defines an abstract action with a default
	 * description string and default icon.
	 */
	public AbstractScreenAction()
	{
		super();
	}
	
	/**
	 * Defines an abstract action with the specified
	 * description string and a default icon.
	 */
	public AbstractScreenAction(String name)
	{
		super(name);
	}
	
	/**
	 * Defines an abstract action with the specified
	 * description string and the specified icon.
	 */
	public AbstractScreenAction(String name, Icon icon)
	{
		super(name, icon);
	}
	
	/**
	 * Add the given <code>JFrame</code> subclass as a user frame
	 * of the action. When an action has no user frame, then all
	 * frames get the action.
	 * @param frameClass the <code>JFrame</code> subclass
	 */
	public void addUserFrame(Class frameClass)
	{
		if (userFrames == null)
			userFrames = new Vector();
		userFrames.addElement(frameClass);
	}
	
	/**
	 * Remove the given <code>JFrame</code> subclass from the user
	 * frames of the action.
	 * @param frameClass the <code>JFrame</code> subclass
	 */
	public void removeUserFrame(Class frameClass)
	{
		if (userFrames == null)
			return;
		userFrames.removeElement(frameClass);
		if (userFrames.size() == 0)
			userFrames = null;
	}
	
	/**
	 * Get whether the action is used by the given frame instance.
	 * @return whether the action is used by the given frame
	 */
	public boolean isUsedBy(Frame frame)
	{
		return userFrames == null || userFrames.contains(frame.getClass());
	}
	
	/**
	 * Utility method to get the <code>JFrame</code> where the
	 * given action event occured.
	 * @param e the action event
	 * @return the frame where the event occurred
	 */
	public JFrame getSourceJFrame(ActionEvent e)
	{
		return (JFrame)getSourceFrame(e);
	}
	
	/**
	 * Utility method to get the <code>Frame</code> where the
	 * given action event occured.
	 * @param e the action event
	 * @return the frame where the event occurred
	 */
	public Frame getSourceFrame(ActionEvent e)
	{
		Object obj = e.getSource();
		if (obj instanceof JMenuItem)
		{
			Component comp = ((JMenuItem)obj).getParent();
			while (comp instanceof JPopupMenu)
			{
				JPopupMenu pm = (JPopupMenu)comp;
				JMenu m = (JMenu)pm.getInvoker();
				comp = m.getParent();
			}
			while (!(comp instanceof Frame))
				comp = ((Container)comp).getParent();
			return (Frame)comp;
		}
		else if (obj instanceof MenuComponent)
		{
			MenuContainer cont = ((MenuComponent)obj).getParent();
			while (cont instanceof MenuComponent)
				cont = ((MenuComponent)cont).getParent();
			return (Frame)cont;
		}
		else if (obj instanceof Component)
		{
			Container cont = ((Component)obj).getParent();
			while (!(cont instanceof Frame))
				cont = cont.getParent();
			return (Frame)cont;
		}
		return null;
	}
}
