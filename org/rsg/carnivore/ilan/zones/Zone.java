package org.rsg.carnivore.ilan.zones;

import java.awt.Point;
import java.awt.Polygon;
import java.util.Iterator;
import org.rsg.carnivore.ilan.CarnivoreILanPApplet;
import org.rsg.carnivore.ilan.Service;
import org.rsg.carnivore.ilan.Services;
import processing.core.PApplet;

public class Zone {
	private String label = "";
	public static final int LABEL_LEFT_MARGIN = 11; 
	public static final int LABEL_FONT_LARGE = (int) (LABEL_LEFT_MARGIN * 1.0f); 
	private Services services = new Services();
	public static int LABEL_COLOR_R = 113;
	public static int LABEL_COLOR_G = 114;
	public static int LABEL_COLOR_B = 116;
	
	public Zone(String label, Services services) {
		super();
		this.label = label;
		this.services = services;
	}
	
	public Polygon polygon() {
		return null;
	}
	
	public boolean isAssociatedWithPort(int port) {
	    Iterator<String> iterator = services.keySet().iterator();
	    while (iterator.hasNext()) {
	    	Service service = services.get(iterator.next());
	    	if(service.port == port) return true;
	    }
	    return false;
	}

	public void display() {
		PApplet papplet = CarnivoreILanPApplet.papplet;
		papplet.noFill(); 
		papplet.stroke(255,0,0); //red
		papplet.strokeWeight(5);
		
		Polygon polygon  = polygon();
		int[] xpoints = polygon.xpoints;
		int[] ypoints = polygon.ypoints;
		
		if(polygon.npoints < 2) return; 
		
		for(int i = 0; i < polygon.npoints-1; i++) {
			int x0 = xpoints[i];
			int y0 = ypoints[i];
			int x1 = xpoints[i+1];
			int y1 = ypoints[i+1];
			papplet.line(x0,y0, x1,y1);   			
		}
		papplet.line(xpoints[polygon.npoints-1],ypoints[polygon.npoints-1], xpoints[0],ypoints[0]); //closes the polygon
		papplet.strokeWeight(1);
	}
	
	public Point getLabelPosition() {
		return null;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Services getServices() {
		return services;
	}

	public void setServices(Services services) {
		this.services = services;
	}
	
	public String portsToString() {
		return "PORTS +- " + services.portsToString(", ");
	}

	  protected static void putIntoServices(Services services, int port, String name, String DEFAULT_COLOR, String description){
	      Service s = new Service(port, name, DEFAULT_COLOR, description);
	      services.put(port+"", s);	  
	  }

}
