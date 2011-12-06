/*******************************************************************************

	$Id: DocumentFile.java,v 1.10 2005/02/25 04:01:33 steve Exp $
	
	File:		DocumentFile.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>

	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>

	Change History:
	02/12/03	Created this file - Steve
	03/31/03	Modified openWith() to call into ApplicationFile instead - Steve
	06/17/03	Use net.roydesign.mac.MRJAdapter - Steve

*******************************************************************************/

package net.roydesign.io;

import java.io.File;
import java.io.IOException;

import net.roydesign.mac.MRJAdapter;

/**
 * <p>A document file is a file meant to be opened in an application. Most
 * platforms have their own way of binding a document to an application,
 * and this class attempts to incorporate the smarts for those varied
 * mechanisms. Of interest is the fact that all methods in this class
 * are cross-platform, even the methods specific to a particular OS. Those
 * methods will simply not do anything, or will act on an internal member
 * variable. In any case, this allows your code to be free of ugly platform
 * checks.</p>
 *
 * <p>In particular, this class supports the Mac OS type/creator binding
 * mechanism. Even though the methods are specific to the Mac OS, they can
 * be called on any platform. For example, calling <code>setMacCreator("ABCD")</code>
 * won't put this information in the file system as it would on Mac OS, but
 * it will still be tracked internally, so that a subsequent call to
 * <code>getMacCreator()</code> will return the set value. This ensures that
 * your code runs unchanged on all platforms.</p>
 *
 * <p>This class also exposes methods to easily change the filename extension
 * of the document on disk. This is meant as an equivalent to the Mac specific
 * type/creator facility so that the type and binding of a document can be
 * set on platforms other that Mac OS.</p>
 *
 * <p>The document can be renamed, and this translates into the file
 * on disk to be renamed as well. This is a convenience over having to use
 * the <code>File.renameTo(File)</code> method and its associated pitfalls.</p>
 *
 * <p>Finally this class also makes it easy to open a document, either as
 * though the user had double-clicked it in the file system, using its default
 * binding, or with a given application of your choice. This functionality is
 * typically highly platform dependent and error prone. Using this class makes
 * it simple. All you have to do is <code>new DocumentFile(myFile).open()</code>.</p>
 * 
 * @version MRJ Adapter 1.0.9
 */
public class DocumentFile
{
	/**
	 * The name of the OS, from the os.name system property.
	 */
	private static final String osName = System.getProperty("os.name");

	/**
	 * The file represented by this document.
	 */
	File file;

	/**
	 * The Mac OS creator code of the file.
	 */
	private String macCreator = "";

	/**
	 * The Mac OS type code of the file.
	 */
	private String macType = "";

	/**
	 * Construct a document file with a path. This is identical
	 * to the <code>java.io.File</code> constructor.
	 * @param path the document path
	 */
	public DocumentFile(String path)
	{
		this.file = new File(path);
	}

	/**
	 * Construct a document file with a parent directory and the
	 * name or subpath of a child document file.  This is identical
	 * to the <code>java.io.File</code> constructor.
	 * @param parent the parent directory
	 * @param child the child directory path or file name
	 */
	public DocumentFile(String parent, String child)
	{
		this.file = new File(parent, child);
	}

	/**
	 * Construct a document file with a parent directory and the
	 * name or subpath of a child document file.  This is identical
	 * to the <code>java.io.File</code> constructor.
	 * @param parent the parent directory
	 * @param child the child directory path or file name
	 */
	public DocumentFile(File parent, String child)
	{
		this.file = new File(parent, child);
	}

	/**
	 * Construct a document file with a <code>java.io.File</code>
	 * object. Note that the given file object is not used internally.
	 * @param file the file object
	 */
	public DocumentFile(File file)
	{
		// We don't use the passed File object, because
		// we want to have full control over it
		this(file.getPath());
	}

