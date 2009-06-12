package org.rsg.carnivore.ilan.zones;

import java.awt.Point;
import java.awt.Polygon;
import org.rsg.carnivore.ilan.CarnivoreILanPApplet;
import org.rsg.carnivore.ilan.Services;

public class Zone2 extends Zone {	
	public Zone2() {
		super("Web", initServices());
	}
	
	public Polygon polygon() {
		int width = CarnivoreILanPApplet.papplet.width;
		int height = CarnivoreILanPApplet.papplet.height;
		Polygon polygon = new Polygon();
		polygon.addPoint(width-20, 20);
		polygon.addPoint((int) (width * (1-.26f)), 20);
		polygon.addPoint((int) (width * (1-.45f)), (int) (height * .2f));
		polygon.addPoint((int) (width * (1-.16f)), (int) (height *.45f));
		polygon.addPoint(width-20, (int) (height *.33f));		
		return polygon;
	}

	public Point getLabelPosition() {
		return new Point(CarnivoreILanPApplet.papplet.width - LABEL_LEFT_MARGIN, LABEL_FONT_LARGE*2);	
	}
	
	public static Services initServices() {
		  Services services = new Services();
		  putIntoServices(services, 80, null, null, null);
		  putIntoServices(services, 8020, null, null, null);
		  putIntoServices(services, 443, null, null, null);
		  putIntoServices(services, 8012, null, null, null); //shoutcast
		  putIntoServices(services, 8080, null, null, null); //web
	      return services;	  
	}
}
