/*******************************************************************************

	$Id: ApplicationDialog.java,v 1.9 2005/02/25 04:01:33 steve Exp $
	
	File:		ApplicationDialog.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>

	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>

	Change History:
	03/05/03	Created this file - Steve
	03/25/03	Moved to the net.roydesign.ui package - Steve
	06/17/03	Use MRJAdapter instead of MRJFileUtils and FileManager - Steve

*******************************************************************************/

package net.roydesign.ui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

import net.roydesign.io.ApplicationFile;
import net.roydesign.mac.MRJAdapter;

/**
 * An application dialog is a modal file dialog to specifically select an
 * application on disk. The dialog installs its own filename filter to only
 * enable file system items that qualify as applications. Note however that
 * this filtering mechanism isn't implemented on all platforms, specifically
 * because Sun themselves didn't bother to implement it in their own Java VM
 * for Windows. In this case, all files will be selectable by the user. The
 * dialog also takes care of the system properties required on Mac OS X to
 * make file dialogs not show application bundles as folders but as files,
 * which means you don't have to worry about any of it.
 * 
 * @version MRJ Adapter 1.0.9
 */
public class ApplicationDialog extends FileDialog
{
	/**
	 * Whether the <code>setMode()</code> method should check calls or not.
	 */
	private boolean modeCheckingEnabled = false;

	/**
	 * Construct an application dialog with the given parent frame.
	 * @param parent the parent frame
	 */
	public ApplicationDialog(Frame parent)
	{
		this(parent, "");
	}

	/**
	 * Construct an application dialog with the given parent frame and
	 * title.
	 * @param parent the parent frame
	 * @param title the title of the dialog
	 */
	public ApplicationDialog(Frame parent, String title)
	{
		super(parent, title, LOAD);
		setFilenameFilter(new ApplicationFilter());
		modeCheckingEnabled = true;
	}

	/**
	 * Get the application file selected by the user. If the user
	 * cancels the operation, this method returns <code>null</code>.
	 * This method is a convenience which replaces the separate calls
	 * <code>getFile()</code> and <code>getDirectory()</code>.
	 * @return the selected application file, or null
	 */
	public ApplicationFile getApplicationFile()
	{
		String f = getFile();
		return f != null ? new ApplicationFile(getDirectory(), f) : null;
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
	 * or <code>dispose</code>.
	 */
	public void show()
	{
		// Set the system property required by Mac OS X
		String prop = null;
		if (MRJAdapter.mrjVersion >= 4.0f)
			prop = "apple.awt.use-file-dialog-packages";
		else if (MRJAdapter.mrjVersion >= 3.0f)
			prop = "com.apple.macos.use-file-dialog-packages";
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
	 * Implementation of the application filter for the dialog.
	 */
	private class ApplicationFilter implements FilenameFilter
	{
		/**
		 * Check if the given file system item is an application file.
		 * @param directory the parent directory of the item
		 * @param name the name of the item
		 * @return whether the item is an application file
		 */
		public boolean accept(File directory, String name)
		{
			try
			{
				if (MRJAdapter.mrjVersion != -1.0f)
					return MRJAdapter.getFileType(new File(directory, name)).equals("APPL");
				/** @todo Any other platform we could handle? */
			}
			catch (IOException e)
			{
				// Don't do anything and play it safe by returning true
			}
			return true;
		}
	}
}
