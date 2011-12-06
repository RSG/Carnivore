/*******************************************************************************

	$Id: ApplicationFile.java,v 1.9 2005/02/25 04:01:33 steve Exp $
	
	File:		ApplicationFile.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>

	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>

	Change History:
	02/20/03	Created this file - Steve
	03/31/03	Added instantiation of osName, which was otherwise undefined,
				implemented the two getMacBundleResource() methods, provided
				a better implementation of open() and openDocuments() - Steve
	06/17/03	Use net.roydesign.mac.MRJAdapter - Steve

*******************************************************************************/

package net.roydesign.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.roydesign.mac.MRJAdapter;

/**
 * An application file is the executable file on disk for an application. This
 * class defines methods to locate, inspect and launch such applications and
 * integrates a few methods to handle traits specific to the Mac OS platform.
 * Due to the intrinsic platform-dependant nature of some of this functionality,
 * this is always work in progress. Support for classic Mac OS, Mac OS X, and
 * Windows is currently implemented.
 * 
 * @version MRJ Adapter 1.0.9
 */
public class ApplicationFile
{
	/**
	 * The name of the OS, from the os.name system property.
	 */
	private static final String osName = System.getProperty("os.name");

	/**
	 * The executable file on disk.
	 */
	File executable;

	/**
	 * Construct an application file with a path. This is identical
	 * to the <code>java.io.File</code> constructor.
	 * @param path the executable path
	 */
	public ApplicationFile(String path)
	{
		this.executable = new File(path);
	}

	/**
	 * Construct an application file with a parent directory and the
	 * name or subpath of a child executable file.  This is identical
	 * to the <code>java.io.File</code> constructor.
	 * @param parent the parent directory
	 * @param child the child directory path or file name
	 */
	public ApplicationFile(String parent, String child)
	{
		this.executable = new File(parent, child);
	}

	/**
	 * Construct an application file with a parent directory and the
	 * name or subpath of a child executable file.  This is identical
	 * to the <code>java.io.File</code> constructor.
	 * @param parent the parent directory
	 * @param child the child directory path or file name
	 */
	public ApplicationFile(File parent, String child)
	{
		this.executable = new File(parent, child);
	}

	/**
	 * Construct an application file with a <code>java.io.File</code>
	 * object. Note that the given file object is not used internally.
	 * @param executable the executable file object
	 */
	public ApplicationFile(File executable)
	{
		// We don't use the passed File object, because
		// we want to have full control over it
		this(executable.getPath());
	}

	/**
	 * Launch the application.
	 * @return whether the file was opened successfully or not
	 * @exception IOException when any error occurs
	 */
	public boolean open() throws IOException
	{
		if (MRJAdapter.mrjVersion >= 3.0f)
		{
			try
			{
				// On Mac OS X, use 'open' on the command line
				Process p = Runtime.getRuntime().exec(new String[]
					{"open", "-a", executable.getAbsolutePath()});
				if (p.waitFor() != 0)
					return false;
			}
			catch (InterruptedException e)
			{
				return false;
			}
		}
		else if (MRJAdapter.mrjVersion != -1.0f)
		{
			Runtime.getRuntime().exec(new String[] {executable.getAbsolutePath()});
		}
		else if (osName.startsWith("Windows"))
		{
			try
			{
				// On Windows, use 'cmd /c start' on the command line
				/** @todo Is this going to work on all flavors of Windows above 3.1? */
				Process p = Runtime.getRuntime().exec(new String[]
					{"cmd", "/c", "start", "\"\"", executable.getAbsolutePath()});
				if (p.waitFor() != 0)
					return false;
			}
			catch (InterruptedException e)
			{
				return false;
			}
		}
		else
		{
			try
			{
				// Just assume for now that we can run the app from its full pathname
				Process p = Runtime.getRuntime().exec(new String[] {executable.getAbsolutePath()});
				if (p.waitFor() != 0)
					return false;
			}
			catch (InterruptedException e)
			{
				return false;
			}
		}

		// We get here when the application was opened successfully,
		// so be happy and let the user know
		return true;
	}

	/**
	 * Launch the application with the given arguments.
	 * @param args the arguments to pass to the application
	 * @return the application process
	 * @exception IOException when any error occurs
	 */
	public Process open(String[] args) throws IOException
	{
		/** @todo Need to provide a better way to start processes */
		String[] nargs = new String[args.length + 1];
		nargs[0] = executable.getAbsolutePath();
		System.arraycopy(args, 0, nargs, 1, args.length);
		return Runtime.getRuntime().exec(nargs);
	}

