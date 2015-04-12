package net.teamdentro.nuclearmc.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import net.teamdentro.nuclearmc.Server;

public abstract class Packet implements IPacket {
	protected Server server;
	protected Socket client;
    protected DataInputStream data;

	public Packet(Server server, Socket client) {
		this.server = server;
		this.client = client;

        if (client != null) {
            try {
                this.data = new DataInputStream(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
