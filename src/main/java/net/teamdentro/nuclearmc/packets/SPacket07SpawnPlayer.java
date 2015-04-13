package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

/**
 * Created by Lignum on 12/04/2015.
 */
public class SPacket07SpawnPlayer extends ServerPacket {
    public SPacket07SpawnPlayer(Server server, User client) {
        super(server, client);
    }

    private int x, y, z;
    private String name;
    private byte playerID;
    private int yaw, pitch;

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
    public byte getID() {
        return 0x07;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeByte(playerID);
        writeString(name);
        getWriter().writeShort((short) x);
        getWriter().writeShort((short) y);
        getWriter().writeShort((short) z);
        getWriter().writeByte((byte) yaw);
        getWriter().writeByte((byte) pitch);
        flush();
    }
}
