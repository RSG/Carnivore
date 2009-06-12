package org.rsg.carnivore.ilan;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.rsg.lib.time.TimeUtilities;

import processing.core.PApplet;
//
//final static int APP_STATE_RUN = 0;
//final static int APP_STATE_PREFS = 1;
//final static int PREFS_PANE_WIDTH = 300;
//
//final static String DEFAULT_COLOR = "666666";
//final static float PIE_CHART_MIN_SLIVER_SIZE = TWO_PI/150;
//final static String STRING_TRUNCATION = "...";
//final static int LINE_HEIGHT = 15;
//final static int PAGE_MARGIN_LEFT = 15;
//final static int LINE_HEIGHT_COLORKEY = 20;
//
//final static int COLORKEY_WIDTH = 700;
//final static int COLORKEY_COLS = 2;
////final static int COLORKEY_ROWS = 30;
//final static int COLORKEY_SWATCH_HEIGHT = 15;
//final static int COLORKEY_SWATCH_WIDTH = 60;
//
//final static int LABEL_WIDTH = 300;
//final static int LABEL_TEXTBOX_HSPACER = 4;
//final static int LABEL_TEXTBOX_VSPACER = 3;
//
//final static int PROTO_TCP = 6;
//final static int PROTO_UDP = 17;
//
//final static color WHITE = 255;
//final static color BLACK = 0;
//final static color RED = #FF0066; 
//final static color GREEN = #009900; 
//final static color BLUE = #0066CC; 
//final static color HORIZON = #D3D3D3;
//final static int LOCALHOST_HALO_WIDTH = 20;
//final static float NODE_START_DIAMETER_LAN = 60.0;
//final static float NODE_START_DIAMETER_WAN = 70.0;
//final static float NODE_START_DIAMETER = NODE_START_DIAMETER_LAN;
//final static int NODE_START_DIE_CYCLE_DIAMETER = 30;
//final static int NODE_MIN_DIAMETER = 10;
//
//final static int NODE_MAX_PERFORMANCE = 20;
//
//final static float LINK_SHRINK_SPEED = .998;
//final static float NODE_SHRINK_SPEED_FAST = .98; //.98;//0.999;
//final static float NODE_SHRINK_SPEED_FAST2 = .98; //.98;//0.999;
//final static float NODE_SHRINK_SPEED_SLOW = .998;//.998; //.98;//0.999;
//final static float WIGGLE_FACTOR_NODE = .5;
//final static float WIGGLE_FACTOR_BEZIER = .03;
//
//
///*
//final static float WAN_MARGIN_LEFT   = EYEBEAM_FLOORPLAN_MARGIN_LEFT/2;
//final static float WAN_MARGIN_RIGHT  = EYEBEAM_FLOORPLAN_MARGIN_RIGHT/2;
//final static float WAN_MARGIN_TOP    = EYEBEAM_FLOORPLAN_MARGIN_TOP/2;
//final static float WAN_MARGIN_BOTTOM = EYEBEAM_FLOORPLAN_MARGIN_BOTTOM/2;
//*/
//
//final static float WAN_MARGIN_LEFT   = 0;
//final static float WAN_MARGIN_RIGHT  = 0;
//final static float WAN_MARGIN_TOP    = 0;
//final static float WAN_MARGIN_BOTTOM = 0;
//
//
//final static int FRAME_RATE_STARTING = 20;
//final static int SCROLL_TIME = FRAME_RATE_STARTING * 15; //in seconds
//
//
//  
//final static int AUTO_ARRANGE_GRID = 0;
//final static int AUTO_ARRANGE_CIRCLE = 1;
//final static int AUTO_ARRANGE_HORIZON = 2;
//final static String HOME = "HOME";
//final static String LAN = "L O C A L   A R E A   N E T W O R K";
//final static String INTERNET = "I N T E R N E T";
//final static String FILENAME_PREFS_NODES      = "preferences_nodepositions.txt";
//final static String FILENAME_COLORKEY         = "colorkey.gif";
//final static String FILENAME_BACKGROUND_MAP   = "EyebeamFloorplan-inverted.png";
//final static float AUTOARRANGE_SPEED = .6;
////final int APP_LAUNCHED_MILLIS = millis();
//
//final static int NODE_TYPE_LOCALHOST = 0;
//final static int NODE_TYPE_LAN = 1;
//final static int NODE_TYPE_WAN = 2;
//
//Localhost localhost = new Localhost();
//
////SET/GET THE STROKE WEIGHT BASED ON NUMBER OF ACTIVE NODES
//final static int STROKEWEIGHT_DEFAULT      = 1;
//final static int HALO_STROKEWEIGHT_DEFAULT = 2;
//final static int HALO_STROKEWEIGHT_MIN     = 2;
///*float currentHaloStrokeWeight = STROKEWEIGHT_DEFAULT;
//float getHaloStrokeWeight() { return currentStrokeWeight; }
//float getHaloStrokeWeightPicked() { return currentStrokeWeight + 1; }
//void setStrokeWeight() {
//  //weight goes from default to min over the first 100 nodes, but then stays at min
//  currentStrokeWeight = max(STROKEWEIGHT_MIN, STROKEWEIGHT_DEFAULT - (nodesActive * .001)); 
//}*/


