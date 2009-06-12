package org.rsg.carnivore.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.rsg.carnivore.Constants;

public class About extends JFrame {	
	private static final long serialVersionUID = 1L;

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final About INSTANCE = new About();
    public static About instance() {
        return INSTANCE;
    }
    
	private About() {
		//add button with image
		Container cp = getContentPane();
		Icon i = new ImageIcon(Constants.IMAGES_ABOUT);
		JButton ok = new JButton(i);
		ok.setBorderPainted(false);
		ok.setMargin(new Insets(0,0,0,0));
		ok.addActionListener(new ActionListener() { //hide jframe on click 
			public void actionPerformed(ActionEvent e) {
				hideMe();
			}
		});

		//init the jframe a little
		int w = i.getIconWidth();
		int h = i.getIconHeight();
		setSize(w,h);
		setResizable(false); 
		setUndecorated(true);		
		setTitle("About CarnivorePE");
		ImageIcon ico = new ImageIcon(Constants.IMAGES_ICON);
		setIconImage(ico.getImage());
	      
		//center the window
		Dimension us	= getSize(), them = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((them.width - us.width) / 2,(them.height - us.height) / 2);
		
		cp.add(ok);
		pack();
		hideMe();
		//System.out.println("[About] instance inited");
	}

	public void showMe() {
		setVisible(true);
	}
	
	public void hideMe() {
		setVisible(false);
	}
	
}
