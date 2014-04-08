/*
 Copyright 2014 - Mhd Sulhan (m.shulhan@gmail.com)
 */

package quickcount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mhd Sulhan <m.shulhan@gmail.com>
 */
public class DatabaseManager {
	private static final String DB_URL = "jdbc:sqlite:resources/quickcount.db";

	private static DatabaseManager dbm = new DatabaseManager ();

	public Connection			_con	= null;
	public ResultSet			_rs		= null;
	public Statement			_stmt	= null;
	public PreparedStatement	_ps		= null;
	public String				_q		= null;

	private DatabaseManager ()
	{
		createConnection ();
	}

	/**
	 *
	 * @return DatabaseManager
	 */
	public static DatabaseManager getInstance ()
	{
		if (DatabaseManager.dbm == null) {
			DatabaseManager.dbm = new DatabaseManager ();
		}
		return DatabaseManager.dbm;
	}

	private void createConnection ()
	{
		if (this._con == null) {
			try {
				this._con = DriverManager.getConnection(DatabaseManager.DB_URL);
			} catch (SQLException ex) {
				Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void createStatement()
	{
		try {
			this._stmt = this._con.createStatement();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void prepare (String q)
	{
		try {
			this._ps = this._con.prepareStatement(q);
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void executePrepare ()
	{
		try {
			this._rs = this._ps.executeQuery();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void close ()
	{
		try {
			this._rs.close();
			this._ps.close();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
