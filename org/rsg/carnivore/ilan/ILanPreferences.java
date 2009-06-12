package org.rsg.carnivore.ilan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import processing.core.PApplet;

// Preferences
public class ILanPreferences {
  final static String FILENAME_PREFS_NODES      = "preferences_nodepositions.txt";
  HashMap<String,Vector3D> nodePositions = new HashMap<String,Vector3D>();
  int max_capacity = 1000; //TODO stress test this? 

  /////////////////////////////////////////////////////////////////////////////
  // constructor
  public ILanPreferences() {
  }
 
  Vector3D setXYZ(Node n) {
    if(nodePositions.containsKey(n.ip.toString())) {
      Vector3D v = (Vector3D) nodePositions.get(n.ip.toString());
      //println("[Preferences] new node "+n.ip+" setting to cached position "+v);
      return v;
    }
    return null;
  }
 
  void loadNodePositions(String lines[]){
    String ip, temp;
    int x,y,splitter;
    for (int i=0; i < lines.length; i++) {
      if(Util.isGoodLine(lines[i])) {
        temp = Util.trimComment(lines[i]);
        splitter = temp.indexOf("=");
        ip = temp.substring(0, splitter).trim();
        temp = temp.substring(splitter+1).trim();
        splitter = temp.indexOf(",");
        x = Integer.valueOf(temp.substring(0, splitter).trim());
        y = Integer.valueOf(temp.substring(splitter+1).trim());        
        nodePositions.put(ip, new Vector3D(x,y));
        
        CarnivoreILanPApplet.papplet.addNodeIfNew(ip, -1); //this means the nodes hashmap saves state 
        //println("\t[Preferences] loadNodePositions "+ip+" "+x+","+y);
      }
    } 
  }
  
  void saveNodePositions() {
    boolean shouldSaveWAN = false;
    ArrayList<String> arraylist = new ArrayList<String>();
	for(Node n : CarnivoreILanPApplet.papplet.nodes) {
//    Iterator<String> iterator = CarnivoreILanPApplet.papplet.nodes.keySet().iterator(); 
//    while (iterator.hasNext()) {  
//      Node n = (Node) CarnivoreILanPApplet.papplet.nodes.get(iterator.next()); 
      if(n.isWAN) {      
        if(shouldSaveWAN) {
          arraylist.add(arraylist.size(),   n.ip + "=" + (int)(n.position.x) + "," + (int)(n.position.y)); //put WANs at end, that way if they get truncated it's okay
        }
      } else {
        arraylist.add(0,                  n.ip + "=" + (int)(n.position.x) + "," + (int)(n.position.y)); //put LANs at start
      }
    }  

    //TRIM TO CAPACITY    
    while(arraylist.size() > max_capacity) {
       arraylist.remove(arraylist.size()-1); 
    }

    System.out.println("[ILanPreferences] saveNodePositions: saving "+arraylist.size()+" of "+CarnivoreILanPApplet.papplet.nodes.size()+" nodes to file '"+FILENAME_PREFS_NODES+"'... ");

    
    String[] nodePositions = new String[arraylist.size()];
    for(int i=0; i<arraylist.size(); i++) {
      nodePositions[i] = (String) arraylist.get(i);
    }

    CarnivoreILanPApplet.papplet.saveStrings(FILENAME_PREFS_NODES, nodePositions);
//    PApplet.println("OK");
  }
}
