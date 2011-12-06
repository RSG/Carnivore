/*******************************************************************************

	$Id: MRJAdapter.java,v 1.19 2005/02/25 04:01:33 steve Exp $
	
	File:		MRJAdapter.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>

	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>

	Change History:
	03/11/03	Created this file - Steve
	03/18/03	Removed workaround in getFileCreator() for pre-release MRJ 4,
				changed getBundleResource() to use MRJFileUtils.getResource()
				with MRJ 3 - Steve
	03/31/03	Made the cocoaClassLoader member a ClassLoader instead of
				URLClassLoader so this works with Java 1.1 code - Steve
	06/11/03	Added swingUsesScreenMenuBar() and the set of frameless
				menu bar methods - Steve
	08/19/03	Added the set of isXXXAutomaticallyPresent() methods,
				did some clean up - Steve
	08/27/03	Added handling of the new Reopen Application event - Steve
	08/31/03	Added the setFileLastModified() method - Steve
	09/08/03	Improved parsing of the mrj.version property to better handle
				the 1.4.1 update 1 VM, and any future version - Steve
	11/25/03    Modified the two getBundleResource() methods to use reflection
				with Java 1.3.1 - Steve
	01/14/04	Corrected findFolder() to work with MRJ 3.0/3.1 on OS X - Steve
	02/17/04    Modified setFramelessMenuBar() to create a JFrame if possible
				over a plain AWT frame - Steve
	02/26/04    Added awtUsesScreenMenuBar() and added caching of the value
				returned by swingUsesScreenMenuBar() - Steve
	03/19/04    Added isAppleJDirectAvailable() and getAppleJDirectVersion(),
				renamed the xxxUsesScreenMenuBar() methods to the 'is' form,
				renamed osTypeStringToInt() to fourCharCodeToInt() and
				osTypeIntToString() to intToFourCharCode() - Steve
	04/09/04    Added the VERSION constant - Steve
	04/15/04    Subclassed invisible JFrame so it's not disposed of, fixed
				checkSwingUsingScreenMenuBar() to recognize com.apple.macos.
				useScreenMenuBar with Java 1.4.* - Steve
	05/26/04    Added openFileResourceFork() with support for MacBinary
				Toolkit - Steve
	06/29/04	Added support for BrowserLauncher and fixed openURL() not to
				hang with Apple's 1.4 VM - Steve
	08/10/04	Added calls to setUndecorated() in setFramelessMenuBar() and
				setFramelessJMenuBar() so this trick works with 1.4.2_05 - Steve
	02/23/05	Removed caching of the result of isSwingUsingScreenMenuBar() so
				that it works with runtime changes of the L&F, added explicit
				support for Quaqua in isSwingUsingScreenMenuBar() - Steve

*******************************************************************************/

package net.roydesign.mac;

import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.apple.eio.FileManager;
import com.apple.mrj.MRJFileUtils;
import com.apple.mrj.MRJOSType;

/**
 * <p>This class offers a unique interface to the functionality provided by
 * MRJToolkit with MRJ 1.5, 2.x and 3.x, and com.apple.eawt and com.apple.eio with
 * MRJ 4.x. Some functionality is not present in all versions of MRJ, and this
 * class makes up for it by implementing itself what was missing, ensuring that
 * the methods you use will work on any version of Mac OS, within reason.</p>
 *
 * <p>The class also contains a collection of methods useful for handling Mac OS
 * specific files (like Info.plist and PkgInfo) and converting Mac OS type codes
 * between Strings and integers. These methods are written to not depend on any
 * Mac OS API to ensure that they can be used on any platform, which could
 * happen for example when handling an application bundle that is temporarily
 * sitting on a machine other than a Mac.</p>
 *
 * <p>All methods are designed to behave in a sensible manner when used on
 * platforms other than the Mac. Most of the time, this means doing nothing or
 * returning the same exception as they do on the Mac when an error occurs.
 * Where possible, some methods are functional on all platforms. Check the
 * documentation specific to each method to know where they stand. However,
 * if you really need cross-platform functionality then you should use the
 * higher-level classes in <code>net.roydesign.app</code> and
 * <code>net.roydesign.io</code> which essentially integrate this functionality
 * in a more cross-platform way.</p>
 * 
 * @version MRJ Adapter 1.0.9
 */
public final class MRJAdapter implements MRJFolderConstants
{
	/**
	 * The version of MRJ Adapter.
	 */
	public static final String VERSION = "1.0.9";

	/**
	 * The version of the Java VM.
	 */
	public static float javaVersion;

	/**
	 * The version of the MRJ VM. When not running on Mac OS, its
	 * value will be -1.
	 */
	public static float mrjVersion = -1.0f;
	
	/**
	 * Whether to try using MacBinary Toolkit in the method
	 * <code>openFileResourceFork()</code>. This is <code>true</code> by
	 * default. If MacBinary Toolkit is not available, it will be set to
	 * <code>false</code> automatically after the first call to that method.
	 */
	public static boolean useMacBinaryToolkit = true;
	
	/**
	 * Whether to try using BrowserLauncher in the method <code>openURL()</code>.
	 * This is <code>true</code> by default. If BrowserLauncher is not
	 * available, it will be set to <code>false</code> automatically after the
	 * first call to that method.
	 */
	public static boolean useBrowserLauncher = true;
	
	/**
	 * The Cocoa class loader when running on Mac OS X.
	 */
	private static ClassLoader cocoaClassLoader;

	/**
	 * The name of the startup disk.
	 */
	private static String startupDisk;

	/**
	 * The path to the current application on disk.
	 */
	private static String applicationPath;

	/**
	 * The frame that we use to fake the frameless menu bar.
	 */
	private static Frame invisibleFrame;

	/**
	 * The MRJFileUtils.getResource(String) method.
	 */
	private static Method getResourceMethod;

	/**
	 * The MRJFileUtils.getResource(String, String) method.
	 */
	private static Method getResourceSubMethod;

	static
	{
		// Get the version of Java
		String prop = System.getProperty("java.version");
		javaVersion = new Float(prop.substring(0, 3)).floatValue();

		// Get the version of MRJ
		/**
		 * @todo Use java.runtime.version instead
		 * See <http://java.sun.com/j2se/versioning_naming.html>
		 */
		prop = System.getProperty("mrj.version");
		if (prop != null)
		{
			// Take all characters up to the second period,
			// if any, and convert that into a float
			int len = prop.length();
			int pos = prop.indexOf('.');
			if (pos != -1 && pos != len - 1)
				pos = prop.indexOf('.', pos + 1);
			if (pos == -1)
				pos = len;
			mrjVersion = new Float(prop.substring(0, pos)).floatValue();
		}
		
		// Instantiate the Cocoa class loader if we're on Mac OS X
		if (mrjVersion >= 3.0f)
		{
			try
			{
				cocoaClassLoader = new URLClassLoader(
					new URL[] {new URL("file://127.0.0.1/System/Library/Java/")});
			}
			catch (MalformedURLException ex)
			{
				// Nothing to worry about since we control the URL string
			}
		}
	}

