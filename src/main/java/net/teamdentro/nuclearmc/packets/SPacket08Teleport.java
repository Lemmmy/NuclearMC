package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

/**
 * Created by Lignum on 12/04/2015.
 */
public class SPacket08Teleport extends ServerPacket {
    public SPacket08Teleport(Server server, User client) {
        super(server, client);
    }

    @Override
    public byte getID() {
        return 0x08;
    }

    private int x, y, z;

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

    private int yaw, pitch;

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
    public void send() {
        write(getID());
        write((byte) -1);
        write((short) x);
        write((short) y);
        write((short) z);
        write((byte) yaw);
        write((byte) pitch);
        flush();
    }
}
