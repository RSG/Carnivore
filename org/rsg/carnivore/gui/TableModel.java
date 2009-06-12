package org.rsg.carnivore.gui;

import javax.swing.table.DefaultTableModel;

public class TableModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;

    public boolean isCellEditable(int row, int col) {
    	return false;
    }
}