	/**
	 * The default constructor is private so this class can't
	 * be instantiated.
	 */
	private MRJAdapter()
	{
	}

	/**
	 * Set the Mac OS type code of the given file. This method does nothing
	 * on other platforms. The type code is a case-sensitive four character
	 * string. Any exceeding character will be ignored and the string will be
	 * padded with spaces if shorter, with the exception of the case where
	 * the string length is zero, in which case the file type will be set
	 * to nothing.
	 * @param file the file to set the type of
	 * @param type the type code
	 * @exception IOException when an I/O error occurs
	 */
	public static void setFileType(File file, String type) throws IOException
	{
		if (mrjVersion >= 4.0f)
			FileManager.setFileType(file.getAbsolutePath(), fourCharCodeToInt(type));
		else if (mrjVersion >= 1.5f)
			MRJFileUtils.setFileType(file, new MRJOSType(fourCharCodeToInt(type)));
		/** @todo Add support for Mac OS bundles on other platforms */
	}

	/**
	 * Get the Mac OS type code of the given file. On other platforms,
	 * this method returns an empty string, which is the same as if
	 * the file type was not set on the Mac platform. A Mac type is
	 * a four character string, unless the file has no type, in which
	 * case an empty string is returned. Note that this method supports
	 * Mac OS style bundles on all platforms, which is useful in the
	 * case where such a bundle is temporarily located on a non-Mac
	 * file system. When this is detected, the method will extract the
	 * type code from the bundle just like Mac OS does natively.
	 * @param file the file to get to type from
	 * @return the type code
	 * @exception IOException when an I/O error occurs
	 */
	public static String getFileType(File file) throws IOException
	{
		if (mrjVersion >= 4.0f)
		{
			long t = FileManager.getFileType(file.getAbsolutePath());
			return intToFourCharCode((int)t);
		}
		else if (file.isDirectory())
		{
			// Check if it's a Mac OS bundle (this should work on any platform)
			File infoPlist = new File(file, "Contents/Info.plist");
			if (infoPlist.exists())
			{
				// The Mac OS X Finder apparently gives priority to the
				// PkgInfo file, so we do the same thing
				File pkgInfo = new File(file, "Contents/PkgInfo");
				if (pkgInfo.exists())
				{
					String t = parsePkgInfo(pkgInfo, "type");
					return t == null ? "" : t;
				}
				else
				{
					String t = parseInfoPlist(infoPlist, "CFBundlePackageType");
					return t == null ? "" : t;
				}
			}
		}
		else if (mrjVersion >= 1.5f)
		{
			MRJOSType t = MRJFileUtils.getFileType(file);
			return t.toInt() == 0 ? "" : t.toString();
		}
		return "";
	}

	/**
	 * Set the Mac OS creator code of the given file. This method does
	 * nothing on other platforms. The creator code is a case-sensitive
	 * four character string. Any exceeding character will be ignored
	 * and the string will be padded with spaces if shorter, with the
	 * exception of the case where the string length is zero, in which
	 * case the file creator will be set to nothing.
	 * @param file the file to set the creator of
	 * @param creator the creator code
	 * @exception IOException when an I/O error occurs
	 */
	public static void setFileCreator(File file, String creator) throws IOException
	{
		if (mrjVersion >= 4.0f)
			FileManager.setFileCreator(file.getAbsolutePath(), fourCharCodeToInt(creator));
		else if (mrjVersion >= 1.5f)
			MRJFileUtils.setFileCreator(file, new MRJOSType(fourCharCodeToInt(creator)));
		/** @todo Add support for Mac OS bundles on other platforms */
	}

	/**
	 * Get the Mac OS creator code of the given file. On other platforms,
	 * this method returns an empty string, which is the same as if
	 * the file creator was not set on the Mac platform. A Mac creator is
	 * a four character string, unless the file has no creator, in which
	 * case an empty string is returned. Note that this method supports
	 * Mac OS style bundles on all platforms, which is useful in the
	 * case where such a bundle is temporarily located on a non-Mac
	 * file system. When this is detected, the method will extract the
	 * creator code from the bundle just like Mac OS does natively.
	 * @param file the file to get to creator from
	 * @return the creator code
	 * @exception IOException when an I/O error occurs
	 */
	public static String getFileCreator(File file) throws IOException
	{
		if (mrjVersion >= 4.0f)
		{
			long c = FileManager.getFileCreator(file.getAbsolutePath());
			return intToFourCharCode((int)c);
		}
		else if (file.isDirectory())
		{
			// Check if it's a Mac OS bundle (this should work on any platform)
			File infoPlist = new File(file, "Contents/Info.plist");
			if (infoPlist.exists())
			{
				// The Mac OS X Finder apparently gives priority to the
				// PkgInfo file, so we do the same thing
				File pkgInfo = new File(file, "Contents/PkgInfo");
				if (pkgInfo.exists())
				{
					String t = parsePkgInfo(pkgInfo, "creator");
					return t == null ? "" : t;
				}
				else
				{
					String t = parseInfoPlist(infoPlist, "CFBundleSignature");
					return t == null ? "" : t;
				}
			}
		}
		else if (mrjVersion >= 1.5f)
		{
			MRJOSType t = MRJFileUtils.getFileCreator(file);
			return t.toInt() == 0 ? "" : t.toString();
		}
		return "";
	}

	/**
	 * Set the Mac OS creator and type codes of the given file. This method
	 * does nothing on other platforms. The creator and type codes are
	 * case-sensitive four character strings. Any exceeding character will
	 * be ignored and the string will be padded with spaces if shorter, with
	 * the exception of the case where the string length is zero, in which
	 * case the file creator will be set to nothing.
	 * @param file the file to set the creator and type of
	 * @param creator the creator code
	 * @param type the type code
	 * @exception IOException when an I/O error occurs
	 */
	public static void setFileCreatorAndType(File file, String creator, String type)
		throws IOException
	{
		if (mrjVersion >= 4.0f)
		{
			FileManager.setFileTypeAndCreator(file.getAbsolutePath(),
				fourCharCodeToInt(type), fourCharCodeToInt(creator));
		}
		else if (mrjVersion >= 1.5f)
		{
			MRJFileUtils.setFileTypeAndCreator(file,
				new MRJOSType(fourCharCodeToInt(type)),
				new MRJOSType(fourCharCodeToInt(creator)));
		}
		/** @todo Add support for Mac OS bundles on other platforms */
	}

