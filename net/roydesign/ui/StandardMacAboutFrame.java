/*******************************************************************************

	$Id: StandardMacAboutFrame.java,v 1.6 2005/02/25 04:01:34 steve Exp $
	
	File:		StandardMacAboutFrame.java
	Author:		Steve Roy
	Copyright:	Copyright (c) 2004 Steve Roy <sroy@roydesign.net>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.

	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://www.roydesign.net/artisticlicense.html>
	
	Change History:
	02/20/04	Created this header - Steve
	04/20/04    Fixed setCredits() to set the caret position to 0 and to
				accept null text - Steve
	08/12/04	Added setCreditsPreferredSize() - Steve

*******************************************************************************/

package net.roydesign.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkListener;

/**
 * Java and cross-platform implementation of an About box similar to the
 * standard About panel built into the Cocoa framework on Mac OS X. This
 * implementation is based on a <code>JFrame</code>. It centers itself
 * automatically on the screen and supports credits in RTF, HTML or plain
 * text formats.
 * 
 * @version MRJ Adapter 1.0.9
 */
public class StandardMacAboutFrame extends JFrame
{
	/**
	 * The label holding the application icon.
	 */
	private JLabel applicationIconLabel;
	
	/**
	 * The field holding the application name.
	 */
	private JTextArea applicationNameField;
	
	/**
	 * The field holding the version string.
	 */
	private JTextArea versionField;
	
	/**
	 * The field displaying the credits.
	 */
	private JEditorPane creditsField;
	
	/**
	 * The scroll pane that holds the credits field.
	 */
	private JScrollPane creditsScrollPane;
	
	/**
	 * The field displaying the copyright string.
	 */
	private JTextArea copyrightField;
	
	/**
	 * The version string of the application.
	 */
	private String applicationVersion;
	
	/**
	 * The version string of the build.
	 */
	private String buildVersion;
	
	/**
	 * The hyperlink listener for the credits field, for when its content type
	 * is text/html.
	 */
	private HyperlinkListener hyperlinkListener;
	
	/**
	 * Construct a standard Mac about frame.
	 * @param applicationName the name of the application
	 * @param applicationVersion the version string of the application
	 */
	public StandardMacAboutFrame(String applicationName, String applicationVersion)
	{
		super();
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JPanel c = (JPanel)getContentPane();
		c.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 100;
		gbc.insets.top = 5;
		gbc.insets.bottom = 5;
		
		// Application icon
		gbc.gridy = 0;
		applicationIconLabel = new JLabel();
		c.add(applicationIconLabel, gbc);
		
		// Application name
		gbc.gridy = 1;
		applicationNameField = new JTextArea("java");
		applicationNameField.setEditable(false);
		applicationNameField.setOpaque(false);
		applicationNameField.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		c.add(applicationNameField, gbc);
		
		// Version
		gbc.gridy = 2;
		versionField = new JTextArea("Version x.x");
		versionField.setEditable(false);
		versionField.setOpaque(false);
		Font f = new Font("Lucida Grande", Font.PLAIN, 10);
		versionField.setFont(f);
		c.add(versionField, gbc);
		
		// Credits
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		creditsField = new JEditorPane();
		creditsField.setMargin(new Insets(2, 4, 2, 4));
		creditsField.setEditable(false);
		creditsScrollPane = new JScrollPane(creditsField,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Border bo = creditsScrollPane.getBorder();
		Insets i = bo.getBorderInsets(creditsScrollPane);
		creditsScrollPane.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(0, -i.left + 1, 0, -i.right + 1), bo));
		creditsScrollPane.setPreferredSize(new Dimension(100, 150));
		c.add(creditsScrollPane, gbc);
		
		// Copyright
		gbc.gridy = 4;
		gbc.insets.bottom = 32;
		gbc.fill = GridBagConstraints.NONE;
		copyrightField = new JTextArea(" ");
		copyrightField.setEditable(false);
		copyrightField.setOpaque(false);
		copyrightField.setFont(f);
		c.add(copyrightField, gbc);
		
