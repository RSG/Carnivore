package org.rsg.carnivore.ilan;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.rsg.carnivore.CarnivorePacket;
//import org.rsg.carnivore.ilan.link.Particles;
import org.rsg.carnivore.ilan.zones.ZoneFactory;
import org.rsg.lib.time.TimeUtilities;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

// Node
public class Node  {
  final static int LOCALHOST_HALO_WIDTH = 20;
  final static float NODE_START_DIAMETER_LAN = 60.0f;
  final static float NODE_START_DIAMETER_WAN = 70.0f;
  final static float NODE_START_DIAMETER = NODE_START_DIAMETER_LAN;
  final static int PROTO_TCP = 6;
  final static int PROTO_UDP = 17;
  final static int HALO_STROKEWEIGHT_DEFAULT = 2;
  final static int HALO_STROKEWEIGHT_MIN     = 2;
  final static int NODE_START_DIE_CYCLE_DIAMETER = 30;
  final static int NODE_MIN_DIAMETER = 10;
  final static float NODE_SHRINK_SPEED_FAST = .98f; //.98;//0.999;
  final static float NODE_SHRINK_SPEED_FAST2 = .98f; //.98;//0.999;
  final static float NODE_SHRINK_SPEED_SLOW = .998f;//.998; //.98;//0.999;
  final static int ONE_MINUTE = 60000;
  final static int ONE_SECOND = 1000;
  public final static String IMAGES_FOLDER = "data/images/";
  public final static String ICON_LAPTOP_BLACK = IMAGES_FOLDER + "laptop_black.png";
  public final static String ICON_LAPTOP_SILVER = IMAGES_FOLDER + "laptop_silver.png";
  public final static String ICON_SERVER_BLACK = IMAGES_FOLDER + "server_black.png";
//  Particles particles;  
  PortStats portstats = new PortStats();
  public IPaddress ip;
  String host;
  String host_short;
  public Vector3D position = new Vector3D();
  Vector3D velocity = new Vector3D(0,0,0);
  Vector3D cachedPositionGRID = new Vector3D();
  //Vector3D cachedPositionRING = new Vector3D();
  //Vector3D cachedPositionHORIZON = new Vector3D();
  Vector3D moveTo = new Vector3D();
  float diameter = 0; 
  float diameter_halo = 0; 
  PImage icon; 
  boolean isPicked = false;
  boolean isLocked = false;
  //int autoArrange = 0; //0=off, 1=on but revert to 0 when in position
  boolean wasManuallyMoved = false;
  float bdifx = 0.0f; 
  float bdify = 0.0f; 
  int datestampCreated, datestampLastTouched;
  //ScrollLabel scrollLabelService; 

  float scaler = 80;
  int totalpoints = 50;   
  float a = 17.7f; //governs number of lobes
  float b = 80f; //depth of grooves (zero means deep) 
  float c = -3.12f;
  float d = .3f; //second groove depth
  Vector3D pointcurrent = new Vector3D();
  Vector3D pointlast = new Vector3D();
  //int strokeweight = 3;
  boolean isOnline = false;
  boolean isAlive = false;
  boolean isLocalhost = false;
  boolean isLAN = false;  
  boolean isWAN = true;  
  int scrollTimer = 0;
  
  //flocking stuff
  /*Vector3D bezierSatellite = new Vector3D(0,0,0);
  //float bezierSatelliteDistance = 100;
  float flockrange = 250;
  //float flockbumper = 30;
  float rule1divisor = .001; //centralization
  float rule2divisor = .01; //decentralization
  float rule2speed = 1; //bumper
  float bezierSatelliteDirection; 
  boolean showDebug = false;*/
  