public class Util {
	final static int ONE_MINUTE = 60000;
	final static int ONE_SECOND = 1000;
	public static int aSecondInTheFuture = CarnivoreILanPApplet.papplet.millis() + ONE_SECOND;
	public static int aMinuteInTheFuture = CarnivoreILanPApplet.papplet.millis() + ONE_MINUTE;
	public final static int EYEBEAM_FLOORPLAN_MARGIN_LEFT   = 157;
	public final static int EYEBEAM_FLOORPLAN_MARGIN_RIGHT  = 147;
	public final static int EYEBEAM_FLOORPLAN_MARGIN_TOP    = 145;
	public final static int EYEBEAM_FLOORPLAN_MARGIN_BOTTOM = 174;
	public final static float LAN_MARGIN_LEFT   = EYEBEAM_FLOORPLAN_MARGIN_LEFT*1.5f;
	public final static float LAN_MARGIN_RIGHT  = EYEBEAM_FLOORPLAN_MARGIN_RIGHT*1.5f;
	public final static float LAN_MARGIN_TOP    = EYEBEAM_FLOORPLAN_MARGIN_TOP*1.5f;
	public final static float LAN_MARGIN_BOTTOM = EYEBEAM_FLOORPLAN_MARGIN_BOTTOM*1.5f;
	public final static int LINK_STROKEWEIGHT_MAX = 35;
	public final static int LINK_STROKEWEIGHT_MIN = 2;
	public final static float LINK_SHRINK_SPEED = .998f;

	//add to PApplet?
	//does the inverse of "constrain" 
	static public final float exclude(float amt, float low, float high){
	  return ((amt < low) || (amt > high)) ? amt : (((amt - low) < (high - amt)) ? low : high);
	}
	
	static public final int exclude(int amt, int low, int high){
	  return ((amt < low) || (amt > high)) ? amt : (((amt - low) < (high - amt)) ? low : high);
	}
	
	/*static public final float exclude(float f, float i, float j){
	  if((f<i) || (f>j)) {
	    return f;
	  } else if((f-i) < (j-f)) {
	    return i;
	  } else {
	    return j;
	  }
	}*/
	
	//GET/SET THE TIMER STUFF
	public static void resetFutureSecond() { aSecondInTheFuture = CarnivoreILanPApplet.papplet.millis() + ONE_SECOND; }
	public static void resetFutureMinute() { aMinuteInTheFuture = CarnivoreILanPApplet.papplet.millis() + ONE_MINUTE; }
	
	public static boolean aSecondHasPassed() {
	  //println("if(millis():"+millis()+" > aSecondInTheFuture:"+aSecondInTheFuture+") {...}");
	  if(CarnivoreILanPApplet.papplet.millis() > aSecondInTheFuture) {
	    resetFutureSecond();
	    return true;
	  }
	  return false;
	}
	
