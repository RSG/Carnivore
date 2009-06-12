package org.rsg.carnivore.ilan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.rsg.carnivore.CarnivorePacket;
import processing.core.PApplet;

// PortStats
public class PortStats extends HashMap<String,PortStat> {
	private static final long serialVersionUID = 1L;
//Node parent;
  int lastKnownTransportProtocol = 0;
  int lastKnownLocalPort = 0; 
  int lastKnownRemotePort = 0; 
  int maxToDisplay = 4;
  float mass = Util.LINK_STROKEWEIGHT_MIN;

  /////////////////////////////////////////////////////////////////////////////
  // constructor
  /*PortStats(Node parent) {
    this.parent = parent;
  }*/
  
  public PortStats() {}

  /////////////////////////////////////////////////////////////////////////////
  // main methods
  public void newPacket(CarnivorePacket packet) {
    newPacket(packet.senderPort, packet.receiverPort, packet.intTransportProtocol);
  }
  
  void newPacket(int senderPort, int receiverPort, int intTransportProtocol) {
    if(mass < Util.LINK_STROKEWEIGHT_MAX) { mass++; }
    lastKnownTransportProtocol = intTransportProtocol;
    lastKnownLocalPort = senderPort;
    lastKnownRemotePort = receiverPort;
    int lower = PApplet.min(lastKnownLocalPort, lastKnownRemotePort);//always save the lower port this is fudging it, but it's a safe assumption 
                                                             //that the lower port number is the service we care about
    //print("[PortStats] "+parent.ip+ " newPacket(int "+portA+", int "+portB+") lower:"+lower+" ... ");
    if(containsKey(lower+"")) { 
      //println("containsKey(lower:"+lower+")");
      ((PortStat) get(lower+"")).packets++;
    } else {
      //println("is new");
      PortStat portstat = new PortStat(this, lower, 0); 
      portstat.packets++;
      put(lower+"", portstat);
    }
  }
  
//  void displayCircleAsPieChart(float diameter) {
//    colorMode(RGB, 255);
//    int rotationCounter = 0;
//   
//    pushMatrix();
//    //translate(parent.position.x, parent.position.y);
//    
//    //draw halo for localhost
//    /*if(parent.isLocalhost) {
//      strokeWeight(LOCALHOST_HALO_WIDTH);
//      stroke(WHITE);
//      noFill();
//      ellipse(0, 0, parent.diameter + LOCALHOST_HALO_WIDTH/2, parent.diameter + LOCALHOST_HALO_WIDTH/2);      
//    }
//    strokeWeight(getStrokeWeight());
//    
//    if(parent.isPicked) { strokeWeight(getStrokeWeightPicked()); }
//    stroke(0,0,0,200); // Circle rim*/
//
//    strokeWeight(HALO_STROKEWEIGHT_DEFAULT); 
//    
//    Iterator iterator = values().iterator(); 
//    while (iterator.hasNext() && (rotationCounter < TWO_PI)) {  
//      PortStat portstat = (PortStat)iterator.next();          
//      if(portstat.packets > 0) {
//        //noFill(); 
//        //if(parent.isOnline || parent.isPicked) { fill(services.port2color(portstat.port)); }    
//        
//        color c = services.port2color(portstat.port);
//        if(diameter < NODE_START_DIE_CYCLE_DIAMETER) {
//          stroke(0,0,0,diameter*6);
//          fill(red(c), green(c), blue(c), diameter*6);
//        } else {
//          stroke(0,0,0,200);
//          fill(c);
//        }
//    
//        //fill(services.port2color(portstat.port));
//        float r = countToScaledRadians(portstat.packets);
//        if(r < PIE_CHART_MIN_SLIVER_SIZE) { r = PIE_CHART_MIN_SLIVER_SIZE;}
//        //println(n+" r("+s.port+") = "+r); 
//        //arc(0, 0, NODE_START_DIAMETER, NODE_START_DIAMETER, 0, r);
//        arc(0, 0, diameter, diameter, 0, r);
//        rotate(r);
//        rotationCounter += r;
//      }
//    }
//    popMatrix();
//    
//    //strokeWeight(1);
//  }
  
  /////////////////////////////////////////////////////////////////////////////
  // helper methods
  String lastKnownService() {
    return CarnivoreILanPApplet.papplet.services.port2description(lastKnownLocalPort, lastKnownRemotePort);
  }

  public void shrink() {
    if(mass > Util.LINK_STROKEWEIGHT_MIN) { 
      mass *= Util.LINK_SHRINK_SPEED; 
    }
  }
  
//  color lastKnownColor() {
  int lastKnownColor() {
    return CarnivoreILanPApplet.papplet.services.port2color(lastKnownLocalPort, lastKnownRemotePort);
  }
  
  float countToScaledRadians(int c) {
    float count = (float) c;
    float total = (float) totalPackets();
    float returnme = (count/total) * PApplet.TWO_PI;
    //println("countToScaledRadians "+count+"/"+total+" = "+returnme+"/"+TWO_PI);
    return PApplet.TWO_PI * (count/totalPackets());
  }
  
  int totalPackets() {
    int count = 0;
    Iterator<PortStat> iterator = values().iterator(); 
    while (iterator.hasNext()) {  
      PortStat p = (PortStat)iterator.next();          
      count += p.packets;
    }
    return count;
  }
  
  public String toString() {
    String plural = "";
    if(size() > 1) {plural = "s";}
    return size() + " port"+plural+" used, "+totalPackets()+" total packets";
  }
  
  int numberToDisplay() {
    return PApplet.min(this.size(), maxToDisplay);
  }
  
  ArrayList<PortStat> getDisplayStats() {
    ArrayList<PortStat> list = new ArrayList<PortStat>();
    int counter = 1;
    Iterator<String> iterator = this.keySet().iterator(); 
    while (iterator.hasNext()) {  
      PortStat p = (PortStat) this.get(iterator.next());     
      list.add(p);
      //println(counter + ". " + s);
      counter++;
    }    
    Collections.sort(list);
    while(list.size() > numberToDisplay()) { list.remove(list.size()-1); }
    return list;
  }  
}

/////////////////////////////////////////////////////////////////////////////
// PortStat 
class PortStat implements Comparable<PortStat> {
  int port, packets;
  PortStats parent;
  
  PortStat(PortStats parent, int port, int packets) {
    this.parent = parent;
    this.port = port;
    this.packets = packets;
  }

  public String packetsAsPercentageOfAll() {
    int i = (int)(Util.scaleToRangeWithBase(packets, 100, parent.totalPackets()));
    if(i < 1) {
      return "<1%";
    } else {
      return i + "%";
    }
  }

  public int compareTo(PortStat p) {
//    PortStat p = (PortStat) o;
    return (packets > p.packets) ? 0 : 1;
  }
    
  public String toString() {
    return "(PortStat) " + port + " "+port+": " + packets + " packets";
  }
}


