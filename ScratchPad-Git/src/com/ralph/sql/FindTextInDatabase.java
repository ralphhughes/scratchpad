package com.ralph.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import com.ralph.util.DBConnection;
import com.ralph.util.DBType;
import com.ralph.util.U;

public class FindTextInDatabase {
	//Connection info is configured here:
	private static DBType dbType = DBType.MYSQL;
	private static String server="horus.frl.local";
	private static String dbName="intranet";
	private static String username = "intranet";
	private static String password = "46Fxmr8J";

	private static String searchText = "Fugro";


	// This class is really primitive, don't run it on anything big or it'll take hours.
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			System.out.println("Connecting to " + dbName + " on " + server + " with user " + username + "...");
			conn = DBConnection.getConnection(dbType, server, dbName, username, password);
			stmt = conn.createStatement();

			String sql = " select table_name, column_name from information_schema.columns ";
			sql = sql + " where table_schema='" + dbName + "' and data_type in ('varchar', 'char','longtext','mediumtext','text','tinytext') ";
			sql = sql + " order by table_name, column_name; ";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs != null) {
				String currentTable = null;
				String colSQL = "";
				while (rs.next()) {
					if (!rs.getString("table_name").equals(currentTable)) {
						if (currentTable != null) {
							colSQL = colSQL.substring(0, colSQL.length() - 4);
							System.out.println(currentTable);
							findResultsInTable(conn, colSQL);
						}
						currentTable = rs.getString("table_name");
						colSQL = "select * from " + currentTable + " where ";
					}
					
					String currentColumn = rs.getString("column_name");
					colSQL = colSQL + currentColumn + " like '%" + searchText + "%' or ";
				}
			}
			rs.close();
			stmt.close();
			System.out.println("Finished.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void findResultsInTable(Connection conn, String colSQL) {
		
		try {
			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery(colSQL);
			if (rs2 != null) {
				U.printResultsFromQuery(rs2, true);
				System.out.println ("\r\n\r\n");
			}
			rs2.close();
			stmt2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