	/**
	 * Set the date and time of last modification of the given file. MRJ used
	 * to implement this functionality because the <code>File</code> class in
	 * Java 1.1 didn't. This method will use <code>File.setLastModified()</code>
	 * when running in Java 1.2 or better, on any platform. When running in
	 * Java 1.1 on a non-Mac platform, this method will return <code>false</code>.
	 * @param file the file to set the creator and type of
	 * @param time the new modification time, measured in milliseconds since
	 *			the epoch (00:00:00 GMT, January 1, 1970)
	 * @return whether the operation succeeded or not
	 */
	public static boolean setFileLastModified(File file, long time)
	{
		if (javaVersion >= 1.2f)
			return file.setLastModified(time);
		else if (mrjVersion >= 1.5f)
			return MRJFileUtils.setFileLastModified(file, time);
		return false;
	}
	
	/**
	 * Find a special Mac OS folder. This method locates on disk a folder
	 * designated by the OS for a specific purpose. For example, this can be
	 * the Preferences folder or the Desktop folder. Sometimes such folders
	 * exist in multiple versions for different contexts, which is why the
	 * method requires a domain to be specified. Most of the time, you will
	 * want to use the value <code>MRJFolderConstants.kUserDomain</code>. Then
	 * you have to specify the type of the folder that you're looking for.
	 * Each such folder is identified by a unique code. If there is no constant
	 * defined in <code>MRJFolderConstants</code> for the folder you're looking
	 * for then you should use the other form of this method which takes a
	 * string. This method throws a <code>FileNotFoundException</code>
	 * on other platforms, which is the same as what this method does on Mac OS
	 * when the folder can't be found.
	 * @param domain the domain of the folder
	 * @param type the type constant of the folder to find
	 * @param create whether to create the folder if it doesn't already exist
	 * @return the special folder object, or null
	 * @exception FileNotFoundException when the folder can't be found
	 */
	public static File findFolder(short domain, int type, boolean create)
		throws FileNotFoundException
	{
		if (mrjVersion >= 4.0f)
			return new File(FileManager.findFolder(domain, type, create));
		else if (mrjVersion >= 3.2f)
			return MRJFileUtils.findFolder(domain, new MRJOSType(type), create);
		else if (mrjVersion >= 3.0f)
			return MRJFileUtils.findFolder(domain, new MRJOSType(type));
		else if (mrjVersion >= 1.5f)
			return MRJFileUtils.findFolder(new MRJOSType(type));
		throw new FileNotFoundException();
	}

	/**
	 * Find a special Mac OS folder. This method locates on disk a folder
	 * designated by the OS for a specific purpose. For example, this can be
	 * the Preferences folder or the Desktop folder. Sometimes such folders
	 * exist in multiple versions for different contexts, which is why the
	 * method requires a domain to be specified. Most of the time, you will
	 * want to use the value <code>MRJFolderConstants.kUserDomain</code>. Then
	 * you have to specify the type of the folder that you're looking for.
	 * Each such folder is identified by a unique code. Just like file types
	 * and creators, this code is four characters long and case sensitive.
	 * If there's a constant defined in <code>MRJFolderConstants</code> for the
	 * folder you're looking for then you should use the other form of this
	 * method. Otherwise, pass the type as a string. The format of the type
	 * code will be normalized to be four-characters long if it isn't already.
	 * Look in Folders.h of the Carbon interfaces for possible values to be
	 * passed. This method throws a <code>FileNotFoundException</code>
	 * on other platforms.
	 * @param domain the domain of the folder
	 * @param type the type code of the folder to find
	 * @param create whether to create the folder if it doesn't already exist
	 * @return the special folder object\
	 * @exception FileNotFoundException when the folder can't be found
	 */
	public static File findFolder(short domain, String type, boolean create)
		throws FileNotFoundException
	{
		return findFolder(domain, fourCharCodeToInt(type), create);
	}

	/**
	 * Find a specific Mac OS application. This method locates on disk an
	 * application given its unique creator code, which is four characters
	 * long and case sensitive. The format of the creator code will be
	 * normalized to be four-characters long if it isn't already. This
	 * method throws a <code>FileNotFoundException</code> on other platforms,
	 * which is the same as what this method does on Mac OS when the folder
	 * can't be found.
	 * @param creator the creator code of the application to find
	 * @return the application file object
	 * @exception FileNotFoundException when the application can't be found
	 */
	public static File findApplication(String creator) throws FileNotFoundException
	{
		if (mrjVersion >= 3.0f)
		{
			try
			{
				/** @todo Can we do this with Cocoa instead (not NSAppleScript)? */
				StringBuffer script = new StringBuffer();
				script.append("tell application \"Finder\" to get POSIX path of (application file id \"");
				script.append(creator);
				script.append("\" as alias)");
				return new File(runAppleScript(script.toString()));
			}
			catch (IOException ex)
			{
				// Do nothing and let the FileNotFoundException be thrown instead
			}
		}
		else if (mrjVersion >= 1.5f)
		{
			return MRJFileUtils.findApplication(new MRJOSType(fourCharCodeToInt(creator)));
		}
		throw new FileNotFoundException();
	}

	/**
	 * Get a resource file from the application bundle. Resource files are
	 * stored in the folder Contents/Resources inside the bundle. This
	 * method will locate the requested resource only if it's located at the
	 * top level of the Resources folder. It returns a <code>File</code>
	 * object that can be used to read the resource. This method throws a
	 * <code>FileNotFoundException</code> on other platforms, which is the
	 * same as what this method does on Mac OS when the resource can't be found.
	 * @param resource the name of the resource file
	 * @return the resource file
	 * @exception FileNotFoundException when the resource can't be found
	 */
	public static File getBundleResource(String resource) throws FileNotFoundException
	{
		if (mrjVersion >= 4.0f)
		{
			return new File(FileManager.getResource(resource));
		}
		else if (mrjVersion >= 3.0f)
		{
			try
			{
				if (getResourceMethod == null)
				{
					// We use reflection here because for some unknown reason
					// Apple hasn't included this method in MRJToolkitStubs
					Class cls = Class.forName("com.apple.mrj.MRJFileUtils");
					getResourceMethod = cls.getMethod("getResource",
						new Class[] {String.class});
				}
				return (File)getResourceMethod.invoke(null,
					new Object[] {resource});
			}
			catch (Exception ex)
			{
				// Just let the method throw a FileNotFoundException
			}
		}
		throw new FileNotFoundException();
	}

