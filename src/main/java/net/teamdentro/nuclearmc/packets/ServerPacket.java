package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ServerPacket implements IPacket {
    protected Server server;
    protected User client;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private DataOutputStream writer = new DataOutputStream(baos);

    public ServerPacket(Server server, User client) {
        this.server = server;
        this.client = client;
    }

    public User getRecipient() {
        return client;
    }

    public void setRecipient(User user) {
        client = user;
    }

    public abstract byte getID();

    public abstract void send() throws IOException;

    public DataOutputStream getWriter() {
        return writer;
    }

    public void writeString(String str) throws IOException {
        getWriter().writeBytes(str);

        int paddingNeeded = 64 - str.length();
        for (int i = 0; i < paddingNeeded; ++i) {
            getWriter().writeByte((byte) 0x00);
        }
    }

    public void flush() {
        client.getChannel().writeAndFlush(Unpooled.wrappedBuffer(baos.toByteArray()));
    }
}
