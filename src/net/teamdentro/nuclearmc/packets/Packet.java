package net.teamdentro.nuclearmc.packets;

import java.net.DatagramPacket;
import java.net.InetAddress;

import net.teamdentro.nuclearmc.Server;

public abstract class Packet {
	protected Server server;
	
	public Packet(Server server) {
		this.server = server;
	}
	
	public abstract int getID();
	
	public abstract void handle(DatagramPacket packet, InetAddress sender);
}