	/**
	 * Get a resource file from the application bundle. Resource files are
	 * stored in the folder Contents/Resources inside the bundle. This
	 * method will locate the requested resource only if it's located in the
	 * specified subfolder of the Resources folder. It returns a <code>File</code>
	 * object that can be used to read the resource. This method throws a
	 * <code>FileNotFoundException</code> on other platforms, which is the same
	 * as what this method does on Mac OS when the resource can't be found.
	 * @param resource the name of the resource file
	 * @param subFolder the name of the subfolder of Resources
	 * @return the resource file
	 * @exception FileNotFoundException when the resource can't be found
	 */
	public static File getBundleResource(String resource, String subFolder)
		throws FileNotFoundException
	{
		if (mrjVersion >= 4.0f)
		{
			return new File(FileManager.getResource(resource, subFolder));
		}
		else if (mrjVersion >= 3.0f)
		{
			try
			{
				if (getResourceSubMethod == null)
				{
					// We use reflection here because for some unknown reason
					// Apple hasn't included this method in MRJToolkitStubs
					Class cls = Class.forName("com.apple.mrj.MRJFileUtils");
					getResourceSubMethod = cls.getMethod("getResource",
						new Class[] {String.class, String.class});
				}
				return (File)getResourceSubMethod.invoke(null,
					new Object[] {resource, subFolder});
			}
			catch (Exception ex)
			{
				// Just let the method throw a FileNotFoundException
			}
		}
		throw new FileNotFoundException();
	}

	/**
	 * Open the Mac OS resource fork of the given file. The resource fork is
	 * returned as a new unbuffered <code>InputStream</code>, which can be
	 * accessed and read in the usual way. This method is functional as is on
	 * Mac OS X. On classic Mac OS and other platforms, it can only be
	 * functional if Gregory Guerin's excellent MacBinary Toolkit
	 * &lt;<a href="http://www.amug.org/~glguerin" target="_blank">http://www.amug.org/~glguerin</a>&gt;
	 * is installed. Note that MacBinary Toolkit will also be used on Mac OS X,
	 * if it's available. Set <code>useMacBinaryToolkit</code> to <code>false</code>
	 * to turn off this behavior. Finally, resource forks are a feature specific
	 * to HFS (Mac) file systems, but this method also recognizes a common
	 * scheme of other systems used as file servers which store resource forks
	 * in a subdirectory named .HSResource. If the file resource fork doesn't
	 * exist or can't be opened, the method throws a <code>FileNotFoundException</code>.
	 * @param file the file for which to open the resource fork
	 * @return a new unbuffered input stream to read the resource fork
	 * @exception FileNotFoundException when the resource fork can't be opened
	 */
	public static InputStream openFileResourceFork(File file)
		throws FileNotFoundException
	{
		// Try the MacBinary Toolkit method
		if (useMacBinaryToolkit)
		{
			try
			{
				/** @todo We might want to cache some of these reflected objects */
				
				Class fileForkerClass = Class.forName("glguerin.io.FileForker");
				Method setFactoryMethod = fileForkerClass.getMethod("SetFactory",
					new Class[] {String.class});
				Class macPlatformClass = Class.forName("glguerin.util.MacPlatform");
				Method selectFactoryNameMethod = macPlatformClass.getMethod(
					"selectFactoryName", new Class[] {String.class});
				String fctry = (String)selectFactoryNameMethod.invoke(null,
					new Object[] {null});
				setFactoryMethod.invoke(null, new Object[] {fctry});
				
				Method makeOneMethod = fileForkerClass.getMethod("MakeOne", null);
				Object ff = makeOneMethod.invoke(null, null);
				
				Class pathnameClass = Class.forName("glguerin.io.Pathname");
				Constructor pathnameConstructor =
					pathnameClass.getConstructor(new Class[] {File.class});
				Object path = pathnameConstructor.newInstance(new Object[] {file});
				
				Method setTargetMethod = fileForkerClass.getMethod("setTarget",
					new Class[] {pathnameClass});
				setTargetMethod.invoke(ff, new Object[] {path});
				
				Method makeForkInputStreamMethod =
					fileForkerClass.getMethod("makeForkInputStream",
					new Class[] {Boolean.TYPE});
				return (InputStream)makeForkInputStreamMethod.invoke(
					ff, new Object[] {Boolean.TRUE});
			}
			catch (Exception ex)
			{
				// Don't try using MacBinary Toolkit the next time
				useMacBinaryToolkit = false;
				
				// Fall through to other methods
			}
		}
		
		// Try the namedfork trick on Mac OS X
		if (mrjVersion >= 3.0f)
		{
			File rf = new File(file, "/..namedfork/rsrc");
			if (rf.length() > 0)
				return new FileInputStream(rf);
		}
		
		// Try the HSResource folder trick on any platform
		File fo = new File(file.getParent(), ".HSResource");
		File rf = new File(fo, file.getName());
		if (rf.exists())
			return new FileInputStream(rf);
		throw new FileNotFoundException();
	}
	
	/**
	 * Open the given URL in the application that is bound to the specified
	 * protocol. While this method can in theory handle 'file' URLs, the
	 * <code>DocumentFile.open()</code> method is preferred. This method is
	 * functional as is on classic Mac OS and Mac OS X. On other platforms, it
	 * can only be functional if Eric Albert's excellent BrowserLauncher
	 * &lt;<a href="http://browserlauncher.sourceforge.net">http://browserlauncher.sourceforge.net</a>&gt;
	 * is installed. Note that BrowserLauncher will also be used on classic
	 * Mac OS and Mac OS X, if it's available. Set <code>useBrowserLauncher</code>
	 * to <code>false</code> to turn off this behavior. This method throws an
	 * <code>IOException</code> if opening URLs is not supported on the current
	 * platform, which is the same as what this method does on Mac OS when the
	 * URL can't be opened.
	 * @param url the URL string to be opened
	 * @exception IOException when an I/O error occurs
	 */
	public static void openURL(String url) throws IOException
	{
		// Try the BrowserLauncher method
		if (useBrowserLauncher && mrjVersion < 4.0f)
		{
			try
			{
				/** @todo We might want to cache some of these reflected objects */
				
				Class browserLauncherClass = Class.forName("edu.stanford.ejalbert.BrowserLauncher");
				Method openURLMethod = browserLauncherClass.getMethod("openURL",
					new Class[] {String.class});
				openURLMethod.invoke(null, new Object[] {url});
				return;
			}
			catch (Exception ex)
			{
				// Don't try using BrowserLauncher the next time
				useBrowserLauncher = false;
				
				// Fall through to other methods
			}
		}
		
		// Try the Apple APIs
		if (mrjVersion >= 4.0f)
		{
			// We don't use FileManager.openURL() because it is known to never
			// return on occasions, seemingly at random
		//	FileManager.openURL(url);
			Runtime.getRuntime().exec(new String[] {"open", url});
		}
		else if (mrjVersion >= 2.2f)
		{
			MRJFileUtils.openURL(url);
		}
		else if (mrjVersion >= 1.5f)
		{
			File finder = MRJFileUtils.findApplication(new MRJOSType("MACS"));
			Runtime.getRuntime().exec(new String[] {finder.getPath(), url});
		}
		else
		{
			throw new IOException("openURL not supported on this platform");
		}
	}

