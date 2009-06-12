package org.rsg.carnivore.ilan;

import processing.core.PApplet;

class ScrollLabel {
  String s;
  Vector3D position; 
  float start = 200; 
  float speed = 3;
  float START_POSITION = -50;
  float relativePosition = START_POSITION;
  boolean isOnline = false;
  Node parent;
  
  ScrollLabel(Node n) {
    this.parent = n;
  }
  
  void goOnline() {
    isOnline = true;
    //println("[ScrollLabel] goOnline: "+ s +" "+parent.ip);    
  }

  void goOffline() {
    isOnline = false;
    //println("[ScrollLabel] goOffline: '"+ s +"' "+parent.ip);    
  }

  void scroll(String s) {
    initString(s);    
    relativePosition = START_POSITION;
    goOnline();
  }
  
  void initString(String s) {
    this.s = "" + s + "        " + s;
  }

  void display() {
    if(isOnline) {

      //UPDATE
      relativePosition += speed;
      if(relativePosition > (CarnivoreILanPApplet.papplet.textWidth(s) + PApplet.abs(START_POSITION))) {
        goOffline();
      }            
      
      //DISPLAY
      char k;
      float tracking = 10;
      float kerning = 0;
      int greyscale; 
      Vector3D charPosition, charOffset;

      for(int i = 0; i < s.length(); i++){
        k = s.charAt(i);
        charPosition = new Vector3D(0);
        kerning += CarnivoreILanPApplet.papplet.textWidth(k);
        charPosition.normalize(kerning - relativePosition);
        //charPosition.add(parent.position);
        //charOffset = new Vector3D(parent.position.x - charPosition.x, parent.position.y - charPosition.y);
        //greyscale = int(charOffset.magnitude()*8 - 100);
        greyscale = (int)(charPosition.magnitude()*5 - 100);
        //greyscale = 0;
        dropShadow(k, greyscale, charPosition);
      }  
    }
  }  
  
  void dropShadow(char k, int c, Vector3D v) {
    //println("dropShadow: " + k + " " + c + " " + v + " parent.position:"+parent.position);
	PApplet papplet = CarnivoreILanPApplet.papplet;
	papplet.noStroke(); 
	papplet.fill(0,0,0, 255-c);       papplet.text(k,v.x, v.y);
	papplet.fill(255,255,255, 255-c); papplet.text(k,v.x+1, v.y-1);    
  }
}
