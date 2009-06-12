package org.rsg.carnivore.ilan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.rsg.lib.comparators.ComparatorIntegerStrings;

import processing.core.PApplet;

// Services
public class Services extends HashMap<String,Service> {
	private static final long serialVersionUID = 1L;
	final static String DEFAULT_COLOR = "666666";
	int splitter;
//	HashMap<String,String> colors = new HashMap<String,String>();
//	public final static String FILENAME_COLORS              = "colors.txt";
	public final static String FILENAME_ETC_SERVICES_BASE   = "etcservices_base.txt";
	public final static String FILENAME_ETC_SERVICES_CUSTOM = "etcservices_custom.txt";
//	public final static String FILENAME_PORTS2COLORS        = "ports2colors.txt";
 
  /////////////////////////////////////////////////////////////////////////////
  // constructor
  Services(//String fileColors[], 
           String fileEtcServicesBase[], 
           String fileEtcServicesCustom[]/*, 
           String filePorts2colors[]*/
                                   ) {
//    loadColors(fileColors);
    loadEtcServices(fileEtcServicesBase);
    loadEtcServices(fileEtcServicesCustom);
//    loadPorts2colors(filePorts2colors);
    //println("6000:"+get("6000"));
  }
  
  public Services() {  
  }
  
//  //other
//  public static Services createZone4() {
//	  Services services = new Services();
//	  putIntoServices(services, 80, null, null, null);
//	  putIntoServices(services, 443, null, null, null);
//      return services;	  
//  }
  
  
  /////////////////////////////////////////////////////////////////////////////
  // main methods
  
//  void loadColors(String lines[]){
//    for (int i=0; i < lines.length; i++) {
//      if(Util.isGoodLine(lines[i])) {
//        String temp = lines[i];
//        temp = Util.trimComment(temp);
//        splitter = temp.indexOf("=");
//        String colorname = temp.substring(0, splitter).trim();
//        String hexvalue = temp.substring(splitter+1).trim();
//        colors.put(colorname, hexvalue);
//      }
//    } 
//  }
//
//  void loadPorts2colors(String lines[]){
//    for (int i=0; i < lines.length; i++) {
//      if(Util.isGoodLine(lines[i])) {
//        //println(lines[i]);      
//        String temp = lines[i];
//        temp = Util.trimComment(temp);
//        
//        splitter = temp.indexOf("=");
//        String port = temp.substring(0, splitter).trim();
//        String colorname = temp.substring(splitter+1).trim();
//        String colorhex = (String) colors.get(colorname);
//        if(colorhex != null) {
//          //println("\n"+port+":"+colors.get(colorname));
//          //println("colorhex:"+colorhex);
//          //color c = unhex(colorhex);
//          //println("hex(c):"+hex(c));
//          if(this.containsKey(port+"")) {
//            Service s = (Service) this.get(port+"");
//            s.colorhex = colorhex;
//          }
//        }
//      }
//    } 
//  }

  void loadEtcServices(String lines[]){
    for (int i=0; i < lines.length; i++) {
      //if((i > 2030) && (i < 2040)) { println("[Services] loadEtcServices line("+i+"): "+lines[i]); }      
      if(Util.isGoodLine(lines[i])) {
        //if((i > 2030) && (i < 2040)) { println("\tline "+i+" is good"); }
        String temp = lines[i];
        String name = null;
        int port = 0;
        String description = null;

        //strip off name
        splitter = temp.indexOf(" ");
        if(splitter > 0) { 
          name = temp.substring(0, splitter).trim();
          temp = temp.substring(splitter+1).trim();

          //strip off port number
          splitter = temp.indexOf("/");
          if(splitter > 0) { 
            port = Integer.valueOf(temp.substring(0, splitter));
          }

          //get description
          splitter = temp.indexOf(" ");
          description = temp.substring(splitter+1).trim();
          if(description.length() < 1) {description = name;} //don't leave empty descriptions
        }
        Service s = new Service(port, name, DEFAULT_COLOR, description);
        //if((i > 2030) && (i < 2040)) { println("\tloadEtcServices adding service: "+s); }
        this.put(port+"", s); //overwrites so don't worry about duplicate entries in the
                              //file. can also load multiple files (ex: 'etcservices_custom.txt')
      }
    } 
  }

