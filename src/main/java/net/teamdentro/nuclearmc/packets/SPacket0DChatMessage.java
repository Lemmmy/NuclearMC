package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

public class SPacket0DChatMessage extends ServerPacket {
    private byte playerID;
    private String message;

    public SPacket0DChatMessage(Server server, User client) {
        super(server, client);
    }

    @Override
    public byte getID() {
        return 0x0D;
    }

    public byte getPlayerID() {
        return playerID;
    }

    public void setPlayerID(byte playerID) {
        this.playerID = playerID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeByte(getPlayerID());
        writeString(getMessage());

        flush();
    }
}
