package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

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
        NuclearMC.getLogger().info("IDENTIFY");
        write(getID());
        write((byte) 0x07);
        write(server.getServerName());
        write(server.getMotd());
        write(isOp() ? (byte) 0x64 : (byte) 0x00); // not op
        flush();
    }
}