  /////////////////////////////////////////////////////////////////////////////
  // helper methods
  String getNameFromPort(int port) {
    if(containsKey(port+"")) { return ((Service) get(port+"")).name; }
    return null;
  }

  //overloaded.. this one effectively does an OR on the two ports
  String port2description(int a, int b) {
    if(containsKey(a+"")) { 
      Service s = (Service) get(a+"");
      //println("port2description("+a+","+b+") containsKey a: "+ a+ ". returning description \""+s.description+"\"");
      return s.description; 
    }
    if(containsKey(b+"")) { 
      Service s = (Service) get(b+"");
      //println("port2description("+a+","+b+") containsKey b: "+ b+ ". returning description \""+s.description+"\"");
      return s.description; 
    }
    //println("port2color("+a+","+b+") not in hash.. returning null");
    return null;
  }
    
  //overloaded.. this one effectively does an OR on the two ports
//  color port2color(int a, int b) {
	int port2color(int a, int b) {
    if(containsKey(a+"")) { 
      Service s = (Service) get(a+"");
      //println("port2color("+a+","+b+") containsKey a: "+ a+ ". returning color "+s.c);
      return hexString2color(s.colorhex); 
    }
    if(containsKey(b+"")) { 
      Service s = (Service) get(b+"");
      //println("port2color("+a+","+b+") containsKey b: "+ b+ ". returning color "+s.c);
      return hexString2color(s.colorhex); 
    }
    //println("port2color("+a+","+b+") not in hash.. returning default");
    return hexString2color(DEFAULT_COLOR);
  }
    
//	color port2color(int a) {
	int port2color(int a) {
    if(containsKey(a+"")) { 
      Service s = (Service) get(a+"");
      //println("[Services] port2color("+a+") containsKey a: "+ a+ ". returning color "+ s.colorhex);
      return hexString2color(s.colorhex); 
    }
    //println("[Services] port2color("+a+") not in hash.. returning default " + defaultColor);
    return hexString2color(DEFAULT_COLOR);
  }
  
//color hexString2color(String s) {
  int hexString2color(String s) {
    s = "FF" + s;
    int i = PApplet.unhex(s);
    return i;
  }
  
  ArrayList<Service> getColoredServices() {
    ArrayList<Service> list = new ArrayList<Service>();
    int counter = 1;
    Iterator<String> iterator = this.keySet().iterator(); 
    while (iterator.hasNext()) {  
      Service s = (Service) this.get(iterator.next());     
      if(s.colorhex.equals(DEFAULT_COLOR)) {continue;}
      list.add(s);
      //println(counter + ". " + s);
      counter++;
    }    
    Collections.sort(list);
    return list;
  }
  

  public String portsToString(String separator) { 
		String s = "";
	    Iterator<String> iterator = this.keySet().iterator(); 
		ArrayList<String> numbers = new ArrayList<String>();

	    while (iterator.hasNext()) {  
	    	Service service = (Service) this.get(iterator.next());     
	    	if(null==service) continue;
	    	numbers.add(service.port+"");
	    }
	    
	    Collections.sort(numbers, new ComparatorIntegerStrings());	  

		
		for(int i = 0; i < numbers.size(); i++) {
//			int pagenumber = Integer.valueOf(pages.get(i));
			boolean isSequence = false;
			int start_i = i;
			String start = numbers.get(i); 
			
			while(nextIsInSequence(numbers, i)) {
				isSequence = true;
				i++;
			}

			s += (start_i==0) ? "" : separator;
			if(isSequence) {
				s += start + "-" + numbers.get(i);				
			} else {
				s += numbers.get(i);
			}
		}
	    
	    return s;
	}
  
	public boolean nextIsInSequence(ArrayList<String> pages, int marker) {
		try {
			int numberThis = Integer.valueOf(pages.get(marker));
			int numberPeek = Integer.valueOf(pages.get(marker+1));
			return numberThis+1 == numberPeek;
		} catch (IndexOutOfBoundsException e) {
			return false;
		} catch (NumberFormatException e) {
			return false;
		}		
	}      
}


