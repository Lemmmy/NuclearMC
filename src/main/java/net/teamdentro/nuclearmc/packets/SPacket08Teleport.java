package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

public class SPacket08Teleport extends ServerPacket {
    private int x, y, z;
    private int yaw, pitch;

    public SPacket08Teleport(Server server, User client) {
        super(server, client);
    }

    @Override
    public byte getID() {
        return 0x08;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getYaw() {
        return yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeByte((byte) -1);
        getWriter().writeShort((short) x);
        getWriter().writeShort((short) y);
        getWriter().writeShort((short) z);
        getWriter().writeByte((byte) yaw);
        getWriter().writeByte((byte) pitch);
        flush();
    }
}
