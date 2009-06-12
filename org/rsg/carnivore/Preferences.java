package org.rsg.carnivore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.rsg.lib.Log;

/*
 * a wrapper for Properties
 * this is static and singleton so that it can be called anywhere at any time 
 */

public class Preferences {
	private static Properties propsPreferences;
	private static File file;
	public static boolean shouldSaveOrLoad = true;

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final Preferences INSTANCE = new Preferences();
    public static Preferences instance() {
        return INSTANCE;
    }
    
	private Preferences() {
		propsPreferences = new Properties();
		file = new File(Constants.FILENAME_PROPS);
		load();
	}

	public void put(String key, Object val) {
		propsPreferences.put(key, val.toString());
		save();
	}

	public String getString(String key) {
		return propsPreferences.getProperty(key);
	}
	
	public int getInt(String key) {
		return (int) Integer.valueOf(propsPreferences.getProperty(key));	
	}

	public boolean getBoolean(String key) {
		return (boolean) Boolean.valueOf(propsPreferences.getProperty(key));	
	}

	//LOAD
	public void load() {
		if(shouldSaveOrLoad) {
			Log.debug("[org.rsg.carnivore.Preferences] loading preference file: " + file);
			try {
				InputStream in = new FileInputStream(file);
				propsPreferences.load(in);
				in.close();

			//if not found assume file is new or missing, and write defaults
			} catch (FileNotFoundException e) {
				Log.debug("[org.rsg.carnivore.Preferences] preference file not found -- saving default values instead");
				loadDefaults();
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//SAVE
	public void save() {
		if(shouldSaveOrLoad) {
			Log.debug("[org.rsg.carnivore.Preferences] saving prefs to disk: " + file);
			try {
				OutputStream os = new FileOutputStream(file);
				propsPreferences.store(os, Constants.PROPS_COMMENT);
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
	private void loadDefaults() {
		propsPreferences.clear();
		shouldSaveOrLoad = false;
		put(Constants.CHANNEL, 						Constants.DEFAULT_CHANNEL);
		put(Constants.SHOULD_SKIP_UDP, 				Constants.DEFAULT_SHOULD_SKIP_UDP);
		put(Constants.SHOULD_SHOW_CONSOLE, 			Constants.DEFAULT_SHOW_CONSOLE);
		put(Constants.SHOULD_ALLOW_EXTERNAL_CLIENTS,Constants.DEFAULT_SHOULD_ALLOW_EXTERNAL_CLIENTS);
		put(Constants.MAXIMUM_VOLUME, 				Constants.DEFAULT_MAXIMUM_VOLUME);
		put(Constants.SERVER_PORT, 					Constants.DEFAULT_SERVER_PORT);
		shouldSaveOrLoad = true;
	}
}
