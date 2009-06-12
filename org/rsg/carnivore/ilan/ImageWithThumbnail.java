package org.rsg.carnivore.ilan;

import processing.core.PImage;

public class ImageWithThumbnail {
	private PImage image;
	private PImage thumbnail;
	private String filename;
	
	public PImage getImage() {
		return image;
	}

	public void setImage(PImage image) {
		this.image = image;
	}

	public PImage getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(PImage thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ImageWithThumbnail(String filename, PImage image, PImage thumbnail) {
		super();
		this.filename = filename; 
		this.image = image;
		this.thumbnail = thumbnail;
	}
}
