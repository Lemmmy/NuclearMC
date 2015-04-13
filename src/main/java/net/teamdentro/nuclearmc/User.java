package net.teamdentro.nuclearmc;

import net.teamdentro.nuclearmc.packets.SPacket0DChatMessage;
import org.jboss.netty.channel.Channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;

/**
 * Created by Lignum on 12/04/2015.
 */
public class User {
    private SocketAddress sender;
    private int port;
    private String username;
    private Channel socket;
    private byte playerID;

    public User(String username, SocketAddress sender, int port, Channel socket) {
        this.sender = sender;
        this.port = port;
        this.socket = socket;
        this.username = username;
    }

    public byte getPlayerID() {
        return playerID;
    }

    public void setPlayerID(byte playerID) {
        this.playerID = playerID;
    }

    public Channel getChannel() {
        return socket;
    }

    public SocketAddress getAddress() {
        return sender;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        sendMessage(message, Byte.MAX_VALUE);
    }

    public void sendMessage(String message, byte playerID) {
        SPacket0DChatMessage msg = new SPacket0DChatMessage(Server.instance, this);
        msg.setMessage(message);
        msg.setPlayerID(playerID);
        msg.setRecipient(this);
        msg.send();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }

        User user = (User)obj;
        return user.getUsername().equals(username);
    }
}
