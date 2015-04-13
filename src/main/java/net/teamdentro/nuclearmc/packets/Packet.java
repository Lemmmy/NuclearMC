package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract class Packet implements IPacket {
	protected Server server;
	protected Channel client;
    protected ByteBuf data;

	public Packet(Server server, Channel client, ByteBuf data) {
		this.server = server;
		this.client = client;
		this.data = data;
    }

    public User getUser() {
        for (User user : server.getOnlineUsers()) {
            if (user.getAddress().equals(client.remoteAddress()) && user.getPort() == ((InetSocketAddress)client.remoteAddress()).getPort()) {
                return user;
            }
        }

        return null;
    }
	
	public abstract byte getID();
	
	public abstract void handle();
	
	protected String readString() throws IOException {
		byte[] b = new byte[64];
		for (int i = 0; i < 64; ++i) {
			b[i] = data.readByte();
		}
		return new String(b);
	}
}
