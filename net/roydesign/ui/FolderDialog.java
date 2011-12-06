/*******************************************************************************

	$Id: FolderDialog.java,v 1.11 2005/02/25 04:01:33 steve Exp $
	
	File:		FolderDialog.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.
	
	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	03/06/03	Created this file - Steve
	03/25/03	Moved to the net.roydesign.ui package, modified to use the
				apple.awt.fileDialogForDirectories property with MRJ 4, added
				the getInitialMode() method, removed getFolder() because
				it's redundant with getDirectory(), removed the filename filter
				which was irrelevant - Steve
	12/16/03    Fixed getDirectory() to check if super.getFile() is null before
				trying to build a path with it - Steve

*******************************************************************************/

package net.roydesign.ui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.Properties;

import net.roydesign.mac.MRJAdapter;

/**
 * A folder dialog is a modal file dialog to specically select a folder on
 * disk. This class takes advantage of a little know trick in Apple's VMs to
 * show a real folder dialog, with a Choose button and all. However, there is
 * no such thing on other platforms, where this class employs the usual
 * kludge which is to show a Save dialog. If you would rather use the Swing
 * JFileChooser, go right ahead.
 * 
 * @version MRJ Adapter 1.0.9
 */
public class FolderDialog extends FileDialog
{
	/**
	 * Whether the <code>setMode()</code> method should check calls or not.
	 */
	private boolean modeCheckingEnabled = false;
	
	/**
	 * Construct a folder dialog with the given parent frame.
	 * @param parent the parent frame
	 */
	public FolderDialog(Frame parent)
	{
		this(parent, "");
	}
	
	/**
	 * Construct a folder dialog with the given parent frame and
	 * title.
	 * @param parent the parent frame
	 * @param title the title of the dialog
	 */
	public FolderDialog(Frame parent, String title)
	{
		super(parent, title, getInitialMode());
		if (MRJAdapter.mrjVersion == -1.0f)
			setFile("-");
		modeCheckingEnabled = true;
	}
	
	/**
	 * Get the file of this file dialog, which in the case of this class,
	 * is always an empty string ("") unless the user has canceled where the
	 * return value will be <code>null</code>.
	 * @return an empty string if a directory was selected, or <code>null</code>
	 */
	public String getFile()
	{
		// MRJ 2 returns "", MRJ 3 and 4 return the folder name, and other
		// platforms return the filename, so let's normalize this
		return super.getFile() != null ? "" : null;
	}
	
	/**
	 * Get the directory of this file dialog.
	 * @return the directory of the dialog, or null
	 */
	public String getDirectory()
	{
		// MRJ 2 returns the folder, MRJ 3 and 4 return the parent folder, and
		// other platforms return the folder, so let's normalize this
		String path = super.getDirectory();
		if (path == null)
			return null;
		if (MRJAdapter.mrjVersion >= 3.0f && super.getFile() != null)
			return new File(path, super.getFile()).getPath();
		return path;
	}
	
	/**
	 * Set the mode of the dialog. This method is overriden because it
	 * doesn't make sense in the context of an application dialog to allow
	 * selection of the mode. It will throw an error if you try to call it.
	 * @param mode the mode
	 */
	public void setMode(int mode)
	{
		if (modeCheckingEnabled)
			throw new Error("can't set mode");
		super.setMode(mode);
	}
	
	/**
	 * Make the dialog visible. Since the dialog is modal, this method
	 * will not return until either the user dismisses the dialog or
	 * you make it invisible yourself via <code>setVisible(false)</code>
	 * or <code>dispose()</code>.
	 */
	public void show()
	{
		// Set the system property required by Mac OS X
		String prop = null;
		if (MRJAdapter.mrjVersion >= 4.0f)
			prop = "apple.awt.fileDialogForDirectories";
		Properties props = System.getProperties();
		Object oldValue = null;
		if (prop != null)
		{
			oldValue = props.get(prop);
			props.put(prop, "true");
		}
		
		// Do the usual thing
		super.show();
		
		// Reset the system property
		if (prop != null)
		{
			if (oldValue == null)
				props.remove(prop);
			else
				props.put(prop, oldValue);
		}
	}
	
	/**
	 * Perform the preparatory setup for the folder dialog and return
	 * the value to use for the mode of the dialog. This method is called
	 * by the constructor.
	 * @return the mode value to use
	 */
	private static int getInitialMode()
	{
		if (MRJAdapter.mrjVersion >= 4.0f)
			return LOAD;
		else if (MRJAdapter.mrjVersion != -1.0f)
			return 3; // Any value >= 2 seems to work, but 3 is the commonly accepted value
		return SAVE;
	}
}
