package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import org.jboss.netty.buffer.ChannelBuffer;

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

    public abstract void send();

    public void writeString(String str) {
        client.getChannel().write(str);

        int paddingNeeded = 64 - str.length();
        for (int i = 0; i < paddingNeeded; ++i) {
            client.getChannel().write((byte)0x00);
        }
    }
}
