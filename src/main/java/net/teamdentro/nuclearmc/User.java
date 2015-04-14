package net.teamdentro.nuclearmc;

import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.packets.SPacket08Teleport;
import net.teamdentro.nuclearmc.packets.SPacket0DChatMessage;
import net.teamdentro.nuclearmc.util.Position;

import java.io.IOException;
import java.net.InetSocketAddress;

public class User {
    private InetSocketAddress sender;
    private int port;
    private String username;
    private Channel socket;
    private byte playerID;
    private Position pos;

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
        }
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos, boolean teleport) {
        this.pos = pos;

        SPacket08Teleport packet = new SPacket08Teleport(Server.instance, this);
        packet.setPos(pos);
        packet.setPlayer(getPlayerID());
        Server.instance.broadcast(packet, false);

        if (teleport) {
            packet = new SPacket08Teleport(Server.instance, this);
            packet.setPos(pos);
            packet.setPlayer((byte) -1);
            try {
                packet.send();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }

        User user = (User) obj;
        return user.getUsername().equals(username);
    }
}
