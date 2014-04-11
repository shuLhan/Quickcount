/*
 Copyright 2014 - Mhd Sulhan (m.shulhan@gmail.com)
 */

package quickcount;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author Mhd Sulhan <m.shulhan@gmail.com>
 */
public class TableHasilPemilu extends javax.swing.JTable {
	private final String[] header;
    private final Class[] types;
	private final boolean[] canEdit;
	private final int[]		minWidth;
	private final int[]		maxWidth;

	private final DefaultTableModel model;

	public TableHasilPemilu (ResultSet rs) {
		this.header = new String [] {
				"type"
			,	"caleg_id"
			,	"partai_id"
			,	"No Urut"
			,	"Nama Caleg"
			,	"Jumlah Suara"
			};
		this.types = new Class [] {
                java.lang.Integer.class
			,	java.lang.Integer.class
			,	java.lang.Integer.class
			,	java.lang.Integer.class
			,	java.lang.String.class
			,	java.lang.Integer.class
            };
		this.canEdit = new boolean [] {
                false
			,	false
			,	false
			,	false
			,	false
			,	true
            };
		this.minWidth = new int [] {0,0,0,120,300,120};
		this.maxWidth = new int [] {0,0,0,120,400,120};

		this.model = new DefaultTableModel(new Object[][]{}, this.header);

		this.setModel(this.model);

		this.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

		for (int i = 0; i < this.getColumnCount(); i++) {
			TableColumn tc = this.getColumnModel().getColumn(i);

			tc.setMinWidth(this.minWidth[i]);
			tc.setMaxWidth(this.maxWidth[i]);
		}

		this.populateTable(rs);

		Action action = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				TableCellListener tcl = (TableCellListener)e.getSource();

				DefaultTableModel dtm = (DefaultTableModel) tcl.getTable().getModel ();

				int r = tcl.getRow();
				int c = tcl.getColumn();

				Integer type		= (Integer) dtm.getValueAt(r, 0);
				Integer caleg_id	= Integer.valueOf ((String) dtm.getValueAt(r, 1));
				Integer partai_id	= Integer.valueOf ((String) dtm.getValueAt(r, 2));
				Integer hasil		= (Integer) dtm.getValueAt(r, c);

				QuickCount qc = QuickCount.getInstance();

				qc.saveHasilPemilu (type, caleg_id, partai_id, hasil);
			}
		};

		TableCellListener tcl = new TableCellListener(this, action);
	}

	@Override
	public Class getColumnClass (int colidx)
	{
		return this.types[colidx];
	}

	@Override
	public boolean isCellEditable (int r, int c)
	{
		return canEdit [c];
	}

	private void populateTable (ResultSet rs)
	{
		try {
			while (rs.next ()) {
				Vector v = new Vector ();

				for (int i = 1; i <= this.getColumnCount(); i++) {
					v.add (rs.getObject(i));
				}

				this.model.addRow(v);
			}
		} catch (SQLException ex) {
			Logger.getLogger(TableHasilPemilu.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
