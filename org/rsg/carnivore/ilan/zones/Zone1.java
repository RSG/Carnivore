package org.rsg.carnivore.ilan.zones;

import java.awt.Point;
import java.awt.Polygon;
import org.rsg.carnivore.ilan.CarnivoreILanPApplet;
import org.rsg.carnivore.ilan.Services;

public class Zone1 extends Zone {	
	public Polygon polygon() {
		int width = CarnivoreILanPApplet.papplet.width;
		int height = CarnivoreILanPApplet.papplet.height;
		Polygon polygon = new Polygon();
		polygon.addPoint(20, 20);
		polygon.addPoint((int) (width * .26), 20);
		polygon.addPoint((int) (width * .45), (int) (height * .2));
		polygon.addPoint((int) (width * .16), (int) (height *.45));
		polygon.addPoint(20, (int) (height *.33));
		return polygon;
	}
	
	public Zone1() {
		super("Email / IM", initServices());
	}

	public Point getLabelPosition() {
		return new Point(LABEL_LEFT_MARGIN, LABEL_FONT_LARGE*2);	
	}

	public static Services initServices() {
		  Services services = new Services();
		  putIntoServices(services, 110, null, null, null); //pop
		  putIntoServices(services, 5190, null, null, null); //aim
		  putIntoServices(services, 25, null, null, null); //smtp
		  putIntoServices(services, 143, null, null, null); //imap
		  putIntoServices(services, 220, null, null, null); //imap3
		  putIntoServices(services, 993, null, null, null); //imaps
	      return services;	  
	}
}
