package org.rsg.carnivore.tcpReassembly.proxyHttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.rsg.carnivore.tcpReassembly.utility.Gzip;
import org.rsg.carnivore.tcpReassembly.utility.HttpMethod;
import org.rsg.carnivore.tcpReassembly.utility.MimeType;
import org.rsg.lib.LibUtilities;
import org.rsg.lib.string.StringUtilities;

public class HttpDatagram {
    public static final int CR   = 13; //ascii carriage return (in decimal, i.e. int)
    public static final int LF   = 10; //ascii line feed (in decimal, i.e. int)
    public static final int lenCRLF = 2; //length = two bytes
    
    public static final int ZERO = 48; //ascii zero (in decimal, i.e. int)
	private final static String HTTP_HEADER_KEY_CONTENT_TYPE 		= "Content-Type";
	private final static String HTTP_HEADER_KEY_CONTENT_ENCODING 	= "Content-Encoding";
	private final static String HTTP_HEADER_KEY_CONTENT_LENGTH 		= "Content-Length";
	private final static String HTTP_HEADER_KEY_TRANSFER_ENCODING	= "Transfer-Encoding";
	private final static String HTTP_HEADER_VALUE_GZIP 				= "gzip";
	private final static String HTTP_HEADER_VALUE_TRANSFER_ENCODING = "chunked";
	private boolean isGzipped = false; 
	private boolean isChunked = false; 
	private boolean isChunkedAndIsComplete = false; 
	private MimeType mimeType = null;
	private HttpMethod httpMethod = null;
	private int contentLengthDeclaredInHeader = -1; 
	private int indexOfNextByteAfterFirstDoubleCRLF = -1;
    private HashMap<String, String> hashHeader = new HashMap<String, String>();
    private byte[] bytesHeader;
    private byte[] bytesContent;
	
	public HttpDatagram(byte[] buffer) {
		super();
		
		//parse http method verb (HTTP, GET, POST, etc.)
		this.httpMethod = HttpMethod.getMethodFromHttpHeader(buffer);
		
		//find instance of first double CRLF, which indicates end of header
		initCRLF(buffer);
		
		//parse header
		parseHeader(buffer);

		//parse Transfer-Encoding
		this.isChunked = StringUtilities.doeStringAStartWithStringB(hashHeader.get(HTTP_HEADER_KEY_TRANSFER_ENCODING), HTTP_HEADER_VALUE_TRANSFER_ENCODING);	

		//parse Content-Encoding
		this.isGzipped = StringUtilities.doeStringAStartWithStringB(hashHeader.get(HTTP_HEADER_KEY_CONTENT_ENCODING), HTTP_HEADER_VALUE_GZIP);	
		
		//parse Content-Type
		this.mimeType = MimeType.getTypeFromHttpHeaderString(hashHeader.get(HTTP_HEADER_KEY_CONTENT_TYPE));

		//parse Content-Length
		String len = hashHeader.get(HTTP_HEADER_KEY_CONTENT_LENGTH);
		if(null!=len && len.length()>0)
			this.contentLengthDeclaredInHeader = Integer.valueOf(len.trim());	
		
		//parse content
		parseContent(buffer);

//		if(isChunked)
//			System.out.println("[Http] constructor " + this);
	}
	
	private boolean isHeaderOnly(byte[] data) {
		return hasNoCRLF() || hadCRLFatEndOfFile(data);
	}

	private boolean hasNoCRLF() {
		return indexOfNextByteAfterFirstDoubleCRLF<0;
	}

	private boolean hadCRLFatEndOfFile(byte[] data) {
		return indexOfNextByteAfterFirstDoubleCRLF>=data.length;
	}
	
	private void initCRLF(byte[] data) {
		 indexOfNextByteAfterFirstDoubleCRLF = indexOfNextByteAfterFirstDoubleCRLF(data);		
	}
	
	public void parseHeader(byte[] data) {
		int i = indexOfNextByteAfterFirstDoubleCRLF;
		if(hasNoCRLF() || hadCRLFatEndOfFile(data)) //normalize the value in case of weird/missing CRLFs
			i = data.length;
		
 		ByteBuffer byteBuffer  = ByteBuffer.wrap(data);
		byte[] bHeader = new byte[i];
		byteBuffer.position(0);
		byteBuffer.get(bHeader, 0, i);
		hashHeader = getHttpHeader(bHeader);
		bytesHeader = bHeader;
	}

