package org.rsg.carnivore.tcpReassembly.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class Gzip {
	private static final int EXPECTED_COMPRESSION_RATIO= 5;
	private static final int BUF_SIZE= 4096;
	public static final byte[] decompress_gzip(byte[] in) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream(EXPECTED_COMPRESSION_RATIO * in.length);
			GZIPInputStream inStream = new GZIPInputStream ( new ByteArrayInputStream(in) );
		
			byte[] buf = new byte[BUF_SIZE];
			while (true) {
				int size = inStream.read(buf);
				if (size <= 0) break;
				outStream.write(buf, 0, size);
			}
			outStream.close();			
			return outStream.toByteArray();
		} catch (IOException e) {
			System.err.println("[Gzip] decompress_gzip IOException");
			e.printStackTrace();
		}
		return null;
	}
}
