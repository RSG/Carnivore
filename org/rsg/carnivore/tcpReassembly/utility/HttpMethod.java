package org.rsg.carnivore.tcpReassembly.utility;

public enum HttpMethod {
	//client request verbs
	HEAD, GET, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT,
	
	//server response 
	HTTP,
	
	UNKNOWN; 
	
	public boolean isServerResponse() {
		return this == HTTP;
	}

	public boolean probablyHasAnInterestingPayload() {
		return this==POST || this==HTTP;
	}

	public boolean isClientRequest() {
		return this==HEAD || this==GET || this==POST || this==PUT || this==DELETE || this==TRACE || this==OPTIONS || this==CONNECT;
	}

	public static HttpMethod getMethodFromHttpHeader(byte[] data) {
		String firstFourCharsOfHeader = bytesToFourCharString(data);
		if(null==firstFourCharsOfHeader || firstFourCharsOfHeader.length()<4) return HttpMethod.UNKNOWN;
		
		if(firstFourCharsOfHeader.equals("HEAD")) {
			return HttpMethod.HEAD;
		} else if(firstFourCharsOfHeader.equals("HEAD")) {
			return HttpMethod.HEAD;
		} else if(firstFourCharsOfHeader.equals("GET ")) {
			return HttpMethod.GET;
		} else if(firstFourCharsOfHeader.equals("POST")) {
			return HttpMethod.POST;
		} else if(firstFourCharsOfHeader.equals("PUT ")) {
			return HttpMethod.PUT;
		} else if(firstFourCharsOfHeader.equals("DELE")) {
			return HttpMethod.DELETE;
		} else if(firstFourCharsOfHeader.equals("TRAC")) {
			return HttpMethod.TRACE;
		} else if(firstFourCharsOfHeader.equals("OPTI")) {
			return HttpMethod.OPTIONS;
		} else if(firstFourCharsOfHeader.equals("CONN")) {
			return HttpMethod.CONNECT;
		} else if(firstFourCharsOfHeader.equals("HTTP")) {
			return HttpMethod.HTTP;
		} else {
			return HttpMethod.UNKNOWN;
		}
	}
	
    private static String bytesToFourCharString(byte[] data) {
    	if(data.length < 4) return null;
        StringBuffer sbuff = new StringBuffer();
                
        for(int i = 0; i < 4; i++) {
            int b = (data[i] + 256) & 0x00ff; //correct for java's lack of unsigned variables
            sbuff.append( (char) b );
        }
        return sbuff.toString();
    }
}
