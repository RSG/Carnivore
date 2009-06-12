package org.rsg.carnivore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.roydesign.mac.MRJAdapter;
import org.rsg.carnivore.gui.About;

/*
 * this was stolen from limewire code
 */

/**
 * This class handles Macintosh specific events. The handled events  
 * include the selection of the "About" option in the Mac file menu,
 * the selection of the "Quit" option from the Mac file menu, and the
 * dropping of a file on LimeWire on the Mac, which LimeWire would be
 * expected to handle in some way.
 */
public class MacEventHandler {
    
    private static MacEventHandler INSTANCE;
    
    public static synchronized MacEventHandler instance() {
        if (INSTANCE==null)
            INSTANCE = new MacEventHandler();
        
        return INSTANCE;
    }
    
    /** Creates a new instance of MacEventHandler */
    private MacEventHandler() {
        
        MRJAdapter.addAboutListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleAbout();
            }
        });
        
        MRJAdapter.addQuitApplicationListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleQuit();
            }
        });
        
        /*MRJAdapter.addOpenDocumentListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                File file = ((ApplicationEvent)evt).getFile();
                handleOpenFile(file);
            }
        });
        
        MRJAdapter.addReopenApplicationListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleReopen();
            }
        });*/
    } 
    
    /**
     * Enable preferences.
     */
    /*public void enablePreferences() {
        MRJAdapter.setPreferencesEnabled(true);
        
        MRJAdapter.addPreferencesListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handlePreferences();
            }
        });
    }*/
    
    /**
    * This responds to the selection of the about option by displaying the
    * about window to the user.  On OSX, this runs in a new ManagedThread to handle
    * the possibility that event processing can become blocked if launched
    * in the calling thread.
    */
    private void handleAbout() {      
    	About.instance().showMe();
    }
    
    /**
    * This method responds to a quit event by closing the application in
    * the whichever method the user has configured (closing after completed
    * file transfers by default).  On OSX, this runs in a new ManagedThread to handle
    * the possibility that event processing can become blocked if launched
    * in the calling thread.
    */
    private void handleQuit() {
        CarnivorePE.instance().quit();
    }
    
    /**
     * This method handles a request to open the specified file.
     */
    /*private void handleOpenFile(File file) {
        
        String filename = file.toString();
        
        if (filename.endsWith("limestart")) {
            Initializer.setStartup();
        } else {
            PackagedMediaFileLauncher.launchFile(filename, false);
        }
    }*/
    
    /*private void handleReopen() {
        GUIMediator.handleReopen();
    }*/
    
    /*private void handlePreferences() {
        GUIMediator.instance().setOptionsVisible(true);
    }*/
}
