package org.rsg.carnivore.tcpReassembly.fileTypes.image;

import org.rsg.carnivore.tcpReassembly.TcpSessionHost;

public class Jpeg extends org.rsg.carnivore.tcpReassembly.fileTypes.Image {
	public final static String EXTENSION = "jpg";

	public static void newData(TcpSessionHost sessionhost, byte[] data) {
		System.out.println("["+EXTENSION+"] newData");
		writeImage(data, sessionhost.getSessionId() + "_" + sessionhost.getInitialSequenceNumber(), EXTENSION);
	}
}
