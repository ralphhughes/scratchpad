package com.ralph.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

// This is basically text utils and anything other methods that will be accessed globally
// Note: Due to this class being global utility methods, it shouldn't import any classes
// from the rest of my code! (java.lang, .io, .math,  Apache Commons etc is fine though)
public class U {

	// Returns a string of num tab characters (useful for indenting stuff)
	public static String tab(int num) {
		return new String(new char[num]).replace("\0", "\t");
	}

	// Trims the last comma off the input (even if there is whitespace after it)
	public static String TrimTrailingComma(String input) {
		String trimmed = rtrim(input);
		if (trimmed.endsWith(",")) {
			return trimmed.substring(0, trimmed.length()-1);
		} else {
			return input;
		}
	}
	
	// Trims whitespace off beginning of string 
	public static String ltrim(String s) {
	    int i = 0;
	    while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
	        i++;
	    }
	    return s.substring(i);
	}
	
	// Trims whitespace off end of string
	public static String rtrim(String s) {
	    int i = s.length()-1;
	    while (i > 0 && Character.isWhitespace(s.charAt(i))) {
	        i--;
	    }
	    return s.substring(0,i+1);
	}
	
	// Horrifically inefficient (no string builder or buffers) but useful:
	public static void saveStringToFile(String fileContents, String filename) {
		try {
			PrintWriter out = new PrintWriter(filename);
			out.print(fileContents);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Reads the first 500 bytes of a file and checks for non-text characters, probably binary file if they exist
	public static Boolean isFilePlainEnglishText(File file) {
		Boolean plainText = true;
	
		try {
			InputStream in = new FileInputStream(file);
			int chunkSize = 500;
			if (file.length() < 500) {
				chunkSize = (int)file.length();
			}
			byte[] bytes = new byte[chunkSize];
			in.read(bytes, 0, chunkSize);
			int numBinaryCharsFound =0;
			for (byte currentByte: bytes) {
				if (currentByte < 32 && currentByte > 127) {
					if (!Character.isWhitespace((char) currentByte)) {
						numBinaryCharsFound++;
					}
				}
				// Allow for byte order marks
				if (numBinaryCharsFound > 4) {
					plainText = false;
					break;
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plainText;
	}
	
	// Rounds a double to n significant figures. Combine with formatNumber() if you need it printing as a String
	public static double roundToSigFig(double num, int n) {
	    if(num == 0) {
	        return 0;
	    }

	    final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
	    final int power = n - (int) d;

	    final double magnitude = Math.pow(10, power);
	    final long shifted = Math.round(num*magnitude);
	    return shifted/magnitude;
	}
	
	// Uses British locale rules to add thousands seperators into a number
	public static String formatNumber(Number number) {
		Locale locale = new Locale("en","GB");
		return NumberFormat.getInstance(locale).format(number);
	}
	
	public static ArrayList<String> getColumnNames(Statement stmt, String table) throws SQLException {
		String query = "select * from " + table;
		ResultSet rs = stmt.executeQuery(query);
		if (rs == null) {
			return new ArrayList<String>();
		}
		ArrayList<String> cols = new ArrayList<String>();
		ResultSetMetaData rsMetaData = rs.getMetaData();
		int numberOfColumns = rsMetaData.getColumnCount();

		// get the column names; column indexes start from 1
		for (int i = 1; i < numberOfColumns + 1; i++) {
			String columnName = rsMetaData.getColumnName(i);
			cols.add(columnName);
		}
		rs.close();
		return cols;
	}
	
	public static void printResultsFromQuery(ResultSet rs, boolean showColumnNames) throws SQLException {
		ResultSetMetaData md = rs.getMetaData(); 
		Integer numColumns = md.getColumnCount();
		
		if (showColumnNames) {
			// get the column names; column indexes start from 1
			String cols = "";
			for (int i = 1; i <= numColumns; i++) {
				cols = cols + md.getColumnName(i) + "\t";
			}
			System.out.println("\t" + cols);
		}
		while (rs.next()) {
			String vals="";
			for (int i=1; i <= numColumns; i++) {
				if (rs.getObject(i) != null) {
					vals = vals + rs.getObject(i).toString() + "\t";
				}else {
					vals = vals + "\t";
				}
			}
			System.out.println("\t" + vals);
		}
	}
	// Generates an MD5 hash
	public static String md5(String input) {
		try {
			byte[] bytesOfMessage = input.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(bytesOfMessage);
			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<messageDigest.length;i++) {
			    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			return hexString.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Convert the double to degrees, minutes and seconds
	public static String DecimalToDMS(Double input) {
		Double wholeDegrees = Math.floor (input); 
		Double minutes = (input - wholeDegrees) * 60;
		Double wholeMinutes = Math.floor(minutes);
		Double seconds = (minutes - wholeMinutes) * 60;
		DecimalFormat secondsFormat = new DecimalFormat("00.00");
		DecimalFormat minutesFormat = new DecimalFormat("00");
		
		// Note: Values are made positive ready for the cardinal initial to be added
		// (but as we don't know if this is a lat or long we can't add them at this stage)
		return Math.abs(wholeDegrees.shortValue()) + "°" + minutesFormat.format(wholeMinutes) + "‘" + secondsFormat.format(seconds) + "\"";
	}
}
