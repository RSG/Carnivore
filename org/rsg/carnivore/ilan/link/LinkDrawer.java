package org.rsg.carnivore.ilan.link;

import java.util.ArrayList;

import org.rsg.carnivore.ilan.CarnivoreILanPApplet;
import org.rsg.carnivore.ilan.Node;

import processing.core.PApplet;
import processing.core.PVector;

public class LinkDrawer{
	  // regions: 
	  //  4\1/2
	  //   /3\
	  //                            					         1   2   3   4	
	  private static final float[] stemAngles   = new float[] {150,150,330,330};
	  private static final float[] middleAngles = new float[] { 30,210,210, 30};
	  private PVector origin;
	  private PVector destination;
	  private PVector nodeEnd;
	  private PVector breakEnd;
	  private ArrayList<PVector> points;

	  public LinkDrawer(Node origin, Node destination){
		  PVector p1 = new PVector(origin.position.x, origin.position.y);
		  PVector p2 = new PVector(destination.position.x, destination.position.y);
		  recalculate(p1, p2);		  
	  }
	  
	  public LinkDrawer(PVector origin, PVector destination){
		  recalculate(origin, destination);
	  }

	  public void recalculate(Node origin, Node destination){
		  PVector p1 = new PVector(origin.position.x, origin.position.y);
		  PVector p2 = new PVector(destination.position.x, destination.position.y);
		  recalculate(p1, p2);		  
	  }
	  
	  public void recalculate(PVector origin, PVector destination){
		  this.origin = origin;
		  this.destination = destination;
		  this.points = getPoints(origin, destination);
	  }

	  public void draw() {
			PApplet papplet = CarnivoreILanPApplet.papplet;
		    papplet.stroke(255);
			papplet.strokeWeight(1);
		    papplet.line(destination.x, destination.y, nodeEnd.x, nodeEnd.y);
		    papplet.line(nodeEnd.x, nodeEnd.y, breakEnd.x, breakEnd.y);
		    papplet.line(breakEnd.x, breakEnd.y, origin.x, origin.y);
	  }

	  public ArrayList<PVector> getPoints() {
			return points;
	  }
	  	  
	  private PVector getIntersection(PVector a, PVector b){
	    PVector aEnd = getLineEnd(a,10,30);
	    PVector bEnd = getLineEnd(b,10,-30);
	    return lineIntersection(a.x,a.y,aEnd.x,aEnd.y,b.x,b.y,bEnd.x,bEnd.y);
	  }

	  private PVector getLineEnd(PVector p, float d, float a){
	    return new PVector(p.x + (PApplet.cos(PApplet.radians(a)) * d), p.y + PApplet.sin(PApplet.radians(a)) * d);
	  }
	  
	  private ArrayList<PVector> getPoints(PVector origin, PVector destination){
	    ArrayList<PVector> points = new ArrayList<PVector>();
	    int region = getRegion(origin, destination);
	    PVector intersect = getIntersection(origin, destination);
	    float nodeDist = PVector.dist(destination, intersect);
	    float breakDist = PVector.dist(origin, intersect);
	    nodeEnd = getLineEnd(destination, nodeDist/2, stemAngles[region-1]);
	    breakEnd = getLineEnd(nodeEnd, breakDist, middleAngles[region-1]);
	    points.add(origin);
	    points.add(breakEnd);
	    points.add(nodeEnd);
	    points.add(destination);
	    return points;
	  }

	  private int getRegion(PVector o, PVector d){
	    //calculate new points to make lines from incoming vectors
	    PVector da = getLineEnd(d,10,-30);
	    PVector db = getLineEnd(d,10,30);
	    PVector oa = getLineEnd(o,10,30);
	    PVector ob = getLineEnd(o,10,-30);
	    //get 2 intersect points for lines
	    PVector ac = lineIntersection(d.x,d.y,da.x,da.y,o.x,o.y,oa.x,oa.y);
	    PVector bd = lineIntersection(d.x,d.y,db.x,db.y,o.x,o.y,ob.x,ob.y);
	    //get the differences
	    if(ac == null){
	    	System.out.println("[LinkDrawer] getRegion() AC NULL");
	    	return -1;
	    }
	    if(bd == null){
	    	System.out.println("[LinkDrawer] getRegion() BD NULL");
	    	return -1;
	    }
	    if(((o.x < ac.x) && (o.y < ac.y)) && ((o.x < bd.x) && (o.y > bd.y))){
	      return 2;
	    }
	    if(((o.x > ac.x) && (o.y > ac.y)) && ((o.x < bd.x) && (o.y > bd.y))){
	      return 1;
	    }
	    if(((o.x < ac.x) && (o.y < ac.y)) && ((o.x > bd.x) && (o.y < bd.y))){
	      return 3;
	    }
	    if(((o.x > ac.x) && (o.y > ac.y)) && ((o.x > bd.x) && (o.y < bd.y))){
	      return 4;
	    }
	    return -1;
	  }

	  private PVector lineIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4)
	  {
	    float bx = x2 - x1;
	    float by = y2 - y1;
	    float dx = x4 - x3;
	    float dy = y4 - y3;
	    float b_dot_d_perp = bx*dy - by*dx;
	    if(b_dot_d_perp == 0) return null;
	    float cx = x3-x1;
	    float cy = y3-y1;
	    float t = (cx*dy - cy*dx) / b_dot_d_perp;
	    return new PVector(x1+t*bx, y1+t*by);
	  }
	}