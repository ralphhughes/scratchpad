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

public class Migrate {

	public static void main(String[] args) {
		try {
			Connection srcCon = DBConnection.getConnection(DBType.SQLSERVER, "eshu.frlweb.dmz","tellus_web","tellus_web","swuRUM77rece");
			Connection destCon = DBConnection.getConnection(DBType.MYSQL, "berlin.frl.local", "tellus_web", "root", "web_dev");

			Statement srcStmt = srcCon.createStatement();
			Statement destStmt = destCon.createStatement();



			String tablename = "tb_TellusUserDetails";

			ArrayList<String> srcColumns = U.getColumnNames(srcStmt, tablename);

			ResultSet srcRs = srcStmt.executeQuery("select * from " + tablename);
			while (srcRs.next()) {
				String insertColList="";
				String insertValList="";
				for (String currentCol: srcColumns) {
					insertColList = insertColList + currentCol + ", ";
					insertValList = insertValList + "'" + srcRs.getObject(currentCol) + "', ";
				}
				insertColList = U.TrimTrailingComma(insertColList);
				insertValList = U.TrimTrailingComma(insertValList);

				String insertSQL = "insert into " + tablename + " ( " + insertColList + ") values (" + insertValList + ")";
				System.out.println(insertSQL);
				destStmt.execute(insertSQL);
			}


			srcCon.close();
			destCon.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
