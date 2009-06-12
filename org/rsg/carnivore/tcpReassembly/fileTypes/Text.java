package org.rsg.carnivore.tcpReassembly.fileTypes;

import org.rsg.carnivore.tcpReassembly.TcpSessionHost;

public class Text extends File {
	public final static String EXTENSION = ".text";
	
	public static void newData(TcpSessionHost sessionhost, byte[] data) {
		writeFile(data, sessionhost.getSessionId() + "_" + sessionhost.getInitialSequenceNumber() + EXTENSION);
	}
}
