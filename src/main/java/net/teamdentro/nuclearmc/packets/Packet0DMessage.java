package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import java.io.IOException;

/**
 * Created by Lignum on 12/04/2015.
 */
public class Packet0DMessage extends Packet {
    public Packet0DMessage(Server server, Channel client, ChannelBuffer data) {
        super(server, client, data);
    }

    @Override
    public byte getID() {
        return 0x0D;
    }

    private byte userdata;
    private String message;

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
            message = readString(64);

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