	public static boolean aMinuteHasPassed() {
	  if(CarnivoreILanPApplet.papplet.millis() > aMinuteInTheFuture) {
	    resetFutureMinute();
	    return true;
	  }
	  return false;
	}
	
	String uptime(){
	  return TimeUtilities.millis2string(CarnivoreILanPApplet.papplet.millis());
	}
	
	//like Perl's chop -- removes last char no matter what it is
	String chop(String s) {
	  if(s.length() > 0) { s = s.substring(0, s.length()-1); }
	  return s;
	}
	
//	//this truncates the first string
//	String truncate(String a, String b, int pixelWidth) {
//	  int targetWidth = pixelWidth - (LABEL_TEXTBOX_HSPACER*2);
//	  if(textWidth(a+b) < targetWidth) {return a+b;}
//	  
//	  while(textWidth(trim(a)+STRING_TRUNCATION+b) > targetWidth) {
//	    a = chop(a);
//	  }
//	  return trim(a)+STRING_TRUNCATION+b;
//	}
//	
//	
//	String truncate(String s, int pixelWidth) {
//	  int targetWidth = pixelWidth - (LABEL_TEXTBOX_HSPACER*2);
//	  if(textWidth(s) < targetWidth) {return s;}
//	  
//	  while(textWidth(trim(s)+STRING_TRUNCATION) > targetWidth) {
//	    s = chop(s);
//	  }
//	  return trim(s)+STRING_TRUNCATION;
//	}
	
//	String millis2string(int milli) {
//	    int h = PApplet.floor(milli / (1000 * 60 * 60));
//	    int m = PApplet.floor((milli / (1000 * 60)) % 60);
//	    int s = PApplet.floor((milli / 1000) % 60);
//	    String returnme; 
//	    
//	    if(h > 0) {
//	      returnme = h + "h "+ m + "m"; 
//	    } else if(m > 1) {
//	      returnme = m + " mins";
//	    } else if(m > 0) {
//	      returnme = m + " min";
//	    } else {
//	      returnme = s + " secs";
//	    }
//	      
//	    //println("[Node] millis2string("+milli+"): "+returnme);  
//	    return returnme;
//	}
	
	public static boolean isGoodLine(String s) {
	    if(s.length() < 1) {return false;} //skip empty lines 
	    if(s.substring(0,1).equals("#")) { return false; } //skip comments
	    if(s.substring(0,1).equals(" ")) { return false; } //skip whitespace lines
	    return true;
	}
	  
	public static String trimComment(String s) {
	    int splitter = s.indexOf("#");
	    if(splitter > 0) { 
	      return s.substring(0, splitter).trim();
	    }
	    return s.trim();
	}
	
	/*Node getLocalNode() {
	  Iterator iterator = nodes.keySet().iterator(); 
	  while (iterator.hasNext()) {  
	    Node n = (Node) nodes.get(iterator.next()); 
	    if(n.isLocalhost) { return n; }
	  }
	  return null;
	}*/
	
	float scale2width (float f) { return f * CarnivoreILanPApplet.papplet.width/255; }
	float scale2height(float f) { return f * CarnivoreILanPApplet.papplet.height/255; }
	float scale2width (float f, int base) { return f * CarnivoreILanPApplet.papplet.width/base; }
	float scale2height(float f, int base) { return f * CarnivoreILanPApplet.papplet.height/base; }
	float scale2bound (float f, int base, float bound) { return f * bound/base; }
	
	/*String[] arrayList2stringArray(ArrayList a) {
	  String[] b = new String[a.size()];
	  for(int i =0; i<a.size();i++) {
	    a[i] = (String) a.get(i);
	  }
	  return a;
	}*/
	