	/**
	 * Get whether the About menu item is automatically present in the menu
	 * bar of the current underlying platform.
	 * @return whether the About menu item is automatically present
	 */
	public static boolean isAboutAutomaticallyPresent()
	{
		return mrjVersion != -1.0f;
	}

	/**
	 * Add an About action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to a
	 * <code>net.roydesign.event.ApplicationEvent</code>.
	 * This method does nothing on other platforms.
	 * @param l the action listener
	 * @see net.roydesign.event.ApplicationEvent
	 */
	public static void addAboutListener(ActionListener l)
	{
		addAboutListener(l, null);
	}

	/**
	 * Add an About action listener that receives events from the given source.
	 * Your application shouldn't normally call this method. Use the single
	 * parameter variant of this method instead.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public static void addAboutListener(ActionListener l, Object source)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().addAboutListener(l, source);
		else if (mrjVersion >= 1.5f)
			MRJ23EventProxy.getInstance().addAboutListener(l, source);
	}

	/**
	 * Remove an About action listener. This method does nothing on other platforms.
	 * @param l the action listener
	 */
	public static void removeAboutListener(ActionListener l)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().removeAboutListener(l);
		else if (mrjVersion >= 1.5f)
			MRJ23EventProxy.getInstance().removeAboutListener(l);
	}

	/**
	 * Get whether the Preferences menu item is automatically present in the menu
	 * bar of the current underlying platform.
	 * @return whether the Preferences menu item is automatically present
	 */
	public static boolean isPreferencesAutomaticallyPresent()
	{
		return mrjVersion >= 3.0f;
	}

	/**
	 * Add a Preferences action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to a
	 * <code>net.roydesign.event.ApplicationEvent</code>.
	 * This method does nothing on other platforms.
	 * @param l the action listener
	 * @see net.roydesign.event.ApplicationEvent
	 */
	public static void addPreferencesListener(ActionListener l)
	{
		addPreferencesListener(l, null);
	}

	/**
	 * Add a Preferences action listener that receives events from the given source.
	 * Your application shouldn't normally call this method. Use the single
	 * parameter variant of this method instead.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public static void addPreferencesListener(ActionListener l, Object source)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().addPreferencesListener(l, source);
		else if (mrjVersion >= 3.0f)
			MRJ23EventProxy.getInstance().addPreferencesListener(l, source);
	}

	/**
	 * Remove a Preferences action listener. This method does nothing on other platforms.
	 * @param l the action listener
	 */
	public static void removePreferencesListener(ActionListener l)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().removePreferencesListener(l);
		else if (mrjVersion >= 3.0f)
			MRJ23EventProxy.getInstance().removePreferencesListener(l);
	}

	/**
	 * Get whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS and other platforms, this method always returns false.
	 * @return whether the Preferences menu item is enabled
	 */
	public static boolean isPreferencesEnabled()
	{
		if (mrjVersion >= 4.0f)
			return MRJ4EventProxy.getInstance().isPreferencesEnabled();
		else if (mrjVersion >= 3.0f)
			return MRJ23EventProxy.getInstance().isPreferencesEnabled();
		return false;
	}

	/**
	 * Set whether the Preferences menu item is enabled or not. This menu
	 * item is automatically provided by the OS on Mac OS X. On classic
	 * Mac OS and other platforms, this method does nothing.
	 * @param enabled whether the menu item is enabled
	 */
	public static void setPreferencesEnabled(boolean enabled)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().setPreferencesEnabled(enabled);
		else if (mrjVersion >= 3.0f)
			MRJ23EventProxy.getInstance().setPreferencesEnabled(enabled);
	}

	/**
	 * Add an Open Application action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to a
	 * <code>net.roydesign.event.ApplicationEvent</code>.
	 * This method does nothing on other platforms.
	 * @param l the action listener
	 * @see net.roydesign.event.ApplicationEvent
	 */
	public static void addOpenApplicationListener(ActionListener l)
	{
		addOpenApplicationListener(l, null);
	}

	/**
	 * Add an Open Application action listener that receives events from the given source.
	 * Your application shouldn't normally call this method. Use the single
	 * parameter variant of this method instead.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public static void addOpenApplicationListener(ActionListener l, Object source)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().addOpenApplicationListener(l, source);
		else if (mrjVersion >= 2.2f)
			MRJ23EventProxy.getInstance().addOpenApplicationListener(l, source);
	}

	/**
	 * Remove an Open Application action listener. This method does nothing on other platforms.
	 * @param l the action listener
	 */
	public static void removeOpenApplicationListener(ActionListener l)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().removeOpenApplicationListener(l);
		else if (mrjVersion >= 2.2f)
			MRJ23EventProxy.getInstance().removeOpenApplicationListener(l);
	}

	/**
	 * Add a Reopen Application action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to a
	 * <code>net.roydesign.event.ApplicationEvent</code>.
	 * This method does nothing on other platforms.
	 * @param l the action listener
	 * @see net.roydesign.event.ApplicationEvent
	 */
	public static void addReopenApplicationListener(ActionListener l)
	{
		addReopenApplicationListener(l, null);
	}

	/**
	 * Add a Reopen Application action listener that receives events from the given source.
	 * Your application shouldn't normally call this method. Use the single
	 * parameter variant of this method instead.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public static void addReopenApplicationListener(ActionListener l, Object source)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().addReopenApplicationListener(l, source);
		else if (mrjVersion >= 2.2f)
			MRJ23EventProxy.getInstance().addReopenApplicationListener(l, source);
	}

	/**
	 * Remove a Reopen Application action listener. This method does nothing on other platforms.
	 * @param l the action listener
	 */
	public static void removeReopenApplicationListener(ActionListener l)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().removeReopenApplicationListener(l);
		else if (mrjVersion >= 2.2f)
			MRJ23EventProxy.getInstance().removeReopenApplicationListener(l);
	}

	/**
	 * Get whether the Quit menu item is automatically present in the menu
	 * bar of the current underlying platform.
	 * @return whether the Quit menu item is automatically present
	 */
	public static boolean isQuitAutomaticallyPresent()
	{
		return mrjVersion >= 3.0f;
	}

	/**
	 * Add a Quit Application action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to a
	 * <code>net.roydesign.event.ApplicationEvent</code>.
	 * This method does nothing on other platforms.
	 * @param l the action listener
	 * @see net.roydesign.event.ApplicationEvent
	 */
	public static void addQuitApplicationListener(ActionListener l)
	{
		addQuitApplicationListener(l, null);
	}

	/**
	 * Add a Quit Application action listener that receives events from the given source.
	 * Your application shouldn't normally call this method. Use the single
	 * parameter variant of this method instead.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public static void addQuitApplicationListener(ActionListener l, Object source)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().addQuitApplicationListener(l, source);
		else if (mrjVersion >= 1.5f)
			MRJ23EventProxy.getInstance().addQuitApplicationListener(l, source);
	}

	/**
	 * Remove a Quit Application action listener. This method does nothing on other platforms.
	 * @param l the action listener
	 */
	public static void removeQuitApplicationListener(ActionListener l)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().removeQuitApplicationListener(l);
		else if (mrjVersion >= 1.5f)
			MRJ23EventProxy.getInstance().removeQuitApplicationListener(l);
	}

	/**
	 * Add an Open Document action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to a
	 * <code>net.roydesign.event.ApplicationEvent</code> which allows to get
	 * a reference to the file associated with the event. This method does
	 * nothing on other platforms.
	 * @param l the action listener
	 * @see net.roydesign.event.ApplicationEvent
	 */
	public static void addOpenDocumentListener(ActionListener l)
	{
		addOpenDocumentListener(l, null);
	}

	/**
	 * Add an Open Document action listener that receives events from the given source.
	 * Your application shouldn't normally call this method. Use the single
	 * parameter variant of this method instead.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public static void addOpenDocumentListener(ActionListener l, Object source)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().addOpenDocumentListener(l, source);
		else if (mrjVersion >= 1.5f)
			MRJ23EventProxy.getInstance().addOpenDocumentListener(l, source);
	}

	/**
	 * Remove an Open Document action listener. This method does nothing on other platforms.
	 * @param l the action listener
	 */
	public static void removeOpenDocumentListener(ActionListener l)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().removeOpenDocumentListener(l);
		else if (mrjVersion >= 1.5f)
			MRJ23EventProxy.getInstance().removeOpenDocumentListener(l);
	}

	/**
	 * Add a Print Document action listener. When the listener is called, the
	 * <code>ActionEvent</code> received can be cast to a
	 * <code>net.roydesign.event.ApplicationEvent</code> which allows to get
	 * a reference to the file associated with the event. This method does
	 * nothing on other platforms.
	 * @param l the action listener
	 * @see net.roydesign.event.ApplicationEvent
	 */
	public static void addPrintDocumentListener(ActionListener l)
	{
		addPrintDocumentListener(l, null);
	}

	/**
	 * Add a Print Document action listener that receives events from the given source.
	 * Your application shouldn't normally call this method. Use the single
	 * parameter variant of this method instead.
	 * @param l the action listener
	 * @param source the source to use when firing the event
	 */
	public static void addPrintDocumentListener(ActionListener l, Object source)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().addPrintDocumentListener(l, source);
		else if (mrjVersion >= 1.5f)
			MRJ23EventProxy.getInstance().addPrintDocumentListener(l, source);
	}

	/**
	 * Remove a Print Document action listener. This method does nothing on
	 * other platforms.
	 * @param l the action listener
	 */
	public static void removePrintDocumentListener(ActionListener l)
	{
		if (mrjVersion >= 4.0f)
			MRJ4EventProxy.getInstance().removePrintDocumentListener(l);
		else if (mrjVersion >= 1.5f)
			MRJ23EventProxy.getInstance().removePrintDocumentListener(l);
	}
	
	/**
	 * Check whether Apple's JDirect API is available in the underlying
	 * platform. Note that there are multiple versions of JDirect with
	 * implementation differences. If you require a specific version of JDirect,
	 * use the <code>getAppleJDirectVersion()</code> method instead.
	 * @return whether Apple's JDirect API is available
	 */
	public static boolean isAppleJDirectAvailable()
	{
		return getAppleJDirectVersion() != -1;
	}
	
	/**
	 * Get the version of Apple's JDirect API implemented by the underlying
	 * platform. If Apple's JDirect is unavailable, this method returns -1.
	 * @return the version of Apple's JDirect implementation
	 */
	public static int getAppleJDirectVersion()
	{
		if (mrjVersion >= 3.0f && mrjVersion < 4.0f)
			return 3;
		else if (mrjVersion >= 2.1f && mrjVersion < 3.0f)
			return 2;
		else if (mrjVersion >= 1.5f && mrjVersion < 2.1f)
			return 1;
		return -1;
	}
	
	/**
	 * Check whether AWT uses the screen menu bar or not. This will
	 * only return true on Mac OS. This method always returns false on
	 * other platforms.
	 * @return whether AWT uses the screen menu bar
	 */
	public static boolean isAWTUsingScreenMenuBar()
	{
		return mrjVersion != -1.0f;
	}
	
	/**
	 * Check whether Swing uses the screen menu bar or not. This will
	 * only return true on Mac OS when the Mac-specific look and feel is
	 * used and the appropriate system properties are set that activate
	 * the frameless menu bar. This method always returns false on
	 * other platforms.
	 * @return whether Swing uses the screen menu bar
	 */
	public static boolean isSwingUsingScreenMenuBar()
	{
		boolean result = false;
		LookAndFeel laf = UIManager.getLookAndFeel();
		String id = laf.getID();
		String name = laf.getClass().getName();
		if (id.equals("Mac") || id.equals("Aqua"))
		{
			// The original Mac L&F on classic Mac OS was not using the screen
			// menu bar, but its ID was "Platinum" so we're fine
			result = true;
		}
		else if (mrjVersion >= 4.0f)
		{
			String prop = System.getProperty("apple.laf.useScreenMenuBar");
			if (prop == null)
				prop = System.getProperty("com.apple.macos.useScreenMenuBar");
			result = prop != null && prop.equalsIgnoreCase("true") &&
				(name.equals("apple.laf.AquaLookAndFeel") ||
				name.startsWith("ch.randelshofer.quaqua"));
		}
		else if (mrjVersion >= 3.0f)
		{
			String prop = System.getProperty("com.apple.macos.useScreenMenuBar");
			result = prop != null && prop.equalsIgnoreCase("true") &&
				(name.equals("com.apple.mrj.swing.MacLookAndFeel") ||
				name.startsWith("ch.randelshofer.quaqua"));
		}
		else if (mrjVersion != -1.0f)
		{
			// The Mac OS L&F by Luca Lutterotti uses the screen menu bar
			result = name.equals("it.unitn.ing.swing.plaf.macos.MacOSLookAndFeel");
		}
		return result;
	}
	
	/**
	 * Set the AWT frameless menu bar. This menu bar is shown when
	 * no frame is visible. This state is normal for a Mac application
	 * whereas Java doesn't have any built-in way to do this because
	 * its menu bars must be attached to frames. On platforms other
	 * than Mac OS and Mac OS X, this method sets the menu bar
	 * internally so that it will be properly returned by a subsequent
	 * call to <code>getFramelessMenuBar()<code>.
	 * @param menuBar the AWT menu bar to use as frameless menu bar
	 */
	public static void setFramelessMenuBar(MenuBar menuBar)
	{
		// Create the frame if needed
		if (invisibleFrame == null)
		{
			try
			{
				Class.forName("javax.swing.JFrame");
				invisibleFrame = new InvisibleJFrame();
			}
			catch (Exception ex)
			{
				invisibleFrame = new Frame();
			}
			if (mrjVersion >= 4.0f)
			{
				// We use reflection here because the setUndecorated() method
				// only exists in Java 1.4 and up
				try
				{
					Method mthd = invisibleFrame.getClass().getMethod("setUndecorated",
						new Class[] {Boolean.TYPE});
					mthd.invoke(invisibleFrame, new Object[] {Boolean.TRUE});
				}
				catch (Exception ex)
				{
					// Shouldn't happen since we've checked mrjVersion
				}
				invisibleFrame.setLocation(0, 10000);
				invisibleFrame.setSize(0, 0);
				invisibleFrame.pack();
			}
			else if (mrjVersion != -1.0f)
			{
				invisibleFrame.setLocation(0, 10000);
				invisibleFrame.pack();
			}
		}
		if (mrjVersion >= 4.0f)
		{
			if (!invisibleFrame.isVisible())
				invisibleFrame.setVisible(true);
		}
		else if (mrjVersion != -1.0f)
		{
			if (!invisibleFrame.isVisible())
				invisibleFrame.setVisible(true);
		}

		// Set the menu bar
		invisibleFrame.setMenuBar(menuBar);
	}

	/**
	 * Get the AWT frameless menu bar. This method is functional on
	 * all platforms.
	 * @return the AWT frameless menu bar
	 */
	public static MenuBar getFramelessMenuBar()
	{
		if (invisibleFrame != null)
			return invisibleFrame.getMenuBar();
		return null;
	}

	/**
	 * Set the Swing frameless menu bar. This menu bar is shown when
	 * no frame is visible. This state is normal for a Mac application
	 * whereas Java doesn't have any built-in way to do this because
	 * its menu bars must be attached to frames. Note that this method
	 * won't have any visual effect if the application doesn't use the
	 * Mac-specific look and feel and if the appropriate system
	 * properties are not set that activate the screen menu bar. Also,
	 * on platforms other than Mac OS and Mac OS X, this method sets
	 * the menu bar internally so that it will be properly returned by
	 * a subsequent call to <code>getFramelessMenuBar()<code>.
	 * @param menuBar the Swing menu bar to use as frameless menu bar
	 */
	public static void setFramelessJMenuBar(JMenuBar menuBar)
	{
		// Make sure the invisible frame is the right class
		if (invisibleFrame != null &&
			!(invisibleFrame instanceof JFrame))
		{
			invisibleFrame.dispose();
			invisibleFrame = null;
		}

		// Create the frame if needed
		if (isSwingUsingScreenMenuBar())
		{
			if (mrjVersion >= 4.0f)
			{
				if (invisibleFrame == null)
				{
					// We use reflection here because the setUndecorated() method
					// only exists in Java 1.4 and up
					invisibleFrame = new InvisibleJFrame();
					try
					{
						Method mthd = invisibleFrame.getClass().getMethod("setUndecorated",
							new Class[] {Boolean.TYPE});
						mthd.invoke(invisibleFrame, new Object[] {Boolean.TRUE});
					}
					catch (Exception ex)
					{
						// Shouldn't happen since we've checked mrjVersion
					}
					invisibleFrame.setSize(0, 0);
					invisibleFrame.pack();
				}
				if (!invisibleFrame.isVisible())
					invisibleFrame.setVisible(true);
			}
			else if (mrjVersion != -1.0f)
			{
				if (invisibleFrame == null)
				{
					invisibleFrame = new InvisibleJFrame();
					invisibleFrame.setLocation(0, 10000);
					invisibleFrame.pack();
				}
				if (!invisibleFrame.isVisible())
					invisibleFrame.setVisible(true);
			}
		}
		else
		{
			if (invisibleFrame == null)
				invisibleFrame = new InvisibleJFrame();
		}

		// Set the menu bar
		((JFrame)invisibleFrame).setJMenuBar(menuBar);
		invisibleFrame.pack();
	}

	/**
	 * Get the Swing frameless menu bar. This method is functional on
	 * all platforms.
	 * @return the Swing frameless menu bar
	 */
	public static JMenuBar getFramelessJMenuBar()
	{
		if (invisibleFrame instanceof JFrame)
			return ((JFrame)invisibleFrame).getJMenuBar();
		return null;
	}

	/**
	 * Convert a four character code string to an integer. This method will
	 * normalize the given string in one of two ways. If the length of the
	 * string is zero, it will return the integer value 0, which provides
	 * support for unsetting entirely the creator or type of a file. On the
	 * other hand, if the given string is longer than zero, then it will be
	 * normalized to a length of four characters before being converted to an
	 * integer. If the string is too long it will be truncated and if it's too
	 * short it will be padded with spaces.
	 * @param code the four character code string
	 * @return the four character code as an integer
	 */
	public static int fourCharCodeToInt(String code)
	{
		byte[] bytes = new byte[4];
		int len = code.length();
		if (len > 0)
		{
			if (len > 4)
				len = 4;
			byte[] bs = code.getBytes();
			System.arraycopy(bs, 0, bytes, 0, Math.min(4, bs.length));
		}
		int val = 0;
		for (int i = 0; i < bytes.length; i++)
		{
			if (i > 0)
				val <<= 8;
			val |= bytes[i] & 0xFF;
		}
		return val;
	}

	/**
	 * Convert a four character code integer to a string. This method will
	 * normalize the returned string in one of two ways. If the given integer
	 * is zero, then an empty string will be returned, which provides support
	 * for files that do not have a creator or type. Otherwise, the given
	 * integer is unpacked into a string with a length of four.
	 * @param code the four character code integer
	 * @return the four character code as a string
	 */
	public static String intToFourCharCode(int code)
	{
		if (code == 0)
			return "";
		byte[] bytes = {(byte)(code >> 24), (byte)(code >> 16), (byte)(code >> 8),(byte) code};
		return new String(bytes);
	}

	/**
	 * Utility method to read and parse the content of the given Mac OS
	 * PkgInfo file, trying to extract the value of the given key. The
	 * method only supports two keys, "type" and "creator", since that's
	 * the only info that can be found in the PkgInfo file. If the given
	 * key is not found, the value <code>null</code> is returned.
	 * @param file the PkgInfo file
	 * @param key the key to look for
	 * @return the value of the key, or null
	 * @exception IOException when an I/O error occurs
	 */
	public static String parsePkgInfo(File file, String key) throws IOException
	{
		// This is nothing fancy but it does the job for now
		/** @todo Should we set the encoding explicitly instead of using the platform default? */
		String val = null;
		LineNumberReader r = new LineNumberReader(new FileReader(file));
		String line = r.readLine();
		if (line != null)
		{
			if (key.equals("type"))
			{
				if (line.length() >= 4)
					val = line.substring(0, 4);
			}
			else if (key.equals("creator"))
			{
				if (line.length() >= 8)
					val = line.substring(4, 8);
			}
		}
		r.close();
		return val;
	}

	/**
	 * Utility method to read and parse the content of the given Mac OS
	 * Info.plist file, trying to extract the value of the given key. The
	 * keys that can be found in the Info.plist are defined by Apple. If
	 * the given key is not found, the value <code>null</code> is returned.
	 * @param file the Info.plist file
	 * @param key the key to look for
	 * @return the value of the key, or null
	 * @exception IOException when an I/O error occurs
	 */
	public static String parseInfoPlist(File file, String key) throws IOException
	{
		// This is nothing fancy but it does the job for now
		/** @todo Should we set the encoding explicitly instead of using the platform default? */
		String val = null;
		LineNumberReader r = new LineNumberReader(new FileReader(file));
		String line;
		while ((line = r.readLine()) != null)
		{
			if (line.indexOf(key) != -1)
			{
				if ((line = r.readLine()) != null)
				{
					line = line.trim();
					val = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));
				}
				break;
			}
		}
		r.close();
		return val;
	}

	/**
	 * Utility method to read and parse the content of the given Mac OS
	 * MRJApp.properties file, trying to extract the value of the given key.
	 * The keys that can be found in the Info.plist are defined by Apple.
	 * If the given key is not found, the value <code>null</code> is returned.
	 * @param file the MRJApp.properties file
	 * @param key the key to look for
	 * @return the value of the key, or null
	 * @exception IOException when an I/O error occurs
	 */
	public static String parseMRJAppProperties(File file, String key) throws IOException
	{
		FileInputStream in = new FileInputStream(file);
		Properties props = new Properties();
		props.load(in);
		in.close();
		return props.getProperty(key);
	}

	/**
	 * Get the name of the startup disk. This method will return the name
	 * of the startup disk, whether you are running on classic Mac OS or
	 * Mac OS X. On all other platforms, it throws an <code>IOException</code>.
	 * @return the name of the startup disk
	 * @exception IOException if an I/O error occurs
	 */
	public static String getStartupDisk() throws IOException
	{
		if (startupDisk == null)
		{
			if (mrjVersion >= 3.0f)
			{
				/** @todo Can we do this with Cocoa instead (not NSAppleScript)? */
				startupDisk = runAppleScript("tell application \"Finder\" to get name of startup disk");
			}
			else if (mrjVersion != -1.0f)
			{
				String path = MRJFileUtils.findFolder(new MRJOSType("macs")).getPath();
				startupDisk = path.substring(1, path.indexOf('/', 1));
			}
			else
			{
				throw new IOException();
			}
		}
		return startupDisk;
	}

	/**
	 * Get the path to the application on disk. This method returns the full
	 * path of the application. Currently it only works with Mac OS X. If we
	 * figure out a way to make it work on classic Mac OS, we could make this
	 * method public. On all other platforms, it throws an <code>IOException</code>.
	 * @return the path to the application on disk
	 * @exception IOException if an I/O error occurs
	 */
	private static String getApplicationPath() throws IOException
	{
		if (applicationPath == null)
		{
			if (mrjVersion >= 3.0f)
			{
				try
				{
					Class nsBundleClass =
						Class.forName("com.apple.cocoa.foundation.NSBundle", true, cocoaClassLoader);
					Method mainBundleMethod = nsBundleClass.getMethod("mainBundle", null);
					Object bndl = mainBundleMethod.invoke(null, null);
					Method bundlePathMethod = nsBundleClass.getMethod("bundlePath", null);
					applicationPath = (String)bundlePathMethod.invoke(bndl, null);
				}
				catch (Exception ex)
				{
					throw new IOException(ex.getMessage());
				}
			}
			else if (mrjVersion != -1.0f)
			{
				/** @todo Does anyone has some briefly elegant JDirect code to do this? */
				throw new IOException();
			}
			else
			{
				throw new IOException();
			}
		}
		return applicationPath;
	}

	/**
	 * Execute the given AppleScript script. This methods compiles and runs
	 * the script using the osascript tool in a shell. Because of this, it
	 * works only on Mac OS X.
	 * @param script the AppleScript script to execute
	 * @return the result
	 * @exception IOException if an I/O error occurs
	 */
	private static String runAppleScript(String script) throws IOException
	{
		Process p = Runtime.getRuntime().exec(new String[] {"osascript", "-e", script});
		InputStreamReader r = new InputStreamReader(p.getInputStream());
		StringBuffer b = new StringBuffer();
		char[] buf = new char[128];
		int n;
		while ((n = r.read(buf)) != -1)
			b.append(buf, 0, n);
		r.close();
		return b.toString().trim();
	}
	
	/**
	 * Implementation of the invisible JFrame.
	 */
	private static class InvisibleJFrame extends JFrame
	{
		InvisibleJFrame()
		{
			super();
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		}
	}
}
