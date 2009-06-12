package org.rsg.carnivore.ilan.link;

import java.util.ArrayList;
import org.rsg.carnivore.ilan.CarnivoreILanPApplet;
import org.rsg.carnivore.ilan.Vector3D;
import processing.core.PApplet;
import processing.core.PVector;

public // Particle
class Particle {
//	float drag = 1.015f;
//	float directionalNoise = PApplet.TWO_PI * .20f;
//	float accelerationFactor = .007f;
  Vector3D position;
//  Vector3D velocity;
//  Node destination;
//  ArrayList<WaypointSegment> waypoints;
  float timer = 0;
//  int color1;
//  int proto;
  float radian;
  float radianstep = .1f;
  public static final float rate = 3f;
  public static float diameter = 4.5f;
  public boolean isDead = false; 

  /////////////////////////////////////////////////////////////////////////////
  // constructor
//  Particle(Node origin, Node destination, int c, int proto) {
  Particle(/*int c, int proto*/) {
//    this.waypoints = waypoints;
//    if(waypoints.size() > 0) {
//    	WaypointSegment ws = waypoints.get(0);
//        this.position =  new Vector3D(ws.getStartPoint().x, ws.getStartPoint().y);
//    } else {
        this.position =  new Vector3D(0,0);
//    }
//    this.timer = 500.0f;
//    this.color1 = c;
//    this.proto = proto;
    
//    if(random(0,2) > 1) {radianstep = -radianstep;} //this puts half of them turning the other way

    //set velocity
//    velocity = new Vector3D(target().heading2D() + random(-directionalNoise, directionalNoise));
//    velocity = new Vector3D(target().heading2D());

//    //move position to outter rim of node
//    Vector3D vnorm = new Vector3D(target().heading2D());
//    vnorm.normalize(destination.diameter/2);
//    position.add(vnorm);    
  }

  /////////////////////////////////////////////////////////////////////////////
  // main methods
  void run(ArrayList<PVector> pointList) {
    update(pointList);
    display();
  }
//  
//  Vector3D velocity() {
//	  return new Vector3D(target().heading2D());
//  }

  // Method to update location
  void update(ArrayList<PVector> pointList) {  
    timer++;
//    velocity.add(acceleration());
//    velocity.mult(drag);
//    if(isAtWaypointTarget()) 
//    	popWaypoints();
        
//    ArrayList<PVector> pointList = ld.getPoints(one,two);
    
    PVector v = getPositionOnLineFromTimeAndVelocity(pointList);
    position.setXY(v.x, v.y);
  }
  
  public PVector getPositionOnLineFromTimeAndVelocity(ArrayList<PVector> pointList) {
	  
	  float distance = timer*rate; 

	  int cumulativeLength = 0;
	  PVector v0 = null;
	  for(int i = 0; i < pointList.size()-1; i++) {
	    v0 = (PVector) pointList.get(i+1);
	    PVector v1 = (PVector) pointList.get(i);
	    PVector v3 = PVector.sub(v0, v1);
	    cumulativeLength += v3.mag();
	    if(cumulativeLength > distance) {

//	      System.out.print("aiming at point #" + (i+1) + " " + (int)(v1.x) + "," + (int)(v1.y));
//	      System.out.println(" -- cumulativeLength:"+(int)(cumulativeLength) + " > distance:"+(int)(distance));

	      
	      v3.limit(distance - cumulativeLength);
	      v3.add(v0);
	      return v3;


//	      v.limit(time*rate);
//	      return v;    
	    }
	  }
	  isDead = true; 
	  return v0;    
	}
  
//  void popWaypoints() {
//	  if(waypoints.size() > 0)
//		  waypoints.remove(0);	  
//  }

  // Method to display
  void display() {
	  displayCircle();
  }
  
  void displayCircle() {
		PApplet papplet = CarnivoreILanPApplet.papplet;
	    papplet.pushMatrix();
	    papplet.translate(position.x, position.y);
	    papplet.fill(255); 
	    papplet.noStroke();
	    papplet.ellipse(0,0, diameter, diameter);
	    papplet.popMatrix();
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // helper methods
//  Vector3D target() {
//	    if(waypoints.size() > 0) {
//	    	WaypointSegment ws = waypoints.get(0);
//	        return new Vector3D(ws.getEndPoint().x - this.position.x, ws.getEndPoint().y - this.position.y);
//	    } else {
//	        return position;
//	    }	  
//  }

//  Vector3D velocityNormal() {
//    Vector3D v = new Vector3D(velocity());
//    v.normalize();
//    return v;
//  }
//
//  Vector3D targetNormal() {
//    Vector3D v = new Vector3D(target());
//    v.normalize();
//    return v;
//  }
  
//  Vector3D acceleration() {
//    Vector3D v = velocity.sub(velocityNormal(), targetNormal());
//    v.reflect();
//    v.mult(accelerationFactor * target().magnitude());
//    return v;
//  }
  
//  void wiggle() {
//    float fHeading = velocity.heading2D() + (float)generator.nextGaussian()*wiggleRoom;
//    Vector3D vHeading = new Vector3D(fHeading);
//    vHeading.normalize(velocity.magnitude());
//    velocity.setXYZ(vHeading);
//  }

//  boolean isAtWaypointTarget() {
//	  return target().magnitude() < MIN_DISTANCE_TO_TARGET;
//  }
  
  // Is the particle still useful?
//  public static final float MIN_DISTANCE_TO_TARGET = 1f;
//  boolean isDead() {
//	  if(waypoints.size() == 0) return true; 
//	  if(waypoints.size() == 1 && isAtWaypointTarget()) return true; 
////	  if (target().magnitude() < MIN_DISTANCE_TO_TARGET) { return true; }
//	  if (timer <= 0.0) { return true; }  
//	  return false;
//  }
}