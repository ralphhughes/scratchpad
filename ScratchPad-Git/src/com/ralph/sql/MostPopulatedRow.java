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

/*
 * This class prints out a summary of the top n rows in a table with the
 * least number of null columns to the console. It also displays the
 * percentage of columns that are populated (ie not null).
 */
public class MostPopulatedRow {
	// Connection info is configured here:
	private static DBType dbType = DBType.MYSQL;
	private static String server="localhost";
	private static String dbName="ergo-fractures-new";
	private static String username = "root";
	private static String password = "web_dev";
	private static String table = "parameters_crosstab";
	private static int numPreviewRows = 10;



	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			System.out.println("Connecting to " + dbName + "." + table + " on " + server + " with user " + username + "...");
			conn = DBConnection.getConnection(dbType, server, dbName, username, password);
			stmt = conn.createStatement();

			// get the column names from the ResultSet
			ArrayList<String> colNames = U.getColumnNames(stmt, table);

			// Generate the SQL for this table
			String sql = "select " + table + ".*, (";
			for (String currentColumn: colNames) {
				sql = sql + "case when `" + currentColumn + "` is null then 1 else 0 end + ";
			}
			// Trim trailing plus and complete the sql
			sql = sql.trim();
			sql = sql.substring(0, sql.length() - 1);
			switch (dbType) {
			case ORACLE:
				sql = sql + ") NumberOfNullFields from " + table + " order by NumberOfNullFields asc";
				break;
			case MYSQL:
				sql = sql + ") NumberOfNullFields from " + table + " order by NumberOfNullFields asc limit " + numPreviewRows;
				break;
			case POSTGRESQL:
				sql = sql + ") NumberOfNullFields from " + table + " order by NumberOfNullFields asc limit " + numPreviewRows;
				break;
			}
			System.out.println("DEBUG: " + sql + "\r\n");

			ResultSet results = stmt.executeQuery(sql);
			results.setFetchSize(numPreviewRows);

			// Print out table header
			String header="Row % Complete\t";
			for (String currentColumn: colNames) {
				header = header + currentColumn + "\t";
			}
			header=header.trim();
			System.out.println(header);


			// Print out top n most populated table rows
			for (int i = 0 ; i <= numPreviewRows; i++) {
				// If iterated past last row of result set then exit loop
				if (!results.next()) {
					break;
				}
				Double percentComplete = (double) (colNames.size() - results.getInt("NumberOfNullFields")) / (double) colNames.size();
				String row=new Double(U.roundToSigFig(percentComplete * 100,3)).toString() + "%\t";
				for (String currentColumn: colNames) {
					Object obj = results.getObject(currentColumn);
					if (obj != null) {
						// Strip out newlines and tabs (this is a summary, accuracy is not required)
						row = row + obj.toString().replace("\n", "").replace("\r", "").replace("\t","") + "\t";
					} else {
						row = row + "\t";
					}
				}
				System.out.println(row.trim());
			}
			results.close();

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
