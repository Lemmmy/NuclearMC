package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.util.Util;

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

    public abstract byte getID();

    public User getUser() {
        for (User user : server.getOnlineUsers()) {
            if (user.getAddress().equals(client.remoteAddress()) && user.getPort() == ((InetSocketAddress) client.remoteAddress()).getPort()) {
                return user;
            }
        }

        return null;
    }

    public abstract void handle() throws IOException;

    protected String readString() throws IOException {
        int size = Util.clamp(data.readableBytes(), 0, 64);

        byte[] b = new byte[size];
        for (int i = 0; i < size; ++i) {
            b[i] = data.readByte();
        }
        return new String(b).trim();
    }
}