	/**
	 * Open the document in its native application. If the application
	 * can't be identified or found, this method will return false.
	 * You should handle this by bringing up a file dialog for the
	 * user to locate an application to use for opening the file. You
	 * can use the class <code>ApplicationDialog</code> for this.
	 * Currently, this method supports all flavors of Mac OS and Windows.
	 * On all other platforms, the value <code>false</code> is returned
	 * so you can take appropriate action, which should actually be the
	 * same action that you would take if you were on Mac OS/Windows and
	 * the method returned <code>false</code>.
	 * @return whether the file was opened successfully or not
	 * @exception IOException when an I/O error occurs
	 */
	public boolean open() throws IOException
	{
		if (MRJAdapter.mrjVersion >= 3.0f)
		{
			try
			{
				// On Mac OS X, use 'open' on the command line
				Process p = Runtime.getRuntime().exec(new String[] {"open", file.getAbsolutePath()});
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
			// On classic Mac OS, use MRJToolkit to find the app and then
			// pass it the document using Runtime.exec()
			File app = MRJAdapter.findApplication(MRJAdapter.getFileCreator(file));

			// If what we found is a packaged app, go back up to the
			// main folder of the bundle
			File folder = new File(app.getParent());
			String n = folder.getName();
			if (n.equals("MacOS") || n.equals("MacOSClassic"))
			{
				folder = new File(folder.getParent());
				if (folder.getName().equals("Contents") && new File(folder, "Info.plist").exists())
				{
					folder = new File(folder.getParent());
					if (folder.getName().endsWith(".app"))
						app = folder;
				}
			}

			// Open the document
			/** @todo In Classic, this fails at passing the file to an *already running* OS X app */
			Runtime.getRuntime().exec(new String[] {app.getAbsolutePath(), file.getAbsolutePath()});
		}
		else if (osName.startsWith("Windows"))
		{
			try
			{
				// On Windows, use 'cmd /c start' on the command line
				/** @todo Is this going to work on all flavors of Windows above 3.1? */
				Process p = Runtime.getRuntime().exec(new String[]
					{"cmd", "/c", "start", "\"\"", file.getAbsolutePath()});
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
			/** @todo What to do? What about Linux, Solaris, etc? */
			return false;
		}

		// We get here when the document was opened successfully,
		// so be happy and let the user know
		return true;
	}

	/**
	 * Open the document with the given application file. If the operation
	 * fails or an error occurs, this method will return false.
	 * @param application the application file to use to open the document
	 * @return whether the file was opened successfully or not
	 * @exception IOException when an I/O error occurs
	 */
	public boolean openWith(ApplicationFile application) throws IOException
	{
		return application.openDocument(this);
	}

	/**
	 * Open the document with the given application. If the operation
	 * fails or an error occurs, this method will return false.
	 * @param application the application to use to open the document
	 * @return whether the file was opened successfully or not
	 * @exception IOException when an I/O error occurs
	 */
	public boolean openWith(File application) throws IOException
	{
		return openWith(new ApplicationFile(application));
	}

	/**
	 * Set the type code of the file as used on Mac OS and Mac OS X.
	 * On all other platforms, this method does nothing to the file
	 * on disk but still keeps track of the assigned type internally
	 * so that <code>getMacType()</code> returns the assigned value.
	 * @param type the Mac type code to assign
	 * @exception IOException when an I/O error occurs
	 * @see net.roydesign.mac.MRJAdapter#setFileType
	 */
	public void setMacType(String type) throws IOException
	{
		this.macType = type;
		MRJAdapter.setFileType(file, type);
	}

	/**
	 * Get the type code of a file as used on Mac OS and Mac OS X.
	 * On all other platforms, this method will returned the value set
	 * in a previous call to <code>setMacType()</code>. Otherwise an
	 * empty string is returned.
	 * @return the Mac type code of the file
	 * @exception IOException when an I/O error occurs
	 * @see net.roydesign.mac.MRJAdapter#getFileType
	 */
	public String getMacType() throws IOException
	{
		String t = MRJAdapter.getFileType(file);
		if (t.length() == 0 && macType != null)
			return macType;
		return t;
	}

	/**
	 * Set the creator code of the file as used on Mac OS and Mac OS X.
	 * On all other platforms, this method does nothing to the file
	 * on disk but still keeps track of the assigned creator internally
	 * so that <code>getMacCreator()</code> returns the assigned value.
	 * @param creator the Mac creator code to assign
	 * @exception IOException when an I/O error occurs
	 * @see net.roydesign.mac.MRJAdapter#setFileCreator
	 */
	public void setMacCreator(String creator) throws IOException
	{
		this.macCreator = creator;
		MRJAdapter.setFileCreator(file, creator);
	}

	/**
	 * Get the creator code of a file as used on Mac OS and Mac OS X.
	 * On all other platforms, this method will returned the value set
	 * in a previous call to <code>setMacCreator()</code>. Otherwise an
	 * empty string is returned.
	 * @return the Mac creator code of the file
	 * @exception IOException when an I/O error occurs
	 * @see net.roydesign.mac.MRJAdapter#getFileCreator
	 */
	public String getMacCreator() throws IOException
	{
		String c = MRJAdapter.getFileCreator(file);
		if (c.length() == 0 && macCreator != null)
			return macCreator;
		return c;
	}

	/**
	 * Set the creator code of the file as used on Mac OS and Mac OS X.
	 * On all other platforms, this method does nothing to the file
	 * on disk but still keeps track of the assigned creator internally
	 * so that <code>getMacCreator()</code> returns the assigned value.
	 * @param creator the Mac creator code to assign
	 * @param type the Mac type code to assign
	 * @exception IOException when an I/O error occurs
	 * @see net.roydesign.mac.MRJAdapter#setFileCreatorAndType
	 */
	public void setMacCreatorAndType(String creator, String type) throws IOException
	{
		this.macCreator = creator;
		this.macType = type;
		MRJAdapter.setFileCreatorAndType(file, creator, type);
	}

	/**
	 * Set the name extension of the file as used on many platforms to
	 * designate the file type and the application binding. The
	 * extension must not start with a period. Since the file will
	 * be renamed on disk, it's up to you to verify if a file
	 * with the new title and extension already exists prior to calling
	 * this method. An I/O exception will be thrown if the document
	 * can't be renamed for whatever reason.
	 * @param extension the new extension of the document
	 * @exception IOException if the method fails to rename the document
	 */
	public void setExtension(String extension) throws IOException
	{
		StringBuffer b = new StringBuffer();
		b.append(getTitle());
		if (extension != null && extension.length() > 0)
		{
			b.append('.');
			b.append(extension);
		}
		File f = new File(file.getParent(), b.toString());
		if (!file.renameTo(f))
			throw new IOException("failed to rename file");
		this.file = f;
	}

	/**
	 * Get the name extension of a file as used on many platforms to
	 * designate the file type and the application binding. The extension
	 * is the name of the document on disk, minus the title and not
	 * including the period.
	 * @return the name extension of the document
	 * @exception IOException when an I/O error occurs
	 */
	public String getExtension() throws IOException
	{
		String n = file.getName();
		int pos = n.lastIndexOf('.');
		if (pos != -1 && pos + 1 != n.length())
			return n.substring(pos + 1);
		return "";
	}

	/**
	 * Set the title of the document file. This method sets the title
	 * of the document, which corresponds to the name of the document
	 * file minus its filename extension. Since the file will be
	 * renamed on disk, it's up to you to verify if a file with the
	 * new title (and extension) already exists prior to calling this
	 * method. An I/O exception will be thrown if the document
	 * can't be renamed for whatever reason.
	 * @param title the new title of the document
	 * @return the new title of the document
	 * @exception IOException if the method fails to rename the document
	 */
	public void setTitle(String title) throws IOException
	{
		if (title == null || title.length() == 0)
			throw new IllegalArgumentException("title can't be null or zero length");
		StringBuffer b = new StringBuffer();
		b.append(title);
		String ext = getExtension();
		if (ext != null && ext.length() > 0)
		{
			b.append('.');
			b.append(ext);
		}
		File f = new File(file.getParent(), b.toString());
		if (!file.renameTo(f))
			throw new IOException("failed to rename file");
		this.file = f;
	}

	/**
	 * Get the title of the document file. The title of the document
	 * is the name of the file on disk minus the filename extension.
	 * @return the title of the document
	 * @exception IOException when an I/O error occurs
	 */
	public String getTitle() throws IOException
	{
		String n = file.getName();
		int pos = n.lastIndexOf('.');
		if (pos != -1 && pos != 0 && pos + 1 != n.length())
			return n.substring(0, pos);
		return n;
	}

	/**
	 * Set the title and extension of the document file. Since the
	 * file will be renamed on disk, it's up to you to verify if a file
	 * with the new title and extension already exists prior to calling
	 * this method. An I/O exception will be thrown if the document
	 * can't be renamed for whatever reason.
	 * @param title the new title of the document
	 * @param extension the new extension of the document
	 * @exception IOException if the method fails to rename the document
	 */
	public void setTitleAndExtension(String title, String extension) throws IOException
	{
		if (title == null || title.length() == 0)
			throw new IllegalArgumentException("title can't be null or zero length");
		StringBuffer b = new StringBuffer();
		b.append(title);
		if (extension != null && extension.length() > 0)
		{
			b.append('.');
			b.append(extension);
		}
		File f = new File(file.getParent(), b.toString());
		if (!file.renameTo(f))
			throw new IOException("failed to rename file");
		this.file = f;
	}

	/**
	 * Get the file object representing this document on disk.
	 * @return the file object for this document
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Get the path to the document file on disk.
	 * @return the path to the document
	 * @see java.io.File#getPath
	 */
	public String getPath()
	{
		return file.getPath();
	}

	/**
	 * Get the absolute path to the document file on disk.
	 * @return the absolute path to the document
	 * @see java.io.File#getAbsolutePath
	 */
	public String getAbsolutePath()
	{
		return file.getAbsolutePath();
	}

	/**
	 * Get the canonical path to the document file on disk.
	 * @return the canonical path to the document
	 * @see java.io.File#getCanonicalPath
	 */
	public String getCanonicalPath() throws IOException
	{
		return file.getCanonicalPath();
	}
}
