package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.util.Position;

import java.io.IOException;

public class SPacket08Teleport extends ServerPacket {
    private Position pos;
    private byte player;

    public SPacket08Teleport(Server server, User client) {
        super(server, client);
    }

    @Override
    public byte getID() {
        return 0x08;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public byte getPlayer() {
        return player;
    }

    public void setPlayer(byte player) {
        this.player = player;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeByte(getPlayer());
        getWriter().writeShort(getPos().getX());
        getWriter().writeShort(getPos().getY());
        getWriter().writeShort(getPos().getZ());
        getWriter().writeByte(getPos().getYaw());
        getWriter().writeByte(getPos().getPitch());
        flush();
    }
}
