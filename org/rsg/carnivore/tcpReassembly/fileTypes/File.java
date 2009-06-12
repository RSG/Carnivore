package org.rsg.carnivore.tcpReassembly.fileTypes;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

public class File {
	public final static String PATH_TO_CACHE = "cache/";
	
	 
//    public static void writeImageAsType(File file, BufferedImage bufferedImage, String type) {
//		try {
//        	ImageIO.write(bufferedImage, type, (ImageOutputStream) file);
//    	} catch (IOException e) {
//    		e.printStackTrace();
//    	}        
//    }

    
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
}
