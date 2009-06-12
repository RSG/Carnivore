package org.rsg.carnivore.ilan.zones;

import java.awt.Point;
import java.awt.Polygon;
import org.rsg.carnivore.ilan.CarnivoreILanPApplet;
import org.rsg.carnivore.ilan.Services;

public class Zone0 extends Zone {	
	public Polygon polygon() {
		int width = CarnivoreILanPApplet.papplet.width;
		int height = CarnivoreILanPApplet.papplet.height;
		Polygon polygon = new Polygon();
		polygon.addPoint((int) (width * .5f), (int) (height * .25f));
		polygon.addPoint((int) (width * .8f), (int) (height * .5f));
		polygon.addPoint((int) (width * .5f), (int) (height * .75f));
		polygon.addPoint((int) (width * .2f), (int) (height * .5f));
		return polygon;
	}
	
	public Zone0() {
		super("Web", initServices());
	}

	public Point getLabelPosition() {
		return new Point(LABEL_LEFT_MARGIN, LABEL_FONT_LARGE*2);	
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
