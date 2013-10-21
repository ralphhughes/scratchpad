package com.ralph.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

import com.ralph.util.DBConnection;
import com.ralph.util.DBType;
import com.ralph.util.U;

public class RunOnMultipleDatabases {
	public static String sqlToRun = "select * from ergo_config order by `key`";
	public static boolean wantResults = true; // If you are running insert, update, delete or DDL set this to false
	
	
	public static void main(String[] args) {
		new RunOnMultipleDatabases().run();
	}
	public void run() {
		try {
			// Set up connections
			ArrayList<Connection> databaseConnections = new ArrayList<Connection>();
			databaseConnections.add(DBConnection.getConnection(DBType.MYSQL, "localhost", "ergo-clastics-new", 	"root", "web_dev"));
			databaseConnections.add(DBConnection.getConnection(DBType.MYSQL, "localhost", "ergo-carbonates", 	"root", "web_dev"));
			databaseConnections.add(DBConnection.getConnection(DBType.MYSQL, "localhost", "ergo-fields", 		"root", "web_dev"));
			databaseConnections.add(DBConnection.getConnection(DBType.MYSQL, "localhost", "ergo-fractures", 	"root", "web_dev"));
			
			for (Connection conn: databaseConnections) {
				DatabaseMetaData mtdt = conn.getMetaData();
				System.out.println("Running SQL on " + mtdt.getURL() + " with user " + mtdt.getUserName());
				Statement stmt = conn.createStatement();
				if (wantResults == true) {
					ResultSet rs = stmt.executeQuery(sqlToRun);
					if (rs != null) {
						U.printResultsFromQuery(rs, true);
					}
				} else {
					int affectedRows = stmt.executeUpdate(sqlToRun);
					System.out.println(affectedRows + " row(s) affected.");
				}
				stmt.close();
				conn.close();
				System.out.println();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
