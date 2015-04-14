package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.util.Position;

import java.io.IOException;

public class SPacket08Teleport extends ServerPacket {
    private Position pos;

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

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeByte((byte) -1);
        getWriter().writeShort(pos.getX());
        getWriter().writeShort(pos.getY());
        getWriter().writeShort(pos.getZ());
        getWriter().writeByte(pos.getYaw());
        getWriter().writeByte(pos.getPitch());
        flush();
    }
}
