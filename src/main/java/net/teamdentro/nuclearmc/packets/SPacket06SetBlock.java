package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.util.Position;

import java.io.IOException;

public class SPacket06SetBlock extends ServerPacket {
    public SPacket06SetBlock(Server server, User client) {
        super(server, client);
    }

    private byte block;
    private short x,y,z;

    @Override
    public byte getID() {
        return 0x06;
    }

    public byte getBlock() {
        return block;
    }

    public void setBlock(byte block) {
        this.block = block;
    }

    public short getX() {
        return x;
    }

    public void setX(short x) {
        this.x = x;
    }

    public short getY() {
        return y;
    }

    public void setY(short y) {
        this.y = y;
    }

    public short getZ() {
        return z;
    }

    public void setZ(short z) {
        this.z = z;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeShort(getX());
        getWriter().writeShort(getY());
        getWriter().writeShort(getZ());
        getWriter().writeByte(getBlock());

        flush();
    }
}
