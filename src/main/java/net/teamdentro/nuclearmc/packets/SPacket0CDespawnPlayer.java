package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

public class SPacket0CDespawnPlayer extends ServerPacket {
    private byte playerid;

    public SPacket0CDespawnPlayer(Server server, User client) {
        super(server, client);
    }

    @Override
    public byte getID() {
        return 0x0C;
    }

    public byte getPlayerID() {
        return playerid;
    }

    public void setPlayerID(byte playerid) {
        this.playerid = playerid;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeByte(getPlayerID());

        flush();
    }
}
