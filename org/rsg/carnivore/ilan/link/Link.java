package org.rsg.carnivore.ilan.link;

import java.util.ArrayList;
import org.rsg.carnivore.CarnivorePacket;
import org.rsg.carnivore.ilan.Node;
import org.rsg.carnivore.ilan.PortStats;

public class Link {
	  public Node origin;
	  public Node destination;
	  public PortStats portstats = new PortStats();
	  public LinkDrawer linkdrawer; 
	  public ArrayList<Particle> particles = new ArrayList<Particle>();    // An arraylist for all the particles

	  public Link(Node origin, Node destination) {
	    this.origin = origin;
	    this.destination = destination;
	    this.linkdrawer = new LinkDrawer(origin, destination);
	    //println("[Link] new link: "+this);
	  }
	  
	  public void addParticle() {
		    particles.add(new Particle());
	  }

	  public void recalculate() {
		  linkdrawer.recalculate(origin, destination);
	  }

	  public void newPacket(CarnivorePacket packet) {
	    this.portstats.newPacket(packet);
	    addParticle();  
	  }

	  public void shrink() {
	    portstats.shrink();
	  }

	  public void display() {  
		  run();  
		  linkdrawer.draw();
	  }
	  
	  public void run() {
		    // Cycle through the ArrayList backwards b/c we are deleting
		    for (int i = particles.size()-1; i >= 0; i--) {
		      Particle p = (Particle) particles.get(i);
		      if (p.isDead) {
		        particles.remove(i);
		      }
		      p.run(this.linkdrawer.getPoints());
		    }
	  }

	  public String toString() {
	    return origin.ip +" -> "+destination.ip;  
	  }
}