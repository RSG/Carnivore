package org.rsg.carnivore.tcpReassembly.fileTypes.text;

import org.rsg.carnivore.tcpReassembly.TcpSessionHost;
import org.rsg.carnivore.tcpReassembly.fileTypes.File;

public class Post extends File {
	public final static String EXTENSION = ".httppost";

	public static void newData(TcpSessionHost sessionhost, byte[] data) {
		System.out.println("["+EXTENSION+"] newData");
		writeFile(data, sessionhost.getSessionId() + "_" + sessionhost.getInitialSequenceNumber() + EXTENSION);
	}
}
