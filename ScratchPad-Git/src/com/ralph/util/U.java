package com.ralph.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

// This is basically text utils and any other methods that will be accessed globally
// Note: Due to this class being global utility methods, it shouldn't import any classes
// from the rest of my code! (java.lang, .io, .math,  Apache Commons etc is fine though)
public class U {
	// Used by bytesToHex method
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	// Methods are static and instantiating this class would be pointless
	private U() {
	    throw new UnsupportedOperationException("Can't instantiate class");
	}
	
    
    
    ///////////////////////////// Start string manipulation ////////////////////////////////////////////////
    
	// Capitalises first letter of each word, lower case for rest of word
    public static String toProperCase(String inputString) {
        StringBuilder sb = new StringBuilder();

        for (String currentWord : inputString.split(" ")) {
            if (currentWord != null && currentWord.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                // First letter
                sb.append(currentWord.substring(0, 1).toUpperCase());
                // Rest of word
                sb.append(currentWord.substring(1, currentWord.length()).toLowerCase());
            }
        }
        return sb.toString();
    }

	// Uses British locale rules to add thousands separators into a number
	public static String formatNumber(Number number) {
		Locale locale = new Locale("en","GB");
		return NumberFormat.getInstance(locale).format(number);
	}
	
	// This is how I like my timestamps
	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz", new Locale("en","GB"));
		return sdf.format(date);
	}
	
	// This is how I like my durations
	public static String formatDuration(Long durationInMillis) {
		if (durationInMillis < 0) {
			// Durations can't be negative
			return null;
		}
		String res = "";
		if (durationInMillis < 1000) {
			res = durationInMillis + "ms";
		} else {
		    long days  = TimeUnit.MILLISECONDS.toDays(durationInMillis);
		    long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
		                   - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(durationInMillis));
		    long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis)
		                     - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(durationInMillis));
		    long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis)
		                   - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationInMillis));
		    if (days == 0 && hours == 0) {
		    	res = String.format("%02dm %02ds", minutes, seconds);
		    } else if (days == 0) {
		    	res = String.format("%02dh %02dm %02ds", hours, minutes, seconds);
		    } else {
		    	res = String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
		    }
		}
	    return res;
	}

    
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
		
	public static int calculateLevenshteinDistance(String str1,String str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];
 
		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= str2.length(); j++)
			distance[0][j] = j;
 
		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
 
		return distance[str1.length()][str2.length()];    
	}

	
	public static ArrayList<String> multiLineStringToList(String input, Boolean trimWhitespace) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader rdr = new BufferedReader(new StringReader(input));
			
			for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
				if (trimWhitespace==true) {
					list.add(line.trim());
				} else {
					list.add(line);
				}
			}
			rdr.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	///////////////////////////// End string manipulation ////////////////////////////////////////////////
	
	
	///////////////////////////// Start Maths ////////////////////////////////////////////////
    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
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

	///////////////////////////// End Maths ////////////////////////////////////////////////
	
	///////////////////////////// Start SQL\database related ////////////////////////////////////////////////
	
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
	
	public static String getResultsFromQuery(ResultSet rs, boolean showColumnNames) throws SQLException {
		String output="";
		ResultSetMetaData md = rs.getMetaData(); 
		Integer numColumns = md.getColumnCount();
		
		if (showColumnNames) {
			// get the column names; column indexes start from 1
			String cols = "";
			for (int i = 1; i <= numColumns; i++) {
				cols = cols + md.getColumnName(i) + "\t";
			}
			output = output + ("\t" + cols + "\r\n");
		}
		int rowCounter=0;
		while (rs.next()) {
			String vals="";
			for (int i=1; i <= numColumns; i++) {
				if (rs.getObject(i) != null) {
					vals = vals + rs.getObject(i).toString() + "\t";
				}else {
					vals = vals + "\t";
				}
			}
			output = output + ("\t" + vals + "\r\n");
			rowCounter++;
		}
		if (rowCounter == 0) {
			output="[No results]";
		}
		return output;
	}
	
	///////////////////////////// End SQL\database related ////////////////////////////////////////////////

	///////////////////////////// Start encoding, hashing and encryption ////////////////////////////////////////////////

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
	
	// Generates an SHA-256 hash
	public static String sha256(String input) {
		try {
			byte[] bytesOfMessage = input.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
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
	
	// Converts a byte array to a string of hexadecimal characters
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	// Converts a string of hexadecimal characters to the equivalent array of bytes
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	// Encodes a binary object to a base64 string (so to decode it you need to know what it is!)
	public static String base64Encode(byte[] input) {
		byte[] encoded = Base64.encodeBase64(input);
		return new String(encoded);
	}
	
	// Encodes a string to a base64 string
	public static String base64Encode(String input) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(input.getBytes());
	}
	
	// Decodes a base64 string to an array of bytes
	public static byte[] base64Decode(String input) {
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] decodedBytes = decoder.decodeBuffer(input);
			return decodedBytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	///////////////////////////// End encoding, hashing and encryption ////////////////////////////////////////////////
	
	///////////////////////////// Start Spatial and lat/long related functions ////////////////////////////////////////////////
	
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
        return Math.abs(wholeDegrees.shortValue()) + "\u00B0" + minutesFormat.format(wholeMinutes) + "\u2032" + secondsFormat.format(seconds) + "\u2033";
	}
	
	
	
	
	// Find bearing from one lat/long to another lat/long
	// Source: http://stackoverflow.com/questions/9457988/bearing-from-one-coordinate-to-another
	// Ported from: http://www.movable-type.co.uk/scripts/latlong.html
	public static double bearing(double lat1, double lon1, double lat2, double lon2){
	  double longitude1 = lon1;
	  double longitude2 = lon2;
	  double latitude1 = Math.toRadians(lat1);
	  double latitude2 = Math.toRadians(lat2);
	  double longDiff= Math.toRadians(longitude2-longitude1);
	  double y= Math.sin(longDiff)*Math.cos(latitude2);
	  double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

	  return (Math.toDegrees(Math.atan2(y, x))+360)%360;
	}
	
	
	// Find distance from one lat/long to another lat/long (ignores elevation)
	// 'unit' can be M for miles (default), K for kilometres or N for nautical miles
	// Source: http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
	public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
		dist = Math.acos(dist);
		dist = Math.toDegrees(dist);
		dist = dist * 60 * 1.1515; // Miles
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}
	
	///////////////////////////// End Spatial and lat/long related functions ////////////////////////////////////////////////
	
	
	//Other:
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

}
