package org.rsg.carnivore.ilan;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import org.rsg.carnivore.CarnivoreListener;
import org.rsg.carnivore.CarnivorePacket;
import org.rsg.carnivore.Constants;
import org.rsg.carnivore.Core;
import org.rsg.carnivore.ilan.ILanPreferences;
import org.rsg.carnivore.ilan.link.Link;
import org.rsg.carnivore.ilan.link.LinkFactory;
import org.rsg.carnivore.ilan.zones.Zone;
import org.rsg.carnivore.ilan.zones.Zone1;
import org.rsg.carnivore.ilan.zones.Zone3;
import org.rsg.carnivore.ilan.zones.Zone4;
import org.rsg.carnivore.ilan.zones.ZoneFactory;
import org.rsg.carnivore.net.DevBPF;
import org.rsg.carnivore.Preferences;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.chrome.Chrome;
import org.rsg.lib.chrome.ChromeDispatcher;
import org.rsg.lib.chrome.ChromeListener;
import processing.core.*;

public class CarnivoreILanPApplet extends PApplet implements ChromeListener, CarnivoreListener {
	private static final long serialVersionUID = 1L;
//	public static CarnivoreConsole carnivoreconsole;
	public static int THUMBNAIL_HEIGHT = 40; 
	public static int THUMBNAIL_WIDTH = 30; 
	final static int PAGE_MARGIN_LEFT = 15;
	final static int FRAME_RATE_STARTING = 20;
	public static float THUMBNAIL_ASPECT_RATIO = (float) THUMBNAIL_HEIGHT/THUMBNAIL_WIDTH; 
	public Core core;
	public boolean isMacAndPromiscuousModeFailed = false; 	
//	public CarnivoreILanPApplet parent;
	public static final int FINGERPRINT_THRESHOLD = 6;
	
	public static CarnivoreILanPApplet papplet = null;
	
	public static PFont font9;
	public static PFont font10;
	public static PFont font12;
	public static PFont font15;
	public static PFont font32;
	
	public Node nodelocalhost = null;
	public Localhost localhost;
	public Services services;
	public ILanPreferences ilanpreferences = new ILanPreferences();
//	public HashMap<String, Node> nodes = new HashMap<String, Node>();
	public ArrayList<Node> nodes = new ArrayList<Node>();
	public LinkFactory linkfactory = new LinkFactory();
	
	PImage terminalInFinder;
	PImage terminalSudoCommand;

	int hud_timer_limit = 1000;
	int hud_counter = hud_timer_limit; 
	boolean shouldDrawHUD2   = true;
	boolean shouldDrawLabels = true;
	int hudx, hudy = 0;
	int lineSpaceVertical = 14; 

	int nodesActive = 0;
	
	private ArrayList<ImageWithThumbnail> images = new ArrayList<ImageWithThumbnail>();
	
	public ZoneFactory zonefactory = new ZoneFactory();

	//MAIN
	//static boolean FULL_SCREEN = false; // add destroy hook
	static final String[] APP_OPTIONS = { 
//		"--present",
//		"--hide-stop",
		"--location=0,0", //416
		/*"--bgcolor=" + Util.toHex(Constants.WHITE), */
		/*"--present-stop-color=#000000",*/
		"org.rsg.carnivore.ilan.CarnivoreILanPApplet" 
	};

	public static void main(String[] args) {
		PApplet.main(APP_OPTIONS);		
	}
		
