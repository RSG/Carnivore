package org.rsg.carnivore.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rsg.carnivore.CarniUtilities;
import org.rsg.carnivore.Constants;
import org.rsg.carnivore.Preferences;
import org.rsg.lib.Log;

public class AGJSlider extends JSlider implements ChangeListener, MouseListener {
	private static final long serialVersionUID = Constants.VERSION;
	final static int MIN = 1;
    final static int MAX = 20;
    final static int INIT = 5; //initial value
    static boolean sliding = false;
    
	public AGJSlider(){
	    super(JSlider.HORIZONTAL, MIN, MAX, INIT);
	    this.setSnapToTicks(true);
	    addChangeListener(this);
	    addMouseListener(this);
	    setFocusable(false); //turns off annoying dotted box around component
	}

	public void stateChanged(ChangeEvent ae) { 
		Float f = new Float(this.getValue());
		GUI.instance().sliderui.setVolumeMaximum(f);
		Log.debug("["+this.getClass().getName()+"] stateChanged: "+f);
		Preferences.instance().put(Constants.MAXIMUM_VOLUME, CarniUtilities.normalizeVolume(f.intValue()));		
	}

	public static boolean isSliding() {
		return sliding;
	}

	//MOUSE LISTENERS
	public void mousePressed(MouseEvent arg0) {
		sliding = true; 
		Log.debug("["+this.getClass().getName()+"] mousePressed");		
	}
	public void mouseReleased(MouseEvent arg0) {
		sliding = false; 
		Log.debug("["+this.getClass().getName()+"] mouseReleased");
	}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}	
}