	/**
	 * Open the given document. Note that the behavior of this method
	 * is platform specific. Some platforms allow multiple instances of
	 * an application to execute simultaneously, some others don't, and
	 * some others have mixed behaviors. For example, on Mac OS X,
	 * double-clickable applications are not allowed to have multiple
	 * instances but command line tools can.
	 * @param documentFile the document to be opened
	 * @return whether the document was opened successfully or not
	 * @exception IOException when any error occurs
	 */
	public boolean openDocument(DocumentFile documentFile) throws IOException
	{
		return openDocument(documentFile.file);
	}

	/**
	 * Open the given file. Note that the behavior of this method
	 * is platform specific. Some platforms allow multiple instances of
	 * an application to execute simultaneously, some others don't, and
	 * some others have mixed behaviors. For example, on Mac OS X,
	 * double-clickable applications are not allowed to have multiple
	 * instances but command line tools can.
	 * @param file the file to be opened
	 * @return whether the file was opened successfully or not
	 * @exception IOException when any error occurs
	 */
	public boolean openDocument(File file) throws IOException
	{
		return openDocuments(new File[] {file});
	}

	/**
	 * Open the given documents. Note that the behavior of this method
	 * is platform specific. Some platforms allow multiple instances of
	 * an application to execute simultaneously, some others don't, and
	 * some others have mixed behaviors. For example, on Mac OS X,
	 * double-clickable applications are not allowed to have multiple
	 * instances but command line tools can.
	 * @param documentFiles the documents to be opened
	 * @return whether the documents were opened successfully or not
	 * @exception IOException when any error occurs
	 */
	public boolean openDocuments(DocumentFile[] documentFiles) throws IOException
	{
		File[] files = new File[documentFiles.length];
		for (int i = 0; i < files.length; i++)
			files[i] = documentFiles[i].file;
		return openDocuments(files);
	}

	/**
	 * Open the given files. Note that the behavior of this method
	 * is platform specific. Some platforms allow multiple instances of
	 * an application to execute simultaneously, some others don't, and
	 * some others have mixed behaviors. For example, on Mac OS X,
	 * double-clickable applications are not allowed to have multiple
	 * instances but command line tools can.
	 * @param files the files to be opened
	 * @return whether the files were opened successfully or not
	 * @exception IOException when any error occurs
	 */
	public boolean openDocuments(File[] files) throws IOException
	{
		if (MRJAdapter.mrjVersion >= 3.0f)
		{
			try
			{
				// On Mac OS X, use 'open' on the command line
				String[] strs = new String[3 + files.length];
				strs[0] = "open";
				strs[1] = "-a";
				strs[2] = executable.getAbsolutePath();
				for (int i = 0; i < files.length; i++)
					strs[3 + i] = files[i].getAbsolutePath();
				Process p = Runtime.getRuntime().exec(strs);
				if (p.waitFor() != 0)
					return false;
			}
			catch (InterruptedException e)
			{
				return false;
			}
		}
		else if (MRJAdapter.mrjVersion != -1.0f)
		{
			/** @todo In Classic, this fails at passing the file to an *already running* OS X app */
			String[] strs = new String[1 + files.length];
			strs[0] = executable.getAbsolutePath();
			for (int i = 0; i < files.length; i++)
				strs[1 + i] = files[i].getAbsolutePath();
			Runtime.getRuntime().exec(strs);
		}
		else if (osName.startsWith("Windows"))
		{
			try
			{
				// On Windows, use 'cmd /c start' on the command line
				/** @todo Is this going to work on all flavors of Windows above 3.1? */
				String[] strs = new String[5 + files.length];
				strs[0] = "cmd";
				strs[1] = "/c";
				strs[2] = "start";
				strs[3] = "\"\"";
				strs[4] = executable.getAbsolutePath();
				for (int i = 0; i < files.length; i++)
					strs[5 + i] = files[i].getAbsolutePath();
				Process p = Runtime.getRuntime().exec(strs);
				if (p.waitFor() != 0)
					return false;
			}
			catch (InterruptedException e)
			{
				return false;
			}
		}
		else
		{
			try
			{
				// Just assume for now that we can run the app from its full pathname
				String[] strs = new String[1 + files.length];
				strs[0] = executable.getAbsolutePath();
				for (int i = 0; i < files.length; i++)
					strs[1 + i] = files[i].getAbsolutePath();
				Process p = Runtime.getRuntime().exec(strs);
				if (p.waitFor() != 0)
					return false;
			}
			catch (InterruptedException e)
			{
				return false;
			}
		}

		// We get here when the documents were opened successfully,
		// so be happy and let the user know
		return true;
	}

