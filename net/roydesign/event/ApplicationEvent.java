/*******************************************************************************

	$Id: ApplicationEvent.java,v 1.9 2005/02/25 04:01:33 steve Exp $
	
	File:		ApplicationEvent.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2003-2004 Steve Roy <sroy@roydesign.net>

	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>

	Change History:
	08/08/03	Created this file - Steve
	08/27/03	Added handling of the new Reopen Application event - Steve
	11/25/03    Added support for action commands - Steve

*******************************************************************************/

package net.roydesign.event;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Implementation of an event as broadcasted by <code>MRJAdapter</code> and
 * <code>Application</code>. This is a subclass of <code>ActionEvent</code> so
 * that these events can be passed on to <code>ActionListeners</code> such
 * as <code>javax.swing.Action</code>. The action listener can simply cast
 * the <code>ActionEvent</code> to <code>ApplicationEvent</code> as needed.
 * <p>
 * There are six types of application events. An action listener can distinguish
 * between them by checking the value returned by the <code>getType()</code>
 * method. In the case of Open Document and Print Document events, the file
 * object representing the target of the event can be retrieved with the
 * <code>getFile()</code> method.
 * <p>
 * Here is a detailed explanation of the various types of event.
 * <ul>
 * <li>About: The About menu item has been selected.</li>
 * <li>Preferences: The Preferences menu item has been selected.</li>
 * <li>Open Application: This is received by the application right after
 * it was launched.</li>
 * <li>Reopen Application: This is received by the application when it's
 * already running and the user makes it forward by trying to launch it
 * again.</li>
 * <li>Quit Application: This is sent to the application to signify that
 * it should exit immediately.</li>
 * <li>Open Document: This is used to pass files to be opened or used in
 * some way by the application. This message can be sent to the application
 * at any time. If the application is launched by clicking one of its
 * documents, this event is received immediately after launching in place
 * of the Open Application event.</li>
 * <li>Print Document: This is used to pass files, usually documents created
 * by the application, to be printed. If the document must be opened to be
 * printed, it should be closed back immediately afterward.</li>
 * </ul>
 *
 * @see net.roydesign.mac.MRJAdapter
 * @see net.roydesign.app.Application
 * 
 * @version MRJ Adapter 1.0.9
 */
public class ApplicationEvent extends ActionEvent
{
	/**
	 * The type designating an About event.
	 */
	public static final int ABOUT = 1;

	/**
	 * The type designating a Preferences event.
	 */
	public static final int PREFERENCES = 2;

	/**
	 * The type designating an Open Application event.
	 */
	public static final int OPEN_APPLICATION = 3;

	/**
	 * The type designating a Quit Application event.
	 */
	public static final int QUIT_APPLICATION = 4;

	/**
	 * The type designating an Open Document event.
	 */
	public static final int OPEN_DOCUMENT = 5;

	/**
	 * The type designating a Print Document event.
	 */
	public static final int PRINT_DOCUMENT = 6;

	/**
	 * The type designating a Reopen Application event.
	 */
	public static final int REOPEN_APPLICATION = 7;

	/**
	 * The type of this event.
	 */
	private int type;

	/**
	 * The file associated with this event.
	 */
	private File file;

	/**
	 * Construct an application event.
	 * @param source the source of the event
	 * @param type the type of the event
	 */
	public ApplicationEvent(Object source, int type)
	{
		this(source, type, (File)null);
	}

	/**
	 * Construct an application event.
	 * @param source the source of the event
	 * @param type the type of the event
	 * @param actionCommand the action command
	 */
	public ApplicationEvent(Object source, int type, String actionCommand)
	{
		this(source, type, null, actionCommand);
	}

	/**
	 * Construct an application event.
	 * @param source the source of the event
	 * @param type the type of the event
	 * @param file the file associated with the event
	 */
	public ApplicationEvent(Object source, int type, File file)
	{
		this(source, type, file, "");
	}

	/**
	 * Construct an application event.
	 * @param source the source of the event
	 * @param type the type of the event
	 * @param file the file associated with the event
	 * @param actionCommand the action command
	 */
	public ApplicationEvent(Object source, int type, File file, String actionCommand)
	{
		super(source, ActionEvent.ACTION_PERFORMED, actionCommand, 0);
		switch (type)
		{
			case ABOUT:
			case PREFERENCES:
			case OPEN_APPLICATION:
			case REOPEN_APPLICATION:
			case QUIT_APPLICATION:
				if (file != null)
					throw new IllegalArgumentException("adapter event ID can't include a file");
				break;
			case OPEN_DOCUMENT:
			case PRINT_DOCUMENT:
				if (file == null)
					throw new IllegalArgumentException("adapter event ID requires a file");
				break;
		}
		this.type = type;
		this.file = file;
	}

	/**
	 * Get the type of this event.
	 * @return the type of the event
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Get the file associated with this event.
	 * @return the file associated with the event
	 */
	public File getFile()
	{
		return file;
	}
}
