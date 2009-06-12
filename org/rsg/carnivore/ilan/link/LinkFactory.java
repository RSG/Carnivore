package org.rsg.carnivore.ilan.link;

import java.util.ArrayList;

import org.rsg.carnivore.ilan.Node;

// Links
public class LinkFactory extends ArrayList<Link> {
  private static final long serialVersionUID = 1L;

  /////////////////////////////////////////////////////////////////////////////
  // constructor
  public LinkFactory(){}
  
  boolean contains(Link a) {
    for(int i = 0; i < this.size(); i++) {
      Link b = (Link) get(i);
      if(a == b) { return true; }
    }
    return false;
  }

  public Link addIfNew(Node a, Node b) {
    for(int i = 0; i < this.size(); i++) {
      Link link = (Link) get(i);
      if((link.origin == a) && (link.destination == b)) { return link; }
      if((link.origin == b) && (link.destination == a)) { return link; }
    }
    Link link = new Link(a, b);
    this.add(link);
    return link;
  }  
  
  public void shrink() {
    for(int i = 0; i < this.size(); i++) {
      Link link = (Link) get(i);
      link.shrink();
    }    
  }
  
  public void recalculateLinksForNode(Node node) {
	  ArrayList<Link> links = getLinksForNode(node);
	  for(Link link : links) {
		  link.recalculate();
	  }
  }

  ArrayList<Link> getLinksForNode(Node a) {
    ArrayList<Link> list = new ArrayList<Link>();
    for(int i = 0; i < this.size(); i++) {
      Link link = (Link) get(i);
      if((link.origin == a) || (link.destination == a)) {
        list.add(link);
      }
    }
    return list;
  }  

  public void display() {
    for(int i = 0; i < this.size(); i++) {
      Link b = (Link) get(i);
      b.display();
    }
  }  
}


