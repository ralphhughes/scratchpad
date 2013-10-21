package com.ralph.util;

import java.io.File;
import java.util.ArrayList;

public class DirectoryReader {
	private static ArrayList<File> files;
	private static String[] fileTypeFilters;
	
	private static void Process(File aFile) {
		if (aFile.isFile()) {
			for (String currentFilter: fileTypeFilters) {
				// Case insensitive compare
				if (aFile.getName().toUpperCase().endsWith(currentFilter.toUpperCase())) {
					files.add(aFile);
				}
			}
		} else if (aFile.isDirectory()) {
			File[] listOfFiles = aFile.listFiles();
			if (listOfFiles != null) {
				for (int i = 0; i < listOfFiles.length; i++)
					Process(listOfFiles[i]);
			} else {
				System.out.println("[ACCESS DENIED]");
			}
		}
	}
	
	public static ArrayList<File> recursiveFindFiles(File startingFolder, String[] fileTypeFilter) {
		files = new ArrayList<File>();
		fileTypeFilters = fileTypeFilter; 
		Process(startingFolder);
		return files;
	}
}
