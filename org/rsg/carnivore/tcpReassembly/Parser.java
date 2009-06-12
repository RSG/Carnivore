package org.rsg.carnivore.tcpReassembly;

import org.rsg.carnivore.tcpReassembly.proxyHttp.ProxyHttp;

public class Parser {

//	color port2color(int port) {
//		  if(port == 21)   { return color2  ; }   //ftp
//		  if(port == 22)   { return color8  ; }   //ssh
//		  if(port == 25)   { return color5  ; }   //smtp
//		  if(port == 53)   { return color11 ; }   //name-domain server
//		  if(port == 5353) { return color11 ; }   //name-domain server
//		  if(port == 68)   { return color9  ; }   //BOOTP client
//		  if(port == 69)   { return color9  ; }   //BOOTP client
//		  if(port == 80)   { return color4  ; }   //http
//		  if(port == 8020) { return color4  ; }   //http
//		  if(port == 443)  { return color4  ; }   //https
//		  if(port == 110)  { return color5  ; }   //pop3
//		  if(port == 123)  { return color7  ; }   //Network Time Protocol
//		  if(port == 137)  { return color10 ; }   //NETBIOS
//		  if(port == 138)  { return color10 ; }   //NETBIOS
//		  if(port == 139)  { return color10 ; }   //NETBIOS
//		  if(port == 427)  { return color3  ; }   //itunes?
//		  if(port == 5190) { return color6  ; }   //aim
//		  return color1;
//		  /*ADD THESE?
//		   	imap2		143/tcp		imap		# Interim Mail Access Proto
//		   	6346 gnutella / p2p
//		   	6348 gnutella / p2p
//		   	445 Samba
//		   	2222 (udp)	broadcasts Office on OSX  
//		   	*/
//		}

	
	public static void parse(TcpSessionHost sessionhost, int service, byte[] buffer) {
		switch(service) {
		case 80: 
			ProxyHttp.parse(sessionhost, buffer);
			break; 
		}
	}
}
