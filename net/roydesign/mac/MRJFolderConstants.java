/*******************************************************************************

	$Id: MRJFolderConstants.java,v 1.9 2005/02/25 04:01:33 steve Exp $
	
	File:		MRJFolderConstants.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.
	
	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	03/13/03	Created this file - Steve

*******************************************************************************/

package net.roydesign.mac;

/**
 * This interface defines some useful Mac OS constants to use with the
 * <code>MRJAdapter.findFolder()</code> method.
 * 
 * @version MRJ Adapter 1.0.9
 */
public interface MRJFolderConstants
{
	/**
	 * Domain constants. These are only the most common domains.
	 * For a complete list of domains, check out the
	 * <a href="http://developer.apple.com/techpubs/macosx/Carbon/Files/FolderManager/Folder_Manager/folder_manager_ref/constant_11.html">
	 * Folder Manager reference, Disk and Domain Constants</a>.
	 */
	
	/**
	 * The system domain.
	 */
	public static final short kSystemDomain = -32766;
	
	/**
	 * The current user domain.
	 */
	public static final short kUserDomain = -32763;
	
	/**
	 * The Classic domain.
	 */
	public static final short kClassicDomain = -32762;
	
	/**
	 * Folder type constants. These are only the most common types.
	 * For a complete list of folder types, check out the
	 * <a href="http://developer.apple.com/techpubs/macosx/Carbon/Files/FolderManager/Folder_Manager/folder_manager_ref/constant_6.html">
	 * Folder Manager reference, Folder Type Constants</a>.
	 */
	
	/**
	 * The System folder.
	 */
	public static final int kSystemFolderType = 0x6D616373; // 'macs'
	
	/**
	 * The Desktop folder.
	 */
	public static final int kDesktopFolderType = 0x6465736b; // 'desk'
	
	/**
	 * The single-user Trash folder.
	 */
	public static final int kTrashFolderType = 0x74727368; // 'trsh'
	
	/**
	 * The Preferences folder in the System Folder.
	 */
	public static final int kPreferencesFolderType = 0x70726566; // 'pref'
	
	/**
	 * The invisible folder on the system disk called “Cleanup at Startup” whose
	 * contents are deleted when the system is restarted, instead of merely
	 * being moved to the Trash. When the <code>findFolder()</code> method indicates
	 * this folder is available (by returning a non-null value), developers should
	 * usually use this folder for their temporary items, in preference to the
	 * Temporary Folder. Supported with Mac OS 8 and later.
	 */
	public static final int kChewableItemsFolderType = 0x666C6E74; // 'flnt'
	
	/**
	 * The Temporary folder. This folder exists as an invisible folder at the volume root.
	 */
	public static final int kTemporaryFolderType = 0x74656D70; // 'temp'
	
	/**
	 * The Application Support folder in the System Folder. This folder contains code
	 * and data files needed by third-party applications. These files should usually
	 * not be written to after they are installed. In general, files deleted from this
	 * folder remove functionality from an application, unlike files in the Preferences
	 * folder, which should be non-essential. One type of file that could be placed
	 * here would be plug-ins that the user might want to maintain separately from
	 * any application, such as for an image-processing application that has many
	 * “fourth-party” plug-ins that the user might want to upgrade separately from the
	 * host application. Another type of file that might belong in this folder would
	 * be application-specific data files that are not preferences, such as for a
	 * scanner application that needs to read description files for specific scanner
	 * models according to which are currently available on the SCSI bus or network.
	 * Supported with Mac OS 8 and later.
	 */
	public static final int kApplicationSupportFolderType = 0x61737570; // 'asup'
	
	/**
	 * The Cache folder.
	 */
	public static final int kCachedDataFolderType = 0x63616368; // 'cach'
	
	/**
	 * The Applications folder installed at the root level of the volume.
	 * Supported with Mac OS 8 and later.
	 */
	public static final int kApplicationsFolderType = 0x61707073; // 'apps'
	
	/**
	 * The Documents folder. This folder is created at the volume root.
	 * Supported with Mac OS 8 and later.
	 */
	public static final int kDocumentsFolderType = 0x646F6373; // 'docs'
	
	/**
	 * The Help folder in the System Folder. Supported with Mac OS 8 and later.
	 */
	public static final int kHelpFolderType = 0xC4686C70; // 'ƒhlp'
	
	/**
	 * The Favorites folder in the System Folder. This folder is for storing
	 * Internet location files, aliases, and aliases to other frequently used items.
	 * Facilities for adding items into this folder are found in Contextual Menus,
	 * the Finder, Navigation Services, and others. Supported with Mac OS 8.1 and later.
	 */
	public static final int kFavoritesFolderType = 0x66617673; // 'favs'
}