	//refer to rfc3330
	public static boolean ipIsLAN(String ip) {
	    //println("\t\tipIsLAN("+ip+") trying private network masks");
	    if(stringMatchesAtStart(ip, "0.")) { return true; }             //class A "this" network 0.0.0.0/8
	    else if(stringMatchesAtStart(ip, "10.")) { return true; }       //class A private network 10.0.0.0/8
	    else if(stringMatchesAtStart(ip, "172.")) {
	      if     (stringMatchesAtStart(ip, "172.16.")) { return true; } //class B private network 172.16.0.0/12
	      else if(stringMatchesAtStart(ip, "172.17.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.18.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.19.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.20.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.21.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.22.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.23.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.24.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.25.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.26.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.27.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.28.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.29.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.30.")) { return true; } //class B private network 
	      else if(stringMatchesAtStart(ip, "172.31.")) { return true; }}//class B private network 
	    else if(stringMatchesAtStart(ip, "192.168.")) { return true; } //class C private network 192.168.0.0/16
	
	    //Node n = getLocalNode();
	    if(CarnivoreILanPApplet.papplet.nodelocalhost != null) {
	      //println("\t\tipIsLAN("+ip+") trying localhost class B netmask");
	      String classBprefix = (int)(CarnivoreILanPApplet.papplet.nodelocalhost.ip.a) + "." + (int)(CarnivoreILanPApplet.papplet.nodelocalhost.ip.b) + ".";
	      //println("\t\t[Util] classBprefix: ipIsLAN(String "+ip+") "+ classBprefix);
	      if(stringMatchesAtStart(ip, classBprefix)) { return true; } //a class B netmask taken from the current machine's IP
	                                                                  //TODO: how reasonable is it to assume a class B netmask means LAN? 
	                                                                  //      is there some way to query the actual netmask instead and use that?? 
	    } else {
	      //println("\t\tipIsLAN("+ip+") no localhost yet...");
	    }
	    return false;
	}
	
	public static boolean isProbablyAnIPaddress(String s) {
	  //print("isProbablyAnIPaddress: " +s + "... ");
	  String A = s.substring(0,1);
	  String B = s.substring(s.length()-1,s.length());
	  if((Character.isDigit(s.charAt(0))) && (Character.isDigit(s.charAt(s.length()-1)))) { return true; }
	  return false;
	} 
	
	public static boolean stringMatchesAtStart(String s, String match){
	    if(s.length() < match.length()) { return false; }
	    if(s.substring(0,match.length()).equals(match)) { return true; }
	    return false;
	}
	

	  
	
	public static float scaleToRangeWithBase(float f, float range, float base) {
	  return f * range / base;
	}
	
	public static boolean getLineLineIntersection(Line2D.Double l1, Line2D.Double l2, Point2D.Double intersection) {
	    if (!l1.intersectsLine(l2))
	        return false;
	        
	    double  x1 = l1.getX1(), y1 = l1.getY1(),
	            x2 = l1.getX2(), y2 = l1.getY2(),
	            x3 = l2.getX1(), y3 = l2.getY1(),
	            x4 = l2.getX2(), y4 = l2.getY2();
	    
	    intersection.x = det(det(x1, y1, x2, y2), x1 - x2,
	                         det(x3, y3, x4, y4), x3 - x4)/
	                     det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
	    intersection.y = det(det(x1, y1, x2, y2), y1 - y2,
	                         det(x3, y3, x4, y4), y3 - y4)/
	                     det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
	
	    return true;
	}
	
	public static double det(double a, double b, double c, double d) {
	    return a * d - b * c;
	}

}



/////////////////////////////////////////////////////////////////////////////
// HostNameFinderThread -- put this in a thread because it blocks 
/*class HostNameFinderThread extends Thread {
  Node parent;
  private HostNameFinderThread(Node parent) {
    this.parent = parent;
    this.start();
  }							
  
  public void run() {	
    try {
      parent.host = java.net.InetAddress.getByName(parent.ip).getHostName();
      println("[HostNameFinderThread] for "+parent.ip+" found host "+parent.host);
    } catch(UnknownHostException e) {
      println("[HostNameFinderThread] UnknownHostException");
    }
  }
}*/


