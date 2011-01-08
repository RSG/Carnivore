package org.rsg.carnivore.ilan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DiskCache {
	public static final String PATH_TO_CACHE = "cache/";
	
	public static void main(String[] args) {
		printAll();	
		getDiskUsage();
//		deleteAll();
	}
	
	public static void writeFile(byte[] data, String fileName) {
		if(null==data || data.length<1) return;
		OutputStream out;
		try {
			out = new FileOutputStream(fileName);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printAll() {
		System.out.println("[DiskCache] printAll -- " + getFileCount() + " files");
		File[] files = getFiles();
		for(File file : files) 
			System.out.println("\t"+file);
	}

	public static void deleteAll() {
		System.out.println("[DiskCache] deleteAll -- " + getFileCount() + " files");
		File[] files = getFiles();
		for(File file : files) 
			file.delete();
		System.out.println("[DiskCache] deleteAll -- " + getFileCount() + " files");
	}

	public static void getDiskUsage() {
		File[] files = getFiles();
		long bytes = 0; 
		for(File file : files) 
			bytes += file.length();
		System.out.println("[DiskCache] getDiskUsage -- "+bytes+" bytes (" + getFileCount() + " files)");
	}

	public static int getFileCount() {
		return getFiles().length;
	}

	public static File[] getFiles() {
		File cacheDirectory = new File(PATH_TO_CACHE);
		return cacheDirectory.listFiles();
	}

}
