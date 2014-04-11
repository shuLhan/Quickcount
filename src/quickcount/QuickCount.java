/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package quickcount;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mhd Sulhan
 */
public class QuickCount {
	private static final QuickCount qc = new QuickCount ();

	public Integer	user_id			= 0;
	public Integer	dapil_id		= 0;
	public Integer	kecamatan_id	= 0;
	public Integer	kelurahan_id	= 0;
	public Integer	tps_id			= 0;
	public String	dapil_nama		= "";
	public String	kecamatan_nama	= "";
	public String	kelurahan_nama	= "";
	public String	kode_saksi		= "";

	public MainWindow		win_main	= null;
	public WinSaksi			win_saksi	= null;
	public WinHasilPemilu	win_hasil	= null;

	/**
	 * @param args the command line arguments
	 * @throws java.lang.ClassNotFoundException
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");

		QuickCount qc = QuickCount.getInstance();

		qc.win_main = new MainWindow();

		qc.win_main.setVisible(true);
	}

	public static QuickCount getInstance ()
	{
		return QuickCount.qc;
	}

	void saveHasilPemilu(Integer type
			, Integer caleg_id
			, Integer partai_id
			, Integer hasil
	) {
		DatabaseManager dbm = DatabaseManager.getInstance();
		String tname = "";

		if (type == 1) {
			tname = "hasil_dpr";
		} else if (type == 2) {
			tname = "hasil_dprd";
		}

		// delete
		dbm.prepare (" delete from "+ tname	+" where caleg_id = ?");

		try {
			dbm._ps.setInt (1, caleg_id);

			dbm._ps.executeUpdate();

			dbm.close ();
		} catch (SQLException ex) {
			Logger.getLogger(QuickCount.class.getName()).log(Level.SEVERE, null, ex);
		}

		// insert
		dbm.prepare (
					" insert into "+ tname +" ("
				+	"	dapil_id, kecamatan_id, kelurahan_id, tps_id, kode_saksi, caleg_id, partai_id, hasil"
				+	" ) values ( "
				+	"	?, ?, ?, ?, ?, ?, ?, ? "
				+	" )"
		);

		try {
			int i = 1;

			dbm._ps.setInt (i++, this.dapil_id);
			dbm._ps.setInt (i++, this.kecamatan_id);
			dbm._ps.setInt (i++, this.kelurahan_id);
			dbm._ps.setInt (i++, this.tps_id);
			dbm._ps.setString (i++, this.kode_saksi);
			dbm._ps.setInt (i++, caleg_id);
			dbm._ps.setInt (i++, partai_id);
			dbm._ps.setInt (i++, hasil);

			dbm._ps.executeUpdate();

			dbm.close ();
		} catch (SQLException ex) {
			Logger.getLogger(QuickCount.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	void exportHasilPemilu() {
		String	os		= System.getProperty("os.name");
		String	path	= "";

		try {
			path = (new File(new File(".").getAbsolutePath())).getCanonicalPath() +"/";
		} catch (IOException ex) {
			Logger.getLogger(QuickCount.class.getName()).log(Level.SEVERE, null, ex);
		}

		DatabaseManager dbm = DatabaseManager.getInstance();

		exportHasil (dbm, path +"/DPR/", "hasil_dpr", "DPR");
		exportHasil (dbm, path +"/DPRD/", "hasil_dprd", "DPRD");
		exportRekap (dbm, path +"/REKAP_DPR/", "rekap_suara_dpr", "DPR");
		exportRekap (dbm, path +"/REKAP_DPRD/", "rekap_suara_dprd" , "DPRD");
	}

	private void exportHasil (
			DatabaseManager dbm
			, String dir
			, String table
			, String label)
	{
		new File (dir).mkdirs ();

		File csv = new File (dir + label
							+"_"+ qc.dapil_id
							+"_"+ qc.kecamatan_id
							+"_"+ qc.kelurahan_id
							+"_"+ qc.tps_id
							+"_"+ qc.kode_saksi
							+".csv" );

		FileWriter		fw = null;
		BufferedWriter	bw;

		try {
			fw = new FileWriter(csv.getAbsoluteFile());
		} catch (IOException ex) {
			Logger.getLogger(QuickCount.class.getName()).log(Level.SEVERE, null, ex);
		}

		bw = new BufferedWriter(fw);

		dbm.prepare (
			" select	dapil_id"
		+	" ,			kecamatan_id"
		+	" ,			kelurahan_id"
		+	" ,			tps_id"
		+	" ,			kode_saksi"
		+	" ,			caleg_id"
		+	" ,			partai_id"
		+	" ,			hasil"
		+	" from "+ table
		+	" where		kode_saksi = '"+ qc.kode_saksi +"'"
		);

		dbm.executePrepare();

		try {
			while (dbm._rs.next()) {
				bw.write (
					dbm._rs.getInt("dapil_id")		+";"
							+	dbm._rs.getInt("kecamatan_id")	+";"
							+	dbm._rs.getInt("kelurahan_id")	+";"
							+	dbm._rs.getInt("tps_id")		+";"
							+	dbm._rs.getString("kode_saksi")	+";"
							+	dbm._rs.getInt("caleg_id")		+";"
							+	dbm._rs.getInt("partai_id")		+";"
							+	dbm._rs.getInt("hasil")			+"\n"
				);
			}

			bw.close ();
		} catch (SQLException ex) {
			Logger.getLogger(QuickCount.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(QuickCount.class.getName()).log(Level.SEVERE, null, ex);
		}

		dbm.close ();
	}

	private void exportRekap(DatabaseManager dbm
			, String dir
			, String table
			, String label)
	{
		new File (dir).mkdirs ();

		File csv = new File (dir
							+"REKAP_"+ label
							+"_"+ qc.dapil_id
							+"_"+ qc.kecamatan_id
							+"_"+ qc.kelurahan_id
							+"_"+ qc.tps_id
							+"_"+ qc.kode_saksi
							+".csv" );

		FileWriter		fw = null;
		BufferedWriter	bw;

		try {
			fw = new FileWriter(csv.getAbsoluteFile());
		} catch (IOException ex) {
			Logger.getLogger(QuickCount.class.getName()).log(Level.SEVERE, null, ex);
		}

		bw = new BufferedWriter(fw);

		dbm.prepare (
			" select	dapil_id"
		+	" ,			kecamatan_id"
		+	" ,			kelurahan_id"
		+	" ,			tps_id"
		+	" ,			kode_saksi"
		+	" ,			jumlah"
		+	" ,			rusak"
		+	" ,			sisa"
		+	" ,			sah"
		+	" ,			tidak_sah"
		+	" from "+ table
		+	" where		kode_saksi = '"+ qc.kode_saksi +"'"
		);

		dbm.executePrepare();

		try {
			while (dbm._rs.next()) {
				bw.write (
					dbm._rs.getInt("dapil_id")		+";"
				+	dbm._rs.getInt("kecamatan_id")	+";"
				+	dbm._rs.getInt("kelurahan_id")	+";"
				+	dbm._rs.getInt("tps_id")		+";"
				+	dbm._rs.getString("kode_saksi")	+";"
				+	dbm._rs.getInt("jumlah")		+";"
				+	dbm._rs.getInt("rusak")			+";"
				+	dbm._rs.getInt("sisa")			+";"
				+	dbm._rs.getInt("sah")			+";"
				+	dbm._rs.getInt("tidak_sah")		+"\n"
				);
			}

			bw.close ();
		} catch (SQLException ex) {
			Logger.getLogger(QuickCount.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(QuickCount.class.getName()).log(Level.SEVERE, null, ex);
		}

		dbm.close ();

	}
}