	public CarnivoreILanPApplet() {
		super();
		checkBPFforMac();
		
//		carnivoreconsole = new CarnivoreConsole(this);
		papplet = this; 
		
		localhost = new Localhost();
	
		//init services -- load services files
//		String fileColors[]            = loadStrings(Services.FILENAME_COLORS);
		String fileEtcServicesBase[]   = loadStrings(Services.FILENAME_ETC_SERVICES_BASE);
		String fileEtcServicesCustom[] = loadStrings(Services.FILENAME_ETC_SERVICES_CUSTOM);
//		String filePorts2colors[]      = loadStrings(Services.FILENAME_PORTS2COLORS);
		services = new Services(/*fileColors, */fileEtcServicesBase, fileEtcServicesCustom/*, filePorts2colors*/);
		  
		//START SNIFFER
//		checkBPFforMac(); //will quit if Mac machines are not in promiscuous 
		if(!isMacAndPromiscuousModeFailed) {
			core = new Core(this);
			core.addCarnivoreListener(this);
			core.start();
			Preferences.instance().put(Constants.MAXIMUM_VOLUME, Constants.VOLUME_MAX);	
		}

		  //LOAD PREFERENCES DATA
		  String filePreferencesNodes[]  = loadStrings(ILanPreferences.FILENAME_PREFS_NODES);  
		  if(filePreferencesNodes != null) {
		    print("[Main] loading "+filePreferencesNodes.length+" entries from '" + ILanPreferences.FILENAME_PREFS_NODES +"'... ");
		    ilanpreferences.loadNodePositions(filePreferencesNodes);
		    println("OK");  
		  }
		  Util.resetFutureSecond();
		  Util.resetFutureMinute();

		  terminalInFinder = loadImage("images/terminalInFinder.png");
		  terminalSudoCommand = loadImage("images/terminalSudoCommand.png");


		  font9 = loadFont("fonts/HelveticaNeue-Bold-9.vlw");
		  font10 = loadFont("fonts/HelveticaNeue-Bold-10.vlw");
		  font15 = loadFont("fonts/HelveticaNeue-Bold-15.vlw");

			
		  font12 = loadFont("fonts/ArialRoundedMTBold-12.vlw");  
		  font32 = loadFont("fonts/AGaramondPro-Regular-32.vlw");  
	}

	public static final int WIDTH = 1000;
	public static final int HEIGHT = 650;
	
	public void setup(){
		smooth();
		ChromeDispatcher.add(this);
		//Chrome.APP_SIZE = new Dimension(w,h); //optional override for default size
		Chrome.shouldWarnAboutLowFrameRate = true; //optional red border on low framerates
		size(WIDTH, HEIGHT);
		stroke(155,0,0);
	}

	public void draw() {
		drawBackground();
		drawZoneLabels();
		//Chrome.instance().draw(this);
		
		if(isMacAndPromiscuousModeFailed) {
		    drawError();
		} else {
			picker();
		    drawMap();
		}
	}

	public void drawBackground() {
		int color_background = color(42, 42, 43);//#3E3E40;
		int color_x = color(18, 18, 19);//#121213;
		background(color_background);
	
		int lineweight = (int) (((width + height)/2) * .42);
		stroke(color_x);
		strokeWeight(lineweight);
	
		float offsetX = width/2;
		float offsetY = height/2;
		float distance = (width/2) / cos(radians(-30)); 
		float startX = offsetX +(cos(radians(150)) * (distance));
		float startY = offsetY +(sin(radians(150)) * (distance));
		float endX = offsetX +(cos(radians(-30)) * (distance));
		float endY = offsetY +(sin(radians(-30)) * (distance));
		line(startX, startY, endX, endY);
	
		startX = offsetX +(cos(radians(-150)) * (distance));
		startY = offsetY +(sin(radians(-150)) * (distance)); 
		endX = offsetX +(cos(radians(30)) * (distance));
		endY = offsetY +(sin(radians(30)) * (distance));
		line(startX, startY, endX, endY);
		
		
		zonefactory.display();
	}
	
	public void drawZoneLabels() {
		drawZoneLabel(ZoneFactory.zone1);
		drawZoneLabel(ZoneFactory.zone2);
		drawZoneLabel(ZoneFactory.zone3);
		drawZoneLabel(ZoneFactory.zone4);
	}

