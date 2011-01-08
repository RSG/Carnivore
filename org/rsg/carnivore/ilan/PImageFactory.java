package org.rsg.carnivore.ilan;

import processing.core.PImage;

public class NodeIconFactory {
	  public final static String IMAGES_FOLDER = "data/images/";
	  public final static String ICON_LAPTOP_BLACK = IMAGES_FOLDER + "laptop_black.png";
	  public final static String ICON_LAPTOP_SILVER = IMAGES_FOLDER + "laptop_silver.png";
	  public final static String ICON_SERVER_BLACK = IMAGES_FOLDER + "server_black.png";
	  public final static String ICON_PRINTER = IMAGES_FOLDER + "printer.png";
	  public final static String ICON_ROUTER_BLACK = IMAGES_FOLDER + "router_black.png";

	  public static PImage laptopSilver = null; 
	  public static PImage laptopBlack = null; 
	  public static PImage serverBlack = null; 
	  public static PImage printer = null; 
	  public static PImage routerBlack = null; 
	  
	  public static boolean isInitialized = false; 
	  
	  public static void init() {
		  laptopSilver = CarnivoreILanPApplet.papplet.loadImage(ICON_LAPTOP_SILVER);   
		  laptopBlack = CarnivoreILanPApplet.papplet.loadImage(ICON_LAPTOP_BLACK);    
		  serverBlack = CarnivoreILanPApplet.papplet.loadImage(ICON_SERVER_BLACK);   
	      printer = CarnivoreILanPApplet.papplet.loadImage(ICON_PRINTER);  		  
	      routerBlack = CarnivoreILanPApplet.papplet.loadImage(ICON_ROUTER_BLACK);        		
	      isInitialized = true;
	  }

}
