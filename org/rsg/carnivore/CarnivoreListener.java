package org.rsg.carnivore;

/**
 * @author alex
 *
 */
public interface CarnivoreListener {
	void newCarnivorePacket(CarnivorePacket packet);
	void newImage(String path);
}
