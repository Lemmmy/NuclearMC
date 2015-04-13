package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

/**
 * Created by Lignum on 12/04/2015.
 */
public class SPacket0DChatMessage extends ServerPacket {
    public SPacket0DChatMessage(Server server, User client) {
        super(server, client);
    }

    @Override
    public byte getID() {
        return 0x0D;
    }

    private byte playerID;
    private String message;

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
    public void send() {
        write(getID());
        write(playerID);
        write(message);

        flush();
    }
}
