package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.Unpooled;
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

    /**
     * Instantiate the packet
     *
     * @param server The server instance (always Server.instance)
     * @param client The client to send it to (this can be null if it is broadcast)
     */
    public ServerPacket(Server server, User client) {
        this.server = server;
        this.client = client;
    }

    /**
     * Write the buffered packet to its recipient and send it
     */
    public void flush() {
        client.getChannel().writeAndFlush(Unpooled.wrappedBuffer(baos.toByteArray()));
    }

    /**
     * The ID of this packet, as a byte
     *
     * @return The ID of this packet, as a byte
     */
    public abstract byte getID();

    /**
     * Get the user to recieve this packet
     *
     * @return The user to recieve this packet
     */
    public User getRecipient() {
        return client;
    }

    /**
     * Set the user to recieve this packet
     *
     * @param user The user to recieve this packet
     */
    public void setRecipient(User user) {
        client = user;
    }

    /**
     * Send and flush the packet to its recipient
     *
     * @throws IOException
     */
    public abstract void send() throws IOException;

    /**
     * Write a 64-byte padded string to the packet buffer
     *
     * @param str The string to write
     * @throws IOException
     */
    public void writeString(String str) throws IOException {
        if (str.length() > 64)
            str = str.substring(0, 64);
        getWriter().writeBytes(str);

        int paddingNeeded = 64 - str.length();
        for (int i = 0; i < paddingNeeded; ++i) {
            getWriter().writeByte((byte) 0x00);
        }
    }

    /**
     * Get the DataOutputStream for writing to the buffer
     *
     * @return The DataOutputStream
     */
    public DataOutputStream getWriter() {
        return writer;
    }
}
