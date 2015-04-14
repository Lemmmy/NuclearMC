package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.util.Position;

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

    public byte getProtVersion() {
        return protVersion;
    }

    public String getUsername() {
        return username;
    }

    public String getKey() {
        return key;
    }

    public byte getUserdata() {
        return userdata;
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
            server.addUser(user);

            NuclearMC.getLogger().info("Player " + username + " [" + user.getAddress().toString() + ":" + user.getPort() + "] (EID " + user.getPlayerID() + ")");

            SPacket0ServerIdentify identify = new SPacket0ServerIdentify(server, user);
            identify.setOp(true);
            identify.send();

            server.getLevel().sendToUser(server, user);

            SPacket07SpawnPlayer spawn = new SPacket07SpawnPlayer(server, user);
            spawn.setX((short) (server.getLevel().getSpawnX() * 32));
            spawn.setY((short) (server.getLevel().getSpawnY() * 32 + 51));
            spawn.setZ((short) (server.getLevel().getSpawnZ() * 32));
            spawn.setPlayerID(user.getPlayerID());
            spawn.setPitch(0);
            spawn.setYaw(0);
            spawn.setName(user.getUsername());
            server.broadcast(spawn, false);

            SPacket08Teleport teleport = new SPacket08Teleport(server, user);
            teleport.setPos(new Position((short) (server.getLevel().getSpawnX() * 32),
                    (short) (server.getLevel().getSpawnY() * 32 + 51),
                    (short) (server.getLevel().getSpawnZ() * 32)));
            teleport.send();

            user.sendMessage("Welcome to the server!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