	private static final int LINELENGTH = 100;
	private void drawZoneLabel(Zone zone) {
		int color = color(Zone.LABEL_COLOR_R,Zone.LABEL_COLOR_G,Zone.LABEL_COLOR_B);
		textFont(font15);  
		
		
		textAlign((zone instanceof Zone1 || zone instanceof Zone3) ? LEFT : RIGHT);
		
		fill(color);
		text(zone.getLabel().toUpperCase(), zone.getLabelPosition().x, zone.getLabelPosition().y);		
		
		strokeWeight(1);
		stroke(color);
		line(zone.getLabelPosition().x, 
			 zone.getLabelPosition().y + 7,
			 zone.getLabelPosition().x + LINELENGTH 
			 	* ((zone instanceof Zone1 || zone instanceof Zone3) ? 1 : -1), 
			 zone.getLabelPosition().y + 7);
		
		
		if(!(zone instanceof Zone4)) {
			textFont(font9);  
			text(zone.portsToString().toUpperCase(), zone.getLabelPosition().x, zone.getLabelPosition().y + 21);	
		}
	}

	
	public void drawError(){
		  textFont(font32);  

		  noStroke();
		  colorMode(RGB, 255);
		  fill(255);

		  rect(0,0,width,height);

		  
		  int x = width/2 - 200; 
		  int y = height/2 - 225;
		  int lineheight = 32; 

		  
		  fill(0, 102, 153);
		  text("Please initialize packet sniffing.", x, y);
		  y += lineheight*2;
		  
		  text("Step 1--Open the Terminal.", x, y);
		  y += 20;
		  
		  image(terminalInFinder, x, y);
		  y += lineheight*4.5;

		  text("Step 2--Type this command:", x, y);
		  y += lineheight;

		  fill(0);
		  text("sudo chmod 777 /dev/bpf*", x, y);
		  y += 10;

		  image(terminalSudoCommand, x-33, y);
		  y += lineheight*6;

		  fill(0, 102, 153);
		  text("Step 3--Quit and relaunch.", x, y);
		  y += lineheight;
		  
		}
	
//	public void drawMap(){
////		line(mouseX,mouseY,10, 10);
//		drawImages();
//	}	

	public void drawMap() {
		  
		  //STUFF TO DO EACH SECOND
		  if(Util.aSecondHasPassed()) {
		    //println("aSecondHasPassed");
		    setNodesActive();
		    //setStrokeWeight();
		  }

		  //STUFF TO DO EACH MINUTE
		  if(Util.aMinuteHasPassed()) {
		    //println("aMinuteHasPassed");
		  }
		  
		//  //MOVE SCREEN TO LEFT
		//  if((app_state == APP_STATE_RUN) && (gui_offset_x > .1)) {
//		    shrinkGuiOffset();
		//
		//  //MOVE SCREEN TO RIGHT
		//  } else if((app_state == APP_STATE_PREFS) && (gui_offset_x < PREFS_PANE_WIDTH)) {
//		    growGuiOffset();
		//  }
		  
		  updateMouse();
		  updateKeyboard();
		  hud_counter++;
		  colorMode(RGB, 255);  
//		  if(!isOnline) { simulatePackets(); }

//		  links.shrink();

		  //move to offset for all drawing methods
		  pushMatrix();
//		  translate(gui_offset_x,0);
		  
//		  drawBackground();
//		  drawPrefsScreen();
		  linkfactory.shrink();
		  drawAllLinks();
		  drawNodes();
		  if(shouldDrawLabels) { drawLabels(); } //this doubles the times we loop through nodes, but puts labels in foreground
		                                         //put back in drawNodes to increase performance? 

		  drawImages();

		  //HUD
//		  displayHUD2(); //the persistent HUD
		  if(hud_counter < hud_timer_limit){
		    cursor();
		    displayHUD1(); //the disappearing HUD
		  } else {
		    noCursor(); 
		    //if(shouldDrawHUD2) { displayHUD2(); }
		  }  
		                
//		  drawColorKey();
		  
		  popMatrix();
		                  

		  //saveFrame("frames/line-####.tif"); 
		  
		  //println("nodes:"+nodes.size() + " links:"+links.size());
		}

	//HUD for persistent stuff
	void displayHUD1() {
	  papplet.textAlign(PConstants.LEFT);
	  textFont(font12);
	  String s;
	  //HUD
	  hudy = 0;
	  noStroke(); 
	  int rate = constrain(ceil(frameRate), 0, FRAME_RATE_STARTING);
	  s = "Frame rate: " + rate + "/"+ FRAME_RATE_STARTING + 
	      ", Nodes:" + nodesActive + "/" + nodesTotal() 
	      /*", Uptime: " + uptime() + */
	      
//	      ", Links:" + links.size()
	      ;
	  int x = (int) (width/2 - textWidth(s)/2);
	  int y = height - lineSpaceVertical*2;
	  int spacer = 5;
	  rectMode(CORNER);
	  if(rate < FRAME_RATE_STARTING) {fill(255,0,0,200);} 
	  else {fill(255,255,255,160);}
	  rect(x -(spacer), y +(spacer), textWidth(s) +spacer*2, -(lineSpaceVertical +spacer));
	  fill(0); text(s, x, y); 
	  //text("backX: " + backX, width*hfactor, hudy+=lineSpaceVertical); 
	  //text("backY: " + backY, width*hfactor, hudy+=lineSpaceVertical); 
	}
	
