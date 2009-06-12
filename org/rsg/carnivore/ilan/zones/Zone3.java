package org.rsg.carnivore.ilan.zones;

import java.awt.Point;
import java.awt.Polygon;
import org.rsg.carnivore.ilan.CarnivoreILanPApplet;
import org.rsg.carnivore.ilan.Services;

public class Zone3 extends Zone {	
	public Zone3() {
		super("Technical", initServices());
	}

	public Polygon polygon() {
		int width = CarnivoreILanPApplet.papplet.width;
		int height = CarnivoreILanPApplet.papplet.height;
		Polygon polygon = new Polygon();
		polygon.addPoint(20, height-20);
		polygon.addPoint((int) (width * .26f), (int) (height-20));
		polygon.addPoint((int) (width * .45f), (int) (height * (1-.2f)));
		polygon.addPoint((int) (width * .16f), (int) (height * (1-.45f)));
		polygon.addPoint(20, (int) (height * (1-.33f)));		
		return polygon;
	}
	
	public Point getLabelPosition() {
		return new Point(LABEL_LEFT_MARGIN, (int) (CarnivoreILanPApplet.papplet.height - LABEL_FONT_LARGE*3.5));		
	}
	
	public static Services initServices() {
		  Services services = new Services();
		  putIntoServices(services, 53, null, null, null); //name-domain server
		  putIntoServices(services, 5353, null, null, null); //name-domain server
		  putIntoServices(services, 123, null, null, null); //Network Time Protocol
		  putIntoServices(services, 137, null, null, null); //NETBIOS
		  putIntoServices(services, 138, null, null, null); //NETBIOS
		  putIntoServices(services, 139, null, null, null); //NETBIOS
		  return services;	  
	}
}
