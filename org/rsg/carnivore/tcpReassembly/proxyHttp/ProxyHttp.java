package org.rsg.carnivore.tcpReassembly.proxyHttp;

import org.rsg.carnivore.tcpReassembly.TcpSessionHost;
import org.rsg.carnivore.tcpReassembly.fileTypes.Pdf;
import org.rsg.carnivore.tcpReassembly.fileTypes.Text;
import org.rsg.carnivore.tcpReassembly.fileTypes.image.Gif;
import org.rsg.carnivore.tcpReassembly.fileTypes.image.Icon;
import org.rsg.carnivore.tcpReassembly.fileTypes.image.Jpeg;
import org.rsg.carnivore.tcpReassembly.fileTypes.image.Png;
import org.rsg.carnivore.tcpReassembly.fileTypes.text.Css;
import org.rsg.carnivore.tcpReassembly.fileTypes.text.Html;
import org.rsg.carnivore.tcpReassembly.fileTypes.text.Javascript;
import org.rsg.carnivore.tcpReassembly.fileTypes.text.Post;
import org.rsg.carnivore.tcpReassembly.fileTypes.text.Xml;

public class ProxyHttp {
	public static void parse(TcpSessionHost sessionhost, byte[] buffer) {
		HttpDatagram http = new HttpDatagram(buffer);
		switch(http.getMimeType()) {
		case GIF: 	Gif.newData(sessionhost, http.getContent()); break;
		case JPEG: 	Jpeg.newData(sessionhost, http.getContent()); break;
		case PNG: 	Png.newData(sessionhost, http.getContent()); break;
//		case ICON: 	Icon.newData(sessionhost, http.getContent()); break;
//		case PDF: 	Pdf.newData(sessionhost, http.getContent()); break;
//		case HTML: 	Html.newData(sessionhost, http.getContent()); break;
//		case XML: 	Xml.newData(sessionhost, http.getContent()); break;
//		case JS: 	Javascript.newData(sessionhost, http.getContent()); break;
//		case CSS: 	Css.newData(sessionhost, http.getContent()); break;
//		case TEXT: 	Text.newData(sessionhost, http.getContent()); break;
//		case POST: 	Post.newData(sessionhost, http.getContent()); break;
//		default: 	System.out.println("[Http] parse HTTP (other)");
		}
	}
}