		// Set the initial state of the controls
		applicationIconLabel.setVisible(false);
		creditsScrollPane.setVisible(false);
		if (applicationName != null)
			applicationNameField.setText(applicationName);
		this.applicationVersion = applicationVersion;
		if (applicationVersion != null)
			versionField.setText(applicationVersion);
		
		// Size and center the frame
		packAndCenter();
	}
	
	/**
	 * Set the icon of the application to be displayed.
	 * @param applicationIcon the icon of the application
	 */
	public void setApplicationIcon(Icon applicationIcon)
	{
		applicationIconLabel.setIcon(applicationIcon);
		applicationIconLabel.setVisible(applicationIcon != null);
		packAndCenter();
	}
	
	/**
	 * Set the name of the application to be displayed. If the application
	 * version is null, the string "java" will be shown.
	 * @param applicationName the name of the application
	 */
	public void setApplicationName(String applicationName)
	{
		applicationNameField.setText(
			applicationName != null ? applicationName : "java");
	}
	
	/**
	 * Set the version of the application to be displayed. If the application
	 * version is null, the string "Version x.x" will be shown.
	 * @param applicationVersion the version string of the application
	 */
	public void setApplicationVersion(String applicationVersion)
	{
		this.applicationVersion = applicationVersion;
		applyVersion();
	}
	
	/**
	 * Set the version of the build to be displayed. This string appears
	 * between parentheses prepended by a "v" immediately after and on the same
	 * line as the application version. If the build version is null, the
	 * parentheses and "v" are not shown.
	 * @param buildVersion the version string of the build
	 */
	public void setBuildVersion(String buildVersion)
	{
		this.buildVersion = buildVersion;
		applyVersion();
	}
	
	/**
	 * Internal method to apply the version string when either the application
	 * version or the build version change.
	 */
	private void applyVersion()
	{
		StringBuffer b = new StringBuffer();
		if (applicationVersion != null)
		    b.append(applicationVersion);
		else
			b.append("Version x.x");
		if (buildVersion != null)
		{
			b.append(" (v");
			b.append(buildVersion);
			b.append(")");
		}
		versionField.setText(b.toString());
	}
	
	/**
	 * Set the text to be displayed in the credits area of the About frame.
	 * This area is only visible if the credits string is non-null. The content
	 * type must be one of text/plain, text/rtf, or text/html. If the type is
	 * text/html and there are hyperlinks in the text, you should register an
	 * hyperlink listener with the method <code>setHyperlinkListener()<code>.
	 * @param credits the credits string to display
	 * @param contentType the content type of the credits string
	 * @see #setHyperlinkListener
	 */
	public void setCredits(String credits, String contentType)
	{
		if (credits != null)
			creditsField.setContentType(contentType);
		creditsField.setText(credits != null ? credits : "");
		creditsField.setCaretPosition(0);
		creditsScrollPane.setVisible(credits != null);
		packAndCenter();
	}
	
	/**
	 * Set the preferred size of the credits area of the About frame. By
	 * default, the preferred size is 100 by 150.
	 * @param preferredSize the preferred size to use
	 */
	public void setCreditsPreferredSize(Dimension preferredSize)
	{
		creditsScrollPane.setPreferredSize(preferredSize);
		packAndCenter();
	}
	
	/**
	 * Set the hyperlink listener to be called when hyperlinks are clicked in
	 * the credits field. To remove it, pass <code>null</code>.
	 * @param l the hyperlink listener
	 */
	public void setHyperlinkListener(HyperlinkListener l)
	{
		if (this.hyperlinkListener != null)
			creditsField.removeHyperlinkListener(this.hyperlinkListener);
		this.hyperlinkListener = l;
		if (l != null)
			creditsField.addHyperlinkListener(l);
	}
	
	/**
	 * Set the coyright text to be displayed.
	 * @param copyright the copyright text to display
	 */
	public void setCopyright(String copyright)
	{
		copyrightField.setText(copyright != null ? copyright : " ");
		packAndCenter();
	}
	
	/**
	 * Internal method to pack and center the About frame.
	 */
	private void packAndCenter()
	{
		pack();
		setSize(285, getSize().height);
		Dimension ss = getToolkit().getScreenSize();
		Dimension fs = getSize();
		setLocation((ss.width - fs.width) / 2, (ss.height - fs.height) / 4);
	}
}
