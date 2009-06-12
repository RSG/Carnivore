package org.rsg.carnivore.ilan;

public class Service implements Comparable<Service> {
  public int port;
  String name;
  String description;
  String colorhex; 
  public Service(int port, String name, String colorhex, String description) {
    this.port = port;
    this.name = name;
    this.colorhex = colorhex;
    this.description = description;
  }
  
  public int compareTo(Service s) {
//    Service s = (Service) o;
    return (port < s.port) ? 0 : 1;
  }
  
  public String toString() {
    return "(Service) " + name + " ("+description+"), port " + port + ", color " + colorhex;
  }
}