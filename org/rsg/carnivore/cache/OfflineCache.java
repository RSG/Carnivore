package org.rsg.carnivore.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Stack;

import javax.swing.JFileChooser;

import org.rsg.carnivore.CarniUtilities;
import org.rsg.carnivore.CarnivorePacket;
import org.rsg.carnivore.Constants;
import org.rsg.carnivore.OfflineThread;
import org.rsg.carnivore.gui.GUI;
import org.rsg.carnivore.gui.Menu;
import org.rsg.lib.ErrorMessages;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.Log;

public class OfflineCache extends java.util.Stack<TimestampedObject> implements Serializable {
	private static final long serialVersionUID = Constants.VERSION;
	public String filename;
	public int marker = 0;
	private long recordingStartTime;

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final OfflineCache INSTANCE = new OfflineCache();
    private OfflineCache() {}
    public static OfflineCache instance() {
        return INSTANCE;
    }
	
	///////////////////////////////////////////////////////////////////////////
    //recording API
	public boolean isRecording = false;
	public boolean startRecording() {
		this.clear();
		if(openSaveDialog()) {
			recordingStartTime = System.currentTimeMillis();
			return setRecordingStatus(true);
		}
		return setRecordingStatus(false);
	}

	public boolean stopRecording() {
		if(isRecording) {
			save();
		}
		return setRecordingStatus(false);
	}

	public void add(CarnivorePacket p) {
		//Log.debug("["+OfflineCache.class.getName()+"] adding packet to \"" + filename + "\"...");
		if(isRecording) {
			this.add(new TimestampedObject(currentTime(), p));
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
    //playback API
	public boolean isPlaying = false;
	public boolean startPlaybackUsingDialogBox() {
		if(openLoadDialog()) {
			return startPlayback();
		}
		stopPlayback();
		return setPlayingStatus(false);
	}

	public boolean startPlaybackUsingFilename(String filename) {
		this.filename = filename;
		return startPlayback();
	}

	public boolean startPlayback() {
		if (load()) {
			Log.debug("[OfflineCache] loaded size: " + this.size());
			if (size() <= 1) {
				Log.window(ErrorMessages.OFFLINECACHE_TOO_SMALL);
				return stopPlayback();
			}

			OfflineThread.instance().startMe();
			return setPlayingStatus(true);
		}

		stopPlayback();
		return setPlayingStatus(false);
	}
	
	public boolean stopPlayback() {
		if(isPlaying) {
			OfflineThread.instance().stopMe();
		}
		return setPlayingStatus(false);
	}
	
	public TimestampedObject getMarked() {
		return get(marker);
	}
	
	public boolean isAtEnd() {
		if(marker >= size())
			return true;
		return false;
	}

	public void resetToBeginning() {
		marker = 0;
	}
		
    ///////////////////////////////////////////////////////////////////////////
    //private
    private boolean openSaveDialog() {
	    //Handle open button action.
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(LibUtilities.getCurrentPath());	//sets to app's directory
		fc.setDialogTitle("Save Packet Session");			//sets title of window
		File f = CarniUtilities.findSuitableFilenameToSave("Carnivore_Session", Constants.SERIALIZED_SUFFIX); //suggests a filename
		fc.setSelectedFile(f);								
		int returnVal = fc.showSaveDialog(null);			//launches dialog

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			filename = fc.getSelectedFile().toString();
			Log.debug("["+OfflineCache.class.getName()+"] save path set to: " + filename);
			return true;
		} else {
			Log.debug("["+OfflineCache.class.getName()+"] Open command cancelled by user");
			return false;
		}
	}

    private boolean openLoadDialog() {
	    //Handle open button action.
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(LibUtilities.getCurrentPath());	//sets to app's directory
		fc.setDialogTitle("Load Packet Session");			//sets title of window		
		
		//filter file types
	    ExampleFileFilter filter = new ExampleFileFilter();
	    filter.addExtension(Constants.SERIALIZED_SUFFIX_SHORT);
	    filter.setDescription("Carnivore Session");
	    fc.setFileFilter(filter);
	    
		int returnVal = fc.showOpenDialog(null);			//launches dialog

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			filename = fc.getSelectedFile().toString();
			Log.debug("["+OfflineCache.class.getName()+"] open: " + filename);
			return true;
		} else {
			Log.debug("["+OfflineCache.class.getName()+"] Open command cancelled by user");
			return false;
		}
	}
    
	private boolean setRecordingStatus(boolean b) {
		isRecording = b;
		
		if(b) {
			GUI.instance().setHeadStrobe(Constants.IMAGES_HEAD_RECORDING);
			Menu.instance().disableAllExcept(Menu.instance().menuItem_record_stop);
		} else {
			Menu.instance().revertEnabledStatusToNormal();		
		}
		
		GUI.instance().image_logo.setSelected(b);
		return b;
	}

	private boolean setPlayingStatus(boolean b) {
		isPlaying = b;
		
		if(b) {
			GUI.instance().setHeadStrobe(Constants.IMAGES_HEAD_PLAYBACK);
			Menu.instance().disableAllExcept(Menu.instance().menuItem_playback_stop);
		} else {
			Menu.instance().revertEnabledStatusToNormal();		
		}
		
		GUI.instance().image_logo.setSelected(b);
		return b;
	}
	
	//LOAD
	@SuppressWarnings("unchecked")
	private boolean load() {
		Log.debug("["+OfflineCache.class.getName()+"] loading from file \"" + filename + "\"...");

		FileInputStream fin = null;
		try {
			fin = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			Log.window(ErrorMessages.CANT_OPEN_OFFLINECACHE, "FileNotFoundException from FileInputStream");
			return false;
		}

		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(fin);
		} catch (IOException e) {
			Log.window(ErrorMessages.CANT_OPEN_OFFLINECACHE, "IOException from ObjectInputStream");
			return false;
		}

		java.util.Stack<TimestampedObject> temp = null;
		try {
			temp = (Stack<TimestampedObject>) ois.readObject();
		} catch (IOException e) {
			Log.window(ErrorMessages.CANT_OPEN_OFFLINECACHE, "IOException from readObject");
			return false;
		} catch (ClassCastException e) { //this usually means that the .ser file was created by an earlier version
			Log.window(ErrorMessages.CANT_OPEN_OFFLINECACHE, "ClassCastException from readObject");
			return false;
		} catch (ClassNotFoundException e) {
			Log.window(ErrorMessages.CANT_OPEN_OFFLINECACHE, "ClassNotFoundException from readObject");
			return false;
		}

		try {
			ois.close();
		} catch (IOException e) {
			Log.window(ErrorMessages.CANT_OPEN_OFFLINECACHE, "IOException on close");
			return false;
		}
		
		//success
		clear();
		addAll(temp);
		return true;
	}

	//SAVE
	private void save() {
		Log.debug("["+getClass().getName()+"] saving to file \"" + filename + "\"...");
		LibUtilities.serialize(this, filename);
	}
	
    public long currentTime() {
    	return System.currentTimeMillis() - recordingStartTime;
    }
}
