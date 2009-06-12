package org.rsg.carnivore.tcpReassembly.fileTypes.image;

import org.rsg.carnivore.tcpReassembly.TcpSessionHost;
import org.rsg.carnivore.tcpReassembly.fileTypes.Image;

public class Icon extends Image {
	public final static String EXTENSION = "ico";

	public static void newData(TcpSessionHost sessionhost, byte[] data) {
		System.out.println("["+EXTENSION+"] newData");
		writeImage(data, sessionhost.getSessionId() + "_" + sessionhost.getInitialSequenceNumber(), EXTENSION);
	}
}
