package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Lignum on 12/04/2015.
 */
public abstract class ServerPacket implements IPacket {
    protected Server server;
    protected User client;
    protected DataOutputStream data;

    public ServerPacket(Server server, User client) {
        this.server = server;
        this.client = client;

        if (client != null) {
            data = client.getOutputStream();
        }
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
        try {
            data.writeBytes(str);

            int paddingNeeded = 64 - str.length();
            for (int i = 0; i < paddingNeeded; ++i) {
                data.writeByte(0x00);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
