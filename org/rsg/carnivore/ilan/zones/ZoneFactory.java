package org.rsg.carnivore.ilan.zones;

import java.awt.Polygon;
import java.awt.Rectangle;

import org.rsg.carnivore.ilan.Vector3D;
import org.rsg.carnivore.ilan.Node;
import org.rsg.lib.random.Random;

public class ZoneFactory {
	public static Zone0 zone0;
	public static Zone1 zone1;
	public static Zone2 zone2;
	public static Zone3 zone3;
	public static Zone4 zone4;
	
	public ZoneFactory() {
		super();
		zone0 = new Zone0();
		zone1 = new Zone1();
		zone2 = new Zone2();
		zone3 = new Zone3();
		zone4 = new Zone4();
	}
	
	public void display() {
//		zone0.display();
//		zone1.display();
//		zone2.display();
//		zone3.display();
//		zone4.display();
	}
	
	public static Vector3D getZonePositionForNode(Node node, int portIndicatingService) {
		Polygon polygon;
		Rectangle rectangle;
		if(node.isLAN()) {
			polygon = zone0.polygon(); 
			rectangle = zone0.polygon().getBounds();
		} else {
			if(zone1.isAssociatedWithPort(portIndicatingService)) {
				polygon = zone1.polygon(); 
				rectangle = zone1.polygon().getBounds();			
			} else if(zone2.isAssociatedWithPort(portIndicatingService)) {
				polygon = zone2.polygon(); 
				rectangle = zone2.polygon().getBounds();			
			} else if(zone3.isAssociatedWithPort(portIndicatingService)) {
				polygon = zone3.polygon(); 
				rectangle = zone3.polygon().getBounds();			
			} else {
				polygon = zone4.polygon(); 
				rectangle = zone4.polygon().getBounds();			
			}		
		}
		
		int x = 0;
		int y = 0; 
		
		while(!polygon.contains(x, y)) {
			x = Random.nextInt((int)rectangle.getMinX(), (int)rectangle.getMaxX());
			y = Random.nextInt((int)rectangle.getMinY(), (int)rectangle.getMaxY());				
		}
		return new Vector3D(x,y);			

	}
}
