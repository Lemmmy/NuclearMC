package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

/**
 * Created by Lignum on 12/04/2015.
 */
public class SPacket0ServerIdentify extends ServerPacket {
    public SPacket0ServerIdentify(Server server, User client) {
        super(server, client);
    }

    @Override
    public byte getID() {
        return 0x00;
    }

    private boolean op;

    public boolean isOp() {
        return op;
    }

    public void setOp(boolean op) {
        this.op = op;
    }

    @Override
    public void send() {
        try {
            data.writeByte(getID());
            data.writeByte(0x07);
            writeString(server.getServerName());
            writeString(server.getMotd());
            data.writeByte(isOp() ? 0x64 : 0x00); // not op
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
