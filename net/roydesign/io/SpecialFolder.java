/*******************************************************************************

	$Id: SpecialFolder.java,v 1.11 2005/02/25 04:01:33 steve Exp $
	
	File:		SpecialFolder.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.
	
	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	02/14/03	Created this file - Steve
	03/19/04    Added use of java.io.tmpdir in getTemporaryItemsFolder() - Steve
	05/24/04    Made getPreferencesFolder() return user.home instead of
				throwing a FileNotFoundException - Steve

*******************************************************************************/

package net.roydesign.io;

import java.io.File;
import java.io.FileNotFoundException;

import net.roydesign.mac.MRJAdapter;
import net.roydesign.mac.MRJFolderConstants;

/**
 * A collection of static methods for locating special folders in the file system.
 * Examples of special folders are the home directory and the preferences folder.
 * This class is highly platform dependant and is also quite minimal at this stage
 * in terms of supported platforms. The idea is to add to it as development needs
 * come up. Code contributions are welcome.
 * 
 * @version MRJ Adapter 1.0.9
 */
public class SpecialFolder
{
	/**
	 * The name of the OS, from the os.name system property.
	 */
	private static final String osName = System.getProperty("os.name");
	
	/**
	 * The default constructor is private so this class can't
	 * be instantiated.
	 */
	private SpecialFolder()
	{
	}
	
	/**
	 * Get the home directory of the current user.
	 * @return the home directory
	 */
	public static File getHomeFolder()
	{
		return new File(System.getProperty("user.home"));
	}
	
	/**
	 * Get the preferences folder for the current user.
	 * @return the preferences folder
	 * @exception FileNotFoundException if the folder can't be found
	 */
	public static File getPreferencesFolder() throws FileNotFoundException
	{
		if (MRJAdapter.mrjVersion != -1.0f)
		{
			return MRJAdapter.findFolder(MRJFolderConstants.kUserDomain,
				MRJFolderConstants.kPreferencesFolderType, true);
		}
		else if (osName.startsWith("Windows"))
		{
			return new File(System.getProperty("user.home"), "Application Data");
		}
		else
		{
			return new File(System.getProperty("user.home"));
		}
	}
	
	/**
	 * Get the temporary items folder.
	 * @return the temporary items folder
	 * @exception FileNotFoundException if the folder can't be found
	 */
	public static File getTemporaryItemsFolder() throws FileNotFoundException
	{
		if (MRJAdapter.mrjVersion != -1.0f)
		{
			return MRJAdapter.findFolder(MRJFolderConstants.kUserDomain,
				MRJFolderConstants.kTemporaryFolderType, true);
		}
		else if (MRJAdapter.javaVersion >= 1.2f)
		{
			return new File(System.getProperty("java.io.tmpdir"));
		}
		else if (osName.startsWith("Windows"))
		{
			return new File("c:\temp\"");
		//	return new File("c:\windows\temp\"");
		}
		throw new FileNotFoundException();
	}
	
	/**
	 * Get the desktop folder for the current user.
	 * @return the desktop folder
	 * @exception FileNotFoundException if the folder can't be found
	 */
	public static File getDesktopFolder() throws FileNotFoundException
	{
		if (MRJAdapter.mrjVersion != -1.0f)
		{
			return MRJAdapter.findFolder(MRJFolderConstants.kUserDomain,
				MRJFolderConstants.kDesktopFolderType, true);
		}
		else if (osName.startsWith("Windows"))
		{
			return new File(System.getProperty("user.home"), "Desktop");
		}
		throw new FileNotFoundException();
	}
	
	/**
	 * Find a special Mac OS folder. This method locates on disk a folder
	 * designated by the Mac OS for a specific purpose.
	 * @param domain the domain of the folder
	 * @param type the type code of the folder to find
	 * @param create whether to create the folder if it doesn't already exist
	 * @return the special folder object
	 * @exception FileNotFoundException when the folder can't be found
	 * @see net.roydesign.mac.MRJAdapter#findFolder
	 */
	public static File findMacFolder(short domain, String type, boolean create)
		throws FileNotFoundException
	{
		return MRJAdapter.findFolder(domain, type, create);
	}
}
