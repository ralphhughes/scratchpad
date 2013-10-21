package com.ralph.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ralph.util.DBConnection;
import com.ralph.util.DBType;

public class ConvertZeroLengthStringsToNulls {

	private static DBType dbType = DBType.MYSQL;
	private static String server = "localhost";
	private static String dbName = "ergo_carbonate_access";
	private static String username = "root";
	private static String password = "web_dev";
	
	
	private static String tableName = "data_parameter_numeric"; 

	
	// Check all varchar columns in the table for zero length strings and convert them to nulls 
	public static void main(String[] args) {
		Connection conn = null;
		try {
			System.out.println("Connecting to " + dbName + " on " + server
					+ " with user " + username + "...");
			conn = DBConnection.getConnection(dbType, server, dbName, username, password);

			
			String sql = "select column_name from information_schema.columns where TABLE_SCHEMA='" + dbName 
					+ "' and TABLE_NAME='" + tableName + "' and data_type='varchar'";
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			if (rs != null) {
				while (rs.next()) {
					String colName = rs.getString(1);
					String sql2 = "update " + tableName + " set `" + colName + "` = null where `" + colName + "` = ''";
					Statement s2 = conn.createStatement();
					int retVal = s2.executeUpdate(sql2);
					System.out.println(retVal + " rows updated for column " + colName);
					s2.close();
				}
			}
			rs.close();
			statement.close();
			conn.close();
			System.out.println("Finished.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
