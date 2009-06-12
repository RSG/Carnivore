package org.rsg.carnivore.tcpReassembly.fileTypes;

import org.rsg.carnivore.tcpReassembly.TcpSessionHost;

public class Pdf extends File {
	public final static String EXTENSION = ".pdf";

	public static void newData(TcpSessionHost sessionhost, byte[] data) {
		writeFile(data, sessionhost.getSessionId() + "_" + sessionhost.getInitialSequenceNumber() + EXTENSION);
	}	
	
	public static int toInt(byte b) {
		return (b + 256) & 0x00ff;
	}
}