  /////////////////////////////////////////////////////////////////////////////
  // constructor
  Node(String ip, int portIndicatingService) {
    this.ip = new IPaddress(ip);
    this.host = ip.toString();  //set to ip for now.. HostNameFinderThread will try to update host later if it can 
    this.host_short = host;     //same
    //this.scrollLabelService = new ScrollLabel(this);

    setIsLANorWAN();
    setIsLocalhost();
    initDiameter();
    
    //POSITION FROM PREFS
    Vector3D v = CarnivoreILanPApplet.papplet.ilanpreferences.setXYZ(this); //sets moveTo if node was cached in prefs
    if(v != null) {
      //normalizeCoords(v);
      this.position.setXYZ(v);
      this.cachedPositionGRID.setXYZ(position);
      this.moveTo.setXYZ(position);      
      this.wasManuallyMoved = true; //excludes the node from auto-arrangement on restart
      
    //POSITION USING IP OCTETS
    } else { 
    	    	
//      Vector3D ipAsPosition;
//      if(isWAN) {
//        ipAsPosition = this.ip.convertToScreenPositionWAN();
//      } else {
//        ipAsPosition = this.ip.convertToScreenPositionLAN();
//      }
      
      /*
      //move LAN nodes to bottom
      float circleHorizonOffset = circleHorizon.y - circleHorizon.z;
      float rimmargin =  height * .2; //to keep nodes off the rim of the circle
      if(isLAN || isLocalhost) {
        ipAsPosition.y = scaleToRangeWithBase(ipAsPosition.y, abs(height - circleHorizonOffset + rimmargin), height) + circleHorizonOffset + rimmargin;        
      
      //and WAN nodes to top
      } else {
        ipAsPosition.y = scaleToRangeWithBase(ipAsPosition.y, circleHorizonOffset - rimmargin, height);
      }*/
      
      if(portIndicatingService > 0)
    	  this.position.setXYZ(ZoneFactory.getZonePositionForNode(this, portIndicatingService));
      
//      this.position.setXYZ(ipAsPosition);
      this.cachedPositionGRID.setXYZ(position);
      //normalizeCoords(position);
    }

//    particles = new Particles(this);
    // Load the images into the program 
    if(this.isLAN) {
	    if(this.isLocalhost) {
	        this.icon = CarnivoreILanPApplet.papplet.loadImage(ICON_LAPTOP_SILVER);    
	    } else {
	        this.icon = CarnivoreILanPApplet.papplet.loadImage(ICON_LAPTOP_BLACK);        	
	    }
    } else {
        this.icon = CarnivoreILanPApplet.papplet.loadImage(ICON_SERVER_BLACK);        	
    }
//    setIcon();
    this.touch();    
    
    //find host name -- this has to be in a thread, otherwise it blocks 
    new Thread(new Runnable() { 
	public void run() { 
          hostNameFinder();
	} 
    }).start();
        
    //set up flock stuff
    //bezierSatellite.copy(bezierPosition());
    //bezierSatelliteDirection = heading();//random(TWO_PI);    
        
    //println("[Node] constructor: "+this);
    //println("getStrokeWeight:"+getStrokeWeight());
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // main methods
  void hostNameFinder() {
		try { 
                  host = java.net.InetAddress.getByName(ip.toString()).getHostName(); //note: this line blocks
                  if(Util.isProbablyAnIPaddress(host)) {
                    /*if(isWAN) {
                      host_short = lastKnownService() + " Server";
                    } else {
                      host_short = "Eyebeam";
                    }*/
                    
                  } else {
                      int splitter;
                      if(isWAN) {
                        splitter = host.lastIndexOf("."); 
                        String s = host.substring(0, splitter);       
                        splitter = s.lastIndexOf("."); 
                        host_short = s.substring(splitter+1, s.length());       
                      } else {
                    	splitter = host.indexOf("."); //first dot on left
                    	host_short = host.substring(0, splitter);
                      }
                  }
                  //println("[HostNameFinderThread] for "+ip+" found host "+host);
		} catch (UnknownHostException e) {
		  //println("[HostNameFinderThread] UnknownHostException");
		}    
  }
  
  void initDiameter() {
    if(isWAN) {
      diameter = NODE_START_DIAMETER_WAN;
      diameter_halo = diameter*2;
    } else {
      diameter = NODE_START_DIAMETER_LAN;      
    }    
  }
  
  /*Vector3D bezierPosition() {
    Vector3D v = new Vector3D(heading() - PI/2);
    v.normalize(diameter/2);
    v.add(position);
    return v;
  }*/
  
  boolean isTCP() {
    if(portstats.lastKnownTransportProtocol == PROTO_TCP) {return true;}
    return false;
  }
  
  float heading() {
	int width = CarnivoreILanPApplet.papplet.width;
	int height = CarnivoreILanPApplet.papplet.height;
    if(isWAN) {
      switch (ip.screenQuadrant) {
        case 0:  return PApplet.TWO_PI/4 * 1;
        case 1:  return PApplet.TWO_PI/4 * 3;
        case 2:  return PApplet.TWO_PI/4 * 2;
        default: return PApplet.TWO_PI/4 * 4;
      }
      
      /*if(position.x == 0) {return TWO_PI/4 * 1;}
      else if(position.y == 0) {return TWO_PI/4 * 2;}
      else if(position.x == width) {return TWO_PI/4 * 3;}
      else if(position.y == height) {return TWO_PI/4 * 4;}*/
    } else {
      Vector3D v = new Vector3D(width/2 - position.x, height/2 - position.y);
      return v.heading2D() + PApplet.PI/2;  
    }  
  }
  
  float satelliteDistance() {
    return (float) diameter * 1.5f;
  }
  
  /*float normalizeSatelliteDirection(float bezierSatelliteDirection) {
    float headingMin = heading() -PI;
    float headingMax = heading();
    if(bezierSatelliteDirection < headingMin) {bezierSatelliteDirection = headingMin;}
    if(bezierSatelliteDirection > headingMax) {bezierSatelliteDirection = headingMax;}    
    return bezierSatelliteDirection;
  }

  void moveBezierSatellite(){
    bezierSatelliteDirection += (float)generator.nextGaussian() * WIGGLE_FACTOR_BEZIER;
    bezierSatelliteDirection = normalizeSatelliteDirection(bezierSatelliteDirection);
    Vector3D v = new Vector3D(bezierSatelliteDirection);
    //if(isLocalhost) {println("isLocalHost bezierSatelliteDirection:" + bezierSatelliteDirection);}
    v.normalize(satelliteDistance());
    v.add(bezierPosition());
    bezierSatellite.setXYZ(v);
    if(showDebug) { 
      stroke(255); noFill(); 
      ellipse(bezierSatellite.x, bezierSatellite.y, 10, 10); 
      line(bezierPosition().x,bezierPosition().y, bezierSatellite.x, bezierSatellite.y);
    }
  }*/
  
  /*Vector3D getFlockCenter() {
    Vector3D centerOfFlock = new Vector3D(position);
    for(int i = 0; i< flock.size(); i++) {
      Node n = (Node) flock.get(i);
      if(n == this) {continue;}
      centerOfFlock.add(n.position);
    }    
    centerOfFlock.div(flock.size() + 1);
    if(showDebug) { stroke(0); fill(200, 0, 0, 100); ellipse(centerOfFlock.x, centerOfFlock.y, 10, 10); }
    return centerOfFlock;
  }
  
  //move to center
  void flockRule1() {
    Vector3D centerOfFlock = getFlockCenter();
    centerOfFlock.sub(position);
    centerOfFlock.normalize(distance(position, centerOfFlock) * rule1divisor);
    position.add(centerOfFlock);
  }*/


  //move keep a distance from others
  /*void flockRule2() {
    Vector3D bumper = new Vector3D(0,0,0);
    Iterator iterator = nodes.keySet().iterator(); 
    while (iterator.hasNext()) {  
      Node n = (Node) nodes.get(iterator.next());
      if(n == this) {continue;}
      if(distance(n.position, this.position) < this.diameter*.75) {
        Vector3D v = subtract3D(n.position, this.position);
        v.normalize(rule2speed); 
        bumper.sub(v);
      }
    }
    this.position.add(bumper);
  }*/

//  void displayLinks() {
//    if(isPicked) {
//      ArrayList mylinks = links.getLinksForNode(this);
//      for(int i = 0; i< mylinks.size(); i++) {
//        Link link = (Link) mylinks.get(i);
//        link.display();
//      }
//    }
//  }
  
  /*void findFlock() {
    flock.clear();
    Iterator iterator = nodes.keySet().iterator(); 
    while (iterator.hasNext()) {  
      Node n = (Node) nodes.get(iterator.next());
      if(n == this) {continue;}
      if(distance(n.position, this.position) < flockrange) {
        flock.add(n);
        if(flock.size() > 10) {return;}
      }
    }
  }*/

  public Vector3D subtract3D(Vector3D v1, Vector3D v2) {
    Vector3D v = new Vector3D(v1.x - v2.x,v1.y - v2.y,v1.z - v2.z);
    return v;
  }
  
  public float distance(Vector3D v1, Vector3D v2) {
    float dx = v1.x - v2.x;
    float dy = v1.y - v2.y;
    float dz = v1.z - v2.z;
    return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
  }
  
  void display() {
    //flock stuff
//    wiggle();
    //moveBezierSatellite();
    //findFlock();
    //flockRule1();   //move to center
    //flockRule2();   //keep a bumper distance from others
    
//    displayLinks();
    
    //updateAutoArrange();
//    shrink();
//    picker();    
//    particles.run();    
    displayNode();
    
    normalizeCoords(this);
  }
	
  
  void normalizeCoords(Node n){
		//void normalizeCoords(Vector3D v){
		
		  /*switch(autoArrangeStyle) {
		        case AUTO_ARRANGE_HORIZON:
		          constrainNodeToCircle(n, circleHorizon); 
		          //constrainNodeToCircle(n, mouseX-n.bdifx, mouseY-n.bdify, circleHorizon); 
		          break;
		
		        case AUTO_ARRANGE_CIRCLE:
		          constrainNodeToCircle(n, circleRing); 
		          //constrainNodeToCircle(n, mouseX-n.bdifx, mouseY-n.bdify, circleRing); 
		  }  */
		  int width = CarnivoreILanPApplet.papplet.width;
		  int height = CarnivoreILanPApplet.papplet.height;
			  
		  float margin = 0;
		  if(n.position.x < margin)         { n.position.x = margin; }
		  if(n.position.y < margin)         { n.position.y = margin; }
		  if(n.position.x > width-margin)   { n.position.x = width-margin; }
		  if(n.position.y > height-margin)  { n.position.y = height-margin; }
  }
  
  void displayNode() {
	PApplet papplet = CarnivoreILanPApplet.papplet;
    
//    papplet.colorMode(PApplet.RGB, 255);
//    int rotationCounter = 0;
   
    papplet.pushMatrix();
    papplet.translate(position.x, position.y);

    if(wasManuallyMoved) {
    	CarnivoreILanPApplet.papplet.linkfactory.recalculateLinksForNode(this);
//    	particles.
    	wasManuallyMoved = false;
    }
    
//    papplet.image(icon, position.x - icon.width/2, position.y - icon.height/2); 
    drawBaseSquare();
    papplet.image(icon, - icon.width/2, - icon.height/2); 

//  if(isWAN) {
    displayNameWAN();  
//  }  
//  else {
//    displayNameLAN();  
//  }


//    //localhost halo
//    if(isLocalhost) {
//      float alphatotal = 200; 
//      int gradientsteps = 8;
//      float ringwidth = LOCALHOST_HALO_WIDTH/gradientsteps;
//      papplet.strokeWeight(ringwidth);
//      papplet.noFill();
//      for(int j = 1; j < gradientsteps; j++) {
//    	  papplet.stroke(255, 255, 255, alphatotal);
//    	  papplet.ellipse(0, 0, diameter + ringwidth*j*2, diameter + ringwidth*j*2);      
//    	  alphatotal -= alphatotal/gradientsteps;
//      }
//    }
    
    //main circle
    //portstats.displayCircleAsPieChart(diameter);

//    //large halo
//    if(isWAN) {
//    	papplet.noStroke();
//    	//fill(255,255,255, diameter_halo/3 + 50); //this also fades the alpha when it's in die cycle
//    	papplet.fill(0,0,0, diameter_halo/3 + 0); //this also fades the alpha when it's in die cycle
//    	papplet.ellipse(0, 0, diameter_halo,diameter_halo);  
//    }
    
    //main body of node
    /*strokeWeight(HALO_STROKEWEIGHT_DEFAULT);
    stroke(255,255,255,200); // Circle rim
    color c = portstats.lastKnownColor();
    fill(red(c), green(c), blue(c), diameter/3);*/
    
    
    
//    papplet.rotate(heading());
    //if(!isTCP()) {
      //ellipse(0, 0, diameter, diameter);  
    //} else { 
      //float r = diameter/2;
      //triangle(-r*4/5, r*3/5, 0, -r, r*4/5, r*3/5);
    //}
    
//    papplet.strokeWeight(HALO_STROKEWEIGHT_DEFAULT);
//    //if(isPicked) { 
//    papplet.strokeWeight(HALO_STROKEWEIGHT_DEFAULT + 1); 
//      portstats.displayCircleAsPieChart(diameter);
    /*} else {   
      stroke(255,255,255,200); // Circle rim
      fill(portstats.lastKnownColor());
      ellipse(0, 0, diameter, diameter);
    }*/
    
    //SERVICE LABLE
    /*String service = lastKnownService();
    if(service == null) {service = "Data";}
    if(scrollTimer < 0) {
      scrollLabelService.scroll(service);    
      scrollTimer = SCROLL_TIME;
    } else {
      scrollTimer--;
    }
    scrollLabelService.display();*/
    //noStroke(); fill(BLACK); text(service, -textWidth(service)/2 +1, 5 +1);    
    //noStroke(); fill(WHITE); text(service, -textWidth(service)/2, 5);    
    
    //LAN, WAN, LOCAL LABEL
    //if(isLocalhost) { noStroke(); fill(WHITE); text("H", -textWidth("H")/2,-10); }    
    //if(isLAN) { noStroke(); fill(WHITE); text("L", -textWidth("L")/2,-10); }    
    //if(isWAN) { noStroke(); fill(WHITE); text("W", -textWidth("W")/2,-10); }    
        
      papplet.popMatrix();
      papplet.strokeWeight(1);    
  }

	public void drawBaseSquare() {
		PApplet papplet = CarnivoreILanPApplet.papplet;
		int color_line = papplet.color(255, 255, 255);
		
		float sideLength = 54;
		float aX = +0;
		float aY = -9;
		
//		int color_fill = papplet.color(18, 18, 19, 190);
		int color_fill = papplet.color(18, 18, 19);
		if(isLocalhost) { //override for home
			color_line = papplet.color(255, 0, 0);
			sideLength = 58;
			aY = -12;
		}
		
		
		float bX = aX +(PApplet.cos(PApplet.radians(30)) * (sideLength));
		float bY = aY +(PApplet.sin(PApplet.radians(30)) * (sideLength));
		float cX = bX +(PApplet.cos(PApplet.radians(150)) * (sideLength));
		float cY = bY +(PApplet.sin(PApplet.radians(150)) * (sideLength));
		float dX = cX +(PApplet.cos(PApplet.radians(-150)) * (sideLength));
		float dY = cY +(PApplet.sin(PApplet.radians(-150)) * (sideLength));

		papplet.stroke(color_line);
		papplet.fill(color_fill);
		papplet.strokeWeight((isPicked) ? 2 : 1);
		papplet.quad(aX, aY, bX, bY, cX, cY, dX, dY);
	}
	
  void displayNameWAN() {
//	PApplet papplet = CarnivoreILanPApplet.papplet;
//	papplet.textFont(CarnivoreILanPApplet.font12);
    String service = lastKnownService();
    if(service == null) {
      service = "Server";
    } else {
      service = service + " Server";
    }
    
    
    if(Util.isProbablyAnIPaddress(host_short)) {
    	displayNodeLabel(service);
    } else {
    	displayNodeLabel(service, host_short);    	
    }

//    //papplet.fill(0,0,0,diameter*6);
//    papplet.fill(255);
//    papplet.pushMatrix();
////    papplet.translate(position.x, position.y);
////    papplet.rotate(heading());
////    papplet.translate(0, -diameter/2 - 5);
////    papplet.translate(0, -40);
////    papplet.rotate(-PApplet.TWO_PI/4);
//    papplet.textAlign(PConstants.CENTER);
//    papplet.text(service,0f,-Label.LINE_HEIGHT * .25f);
//    papplet.text(host_short,0f,+Label.LINE_HEIGHT * .75f);
//    papplet.popMatrix();  
  }

  public void displayNodeLabel(String a) {
		PApplet papplet = CarnivoreILanPApplet.papplet;
		papplet.textFont(CarnivoreILanPApplet.font10); 
		papplet.textAlign(PConstants.CENTER);
	    float w = papplet.textWidth(a);
	    float h = 15;
	    int spacer = 4;
	    
	    papplet.pushMatrix();
	    papplet.translate(0, 50);
	    
	    papplet.noStroke();
	    papplet.fill(0, 0, 0, 200);
	    papplet.rect( - w/2 - spacer,  -h*.7f, w + spacer*2, h);
	  
	    papplet.textAlign(PConstants.LEFT);
	    papplet.fill(255);
	    papplet.text(a, - w/2, 0);

	    papplet.popMatrix();  
	}
  
  public void displayNodeLabel(String a, String b) {
		PApplet papplet = CarnivoreILanPApplet.papplet;
		papplet.textFont(CarnivoreILanPApplet.font10); 
		papplet.textAlign(PConstants.CENTER);
	    float w = papplet.textWidth(a + " " + b);
	    float h = 15;
	    int spacer = 4;
	    
	    papplet.pushMatrix();
	    papplet.translate(0, 50);
	    
	    papplet.noStroke();
	    papplet.fill(0, 0, 0, 200);
	    papplet.rect( - w/2 - spacer,  -h*.7f, w + spacer*2, h);
	  
	    papplet.textAlign(PConstants.LEFT);
	    papplet.fill(255);
	    papplet.text(a, - w/2, 0);

	    papplet.textAlign(PConstants.RIGHT);

	    papplet.fill(papplet.color(0, 177, 255)); //blue
	    papplet.text(b, + w/2, 0);
	    papplet.popMatrix();  
	}
  
  
//  void displayNameLAN() {
//	PApplet papplet = CarnivoreILanPApplet.papplet;
//    float radiansPerLetterDivisor = 70;//.09;
//    float radiansPerLetter = .09f;
//    //String domain = host_short;
//    
//    papplet.fill(255);
//    papplet.pushMatrix();
//    papplet.translate(position.x, position.y);
////    papplet.rotate(heading());
//    papplet.translate(0, NODE_START_DIAMETER_LAN);
////    papplet.rotate(-(radiansPerLetter * host_short.length()/2));
//    char k;
//    
//    float r;
//    if(isWAN) {
//      r = NODE_START_DIAMETER_WAN + Label.LINE_HEIGHT*2.2f;
//    } else {
//      r = NODE_START_DIAMETER_LAN + Label.LINE_HEIGHT*2.2f;
//    }
//    
//    //for(int i = host_short.length()-1; i >= 0; i--){
//    for(int i = 0; i < host_short.length(); i++){
//      try{
//        k = host_short.charAt(i);
//        papplet.rotate(papplet.textWidth(k)/radiansPerLetterDivisor);
//        papplet.translate(0,-r);
//        papplet.text(k,0,0);
//        papplet.translate(0,r);
//      } catch(StringIndexOutOfBoundsException e) {
//    	papplet.println(e);
//      }
//    }
//    papplet.popMatrix();     
//  }

  //newPacket overloaded three times
  void newPacket(int senderPort, int receiverPort, int intTransportProtocol, Node destination) {
    this.portstats.newPacket(senderPort, receiverPort, intTransportProtocol);
    destination.portstats.newPacket(senderPort, receiverPort, intTransportProtocol);
//    ps.addParticle(this, destination, services.port2color(senderPort, receiverPort), intTransportProtocol);    
//    particles.addParticle(/*this, destination, */waypoints, 255, intTransportProtocol);    
    this.goOnline();
    destination.goOnline();    
  }

//  void newPacket(int senderPort, int receiverPort, Node destination) {
//    newPacket(senderPort, receiverPort, PROTO_TCP, destination);
//  }

  void newPacket(CarnivorePacket packet, Node destination) {
    newPacket(packet.senderPort, packet.receiverPort, packet.intTransportProtocol, destination);
  }
  
  void shrink() {
    if(!isLocalhost) {
    //if(nodesActive > NODE_MAX_PERFORMANCE) { //this only shrinks once there are more than n nodes on the screen 
      if(diameter > NODE_MIN_DIAMETER) { 
        
        //normal shrinkage
        if(diameter > NODE_START_DIE_CYCLE_DIAMETER) {
          diameter = diameter * NODE_SHRINK_SPEED_SLOW; 
        
        //start die cycle shrinkage
        } else {
          diameter = diameter * NODE_SHRINK_SPEED_FAST; 
          diameter_halo = diameter_halo * NODE_SHRINK_SPEED_FAST2; 
          
        }
      } else {
    	  CarnivoreILanPApplet.papplet.deleteNode(this);
        //goOffline();
      }
    }
  }

//  // Method to update location
//  void wiggle() {  
//    //wiggle
//    if(isOnline && !isPicked && isWAN){
//      
//      switch (ip.screenQuadrant) {
//        case 0:  position.y += (float)generator.nextGaussian() * WIGGLE_FACTOR_NODE; break;
//        case 1:  position.y += (float)generator.nextGaussian() * WIGGLE_FACTOR_NODE; break;
//        case 2:  position.x += (float)generator.nextGaussian() * WIGGLE_FACTOR_NODE; break;
//        default: position.x += (float)generator.nextGaussian() * WIGGLE_FACTOR_NODE; break;
//      }
//    
//      /*if(position.x > 1) {
//        position.x += (float)generator.nextGaussian() * WIGGLE_FACTOR_NODE;
//      } else {
//        position.y += (float)generator.nextGaussian() * WIGGLE_FACTOR_NODE;  
//      }*/
//    }
//    //position.add(velocity);
//  }
  
  public boolean checkIsPicked() {
	  
    float disX = position.x - CarnivoreILanPApplet.papplet.mouseX;
    float disY = position.y - CarnivoreILanPApplet.papplet.mouseY;
    
    if(PApplet.sqrt(PApplet.sq(disX) + PApplet.sq(disY)) < diameter/2 ) {
      //if(!atLeastOneNodeIsPicked()) {
        //moveToTopOfNodeStack(this);
        isPicked = true;  
      //}
    } else {
      isPicked = false;
    }
    return isPicked;
  }

  /////////////////////////////////////////////////////////////////////////////
  // helper methods
  /*Vector3D scaleGridPositionToHORIZON() {
    Vector3D v = new Vector3D(cachedPositionGRID);
    v.setY(v.y * .6);
    return v;    
  }*/
  
  float radius() {
    return diameter/2;
  }
  
  void touch() {
    //if(!isOnline) {goOnline();}    
    datestampLastTouched = CarnivoreILanPApplet.papplet.millis();
  }
  
  String age() {
    //println("[Node] age()");  
    return TimeUtilities.millis2string(CarnivoreILanPApplet.papplet.millis() - datestampCreated);
    //return millis2string(122 * ONE_MINUTE);
  }
    
  String lastTouched() {
    //println("[Node] lastTouched()");  
    int idletime = CarnivoreILanPApplet.papplet.millis() - datestampLastTouched;
    if(idletime > ONE_MINUTE) {
      return TimeUtilities.millis2string(idletime);
    }
    return null; //don't return it every friggin second.. jeesh 
  }

  String lastKnownService() {
    return portstats.lastKnownService();
  }

//  private void setIcon() {
//	    this.icon = loadImage(ICON_LAPTOP_BLACK); // Load the images into the program    
//  }
  
  /*
  void setIcon() {
    int r = int((position.x + position.y) % 4); //assign an icon that will be the same for the same x/y poss
    String icon_file = "computer_color.gif";
    
    if(isLocalhost) {
      icon_file = "home.gif";
    } else if(r == 0) { 
      if(isOnline) { icon_file = "computer_color.gif"; } 
      else         { icon_file = "computer_bw.gif"; } 
    } else if(r == 1) { 
      if(isOnline) { icon_file = "modem_color.gif"; } 
      else         { icon_file = "modem_bw.gif"; } 
    } else if(r == 2) { 
      if(isOnline) { icon_file = "server1_color.gif"; } 
      else         { icon_file = "server1_bw.gif"; } 
    } else { 
      if(isOnline) { icon_file = "server2_color.gif"; } 
      else         { icon_file = "server2_bw.gif"; } 
    }
    this.icon = loadImage(icon_file); // Load the images into the program    
  }*/
  
  void goOnline(){
    isAlive = true;
    isOnline = true;
    initDiameter();
    //setIcon();
    touch();
  }

  void goOffline(){
    if(isOnline) {
      //println("[Node] going offline: "+this);
      isOnline = false;
      //setIcon();
    }
  }

  Vector3D target() {
    return new Vector3D(moveTo.x - position.x, moveTo.y - this.position.y);
  }
  
  /*boolean isAutoArranging() {
    if(target().magnitude() > 1) {return true;}
    return false;
  }*/
  
  /*boolean shouldAutoArrange() {
    
    //if at target already, we're done
    if((autoArrange > 0) && (target().magnitude() < 1)) {
      autoArrange = 0;
      return false;
    }    

    //if not at target yet...      
    switch(autoArrangeStyle) {
        case AUTO_ARRANGE_OFF: 
        if(autoArrange == 2) { return true; }
        break;

        case AUTO_ARRANGE_CIRCLE:
        return true;

        case AUTO_ARRANGE_HORIZON: 
        if(isLAN) { return true; }
    }    
    
    if(autoArrange > 0) { return true; }

    return false;
  }*/
  
  void updateAutoArrange() {
    /*
    //at target
    if((autoArrangeStyle == AUTO_ARRANGE_GRID) && (target().magnitude() < 1)) {
      cachedPositionGRID.setXYZ(position);

    //still moving to target
    } else {
      Vector3D target = target();
      target.mult(AUTOARRANGE_SPEED);
      position.add(target);
    } */ 
  }
  
  void displayLabel() {
    if(isPicked) {
      Label label = new Label(this);
      label.display();
    }
  }

  void setIsLANorWAN(){
    if(CarnivoreILanPApplet.papplet.localhost.isLocal(ip.toString(),host)) { return; } //always exclude localhost from this method
    
    //LAN
    if(Util.ipIsLAN(ip.toString())) {
      //println("\tsetIsLANorWAN("+ip+") = LAN");
      isLocalhost = false;
      isLAN = true;  
      isWAN = false;  
    
    //WAN
    } else {
      //println("\tsetIsLANorWAN("+ip+") = WAN");
      isLocalhost = false;
      isLAN = false;  
      isWAN = true;        
    }   
  }
  
  void setIsLocalhost(){
    //LOCALHOST -- note: this application distinguishes localhost from LAN throughout
    if(CarnivoreILanPApplet.papplet.localhost.isLocal(ip.toString(), host)) { 
      isLocalhost = true;
      isLAN = false;  
      isWAN = false;  
      CarnivoreILanPApplet.papplet.nodelocalhost = this;
      //println("[Node] setIsLocalhost -- found local host "+ip+", resetting LAN/WAN for all...");
      CarnivoreILanPApplet.papplet.resetAllIsLANorWAN(); //reset all nodes once we find localhost for the first time. this is necessary for  
                            //situations where the localhost has a static IP. we get a netmask pattern from the 
                            //localhost static IP and use it as one of the LAN/WAN sorting patterns       
    }
  }

  public String toString() {
    return "(Node) " + ip + " position=["+(int)(position.x)+","+(int)(position.y)+"], diameter="+ diameter + 
           " isAlive="+isAlive+", isOnline="+isOnline+", isPicked="+isPicked+", isLocked="+isLocked+
           ", portstats={"+portstats+"}";
  }

public boolean isLocalhost() {
	return isLocalhost;
}

public boolean isLAN() {
	return isLAN;
}

public boolean isWAN() {
	return isWAN;
}
}
