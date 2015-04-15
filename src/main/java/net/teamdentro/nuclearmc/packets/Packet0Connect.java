package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Packet0Connect extends Packet {
    private byte protVersion;
    private String username;
    private String key;
    private byte userdata;

    public Packet0Connect(Server server, Channel client, ByteBuf data) {
        super(server, client, data);
    }

    @Override
    public byte getID() {
        return (byte) 0x0;
    }

    @Override
    public void handle() {
        try {
            protVersion = data.readByte();
            username = readString().trim();
            key = readString().trim();
            userdata = data.readByte();

            User user = new User(username, (InetSocketAddress) client.remoteAddress(), ((InetSocketAddress) client.remoteAddress()).getPort(), client);
            user.setPlayerID(server.makeUniquePlayerID());
            user.setLevel(server.getMainLevel());
            server.addUser(user);

            NuclearMC.getLogger().info("Player " + username + " [" + user.getAddress().toString() + ":" + user.getPort() + "] (EID " + user.getPlayerID() + ")");

            SPacket0ServerIdentify identify = new SPacket0ServerIdentify(server, user);
            identify.setOp(true);
            identify.send();

            user.setLevel(server.getMainLevel());
        } catch (IOException ignored) {
        }
    }

    public String getKey() {
        return key;
    }

    public byte getProtVersion() {
        return protVersion;
    }

    public byte getUserdata() {
        return userdata;
    }

    public String getUsername() {
        return username;
    }
}
