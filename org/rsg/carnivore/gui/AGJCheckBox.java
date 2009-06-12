package org.rsg.carnivore.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import org.rsg.carnivore.CarnivorePE;
import org.rsg.carnivore.Constants;
import org.rsg.carnivore.Preferences;
import org.rsg.lib.Log;

public class AGJCheckBox extends JCheckBox implements ActionListener {
	private static final long serialVersionUID = Constants.VERSION;

	public AGJCheckBox(){
		super();
		addActionListener(this);

		//ICONS 
		Icon icon1 = new ImageIcon(Constants.IMAGES_CHECKBOXoff);
		Icon icon2 = new ImageIcon(Constants.IMAGES_CHECKBOXon);
		this.setIcon(icon1);
		this.setSelectedIcon(icon2);
	}

	public void actionPerformed(ActionEvent ae) {
		
		//HANDLE CARNIVORE (ASCII) CHANNEL
		if(ae.getSource().equals(GUI.instance().checkbox_ascii)) {
			Preferences.instance().put(Constants.CHANNEL, Constants.CHANNEL_CARNIVORE);
			GUI.instance().checkbox_ascii.setSelected(true);
			GUI.instance().checkbox_hex.setSelected(false);
			GUI.instance().checkbox_header.setSelected(false);
			Log.debug("["+this.getClass().getName()+"] checkbox_ascii: true");
			return;
		}

		//HANDLE HEXIVORE (HEX) CHANNEL
		if(ae.getSource().equals(GUI.instance().checkbox_hex)) {
			Preferences.instance().put(Constants.CHANNEL, Constants.CHANNEL_HEXIVORE);
			GUI.instance().checkbox_ascii.setSelected(false);
			GUI.instance().checkbox_hex.setSelected(true);
			GUI.instance().checkbox_header.setSelected(false);
			Log.debug("["+this.getClass().getName()+"] checkbox_hex: true");
			return;
		}

		//HANDLE MINIVORE (HEADER) CHANNEL
		if(ae.getSource().equals(GUI.instance().checkbox_header)) {
			Preferences.instance().put(Constants.CHANNEL, Constants.CHANNEL_MINIVORE);
			GUI.instance().checkbox_ascii.setSelected(false);
			GUI.instance().checkbox_hex.setSelected(false);
			GUI.instance().checkbox_header.setSelected(true);
			Log.debug("["+this.getClass().getName()+"] checkbox_header: true");
			return;
		}

		//HANDLE UDP CHECKBOX
		if(ae.getSource().equals(GUI.instance().checkbox_udp)) {
			if(isSelected()) {
				Preferences.instance().put(Constants.SHOULD_SKIP_UDP, "true");
				Log.debug("["+this.getClass().getName()+"] shouldSkipUDP: true");
			} else {
				Preferences.instance().put(Constants.SHOULD_SKIP_UDP, "false");
				Log.debug("["+this.getClass().getName()+"] shouldSkipUDP: false");
			}
			
			try {
				CarnivorePE.instance().core.setFilter(); //change filter
			} catch (NullPointerException e) {} //do nothing, this probably means we are on launch and the sniffer hasn't been created yet
			return;
		}
		
		//HANDLE SECURITY CHECKBOX
		if(ae.getSource().equals(GUI.instance().checkbox_allow)) {
			if(isSelected()) {
				Preferences.instance().put(Constants.SHOULD_ALLOW_EXTERNAL_CLIENTS, "true");
				Log.debug("["+this.getClass().getName()+"] shouldAllowExternalClients: true");
			} else {
				Preferences.instance().put(Constants.SHOULD_ALLOW_EXTERNAL_CLIENTS, "false");
				Log.debug("["+this.getClass().getName()+"] shouldAllowExternalClients: false");
			}
			return;
		}
		return;
	} 
}