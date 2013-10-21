package com.ralph.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import com.ralph.util.DBConnection;
import com.ralph.util.DBType;
import com.ralph.util.U;

public class MostPopulatedColumn {
	// Connection info is configured here:
	private static DBType dbType = DBType.MYSQL;
	private static String server="localhost";
	private static String dbName="ergo-fractures-new";
	private static String username = "root";
	private static String password = "web_dev";
	private static String table = "fractures";




	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			System.out.println("Connecting to " + dbName + "." + table + " on " + server + " with user " + username + "...");
			conn = DBConnection.getConnection(dbType, server, dbName, username, password);
			stmt = conn.createStatement();

			// get the column names from the ResultSet
			ArrayList<String> colNames = U.getColumnNames(stmt, table);

			// SQL to get the number of non-nulls in each column
			String sql="select tmp.ColName as ColName, tmp.cnt as Count from (";
			for (String currentCol: colNames) {
				sql = sql + "select '" + currentCol + "' as ColName, count(" + currentCol + ") as cnt from " + table + " where " + currentCol + " is not null union all ";
			}
			sql = sql.substring(0, sql.lastIndexOf("union all"));

			sql = sql + ") as tmp order by tmp.cnt desc, tmp.colName asc";

			// Print out sql for debugging
			System.out.println("DEBUG: " + sql);

			ResultSet results = stmt.executeQuery(sql);

			// Print header
			System.out.println("ColName\tCount");

			// Print values
			while (results.next()) {
				System.out.println(results.getString("ColName") + "\t" + results.getString("Count"));
			}

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
