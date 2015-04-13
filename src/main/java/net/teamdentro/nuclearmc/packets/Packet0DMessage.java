package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

public class Packet0DMessage extends Packet {
    private byte userdata;
    private String message;

    public Packet0DMessage(Server server, Channel client, ByteBuf data) {
        super(server, client, data);
    }

    @Override
    public byte getID() {
        return 0x0D;
    }

    public byte getUserdata() {
        return userdata;
    }

    public void setUserdata(byte userdata) {
        this.userdata = userdata;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void handle() {
        try {
            userdata = data.readByte();
            message = readString();

            User user = getUser();
            NuclearMC.getLogger().info(message);

            if (user != null) {
                server.broadcastMessage("<" + user.getUsername() + "> " + message);
                NuclearMC.getLogger().info("<" + user.getUsername() + "> " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
