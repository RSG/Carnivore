package org.rsg.carnivore.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JSlider;

import org.rsg.carnivore.Constants;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.Log;

public class AGJSliderUI extends javax.swing.plaf.basic.BasicSliderUI { 
	Float volume_current = new Float(0); 
	Float volume_maximum = new Float(0); 
	Float WIDTH = new Float(249); 
	Image knob;

    public AGJSliderUI(JSlider s) { 
    	super(s); 
    	//knob = CarnivorePE.gui.getToolkit().getImage(getClass().getResource("images/knob.gif")); //wow isn't image code elegant in java? :-(
    	knob = GUI.instance().getToolkit().getImage(Constants.IMAGES_KNOB); //wow isn't image code elegant in java? :-(
    } 
    
    public void paintThumb(Graphics g) { 
    	g.drawImage(knob,thumbRect.x+2,thumbRect.y+9,null);
    } 

    public void paintTrack(Graphics g) { 
		g.setColor(new Color(100,100,100)); 
		
		int maxx;
		if(LibUtilities.isWindows()) { //more weird swing bugs
			g.drawRect(contentRect.x+2,  contentRect.y+5, contentRect.width-3, contentRect.height-8); 
			maxx = contentRect.x+3;
		} else {
			g.drawRect(contentRect.x,  contentRect.y+5, contentRect.width, contentRect.height-8); 			
			maxx = contentRect.x+1;
		}
		int maxy = contentRect.y+5;
		int maxw;
		if(volume_current.intValue() < volume_maximum.intValue()){			//use current if it falls below max
			maxw = scale(volume_current).intValue();
		} else {															//otherwise cap at max
			maxw = scale(volume_maximum).intValue() - (thumbRect.width)/2;
		}
		int maxh = 1;
		int o_w;
		int greyscale;
		int o_greyscale;

		//DRAW BOX -- TOP GRADIENT
		greyscale = 190;
		o_greyscale = 220;
		for(int i = 0; i < 5 ; i++){
			maxy++;

			//OVERFLOW BOX
			if(volume_maximum.intValue() < volume_current.intValue()){			//only draw overflow if needed
				//o_w = scale(volume_current.intValue() - volume_maximum.intValue()).intValue();
				o_w = scale(volume_current.intValue()).intValue();
				//Log.debug("yellowbox["+curx+" "+cury+" "+curw+" "+curh+"]");
				g.setColor(new Color(o_greyscale,o_greyscale,o_greyscale)); 
				g.fillRect(maxx,maxy,o_w,maxh); 
				o_greyscale = (int) (o_greyscale * .97); //darken
			}			

			//NORMAL BOX
			g.setColor(new Color(greyscale,greyscale,greyscale)); 
			g.fillRect(maxx,maxy,maxw,maxh); 			
			greyscale = (int) (greyscale * .97); //darken
		}

		//DRAW BOX -- BOTTOM GRADIENT
		greyscale = 120;
		o_greyscale = 170;
		for(int i = 0; i < 6 ; i++){
			maxy++;

			//OVERFLOW BOX
			if(volume_maximum.intValue() < volume_current.intValue()){			//only draw overflow if needed
				//o_w = scale(volume_current.intValue() - volume_maximum.intValue()).intValue();
				o_w = scale(volume_current.intValue()).intValue();
				//Log.debug("yellowbox["+curx+" "+cury+" "+curw+" "+curh+"]");
				g.setColor(new Color(o_greyscale,o_greyscale,o_greyscale)); 
				g.fillRect(maxx,maxy,o_w,maxh); 
				o_greyscale = (int) (o_greyscale * 1.05); //darken
			}		

			//NORMAL BOX
			g.setColor(new Color(greyscale,greyscale,greyscale)); 
			g.fillRect(maxx,maxy,maxw,maxh); 			
			greyscale = (int) (greyscale * 1.1); //lighten
		}
		
		//DRAW SLIDER LABELS
		int y = 0;
        if(LibUtilities.isWindows()) { 	//an attempt to fix 
        	y = 14;										//weird swing inconsistencies 
        } else {
        	y = 16;
        }
		g.setColor(Color.black); 
		g.setFont(GUI.instance().font); 
		if(LibUtilities.isWindows()) { 
			g.drawString("1",contentRect.x+5,  y);
		} else {
			g.drawString("1",contentRect.x+3,  y);
		}
		g.drawString("20",contentRect.x+contentRect.width-13,  y);

		//DRAW VOLUME STRING 
		if(AGJSlider.isSliding()){
			int x = contentRect.x+(contentRect.width/2)-90;
			g.setColor(Color.black); 
			
			int a = getVolumeMaximum().intValue();
			int c = GUI.instance().slider.getMaximum();
			if(a != c){
				g.drawString("SET MAXIMUM TO "+getVolumeMaximum().intValue()+" PACKETS/SECOND",x,y);			
			} else {
				g.drawString("UNLIMITED PACKETS/SECOND",x,y);							
			}
		} else {
			int x = contentRect.x+(contentRect.width/2)-35;
			g.setColor(Color.black); 
			g.drawString("VOLUME = "+getVolumeCurrent().intValue()+" P/S",x,y);			
		}
    } 

    public Dimension getPreferredHorizontalSize() { 
        return new Dimension(WIDTH.intValue(), 20); 
      } 

    protected Dimension getThumbSize() { 
      return new Dimension(12, 20); 
    } 

	public Float getVolumeCurrent() {
		return volume_current;
	}

	public void setVolumeCurrent(Float v) {
		volume_current = v;
	}

	public Float getVolumeMaximum() {
		return volume_maximum;
	}

	public void setVolumeMaximum(Float v) {
		volume_maximum = v;
	}

	public Float scale(int i) {		
		Float f = new Float(i);
		return scale(f);		
	}
	
	public Float scale(Float i) {		
		Float f = new Float((i.intValue())*WIDTH.intValue()/20);
		//Float f = new Float((i.intValue())*10);
		//Log.debug("scale("+i+")="+f.intValue());
		if(f.intValue() > WIDTH.intValue()-5) { f = new Float(WIDTH.intValue()-5); } //cap value so it doesn't go past the right bounds
		return f;		
	}

    public void printSummary() {
		Log.debug("[AGJSliderUI] vc"+getVolumeCurrent()+" vm" + getVolumeMaximum());
    }
} 