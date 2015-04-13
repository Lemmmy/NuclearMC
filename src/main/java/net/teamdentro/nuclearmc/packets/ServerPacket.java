package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Lignum on 12/04/2015.
 */
public abstract class ServerPacket implements IPacket {
    protected Server server;
    protected User client;

    public ServerPacket(Server server, User client) {
        this.server = server;
        this.client = client;
    }

    public void setRecipient(User user) {
        client = user;
    }

    public User getRecipient() {
        return client;
    }

    public abstract byte getID();

    public abstract void send() throws IOException;

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private DataOutputStream writer = new DataOutputStream(baos);

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
        ChannelFuture cf = client.getChannel().writeAndFlush(Unpooled.wrappedBuffer(baos.toByteArray()));
    }
}
