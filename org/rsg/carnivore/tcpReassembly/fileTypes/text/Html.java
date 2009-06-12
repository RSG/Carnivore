package org.rsg.carnivore.tcpReassembly.fileTypes.text;

import org.rsg.carnivore.tcpReassembly.TcpSessionHost;
import org.rsg.carnivore.tcpReassembly.fileTypes.Text;

public class Html extends Text {
	public final static String EXTENSION = ".html";

	public static void newData(TcpSessionHost sessionhost, byte[] data) {
		System.out.println("["+EXTENSION+"] newData");
		writeFile(data, sessionhost.getSessionId() + "_" + sessionhost.getInitialSequenceNumber() + EXTENSION);
	}
}