	public void parseContent(byte[] data) {
		if(isHeaderOnly(data)) return;
			    
		ByteBuffer byteBuffer  = ByteBuffer.wrap(data);
		byte[] bContent = new byte[data.length-indexOfNextByteAfterFirstDoubleCRLF];		
		byteBuffer.position(indexOfNextByteAfterFirstDoubleCRLF);
		byteBuffer.get(bContent, 0, data.length-indexOfNextByteAfterFirstDoubleCRLF);
		
		if(isChunked) {
			bytesContent = unchunk(bContent);
		} else {
			bytesContent = bContent;			
		}
	}
	
	public byte[] unchunk(byte[] data) {
//		System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//		System.out.println("[HttpDatagram] unchunk\n" + LibUtilities.bytesToCharString(data) + "\n\n");
		
		int position = 0;
		
		ByteBuffer byteBuffer  = ByteBuffer.wrap(data);
		ByteArrayOutputStream baosUnchunked = new ByteArrayOutputStream();
		
		int count = 0;
		UNCHUNK:
		while(position < data.length) {
			int nextCRLF = indexOfNextByteAfterNextCRLF(data, position);
			
//			System.out.println("[HttpDatagram] "+ count++);
//			System.out.println("[HttpDatagram] nextCRLF:"+ nextCRLF);
//			if(count > 10) break UNCHUNK;

			if(nextCRLF==-1) {
//				System.out.println("break UNCHUNK: nextCRLF==-1 "); 
				isChunkedAndIsComplete = true; //not necessarily complete, but something's wrong so flush it
				break UNCHUNK;
			}

			//get chunk-size
			byte[] bytesChunkSize = new byte[nextCRLF-position];		
			byteBuffer.position(position);
			byteBuffer.get(bytesChunkSize, 0, bytesChunkSize.length);
			String s = LibUtilities.encodeBytesAsUTF8(bytesChunkSize).trim();
			int chunkSize = 0;
			
			if(s.length()>0) {
				try {
					chunkSize = Integer.parseInt(s, 16);
				} catch (NumberFormatException e) {
					System.out.println("[HttpDatagram] NumberFormatException -- breaking");
					isChunkedAndIsComplete = true; //not necessarily complete, but something's wrong so flush it
					break UNCHUNK;					
				}
			}
				
			byte[] bytesChunk = new byte[chunkSize];		
			try {
				byteBuffer.position(nextCRLF);
				byteBuffer.get(bytesChunk, 0, bytesChunk.length);
			} catch (BufferUnderflowException e) {
				System.out.println("[HttpDatagram] BufferUnderflowException -- breaking");
				isChunkedAndIsComplete = true; //not necessarily complete, but something's wrong so flush it
				break UNCHUNK;				
			}
			
			try {
				baosUnchunked.write(bytesChunk);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			position = nextCRLF + chunkSize + lenCRLF;
//			System.out.println("\n[HttpDatagram] CHUNK #"+ (count++) +" -- chunkSize: "+chunkSize + " ("+LibUtilities.bytesToAsciiCharString(bytesChunkSize).trim()+"), nextCRLF:"+ nextCRLF);
//			System.out.println("[HttpDatagram] bytesChunk:\n"+LibUtilities.bytesToCharString(bytesChunk));
//			System.out.println("[HttpDatagram] position:"+ position + " data.length:"+data.length);

			if(chunkSize==0) {
//				System.out.println("break UNCHUNK: chunkSize==0"); 
				isChunkedAndIsComplete = true; //chunked is complete
				break UNCHUNK;
			}

//			if(nextCRLF>=data.length) {
//				System.out.println("break UNCHUNK: nextCRLF ("+nextCRLF+") >= data.length ("+data.length+")"); 
//				break UNCHUNK;
//			}
        }		
		return baosUnchunked.toByteArray();
	}
	
	public int indexOfNextByteAfterNextCRLF(byte[] data, int marker) {
        for(; marker < data.length; marker++) {
            if (marker>0 && 
         		(toInt(data[marker-1]) == CR) && (toInt(data[marker]) == LF))     //break on <CR><LF>         	
        		return marker+1;
        }		
        return -1;
	}	
	
	public int indexOfNextByteAfterFirstDoubleCRLF(byte[] data) {
        for(int i = 0; i < data.length; i++) {
            if (i>3 && 
                (toInt(data[i-3]) == CR) && (toInt(data[i-2]) == LF) && //break on <CR><LF><CR><LF> 
         		(toInt(data[i-1]) == CR) && (toInt(data[i]) == LF))              	
        		return i+1;
        }		
        return -1;
	}
	
	public HashMap<String, String> getHttpHeader(byte[] data) {
		return getHttpHeader(LibUtilities.bytesToCharString(data));
	}
	
	public HashMap<String, String> getHttpHeader(String header) {
		HashMap<String, String> hashmap = new HashMap<String, String>();
		
		String[] headerarray = header.split("\n");
		for(int i = 0; i < headerarray.length; i++) {
			
			//for http server responses split on whitespace and retain status code as value
			if(i==0 && headerarray[i].length()>3 && headerarray[i].substring(0,4).equals("HTTP")) {
				String[] linearray = headerarray[i].split(" ", 3);
				if(linearray.length>=2)
					hashmap.put("HTTP", linearray[1]);
				
			//else split on "key: value" pairs
			} else {				
				String[] linearray = headerarray[i].split(": ", 2);
				if(linearray.length>=2)
					hashmap.put(linearray[0], linearray[1]);
			}
		}		
		return hashmap;
	}
	
	public boolean doesContentMatchExpectedLength() {
		return contentLengthDeclaredInHeader>-1 && contentLengthDeclaredInHeader<=lengthContent();
	}
	
	public boolean shouldFlush() {
		if(probablyHasAnInterestingPayload()) {
			
			//chunked
			if(isChunked) {
				return isChunkedAndIsComplete;
				
			//no content length set, so we assume flush now
			} else if(contentLengthDeclaredInHeader==-1) {
				return true;
			
			//content length is set, so wait until we're full 
			} else {				
				return doesContentMatchExpectedLength(); //wait until reassembly is finished
			}
				
		//always flush ones w/out payloads
		} else {
			return true;
		}
	}

//	public boolean hasChunkedTerminationMarker(byte[] data) {
//        for(int i = 0; i < data.length; i++) {
//            if (i>5 && 
//                (toInt(data[i-4]) == CR) && (toInt(data[i-3]) == LF) && //break on <CR><LF>0<CR><LF>
//                (toInt(data[i-2]) == ZERO) && 			4			    
//         		(toInt(data[i-1]) == CR) && (toInt(data[i]) == LF))               	
//        		return true;
//        }		
//        return false;
//	}

	public int toInt(byte b) {
		return (b + 256) & 0x00ff; //correct for java's lack of unsigned variables
	}
	
	public boolean probablyHasAnInterestingPayload() {
		return null!=httpMethod && httpMethod.probablyHasAnInterestingPayload();		
	}
	
	public boolean isServerResponse() {
		return null!=httpMethod && httpMethod.isServerResponse();
	}

	public boolean isClientRequest() {
		return null!=httpMethod && httpMethod.isClientRequest();
	}

	private int lengthContent() {
		if(null==bytesContent) return 0;
		return bytesContent.length;
	}

	public byte[] getContent() {
		if(isGzipped) 
			return Gzip.decompress_gzip(bytesContent);
		return bytesContent;
	}

	public MimeType getMimeType() {
		return mimeType;
	}
		
	public String toString() {
		String s = httpMethod.toString();
		if(isServerResponse()) { 
			s += " mimeType:"+mimeType;
			s += " isChunked:"+isChunked;
			s += " isGzipped:"+isGzipped;
			s += " contentLengthDeclaredInHeader:" +contentLengthDeclaredInHeader;
			s += " lengthContent:" +lengthContent();
			s += " doesContentMatchExpectedLength:"+ doesContentMatchExpectedLength();	
			
		} else {
			
		}
		s += "\n";
		if(null!=bytesHeader)
			s += "<HEADER>\n"+LibUtilities.bytesToCharString(bytesHeader) + "</HEADER>\n";
		
		if(null!=bytesContent)
			s += "<CONTENT>\n"+LibUtilities.bytesToCharString(bytesContent) + "</CONTENT>\n";
		return s;
	}
}
