package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.Blocks;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.event.EventPostSetBlock;
import net.teamdentro.nuclearmc.event.EventPreSetBlock;

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

        EventPreSetBlock event = new EventPreSetBlock();
        event.setUser(getUser());
        event.setPosx(posx);
        event.setPosy(posy);
        event.setPosz(posz);
        event.setMode(mode);
        event.setBlock(mode == 0 ? Blocks.AIR : Blocks.values()[block]);
        event.invoke();

        if (event.isCancelled()) return;

        if (mode == (byte) 0x00)
            getUser().getLevel().setBlockNotify(posx, posy, posz, Blocks.AIR);
        else
            getUser().getLevel().setBlockNotify(posx, posy, posz, Blocks.values()[block]);

        EventPostSetBlock eventPost = new EventPostSetBlock();
        eventPost.setUser(getUser());
        eventPost.setPosx(posx);
        eventPost.setPosy(posy);
        eventPost.setPosz(posz);
        eventPost.setMode(mode);
        eventPost.setBlock(mode == 0 ? Blocks.AIR : Blocks.values()[block]);
        eventPost.invoke();
    }
}
