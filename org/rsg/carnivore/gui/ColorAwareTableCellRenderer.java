package org.rsg.carnivore.gui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.rsg.carnivore.Constants;

public class ColorAwareTableCellRenderer implements TableCellRenderer { 
    private TableCellRenderer cellRenderer; 

    //CONSTRUCTOR
    public ColorAwareTableCellRenderer(TableCellRenderer cellRenderer) { 
        if ( cellRenderer == this || cellRenderer == null ) 
            throw new IllegalArgumentException(); 
        this.cellRenderer = cellRenderer; 
    }
    
	////////////////////////////////////////////////////////////////////////////////
	//INTERFACE FOR	TableCellRenderer -- GOVERNS HOW CELLS ARE DISPLAYED
    public Component getTableCellRendererComponent( JTable table, Object v, boolean sel, boolean focus, int y, int x ) { 
        //setHorizontalAlignment(JLabel.CENTER);

    	Component c = cellRenderer.getTableCellRendererComponent( table, v, sel, focus, y, x ); 
        c.setFont(Constants.CONSOLE_FONT);
        if ( c != null ) { 
			if (y % 2 == 0) {
				c.setBackground(Constants.grey1); 
			} else {
				c.setBackground(Constants.grey2); 
			}
        } 
        return c;
    } 
} 