	/**
	 * Get the path to the application file on disk.
	 * @return the path to the application
	 * @see java.io.File#getPath
	 */
	public String getPath()
	{
		return executable.getPath();
	}

	/**
	 * Get the absolute path to the application file on disk.
	 * @return the absolute path to the application
	 * @see java.io.File#getAbsolutePath
	 */
	public String getAbsolutePath()
	{
		return executable.getAbsolutePath();
	}

	/**
	 * Get the canonical path to the application file on disk.
	 * @return the canonical path to the application
	 * @see java.io.File#getCanonicalPath
	 */
	public String getCanonicalPath() throws IOException
	{
		return executable.getCanonicalPath();
	}

	// exists() ?
	// canExecute() ?
	// getLocalizedName() ?
	// getParent() ?
	// getParentFile() ?
	// isMacBundle() ?
	// isCFM() ?
	// isMachO() ?
	// isBoth() ?

	/**
	 * Get the name of the executable file on disk.
	 * @return the name of the executable
	 */
	public String getExecutableName()
	{
		return executable.getName();
	}

	/**
	 * Get the name displayed by the application to the user.
	 * @return the name displayed to the user
	 * @exception IOException when an I/O error occurs
	 */
	public String getDisplayedName() throws IOException
	{
		if (MRJAdapter.mrjVersion != -1.0f)
		{
			if (executable.isDirectory())
			{
				// Try to extract it out of the bundle
				File f = new File(executable, "Contents/MRJApp.properties");
				if (f.exists())
				{
					String name =
						MRJAdapter.parseMRJAppProperties(f, "com.apple.mrj.application.apple.menu.about.name");
					if (name != null)
						return name;
				}
				/** @todo Add support for InfoPlist.strings */
				f = new File(executable, "Contents/Info.plist");
				if (f.exists())
				{
					String name = MRJAdapter.parseInfoPlist(f, "com.apple.mrj.application.apple.menu.about.name");
					if (name == null)
					{
						name = MRJAdapter.parseInfoPlist(f, "CFBundleName");
						if (name == null)
							name = MRJAdapter.parseInfoPlist(f, "CFBundleExecutable");
					}
					return name;
				}
			}
		}
		else if (osName.startsWith("Windows"))
		{
			/** @todo How do we get this information on Windows? */
		}
		return getExecutableName();
	}

	/**
	 * Get the Mac OS creator code of the application. On the Mac OS and
	 * Mac OS X, this method will return the assigned case-sensitive four
	 * character type string. On all other platforms, the behavior depends
	 * whether the application is a Mac OS bundled application that is
	 * temporarily sitting on another platform, or not. When this is the
	 * case, this method will recognize the bundled app and will extract
	 * the Mac OS creator code from it. This will only work for correctly
	 * bundled applications. Otherwise, an empty string will be returned
	 * to provide a consistent cross-platform behavior.
	 * @return the Mac OS creator code, or an empty string
	 * @exception IOException when an I/O error occurs
	 * @see net.roydesign.mac.MRJAdapter#getFileCreator
	 */
	public String getMacCreator() throws IOException
	{
		return MRJAdapter.getFileCreator(executable);
	}

	/**
	 * Get a Mac OS resource file from the application bundle. Resource files
	 * are stored in the folder Contents/Resources inside the bundle. This
	 * method will locate the requested resource only if it's located at the
	 * top level of the Resources folder. It returns a <code>File</code>
	 * object that can be used to read the resource.
	 * @param resource the name of the resource file
	 * @return the resource file
	 * @exception FileNotFoundException when the resource can't be found
	 * @see net.roydesign.mac.MRJAdapter#getBundleResource
	 */
	public File getMacBundleResource(String resource) throws FileNotFoundException
	{
		return MRJAdapter.getBundleResource(resource);
	}

	/**
	 * Get a Mac OS resource file from the application bundle. Resource files
	 * are stored in the folder Contents/Resources inside the bundle. This
	 * method will locate the requested resource only if it's located in the
	 * specified subfolder of the Resources folder. It returns a <code>File</code>
	 * object that can be used to read the resource.
	 * @param resource the name of the resource file
	 * @param subFolder the name of the subfolder of Resources
	 * @return the resource file
	 * @exception FileNotFoundException when the resource can't be found
	 * @see net.roydesign.mac.MRJAdapter#getBundleResource
	 */
	public File getMacBundleResource(String resource, String subFolder)
		throws FileNotFoundException
	{
		return MRJAdapter.getBundleResource(resource, subFolder);
	}
}
