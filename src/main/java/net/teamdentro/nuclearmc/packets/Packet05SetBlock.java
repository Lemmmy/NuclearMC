package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.Blocks;
import net.teamdentro.nuclearmc.Server;

public class Packet05SetBlock extends Packet {
    public Packet05SetBlock(Server server, Channel client, ByteBuf data) {
        super(server, client, data);
    }

    @Override
    public byte getID() {
        return 0x05;
    }

    @Override
    public void handle() {
        short posx = data.readShort();
        short posy = data.readShort();
        short posz = data.readShort();
        byte mode = data.readByte();
        byte block = data.readByte();

        if (mode == (byte) 0x00)
            getUser().getLevel().setBlockLOUDLY(posx, posy, posz, Blocks.AIR);
        else
            getUser().getLevel().setBlockLOUDLY(posx, posy, posz, Blocks.values()[block]);
    }
}
