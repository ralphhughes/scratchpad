package com.ralph.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.ralph.util.DBConnection;
import com.ralph.util.DBType;
import com.ralph.util.U;

public class DeleteEmptyRows {

	//Connection info is configured here:
	private static DBType dbType = DBType.MYSQL;
	private static String server="localhost";
	private static String dbName="ergo-carbonates";
	private static String username = "root";
	private static String password = "web_dev";
	private static String table = "sediment_body";




	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			System.out.println("Connecting to " + dbName + "." + table + " on " + server + " with user " + username + "...");
			conn = DBConnection.getConnection(dbType, server, dbName, username, password);
			stmt = conn.createStatement();

			// Find primary key field(s)
			//String findKeysSQL = "select column_name from information_schema.columns where column_key <> '' and table_schema='" + dbName + "' and table_name='" + table + "'";
			
			// Find all columns that are nullable and not primary keys
			String columnSQL = "select column_name from information_schema.columns " +
					" where column_key = '' and is_nullable = 'YES' and table_schema='" + dbName + "' and table_name='" + table + "'";
			
			
			String deleteNullsSQL = "DELETE FROM " + table + " WHERE ";
			ResultSet rs = stmt.executeQuery(columnSQL);
			if (rs != null) {
				while (rs.next()) {
					deleteNullsSQL = deleteNullsSQL + rs.getString("column_name") + " is null and ";
				}
			}
			// trim final ' and '
			deleteNullsSQL = deleteNullsSQL.substring(0,deleteNullsSQL.length()-5);
			
			System.out.println("Executing: " + deleteNullsSQL);
			Integer count = stmt.executeUpdate(deleteNullsSQL);
			
			System.out.println(count + " empty row(s) deleted from " + table);
			// make SQL to delete from table where all non-primary key columns are null
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			// Clean up and release connections
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}


}
