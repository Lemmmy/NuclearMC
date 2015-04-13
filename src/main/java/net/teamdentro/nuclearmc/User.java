package net.teamdentro.nuclearmc;

import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.packets.SPacket0DChatMessage;

import java.net.InetSocketAddress;

/**
 * Created by Lignum on 12/04/2015.
 */
public class User {
    private InetSocketAddress sender;
    private int port;
    private String username;
    private Channel socket;
    private byte playerID;

    public User(String username, InetSocketAddress sender, int port, Channel socket) {
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

    public InetSocketAddress getAddress() {
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
        try {
            msg.send();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
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
