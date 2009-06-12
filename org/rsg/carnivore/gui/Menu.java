package org.rsg.carnivore.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.rsg.carnivore.CarnivorePE;
import org.rsg.carnivore.Constants;
import org.rsg.carnivore.Preferences;
import org.rsg.carnivore.cache.OfflineCache;
import org.rsg.carnivore.cache.OfflineLogger;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.Log;

public class Menu extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = Constants.VERSION;
    private JMenu menu_file;
    public JMenuItem 	menuItem_about, menuItem_exit, 
    					menuItem_playback_start, menuItem_playback_stop, 
    					menuItem_record_start, menuItem_record_stop,
    					menuItem_log_start, menuItem_log_stop;
    public JCheckBoxMenuItem menuItem_console;

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final Menu INSTANCE = new Menu();
    public static Menu instance() {
        return INSTANCE;
    }

    ///////////////////////////////////////////////////////////////////////////
	//constructor
	private Menu() {
		
		//"FILE" MENU
		menu_file = new JMenu(Constants.MENU_FILE);
		add(menu_file);
		
		if (!LibUtilities.isMac()){ //gets embedded into mac's Finder via net.roydesign.mac.MRJAdapter
			menuItem_about = packMenuItem(menu_file, Constants.MENU_ABOUT, '\0', true);				//about
			menu_file.addSeparator();
		}

		menuItem_console = packCheckBoxMenuItem(menu_file, Constants.MENU_SHOW_CONSOLE, 'N', true);	//console
		menu_file.addSeparator();
		menuItem_playback_start = packMenuItem(menu_file, Constants.MENU_PLAYBACK_START, 'P', true);	//playback start
		menuItem_playback_stop = packMenuItem(menu_file, Constants.MENU_PLAYBACK_STOP, '\0', false);	//playback stop
		menu_file.addSeparator();
		menuItem_record_start = packMenuItem(menu_file, Constants.MENU_RECORD_START, 'R', true);		//record start
		menuItem_record_stop = packMenuItem(menu_file, Constants.MENU_RECORD_STOP, '\0', false);		//record stop
		menu_file.addSeparator();
		menuItem_log_start = packMenuItem(menu_file, Constants.MENU_LOG_START, 'L', true);				//log start
		menuItem_log_stop = packMenuItem(menu_file, Constants.MENU_LOG_STOP, '\0', false);				//log stop
		
		if (!LibUtilities.isMac()){ //gets embedded into mac's Finder via net.roydesign.mac.MRJAdapter
			menu_file.addSeparator();
			menuItem_exit = packMenuItem(menu_file, Constants.MENU_EXIT, 'Q', true);					//quit
		}
	}
	
    ///////////////////////////////////////////////////////////////////////////
	//public API
	public void actionPerformed(ActionEvent ae) {
		
		//ABOUT
		if(ae.getActionCommand().equals(Constants.MENU_ABOUT)) {
			About.instance().showMe();

		//CONSOLE
		} else if (ae.getActionCommand().equals(Constants.MENU_SHOW_CONSOLE)) { 
			Log.debug("["+this.getClass().getName()+"] MENU_SHOW_CONSOLE");
			//show
			if(!Console.instance().isVisible()) {
				Console.instance().updateMagneticPosition();	
				Console.instance().showMe();	
				Preferences.instance().put(Constants.SHOULD_SHOW_CONSOLE, true);
			//hide
			} else {
				Console.instance().hideMe();
				Preferences.instance().put(Constants.SHOULD_SHOW_CONSOLE, false);
			}
			
		//PLAYBACK START
		} else if (ae.getActionCommand().equals(Constants.MENU_PLAYBACK_START)) { 
			Log.debug("["+this.getClass().getName()+"] MENU_PLAYBACK_START");
			OfflineCache.instance().startPlaybackUsingDialogBox();

		//PLAYBACK STOP
		} else if (ae.getActionCommand().equals(Constants.MENU_PLAYBACK_STOP)) { 
			Log.debug("["+this.getClass().getName()+"] MENU_PLAYBACK_STOP");
			OfflineCache.instance().stopPlayback();

		//RECORD START
		} else if (ae.getActionCommand().equals(Constants.MENU_RECORD_START)) { 
			Log.debug("["+this.getClass().getName()+"] MENU_RECORD_START");			
			OfflineCache.instance().startRecording();

		//RECORD STOP
		} else if (ae.getActionCommand().equals(Constants.MENU_RECORD_STOP)) { 
			Log.debug("["+this.getClass().getName()+"] MENU_RECORD_STOP");
			OfflineCache.instance().stopRecording();

		//LOGGER START
		} else if (ae.getActionCommand().equals(Constants.MENU_LOG_START)) { 
			Log.debug("["+this.getClass().getName()+"] MENU_LOG_START");
			OfflineLogger.instance().start();

		//LOGGER STOP
		} else if (ae.getActionCommand().equals(Constants.MENU_LOG_STOP)) { 
			Log.debug("["+this.getClass().getName()+"] MENU_LOG_STOP");
			OfflineLogger.instance().stop();

		} else if (ae.getActionCommand().equals(Constants.MENU_EXIT)) {
			CarnivorePE.instance().quit();
		}
	}

	public void confirmConsoleIsClosed() {
		menuItem_console.setState(false);
	}

	public void revertEnabledStatusToNormal() {
		menuItem_playback_start.setEnabled(true);
		menuItem_playback_stop.setEnabled(false);
		menuItem_record_start.setEnabled(true);
		menuItem_record_stop.setEnabled(false);
		menuItem_log_start.setEnabled(true);
		menuItem_log_stop.setEnabled(false);
	}
	
	public void disableAllExcept(JMenuItem i) {
		//System.err.println("[Menu] disableAllExcept i:"+i);
		disableUnlessSame(menuItem_playback_start, i);
		disableUnlessSame(menuItem_playback_stop, i);
		disableUnlessSame(menuItem_record_start, i);
		disableUnlessSame(menuItem_record_stop, i);
		disableUnlessSame(menuItem_log_start, i);
		disableUnlessSame(menuItem_log_stop, i);
	}
	
    ///////////////////////////////////////////////////////////////////////////
	//private methods
    private KeyStroke keyStroke(char c) {
    	return KeyStroke.getKeyStroke(c, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }

    private JMenuItem packMenuItem(JMenu m, String s, char c, boolean b) {
    	JMenuItem i = new JMenuItem(s);
    	i.addActionListener(this);       
   		i.setEnabled(b);
   		if(c != '\0')
    		i.setAccelerator(keyStroke(c));
   		m.add(i);
   		return i;
    }
    
    private JCheckBoxMenuItem packCheckBoxMenuItem(JMenu m, String s, char c, boolean b) {
    	JCheckBoxMenuItem i = new JCheckBoxMenuItem(s);
    	i.addActionListener(this);       
   		i.setEnabled(b);
   		if(c != '\0')
    		i.setAccelerator(keyStroke(c));
   		m.add(i);
   		return i;
    }

    private void disableUnlessSame(JMenuItem a, JMenuItem b) {
		if(a != b) {
			a.setEnabled(false);
		} else {
			a.setEnabled(true);			
		}
	}
}
