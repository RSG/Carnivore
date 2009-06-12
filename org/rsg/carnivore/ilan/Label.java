package org.rsg.carnivore.ilan;

import java.util.ArrayList;

import org.rsg.lib.string.StringUtilities;

import processing.core.PApplet;
import processing.core.PConstants;

// Label
public class Label {
	final static int LABEL_WIDTH = 300;
	final static int LABEL_TEXTBOX_HSPACER = 4;
	final static int LABEL_TEXTBOX_VSPACER = 3;
	final static int LINE_HEIGHT = 15;
	final static String HOME = "HOME";

  Node parent;
  int tetherWidth  = 8; 
  int tetherHeight = 30; 
  int labelHoffset = -15;
  ArrayList<String> textarray = new ArrayList<String>();
    
  /////////////////////////////////////////////////////////////////////////////
  // constructor
  Label(Node parent) {
    this.parent = parent;
  }
  
  void display() {
	PApplet papplet = CarnivoreILanPApplet.papplet;
	papplet.textFont(CarnivoreILanPApplet.font12);

    rebuildTextArray(papplet);
    int lineNumber   = 0;
//    String s;
    
    papplet.pushMatrix();
    papplet.translate(parent.position.x, parent.position.y);
    
    //tether
    papplet.noStroke(); 
    papplet.fill(0);
    switch (quadrant()) {
      case 0: papplet.triangle(0, 0, tetherWidth, tetherHeight, tetherWidth*2, tetherHeight); break; //SE
      case 1: papplet.triangle(0, 0, -tetherWidth, tetherHeight, -tetherWidth*2, tetherHeight); break; //SW
      case 2: papplet.triangle(0, 0, -tetherWidth, -tetherHeight, -tetherWidth*2, -tetherHeight); break; //NW
      case 3: papplet.triangle(0, 0, tetherWidth, -tetherHeight, tetherWidth*2, -tetherHeight); break; //NE
    }

    //move to box
    switch (quadrant()) {
      case 0: papplet.translate((labelHoffset), (tetherHeight)); break; //SE
      case 1: papplet.translate(-(labelHoffset+LABEL_WIDTH), (tetherHeight)); break; //SW
      case 2: papplet.translate(-(labelHoffset+LABEL_WIDTH), -(tetherHeight+labelHeight())); break; //NW
      case 3: papplet.translate((labelHoffset), -(tetherHeight+labelHeight())); break; //NE
    }    
    
    //box
    papplet.rectMode(PApplet.CORNER);
    papplet.stroke(0); 
    papplet.fill(255); 
    papplet.rect(0, 0, LABEL_WIDTH, labelHeight());
  
    //print the text array
    papplet.noStroke();
    for(int i =0; i<textarray.size(); i++) {
      lineNumber++; 
      papplet.fill(0);
      
      //for the header
      if(i == 0) {
    	  papplet.fill(0); 
    	  papplet.rect(0, 0, LABEL_WIDTH, LINE_HEIGHT); 
    	  papplet.fill(255); 
      } 
      
      papplet.textAlign(PConstants.LEFT);
      papplet.text(((String) textarray.get(i)), LABEL_TEXTBOX_HSPACER, (lineNumber * LINE_HEIGHT) - LABEL_TEXTBOX_VSPACER);
    }    
    
    papplet.popMatrix();
  }  
  
  int labelHeight() {
    return (LINE_HEIGHT * textarray.size()) + LABEL_TEXTBOX_VSPACER;
  }
  
  void rebuildTextArray(PApplet papplet){
    textarray.clear();
    String[] sarray;
    
    if(parent.isLocalhost) {
      textarray.add(HOME);
    } else {
      textarray.add(StringUtilities.truncate(papplet, parent.host, LABEL_WIDTH));
    }
    
    textarray.add(StringUtilities.truncate(papplet, "IP Address: "+parent.ip, LABEL_WIDTH) );
    
    sarray = getLastServiceAsString();    
    textarray.add(StringUtilities.truncate(papplet, sarray[0], sarray[1], LABEL_WIDTH));
    
    textarray.add(getTimeAsString());
    
    textarray.add(StringUtilities.truncate(papplet, parent.portstats.toString(), LABEL_WIDTH));

    //print port stats 
    ArrayList displaystats = parent.portstats.getDisplayStats();
    for(int j = 0; j < displaystats.size(); j++) {
      PortStat portstat = (PortStat) displaystats.get(j);
      sarray = getServiceStringFromPort(portstat.port);    
      textarray.add(StringUtilities.truncate(papplet, portstat.packetsAsPercentageOfAll() + " for " + sarray[0], sarray[1], LABEL_WIDTH));      
      //s = portstat.packets + " packets ("+ portstat.packetsAsPercentageOfAll() + ") on port " + portstat.port;
      //text(s, LABEL_TEXTBOX_HSPACER, (lineNumber * LINE_HEIGHT) - LABEL_TEXTBOX_VSPACER);            
    }
  }

  String getTimeAsString() {
    String time = "Discovered " + parent.age() + " ago";
    String last = parent.lastTouched();
    if(last != null) {
      time = time + " (idle " + last + ")";
    }
    return time;
  }

  String[] getLastServiceAsString() {
    String returnme[] = new String[2]; 
    String service = parent.lastKnownService();
    int port = parent.portstats.lastKnownLocalPort;
    if(service == null) {  
      returnme[0] = "Last Service: ";
      returnme[1] = "port " + port;
    } else {
      returnme[0] = "Last Service: " + service;
      returnme[1] = " (port " + port + ")";
    }
    return returnme;
  }

  String[] getServiceStringFromPort(int port) {
    String returnme[] = new String[2]; 
    String service =  CarnivoreILanPApplet.papplet.services.port2description(port, -1); //parent.lastKnownService();
    //int port = parent.portstats.lastKnownLocalPort;
    if(service == null) {  
      returnme[0] = "";
      returnme[1] = "port " + port;
    } else {
      returnme[0] = service;
      returnme[1] = " (port " + port + ")";
    }
    return returnme;
  }
  
  //sets quadrant (0=SE, 1=SW, 2=NW, 3=NE) where label should hover, vis-a-vis node position
  int quadrant() {
    if(isAtTop()) {
      if(isAtRight()) {
        return 1;
      }
      return 0;
    }
    if(isAtRight()) {
      return 2;
    }
    return 3;
  }
  
  boolean isAtTop(){
    if((parent.position.y - tetherHeight - labelHeight()) < 0) {
      return true;
    }
    return false;
  }
  
  boolean isAtRight(){
    if((parent.position.x + labelHoffset + LABEL_WIDTH) > CarnivoreILanPApplet.papplet.width) {
      return true;
    }
    return false;
  }
}

