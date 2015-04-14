package net.teamdentro.nuclearmc.util;

public class Position {
    private short x, y, z;
    private byte yaw, pitch;

    public Position(short x, short y, short z) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.yaw = 0x00;
        this.pitch = 0x00;
    }

    public Position(short x, short y, short z, byte yaw, byte pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
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

    public byte getYaw() {
        return yaw;
    }

    public void setYaw(byte yaw) {
        this.yaw = yaw;
    }

    public byte getPitch() {
        return pitch;
    }

    public void setPitch(byte pitch) {
        this.pitch = pitch;
    }

    public double getXAsDouble() {
        return (double) (x / 32);
    }

    public void setXAsDouble(double x) {
        this.x = (short) (x * 32);
    }

    public double getYAsDouble() {
        return (double) (y / 32);
    }

    public void setYAsDouble(double y) {
        this.y = (short) (y * 32);
    }

    public double getZAsDouble() {
        return (double) (z / 32);
    }

    public void setZAsDouble(double z) {
        this.z = (short) (z * 32);
    }
}
