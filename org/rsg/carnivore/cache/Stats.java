package org.rsg.carnivore.cache;

public class Stats {
	public float packetsPerSecTotal;
	public float packetsPerSecTCP;
	public float packetsPerSecUDP;
	
	public Stats(float packetsPerSecTotal, float packetsPerSecTCP, float packetsPerSecUDP) {
		this.packetsPerSecTotal	= packetsPerSecTotal;
		this.packetsPerSecTCP 	= packetsPerSecTCP;
		this.packetsPerSecUDP 	= packetsPerSecUDP;		
	}
}
