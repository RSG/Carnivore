package org.rsg.carnivore.cache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JFileChooser;

import org.rsg.carnivore.CarniUtilities;
import org.rsg.carnivore.CarnivorePacket;
import org.rsg.carnivore.Constants;
import org.rsg.carnivore.gui.GUI;
import org.rsg.carnivore.gui.Menu;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.Log;

public class OfflineLogger implements Serializable {
	private static final long serialVersionUID = Constants.VERSION;
	public String filename;
	public boolean isLogging = false;
	BufferedWriter bw;

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final OfflineLogger INSTANCE = new OfflineLogger();
    private OfflineLogger() {}
    public static OfflineLogger instance() {
        return INSTANCE;
    }
	
    ///////////////////////////////////////////////////////////////////////////
    //public API
	public boolean start() {
		if(openSaveDialog()) {
	        try {
				bw = new BufferedWriter(new FileWriter(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}

			return setEnabledStatus(true);
		}
		return setEnabledStatus(false);
	}

	public boolean stop() {
		if(isLogging) {
			save();
		}
		return setEnabledStatus(false);
	}

	public void add(CarnivorePacket p) {
		//Log.debug("["+OfflineCache.class.getName()+"] adding packet to \"" + filename + "\"...");
		if(isLogging) {
			try {
				bw.write(p.toString() + Constants.LINE_SEPARATOR);
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    ///////////////////////////////////////////////////////////////////////////
    //private
    private boolean openSaveDialog() {
	    //Handle open button action.
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(LibUtilities.getCurrentPath());	//sets to app's directory
		fc.setDialogTitle("Log to Text File");			//sets title of window
		File f = CarniUtilities.findSuitableFilenameToSave("Log", Constants.LOG_SUFFIX); //suggests a filename
		fc.setSelectedFile(f);				
		int returnVal = fc.showSaveDialog(null);			//launches dialog

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			filename = fc.getSelectedFile().toString();
			Log.debug("["+OfflineLogger.class.getName()+"] save path set to: " + filename);
			return true;
		} else {
			Log.debug("["+OfflineLogger.class.getName()+"] Open command cancelled by user");
			return false;
		}
	}
	
	private boolean setEnabledStatus(boolean b) {
		isLogging = b;
		
		if(b) {
			GUI.instance().setHeadStrobe(Constants.IMAGES_HEAD_LOGGING);
			Menu.instance().disableAllExcept(Menu.instance().menuItem_log_stop);
		} else {
			Menu.instance().revertEnabledStatusToNormal();		
		}
		
		GUI.instance().image_logo.setSelected(b);
		return b;
	}
	
	//SAVE
	private void save() {
		Log.debug("["+getClass().getName()+"] saving to file \"" + filename + "\"...");
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
