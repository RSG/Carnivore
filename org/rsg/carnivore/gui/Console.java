package org.rsg.carnivore.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.rsg.carnivore.CarniUtilities;
import org.rsg.carnivore.CarnivorePacket;
import org.rsg.carnivore.Constants;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.Log;

public class Console extends JFrame implements WindowFocusListener, WindowListener, ComponentListener {	
	private static final long serialVersionUID = Constants.VERSION;
	//private static JTextArea textArea = new JTextArea();
	public JTable table;
	public TableModel tablemodel;
	public boolean isMagnetic = true;

    ///////////////////////////////////////////////////////////////////////////
	//singleton pattern
    private static final Console INSTANCE = new Console();
    public static Console instance() {
        return INSTANCE;
    }
	
    ///////////////////////////////////////////////////////////////////////////
	//constructor
	private Console() {
		addWindowFocusListener(this);
		addWindowListener(this);
		addComponentListener(this);
		
		setTitle("CarnivorePE Console");
		ImageIcon ico = new ImageIcon(Constants.IMAGES_ICON);
		setIconImage(ico.getImage());
		//setResizable(false); 
		//setUndecorated(true);
        
		//init table model
		tablemodel = new TableModel();
		tablemodel.setColumnIdentifiers(Constants.CONSOLE_COLUMN_HEADERS);
		
		//add table model to table
        table = new JTable(tablemodel);
       
        //add cell renderer
        table.setDefaultRenderer(Object.class, new ColorAwareTableCellRenderer(table.getDefaultRenderer(Object.class))); 
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 

        //"type" column 
        TableColumn column = null;
        column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(Constants.CONSOLE_COLUMN_WIDTH_0);

        //"date" column 
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(Constants.CONSOLE_COLUMN_WIDTH_1);

        //"sender" column 
        column = table.getColumnModel().getColumn(2);
        column.setPreferredWidth(Constants.CONSOLE_COLUMN_WIDTH_2);

        //"receiver" column 
        column = table.getColumnModel().getColumn(3);
        column.setPreferredWidth(Constants.CONSOLE_COLUMN_WIDTH_3);

        //"receiver" column 
        column = table.getColumnModel().getColumn(4);
        column.setPreferredWidth(Constants.CONSOLE_COLUMN_WIDTH_4);

        //put table into a scroller and add to frame
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        pack();
        hideMe();
	}
	
    ///////////////////////////////////////////////////////////////////////////
	//public API
	public void addPacket(CarnivorePacket packet) {
		tablemodel.insertRow(0, CarniUtilities.packetToObjectArray(packet));		
		prune();
	}
	
	public void prune() {
		while(tablemodel.getRowCount() > Constants.CONSOLE_MAX_ROWS) {
			tablemodel.removeRow(tablemodel.getRowCount()-1);
		}
	}

	public void updateMagneticPosition() {
		Rectangle mainRect = GUI.instance().frame.getBounds();
		Dimension mainSize = GUI.instance().frame.getSize();
		setLocation(mainRect.x + mainSize.width, mainRect.y);	
	}

	public void initSize() {
		Dimension mainSize = GUI.instance().frame.getSize();
		setSize(getSize().width, mainSize.height);
	}

	public void showMe() {
		Log.debug("["+this.getClass().getName()+"] show");
		setVisible(true);
		//GUI.instance().frame.toFront();
	}
	
	public void hideMe() {
		Log.debug("["+this.getClass().getName()+"] hide");
		setVisible(false);
	}

	////////////////////////////////////////////////////////////////////////////////
	//INTERFACE FOR WindowFocusListener
	public void windowLostFocus(WindowEvent e) {}
	public void windowGainedFocus(WindowEvent e) {
		if(LibUtilities.isMac()) //this looks bad on Win so skip it
			setJMenuBar(Menu.instance());
		if(e.getOppositeWindow() == null) {
			//System.out.println("[Console] windowGainedFocus app was foregrounded");	
			GUI.instance().frame.toFront();
			toFront();
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//INTERFACE FOR	ComponentListener 
	public void componentResized(ComponentEvent arg0) {}
	public void componentShown(ComponentEvent arg0) {}
	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {
		Rectangle mainRect = GUI.instance().frame.getBounds();
		Dimension mainSize = GUI.instance().frame.getSize();
		Rectangle thisRect = getBounds();		
		int delta_x = Math.abs(mainRect.x + mainSize.width - thisRect.x);
		int delta_y = Math.abs(mainRect.y - thisRect.y);
		
		if(	(delta_x < Constants.THRESHOLD_FOR_MAGNETIC_WINDOWS) &&
			(delta_y < Constants.THRESHOLD_FOR_MAGNETIC_WINDOWS)) {
			isMagnetic = true;
			updateMagneticPosition();
		} else {
			isMagnetic = false;
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//INTERFACE FOR WindowFocusListener
	public void windowStateChanged(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowActivated(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowClosing(WindowEvent arg0) {		
		//System.out.println("[Console] windowClosing");
		Menu.instance().confirmConsoleIsClosed(); 	//otherwise user can close the window manually with "X"
														//and Menu will still think it's opened
	}
}
