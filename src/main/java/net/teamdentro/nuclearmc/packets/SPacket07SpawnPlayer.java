package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.util.Position;

import java.io.IOException;

public class SPacket07SpawnPlayer extends ServerPacket {
    private String name;
    private byte playerID;
    private Position pos;

    public SPacket07SpawnPlayer(Server server, User client) {
        super(server, client);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getPlayerID() {
        return playerID;
    }

    public void setPlayerID(byte playerID) {
        this.playerID = playerID;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    @Override
    public byte getID() {
        return 0x07;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeByte(playerID);
        writeString(name);
        getWriter().writeShort(pos.getX());
        getWriter().writeShort(pos.getY());
        getWriter().writeShort(pos.getZ());
        getWriter().writeByte(pos.getYaw());
        getWriter().writeByte(pos.getPitch());
        flush();
    }
}