	int nodesTotal() {
		  return nodes.size();
		}

	//HUD for stuff that you want to auto hide 
	void displayHUD2() {
	  textFont(font12);
      papplet.textAlign(PConstants.LEFT);
	  colorMode(RGB, 100);
	  //color c = color(255, 255, 255, 50);//100 - hud_counter);
	  noStroke(); fill(0);           
	  hudy = 0;
	  hudx = PAGE_MARGIN_LEFT;

//	  text("gui_offset_x:"+gui_offset_x,                       hudx, hudy+=lineSpaceVertical); 	

	  text("CONTROLS",                       hudx, hudy+=lineSpaceVertical); 	
	  text("(Right Mouse) background color", hudx, hudy+=lineSpaceVertical); 	
	  text("(C)ircle layout",                  hudx, hudy+=lineSpaceVertical); 	
	  text("(H)orizon layout",               hudx, hudy+=lineSpaceVertical); 	
	  text("(K) color key",                    hudx, hudy+=lineSpaceVertical); 	
	  //text("(G)rid layout",                  PAGE_MARGIN_LEFT, hudy+=lineSpaceVertical); 	
	  //text("(D)elete inactive nodes",        PAGE_MARGIN_LEFT, hudy+=lineSpaceVertical); 	
	  colorMode(RGB, 255);
	}
	
	public void moveToFront(Node node) {
		nodes.remove(node);
		nodes.add(0, node);
	}

	public void clearPickedExcept(Node node) {
//		for(Node n : nodes) {
		for(int i = 0; i < nodes.size(); i++) {
			Node n = (Node) nodes.get(i);
			if(n != node)
				n.isPicked = false;
		}
	}
		
	public void picker() {
//		for(Node n : nodes) {
		for(int i = 0; i < nodes.size(); i++) {
			Node n = (Node) nodes.get(i);
			if(n.checkIsPicked()) {
				moveToFront(n);
				clearPickedExcept(n);
				return; 
			}
		}		
	}
	
	synchronized void drawNodes() {
//		for(Node n : nodes) {
		for(int i = nodes.size()-1; i >= 0; i--) {
			Node n = (Node) nodes.get(i);
//		
//		  Iterator<String> iterator = nodes.keySet().iterator(); 
//		  while (iterator.hasNext()) {  
//			  try {
//				  Node n = (Node) nodes.get(iterator.next());
				  if(!n.isAlive) {continue;}
				  n.display();
//			  } catch(ConcurrentModificationException e) {
//				  System.out.println("[Main] drawNodes ConcurrentModificationException"); 
//				  return; 
//			  }
		  }    
	}

	void drawAllLinks() {
		  //if(keyPressed || alwaysDrawLinks) {
		    //if ((key == 'l') || (key == 'L') || alwaysDrawLinks) { 
		      for(int i = 0; i< linkfactory.size(); i++) {
		        Link link = (Link) linkfactory.get(i);
		        link.display();
		      }
		    //}
		  //}  
		}
	
	synchronized void drawLabels() {
//		for(Node n : nodes) {
		for(int i = 0; i < nodes.size(); i++) {
			Node n = (Node) nodes.get(i);
			
//		  Iterator<String> iterator = nodes.keySet().iterator(); 
//		  while (iterator.hasNext()) {  
			  try {
//				  Node n = (Node) nodes.get(iterator.next());
				  if(!n.isAlive) {continue;}
				  n.displayLabel();
			  } catch(ConcurrentModificationException e) {
				  System.out.println("[Main] drawLabels ConcurrentModificationException"); 
				  return; 
			  }		    
		  }    
	}

	synchronized public void newImage(String filename) {
		String cachepath = org.rsg.carnivore.tcpReassembly.fileTypes.File.PATH_TO_CACHE;
		PImage image = loadImage(cachepath + filename);
		PImage thumb = thumbnailize(image);
//		thumb.g
//		thumb.resize(0, THUMBNAIL_MAX_HEIGHT);
		images.add(new ImageWithThumbnail(filename, image, thumb));
	}
	
