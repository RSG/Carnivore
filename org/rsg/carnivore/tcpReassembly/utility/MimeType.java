package org.rsg.carnivore.tcpReassembly.utility;

import org.rsg.lib.string.StringUtilities;

public enum MimeType {
	//supported text formats
	TEXT, HTML, CSS, XML, JS,
	
	//supported image formats
	GIF, JPEG, PNG, ICON,

	//supported file formats
	PDF,

	//http formats
	POST,
	
	UNKNOWN; 	
	
	public static MimeType getTypeFromHttpHeaderString(String httpContentTypeHeaderString) {
		if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "image/gif")) {
			return MimeType.GIF;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "image/jpeg")) {
			return MimeType.JPEG;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "image/png")) {
			return MimeType.PNG;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "image/x-icon")) {
			return MimeType.ICON;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "application/pdf")) {
			return MimeType.PDF;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "text/html")) {
			return MimeType.HTML;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "text/xml")) {
			return MimeType.XML;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "text/css")) {
			return MimeType.CSS;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "application/javascript")) {
			return MimeType.JS;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "application/x-javascript")) {
			return MimeType.JS;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "text/javascript")) {
			return MimeType.JS;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "text/")) {
			return MimeType.TEXT;
		} else if(StringUtilities.doeStringAStartWithStringB(httpContentTypeHeaderString, "application/x-www-form-urlencoded")) {
			return MimeType.POST;
		} else {
			return MimeType.UNKNOWN;
		}
	}
	

}
