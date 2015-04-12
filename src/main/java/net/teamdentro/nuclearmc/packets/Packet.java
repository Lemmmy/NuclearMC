package net.teamdentro.nuclearmc.packets;

import java.io.DataInputStream;
import java.io.IOException;

import net.teamdentro.nuclearmc.Server;

public abstract class Packet {
	protected Server server;
	protected DataInputStream data;
	
	public Packet(Server server, DataInputStream data) {
		this.server = server;
		this.data = data;
	}
	
	public abstract byte getID();
	
	public abstract void handle();
	
	protected String readString(int bytes) throws IOException {
		byte[] b = new byte[bytes];
		for (int i = 0; i < bytes; ++i) {
			b[i] = data.readByte();
		}
		return new String(b);
	}
}
