package org.rsg.carnivore.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.rsg.carnivore.CarniUtilities;
import org.rsg.carnivore.Constants;
import org.rsg.carnivore.Preferences;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.Log;

public class GUI extends JFrame implements /*MouseMotionListener,*/ ComponentListener,/* MouseListener,*/ 
											WindowFocusListener /*WindowListener, WindowStateListener*/ {
	private static final long serialVersionUID = Constants.VERSION;
	static int WIDTH = 249;
	static int row = 0;
    //static JTextArea textarea_packets;
    public AGImage image, image_logo, image_label1, image_label2, image_label3;
    public AGJSlider slider;
    public AGJSliderUI sliderui;
    public JLabel label;
    public AGJCheckBox checkbox_device, checkbox_udp, checkbox_ascii, checkbox_hex, checkbox_header, checkbox_allow;
    public Font font;
    public Vector device_checkboxes;
    public JFrame frame;
	public boolean isFinishedLaunching = false;

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final GUI INSTANCE = new GUI();
    public static GUI instance() {
        return INSTANCE;
    }
    
	//CONSTRUCTOR
	private GUI() {
        image_label1	= new AGImage(Constants.IMAGES_LABEL1off, Constants.IMAGES_LABEL1on);
        image_label2	= new AGImage(Constants.IMAGES_LABEL2off, Constants.IMAGES_LABEL2on);
        image_label3 	= new AGImage(Constants.IMAGES_LABEL3, Constants.IMAGES_LABEL3);
        image_logo		= new AGImage(Constants.IMAGES_HEAD_OFF, Constants.IMAGES_HEAD_OFF);        
        
        //an attempt to fix weird swing inconsistencies 
        if(LibUtilities.isWindows()) {
	        image_label1.setMargin(new Insets(0,0,0,0));
	        image_label2.setMargin(new Insets(0,0,0,0));
	        image_label3.setMargin(new Insets(0,0,0,0));
	        image_logo.setMargin(new Insets(0,0,0,0));
	        Color color_background = new Color(240,240,240);
	        image_label1.setBackground(color_background);
	        image_label2.setBackground(color_background);
	        image_label3.setBackground(color_background);
	        image_logo.setBackground(color_background);
        }
	}
	
	public void start() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//INIT GUI
				createAndShowGUI();
			}
		});
	}

	public void setHeadStrobe(String path) {
		image_logo.updateIcons(Constants.IMAGES_HEAD_OFF, path);
	}

	public void lightLabel1(boolean b) {
		image_label1.setSelected(b);
	}

	public void lightLabel2(boolean b) {
		image_label2.setSelected(b);
	}

	public void toggleHeader() {
		if(image_logo.isSelected()) {
			image_logo.setSelected(false);
		} else {
			image_logo.setSelected(true);
		}
	}

	public void initGUIWithPreferences() {
		Log.debug("["+this.getClass().getName()+"] initializing GUI with preferences");
		
		Preferences.shouldSaveOrLoad = false; 
				
		//INIT CHANNEL
		int channel = Preferences.instance().getInt(Constants.CHANNEL);
		if(channel == Constants.CHANNEL_CARNIVORE) {
			checkbox_ascii.doClick();
		} else if(channel == Constants.CHANNEL_HEXIVORE) {
			checkbox_hex.doClick();
		} else {
			checkbox_header.doClick();
		}

		//INIT shouldAllowExternalClients
		if(Preferences.instance().getBoolean(Constants.SHOULD_ALLOW_EXTERNAL_CLIENTS)) {
			checkbox_allow.doClick();
		}

		//INIT shouldSkipUDP
		if(Preferences.instance().getBoolean(Constants.SHOULD_SKIP_UDP)) {
			checkbox_udp.doClick();
		}

		//INIT console
		if(Preferences.instance().getBoolean(Constants.SHOULD_SHOW_CONSOLE)) {
			Menu.instance().menuItem_console.doClick();
		}

		//INIT maximum_volume
		int maximum_volume = Preferences.instance().getInt(Constants.MAXIMUM_VOLUME);	
		slider.setValue(CarniUtilities.normalizeVolume(maximum_volume));
		slider.stateChanged(null);

		Preferences.shouldSaveOrLoad = true; 
	}
	
	private void createAndShowGUI() {

		Log.debug("["+this.getClass().getName()+"] creating and showing GUI");
		//LOAD FONT
		try {
			File file = new File(Constants.FONTS_SEVENET7);
			FileInputStream fis = new FileInputStream(file);
			Font ttfBase = Font.createFont(Font.TRUETYPE_FONT, fis);
			font = ttfBase.deriveFont(Font.PLAIN,8);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		//might have to use this for JAR??
		InputStream is = CarnivorePE.class.getResourceAsStream(Constants.FONTS_SEVENET7);
		if(is == null){
			Log.debug("[GUI] Cannot open " + Constants.FONTS_SEVENET7);
		}
		Font ttfBase = null;
		try {
			ttfBase = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		//INIT ABOUT
		//About.instance();
		
		//UI MANAGER
		Color color_background = new Color(240,240,240);
		UIManager.put("Panel.background", color_background);
		UIManager.put("CheckBox.background", color_background);
		UIManager.put("Slider.background", color_background);
		//UIManager.put("ComboBox.font", font);
                
		frame = new JFrame("CarnivorePE");
		frame.setResizable(false); 
		//frame.setUndecorated(true);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Carnivore.ICON));
		ImageIcon ico = new ImageIcon(Constants.IMAGES_ICON);
		frame.setIconImage(ico.getImage());

		//listeners
		frame.addComponentListener(this);
		frame.addWindowFocusListener(this);

		//add menu
		frame.setJMenuBar(Menu.instance());

		//frame / ContentPane / Panel
        Container contentPane1 = frame.getContentPane();
        JPanel panel = new JPanel();

        //LAYOUT MANAGER
        //contentPane1.setLayout(new GridLayout(0,1));               
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START; //justify left
        
        //COLORIZE
        panel.setBorder(BorderFactory.createLineBorder (Color.white, 2));
        
        //LOGO
        c.gridx = 0;		c.gridy = row++;
        c.gridwidth = 3; 	//colspan
        panel.add(image_logo,c);

        //LABEL CLIENT
        c.gridy = row++;
        panel.add(image_label1,c);

        //CHECKBOX ASCII
        c.gridy = row++;
        c.gridx = 0;
        c.gridwidth = 1; 	//colspan
        panel.add(checkbox_ascii = new AGJCheckBox(),c);
        c.gridx = 1;
        c.gridwidth = 2; 	//colspan
        panel.add(label = new JLabel("SEND PACKETS IN AN ASCII STRING"),c);	
        label.setFont(font);

        //CHECKBOX HEX
        c.gridy = row++;
        c.gridx = 0;
        c.gridwidth = 1; 	//colspan
        panel.add(checkbox_hex = new AGJCheckBox(),c);
        c.gridx = 1;
        c.gridwidth = 2; 	//colspan
        panel.add(label = new JLabel("SEND PACKETS IN A HEX STRING"),c);	
        label.setFont(font);
        
        //CHECKBOX HEADERS
        c.gridy = row++;
        c.gridx = 0;
        c.gridwidth = 1; 	//colspan
        panel.add(checkbox_header = new AGJCheckBox(),c);
        c.gridx = 1;
        c.gridwidth = 2; 	//colspan
        panel.add(label = new JLabel("SEND HEADER STRING ONLY"),c);	
        label.setFont(font);
        
        //NETWORK
        c.gridy = row++;
        c.gridx = 0;
        c.gridwidth = 3; 	//colspan
        panel.add(image_label2,c);

        //DEVICES
        /*
        device_checkboxes = new Vector();
        HashMap d = Carnivore.devices.getDevices();
		Iterator it = d.keySet().iterator();
		while(it.hasNext()){
			String name = (String)it.next();
			String long_name = d.get(name).toString();
			//Log.debug("[Devices] printDevices(): " + name + " \""+long_name+"\"");
	        c.gridy = row++;
	        c.gridx = 0;
	        c.gridwidth = 1; 	//colspan
	        panel.add(checkbox_device = new AGJCheckBox(),c);
	        device_checkboxes.add(checkbox_device);
	        checkbox_device.setTag(name);  //put the name inside the checkbox object so we can get it later 
	        c.gridx = 1;
	        c.gridwidth = 2; 	//colspan
	        panel.add(label = new JLabel(truncate(long_name)),c);	
	    }*/

        //SLIDER
        c.gridy = row++;
        c.gridx = 0;
        c.gridwidth = 3; 	//colspan
        panel.add(slider = new AGJSlider(),c);			
        slider.setUI(sliderui = new AGJSliderUI(slider)); 

        //UDP CHECKBOX
        c.gridy = row++;
        c.gridx = 0;
        c.gridwidth = 1; 	//colspan
        panel.add(checkbox_udp = new AGJCheckBox(),c);
        c.gridx = 1;
        c.gridwidth = 2; 	//colspan
        panel.add(label = new JLabel("SKIP UDP PACKETS?"),c);	
        label.setFont(font);
        
        //SECURITY
        c.gridy = row++;
        c.gridx = 0;
        c.gridwidth = 3; 	//colspan
        panel.add(image_label3,c); 
        
        //CHECKBOX ALLOW OTHERS
        c.gridy = row++;
        c.gridx = 0;
        c.gridwidth = 1; 	//colspan
        panel.add(checkbox_allow = new AGJCheckBox(),c);
        c.gridx = 1;
        c.gridwidth = 2; 	//colspan
        panel.add(label = new JLabel("ALLOW CLIENTS FROM OTHER COMPUTERS"),c);
        label.setFont(font);
				
	    contentPane1.add(panel);
		frame.pack();
		frame.setVisible(true);
		
		System.out.println("[GUI] isFinishedLaunching = true;");
		isFinishedLaunching = true;
	}
	
	public Vector getDeviceCheckboxes() {
		return device_checkboxes;
	}

	////////////////////////////////////////////////////////////////////////////////
	//INTERFACE FOR	ComponentListener 
	public void componentResized(ComponentEvent arg0) {}
	public void componentShown(ComponentEvent arg0) {}
	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {
		//System.out.println("[GUI] componentMoved");
		if(Console.instance().isMagnetic) 
			Console.instance().updateMagneticPosition();
	}


	////////////////////////////////////////////////////////////////////////////////
	//INTERFACE FOR MouseListener
	/*public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		//System.out.println("[GUI] mousePressed:" + e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		//System.out.println("[GUI] mouseReleased:" + e.getY());	
	}*/

	////////////////////////////////////////////////////////////////////////////////
	//INTERFACE FOR	MouseMotionListener 
	/*public void mouseMoved(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {
		//System.out.println("[GUI] mouseDragged:" + e.getY());
		if(Console.instance().isMagnetic) 
			Console.instance().updateMagneticPosition();
	}*/

	////////////////////////////////////////////////////////////////////////////////
	//INTERFACE FOR WindowFocusListener
	public void windowLostFocus(WindowEvent e) {}
	public void windowGainedFocus(WindowEvent e) {
		if(LibUtilities.isMac()) //this looks bad on Win so skip it
			frame.setJMenuBar(Menu.instance());
		if(e.getOppositeWindow() == null) {
			//System.out.println("[GUI] windowGainedFocus app was foregrounded");		
			Console.instance().toFront(); 		
			frame.toFront();
		}
	}
}