	private PImage thumbnailize(PImage image) {
		if(null==image) return null;
		float thumb_aspect_ratio = (float) image.height/image.width;
		System.out.print(image.width + "x" + image.height + " (1:"+thumb_aspect_ratio+ ") ...");
		
		if(thumb_aspect_ratio < THUMBNAIL_ASPECT_RATIO) {
			System.out.println("too wide");			
			image.resize(0, THUMBNAIL_HEIGHT);
//			return image;
			return image.get(	image.width/2 - THUMBNAIL_WIDTH/2, 0, 
								THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
		} else {
			System.out.println("too tall");
			image.resize(THUMBNAIL_WIDTH, 0);			
//			return image;
			return image.get(	0, image.height/2 - THUMBNAIL_HEIGHT/2,
								THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
		}
	}
	
	//CALLBACK FROM CORE
	public void newCarnivorePacket(CarnivorePacket packet) {
//		if(packet.isTCP()){
////			System.out.println(Fingerprints.instance().getFingerprint(packet, FINGERPRINT_THRESHOLD));
//		} else {
//			//Log.debug("UDP length = " + packet.jpUDPPacket.getLength());
//		}		

//	    println("[Main] newCarnivorePacket (" + packet.strTransportProtocol + " packet) " + packet.senderSocket() + " > " + packet.receiverSocket() + 
//	            " ipIdentification:" + packet.ipIdentification + " tcpSequenceNumber:" + packet.getTcpSequenceNumber());
//	    println(bytesToCharString(packet.data));

	    //create sender and receiver if necessary
	    addNodeIfNew(packet.senderAddress.toString(), packet.portIndicatingService());
	    addNodeIfNew(packet.receiverAddress.toString(), packet.portIndicatingService());
	    
	    //nodes already exist
	    Node origin = getNodeFromIp(packet.senderAddress.toString());
	    Node destination = getNodeFromIp(packet.receiverAddress.toString());
	    
	    //KEEP TRACK OF LINKS
	    Link l = linkfactory.addIfNew(origin, destination);
	    
	    origin.newPacket(packet, destination);
	    l.newPacket(packet);
	}
	
	Node getNodeFromIp(String ip){
//		for(Node n : nodes) {
		for(int i = 0; i < nodes.size(); i++) {
			Node n = (Node) nodes.get(i);
			if(n.ip.toString().equals(ip))
				return n;
		}
		
//		  if(nodes.containsKey(ip)) {
//		    return (Node) nodes.get(ip);
//		  }
		  return null;
	}
	
//	  /**
//	   * Resize this image to a new width and height.
//	   * Use 0 for wide or high to make that dimension scale proportionally.
//	   */
//	  public void resize(PImage p, int wide, int high) {  // ignore
//	    // Make sure that the pixels[] array is valid
//	    p.loadPixels();
//
//	    if (wide <= 0 && high <= 0) {
//	      p.width = 0;  // Gimme a break, don't waste my time
//	      p.height = 0;
//	      p.pixels = new int[0];
//
//	    } else {
//	      if (wide == 0) {  // Use height to determine relative size
//	        float diff = (float) high / (float) p.height;
//	        wide = (int) (width * diff);
//	      } else if (high == 0) {  // Use the width to determine relative size
//	        float diff = (float) wide / (float) p.width;
//	        high = (int) (p.height * diff);
//	      }
//	      PImage temp = new PImage(wide, high, p.format);
//	      p.copy(p, 0, 0, p.width, p.height, 0, 0, wide, high);
//	      p.width = wide;
//	      p.height = high;
//	      p.pixels = temp.pixels;
//	    }
//	    // Mark the pixels array as altered
//	    p.updatePixels();
//	  }

	  
	synchronized public void drawImages() {
		int xinit = 20;
		int yinit = 30;
		int x = xinit;
		int y = yinit;
		int spacer = 5;

		for(ImageWithThumbnail image : images) {
			PImage thumb = image.getThumbnail();
			if(null==thumb) continue;

			if(x + thumb.width > width) {
				x = xinit;
				y += THUMBNAIL_HEIGHT + spacer;
			}

			if(y + thumb.height > height) {
				y = yinit;
			}

			image(thumb, x, y); 
			noFill();
			rect(x,y,thumb.width,thumb.height);
			x += thumb.width + spacer;
			
		}
	}

	public void rescaleWindow() {
		// TODO Auto-generated method stub
		
	}
	
	public void resetAllIsLANorWAN() {
	  //println("[Main] resetAllIsLANorWAN");
		for(Node n : nodes) {
//	  Iterator<String> iterator = nodes.keySet().iterator(); 
//	  while (iterator.hasNext()) {  
//	    Node n = (Node) nodes.get(iterator.next());
	    n.setIsLANorWAN();
	  }
	}
	
	//this should have everything needed to "delete" a node
	public void deleteNode(Node n) {
	  n.isAlive = false;    //suppress node by flagging it dead
	  removeLinksToNode(n); //remove any link objects that start or end at node
	  
	  nodes.remove(n);
	}

	//remove any link objects that start or end at node
	void removeLinksToNode(Node n) {
	  for(int i = 0; i < linkfactory.size(); i++) {
	    Link link = (Link) linkfactory.get(i);  
	    if((link.origin == n) || (link.destination == n)) {
	      linkfactory.remove(i);
	    }
	  }  
	}
	
	public void addNodeIfNew(String ip, int portIndicatingService) {
		  if(!nodes.contains(getNodeFromIp(ip))) {
		    nodes.add(new Node(ip, portIndicatingService));       
		    //autoArrange(); 
		  }  
	}

	private void checkBPFforMac() {
		if (!LibUtilities.isMac()) return;	//return if not a mac 
		
		DevBPF devbpf = new DevBPF();
		isMacAndPromiscuousModeFailed = !devbpf.isPromiscuous;
		
//		if(!devbpf.isPromiscuous) {
//			System.err.println(ErrorMessages.MAC_NOT_PROMISCUOUS);
//			System.exit(0);
//			return false; 
//		}
	}

	public void updateKeyboard() {
		  if(keyPressed) {
		    if (key == 'n') { printAllNodes(); }
		    if (key == 's') { ilanpreferences.saveNodePositions(); }
//
//		    if ((key == 'p') || (key == 'P')) { toggleAppState(); }
		    
		    //if (key == '1') { drag += .0005; }
		    //if (key == 'q') { drag -= .0005; }
		    //if (key == '2') { directionalNoise += .05; }
		    //if (key == 'w') { directionalNoise -= .05; }
		    //if (key == '3') { accelerationFactor += .001; }
		    //if (key == 'e') { accelerationFactor -= .001; }
		    //if (key == '4') { wiggleRoom += .01; }
		    //if (key == 'r') { wiggleRoom -= .01; }
		    //if (key == '5') { autoArrange(); alwaysAutoArrange = true; }
		    //if (key == 't') { autoArrange(); alwaysAutoArrange = false; }
		    //if (key == 'l') { printLAN(); }
		    //if (key == 'w') { printWAN(); }
		    //if(key == 'o') { nodes.put(millis()+"", new Node("1.2.3.4")); }
		    //if ((key == 'd') || (key == 'D')) { deleteOldNodes(); }
//		    if ((key == 'c') || (key == 'C')) { resetAndAutoArrange(AUTO_ARRANGE_CIRCLE); }
//		    if ((key == 'h') || (key == 'H')) { resetAndAutoArrange(AUTO_ARRANGE_HORIZON); }
		    //if ((key == 'g') || (key == 'G')) { resetAndAutoArrange(AUTO_ARRANGE_GRID); }
		    //if (key == 'p') { preferences.saveNodePositions(); }
		    //if (key == 'b') { giveSomeBreathingRoom(); }
		  }
		}
	
	public void mousePressed() {
//		println(mouseX+","+mouseY);
		for(Node n : nodes) {
//		  Iterator<String> iterator = nodes.keySet().iterator(); 
//		  while (iterator.hasNext()) {  
//		    Node n = (Node) nodes.get(iterator.next());
		    if(!n.isAlive) {continue;}
		    if(n.isPicked){
		      n.isLocked = true; 
		    } else {
		      n.isLocked = false;
		    }
		    n.bdifx = mouseX - n.position.x; 
		    n.bdify = mouseY - n.position.y;   
		  }  
		}

	public void mouseDragged() {
		for(Node n : nodes) {
//		  Iterator<String> iterator = nodes.keySet().iterator(); 
//		  while (iterator.hasNext()) {  
//		    Node n = (Node) nodes.get(iterator.next());
		    if(!n.isAlive) {continue;}
		    if(n.isLocked) {
		      n.position.x = mouseX-n.bdifx; 
		      n.position.y = mouseY-n.bdify;       
		      //if(autoArrangeStyle == AUTO_ARRANGE_GRID) { n.moveTo.setXYZ(n.position); } //update moveTo for grid.. this helps set the cachedPositionGRID correctly
		      n.wasManuallyMoved = true;
		      
//		      switch(autoArrangeStyle) {
//		        case AUTO_ARRANGE_HORIZON:
//		          constrainNodeToCircle(n, circleHorizon); 
//		          //constrainNodeToCircle(n, mouseX-n.bdifx, mouseY-n.bdify, circleHorizon); 
//		          break;
//
//		        case AUTO_ARRANGE_CIRCLE:
//		          constrainNodeToCircle(n, circleRing); 
//		          //constrainNodeToCircle(n, mouseX-n.bdifx, mouseY-n.bdify, circleRing); 
//		      }
		  
		      n.normalizeCoords(n);
//		      normalizeCoords(n);
		      n.moveTo.setXYZ(n.position);      
		    }    
		  }    
	}

	public void mouseReleased() {
		for(Node n : nodes) {
//		  Iterator<String> iterator = nodes.keySet().iterator(); 
//		  while (iterator.hasNext()) {  
//		    Node n = (Node) nodes.get(iterator.next());
		    if(!n.isAlive) {continue;}
		    n.isLocked = false;    
		  }    
	}

	public void updateMouse() {
//		  int gui_trigger_margin = 30;
//		  //SHOW/HIDE PREFS WINDOW
//		  switch(app_state) {
//		  case(APP_STATE_RUN):
//		    if(mouseX < gui_trigger_margin) {app_state = APP_STATE_PREFS;}
//		    break;  
//		  case(APP_STATE_PREFS): 
//		    if(mouseX > PREFS_PANE_WIDTH) {app_state = APP_STATE_RUN;}
//		    break;  
//		  }  
		  
//		  //background color
//		  if (mousePressed && (mouseButton == RIGHT)) { 
//		    backX = mouseX;
//		    backY = mouseY;    
//		  }
	}

	void printAllNodes(){
		  println("\nNODE REPORT");
		  int counter = 0;
			for(Node n : nodes) {
//		  Iterator<String> iterator = nodes.keySet().iterator(); 
//		  while (iterator.hasNext()) {  
		    counter++;
//		    Node n = (Node) nodes.get(iterator.next());
		    println(counter+ ". "+n);
		  }    
		}

		void printLAN(){
		  println("\nLAN REPORT");
		  int counter = 0;
			for(Node n : nodes) {
//		  Iterator<String> iterator = nodes.keySet().iterator(); 
//		  while (iterator.hasNext()) {  
//		    Node n = (Node) nodes.get(iterator.next());
		    if(n.isLAN) {
		      counter++;
		      println(counter+ ". "+n);
		    }
		  }    
		}

		void printWAN(){
		  println("\nWAN REPORT");
		  int counter = 0;
			for(Node n : nodes) {
//		  Iterator<String> iterator = nodes.keySet().iterator(); 
//		  while (iterator.hasNext()) {  
//		    Node n = (Node) nodes.get(iterator.next());
		    if(n.isWAN) {
		      counter++;
		      println(counter+ ". "+n);
		    }
		  }    
		}
		
		public void mouseMoved() {
			  //println("mouseX:"+mouseX + " mouseY:"+mouseY);
			  if(((pmouseX - mouseX) > 0) || ((pmouseY - mouseY) > 0)) {
			    hud_counter = 0;
			  }
		}		

		public void setNodesActive() {
			  int count = 0;
				for(Node n : nodes) {
//			  Iterator<String> iterator = nodes.keySet().iterator(); 
//			  while (iterator.hasNext()) {  
//			    Node n = (Node) nodes.get(iterator.next());
			    if(!n.isAlive) {continue;}
			    count++;
			  } 
			  nodesActive = count;
			  //println("[Main] setNodesActive: "+nodesActive);
		}

}
