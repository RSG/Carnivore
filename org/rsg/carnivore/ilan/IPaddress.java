package org.rsg.carnivore.ilan;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.rsg.lib.random.Random;

/////////////////////////////////////////////////////////////////////////////
//does geoip functionality
/*import com.maxmind.geoip.*;
void ip2LatLon(String ip) {
  try {
            LookupService geoip = new LookupService("./GeoLiteCity.dat");
            println("[GeoIP] ip2LatLon("+ip+")");
            Location location = geoip.getLocation(ip);
	    println(location.latitude + "," + location.longitude);
	    geoip.close();

  //seems to throw a java.lang.NullPointerException for private addresses like 192.168.x.x etc
  } catch (Exception e) {
    System.out.println("Exception: " + e);
  }
}*/

/////////////////////////////////////////////////////////////////////////////
public class IPaddress {
  public float a,b,c,d;
  int lat, lon;
  int screenQuadrant = (int) (Random.random.nextInt(4));
  Line2D.Double rect1, rect2, rect3, rect4, hypotenuse; 
  Point2D.Double intersection;

  IPaddress(String ip) {  //takes an IPv4 address
    int splitter, octet1, octet2, octet3, octet4;
    
    //ip2LatLon(ip);
    
    // Use last two IP address bytes for x/y coords
    splitter = ip.indexOf(".");
    octet1 = Integer.valueOf(ip.substring(0, splitter));
    ip = ip.substring(splitter+1, ip.length());
  
    // Use last two IP address bytes for x/y coords
    splitter = ip.indexOf(".");
    octet2 = Integer.valueOf(ip.substring(0, splitter));
    ip = ip.substring(splitter+1, ip.length());
    
    // Use last two IP address bytes for x/y coords
    splitter = ip.indexOf(".");
    octet3 = Integer.valueOf(ip.substring(0, splitter));
    octet4 = Integer.valueOf(ip.substring(splitter+1, ip.length()));    
    
    this.a = octet1;
    this.b = octet2;
    this.c = octet3;
    this.d = octet4;
    //println("[Vector4D] constructor: "+this);
  }
  
  /*Vector3D convertToScreenPositionLAN(){
    float w = width - (EYEBEAM_FLOORPLAN_MARGIN_LEFT + EYEBEAM_FLOORPLAN_MARGIN_RIGHT);
    float h = height - (EYEBEAM_FLOORPLAN_MARGIN_TOP + EYEBEAM_FLOORPLAN_MARGIN_BOTTOM);
    Vector3D v = new Vector3D(scaleToRangeWithBase(c, w, 255)+EYEBEAM_FLOORPLAN_MARGIN_LEFT, 
                              scaleToRangeWithBase(d, h, 255)+EYEBEAM_FLOORPLAN_MARGIN_TOP);
    //Vector3D offset = new Vector3D(scale2bound(c, 255, TWO_PI));  //the c and d additions don't really do anything legible 
    //offset.normalize(d/10);
    //v.sub(offset);
    println("[Vector4D] " + this + " v:" + v);
    return v;
  }*/

  void computeIntersection() {
	int width = CarnivoreILanPApplet.papplet.width;
	int height = CarnivoreILanPApplet.papplet.height;
    hypotenuse = new Line2D.Double(width/2,height/2,0,0); 
    intersection = new Point2D.Double();    
    float r = (a + b + c + d)/100;
    float w = width/2;
    float h = height/2;
    Vector3D v = new Vector3D(r);
    Vector3D middle = new Vector3D(w,h);
    v.normalize(w);
    v.add(middle);
    hypotenuse.x2 = v.x; 
    hypotenuse.y2 = v.y; 
  
    if(		Util.getLineLineIntersection(rect1,hypotenuse,intersection) ||
    		Util.getLineLineIntersection(rect2,hypotenuse,intersection) ||
    		Util.getLineLineIntersection(rect3,hypotenuse,intersection) ||
    		Util.getLineLineIntersection(rect4,hypotenuse,intersection)) {};    
  }

  Vector3D convertToScreenPositionLAN(){ 
	  int width = CarnivoreILanPApplet.papplet.width;
	  int height = CarnivoreILanPApplet.papplet.height;
      rect1 = new Line2D.Double(Util.LAN_MARGIN_LEFT,Util.LAN_MARGIN_TOP,width-Util.LAN_MARGIN_RIGHT,Util.LAN_MARGIN_TOP); 
      rect2 = new Line2D.Double(Util.LAN_MARGIN_LEFT,Util.LAN_MARGIN_TOP,Util.LAN_MARGIN_LEFT,height-Util.LAN_MARGIN_BOTTOM); 
      rect3 = new Line2D.Double(width-Util.LAN_MARGIN_LEFT,Util.LAN_MARGIN_TOP,width-Util.LAN_MARGIN_RIGHT,height-Util.LAN_MARGIN_BOTTOM); 
      rect4 = new Line2D.Double(Util.LAN_MARGIN_LEFT,height-Util.LAN_MARGIN_BOTTOM,width-Util.LAN_MARGIN_RIGHT,height-Util.LAN_MARGIN_BOTTOM); 
      computeIntersection();
      return new Vector3D((float)intersection.x, (float)intersection.y);
  } 
   
  Vector3D convertToScreenPositionWAN(){ 
    /*rect1 = new Line2D.Double(WAN_MARGIN_LEFT,WAN_MARGIN_TOP,width-WAN_MARGIN_RIGHT,WAN_MARGIN_TOP); 
    rect2 = new Line2D.Double(WAN_MARGIN_LEFT,WAN_MARGIN_TOP,WAN_MARGIN_LEFT,height-WAN_MARGIN_BOTTOM); 
    rect3 = new Line2D.Double(width-WAN_MARGIN_LEFT,WAN_MARGIN_TOP,width-WAN_MARGIN_RIGHT,height-WAN_MARGIN_BOTTOM); 
    rect4 = new Line2D.Double(WAN_MARGIN_LEFT,height-WAN_MARGIN_BOTTOM,width-WAN_MARGIN_RIGHT,height-WAN_MARGIN_BOTTOM); 
    computeIntersection();
    return new Vector3D((float)intersection.x, (float)intersection.y);*/
	int width = CarnivoreILanPApplet.papplet.width;
	int height = CarnivoreILanPApplet.papplet.height;

    float x,y;
    int base = 100;
    float rand = Random.random.nextFloat()*base;
    switch (screenQuadrant) {
      case 0:  x=0;     y=Util.scaleToRangeWithBase(rand, height, base); break;
      case 1:  x=width; y=Util.scaleToRangeWithBase(rand, height, base); break;    
      case 2:  x=Util.scaleToRangeWithBase(rand, width, base); y=0; break;    
      default: x=Util.scaleToRangeWithBase(rand, width, base); y=height; break;
    }
    return new Vector3D(x,y);
  } 
  
  public String toString() {
    return (int)(a)+"."+(int)(b)+"."+(int)(c)+"."+(int)(d);
  }
}
