package org.rsg.carnivore.gui;

import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import org.rsg.carnivore.Constants;

//yes, a kludge  
public class AGImage extends JCheckBox {
	private static final long serialVersionUID = Constants.VERSION;

	public AGImage(String path1, String path2){
	    super();

	    //ICONS 
        //Icon icon1 = new ImageIcon(getClass().getResource(path1));
        //Icon icon2 = new ImageIcon(getClass().getResource(path2));
        /*Icon icon1 = new ImageIcon(path1);
        Icon icon2 = new ImageIcon(path2);
        this.setIcon(icon1);
        this.setSelectedIcon(icon2);*/
	    updateIcons(path1, path2);
	}	
	
	public void updateIcons(String path1, String path2) {
        Icon icon1 = new ImageIcon(path1);
        Icon icon2 = new ImageIcon(path2);
        this.setIcon(icon1);
        this.setSelectedIcon(icon2);		
	}

	//override click functionality 
	protected void processMouseEvent(MouseEvent me) {
		//do nothing
	}
}