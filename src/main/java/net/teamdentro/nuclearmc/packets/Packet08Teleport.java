package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.Server;

public class Packet08Teleport extends Packet {
    private byte player;
    private short posx;
    private short posy;
    private short posz;
    private byte yaw;
    private byte pitch;
    public Packet08Teleport(Server server, Channel client, ByteBuf data) {
        super(server, client, data);
    }

    public byte getPlayer() {
        return player;
    }

    public void setPlayer(byte player) {
        this.player = player;
    }

    public short getPosx() {
        return posx;
    }

    public void setPosx(short posx) {
        this.posx = posx;
    }

    public short getPosy() {
        return posy;
    }

    public void setPosy(short posy) {
        this.posy = posy;
    }

    public short getPosz() {
        return posz;
    }

    public void setPosz(short posz) {
        this.posz = posz;
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

    @Override
    public byte getID() {
        return 0x08;
    }

    @Override
    public void handle() {
        player = data.readByte();
        posx = data.readShort();
        posy = data.readShort();
        posz = data.readShort();
        yaw = data.readByte();
        pitch = data.readByte();
    }
}
