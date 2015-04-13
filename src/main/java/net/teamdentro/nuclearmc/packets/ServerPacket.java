package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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

    public abstract void send();

    private ByteArrayOutputStream toWrite;

    public void write(byte b) {
        if (toWrite == null) {
            toWrite = new ByteArrayOutputStream();
        }

        toWrite.write(b);
    }

    public void write(byte[] b) {
        if (toWrite == null) {
            toWrite = new ByteArrayOutputStream();
        }

        try {
            toWrite.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(short s) {
        if (toWrite == null) {
            toWrite = new ByteArrayOutputStream();
        }

        byte b1 = (byte)(s);
        byte b2 = (byte)((s >> 8) & 0xff);

        toWrite.write(b1);
        toWrite.write(b2);
    }

    public void write(int i) {
        if (toWrite == null) {
            toWrite = new ByteArrayOutputStream();
        }

        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(i);

        try {
            toWrite.write(b.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String str) {
        try {
            write(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int paddingNeeded = 64 - str.length();
        for (int i = 0; i < paddingNeeded; ++i) {
            write((byte) 0x00);
        }
    }

    public void flush() {
        ChannelFuture cf = client.getChannel().write(Unpooled.wrappedBuffer(toWrite.toByteArray()));
        client.getChannel().flush();
        if (!cf.isSuccess()) {
            System.out.println("Send failed: " + cf.cause());
        }
    }
}
