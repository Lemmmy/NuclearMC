package net.teamdentro.nuclearmc.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

public abstract class Packet implements IPacket {
	protected Server server;
	protected Channel client;
    protected ChannelBuffer data;

	public Packet(Server server, Channel client, ChannelBuffer data) {
		this.server = server;
		this.client = client;
		this.data = data;
    }

    public User getUser() {
        for (User user : server.getOnlineUsers()) {
            if (user.getAddress().equals(client.getRemoteAddress()) && user.getPort() == ((InetSocketAddress)client.getRemoteAddress()).getPort()) {
                return user;
            }
        }

        return null;
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
