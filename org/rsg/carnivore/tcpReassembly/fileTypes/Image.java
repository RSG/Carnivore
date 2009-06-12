package org.rsg.carnivore.tcpReassembly.fileTypes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.rsg.carnivore.Core;
import processing.core.PImage;

public class Image extends PImage {
	public static int toInt(byte b) {
		return (b + 256) & 0x00ff;
	}
	
    public static void writeImage(byte[] data, String fileName, String extension) {
    	System.out.println("[Image] writeImage("+data+", "+fileName+", "+extension+")");
    	
    	
    	writeFile(data, fileName + "." + extension);
    	
    	Core.dispatchImage(fileName + "." + extension);
//    	CarnivoreILanPApplet.carnivoreconsole.parent.newImage(fileName + "." + extension);
    	
//    	java.awt.Image image = Toolkit.getDefaultToolkit().getImage(fileName + "." + extension);
//    	
////    	java.awt.Image image = Toolkit.getDefaultToolkit().createImage(data);
//    	System.out.println("\t" + image + " " + image.getWidth(null) + " x " + image.getHeight(null));
//    	
////    	BufferedImage bufferedimage = toBufferedImage(image);
////    	writeImage(bufferedimage, fileName, extension);
    }
    
	public static void writeFile(byte[] data, String fileName) {
		String cachepath = org.rsg.carnivore.tcpReassembly.fileTypes.File.PATH_TO_CACHE;
		if(null==data || data.length<1) return;
		OutputStream out;
		try {
			out = new FileOutputStream(cachepath + fileName);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//    private static void writeImage(BufferedImage bufferedimage, String fileName, String extension) {
////        String fileName = "savingAnImage";
//        File file = new File(fileName + "." + extension);
//        try {
//            ImageIO.write(bufferedimage, extension, file);  // ignore returned boolean
//        } catch(IOException e) {
//            System.out.println("Write error for " + file.getPath() +
//                               ": " + e.getMessage());
//        }
//    }
//
//    private static BufferedImage toBufferedImage(java.awt.Image image) {
//        int w = image.getWidth(null);
//        int h = image.getHeight(null);
//        int type = BufferedImage.TYPE_INT_RGB;  // other options
//        BufferedImage bufferedimage = new BufferedImage(w, h, type);
//        Graphics2D g2 = bufferedimage.createGraphics();
//        g2.drawImage(image, 0, 0, null);
//        g2.dispose();
//        return bufferedimage;
//    }

}
