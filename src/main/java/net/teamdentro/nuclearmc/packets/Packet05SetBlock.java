package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.Blocks;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.event.EventSetBlock;

import java.io.IOException;

public class Packet05SetBlock extends Packet {
    public Packet05SetBlock(Server server, Channel client, ByteBuf data) {
        super(server, client, data);
    }

    @Override
    public byte getID() {
        return 0x05;
    }

    @Override
    public void handle() throws IOException {
        short posx = data.readShort();
        short posy = data.readShort();
        short posz = data.readShort();
        byte mode = data.readByte();
        byte block = data.readByte();

        Blocks newBlock = Blocks.values()[block];
        Blocks currentBlock = getUser().getLevel().getBlock(posx, posy, posz);

        EventSetBlock event = new EventSetBlock();
        event.setUser(getUser());
        event.setPosx(posx);
        event.setPosy(posy);
        event.setPosz(posz);
        event.setMode(mode);
        event.setBlock(mode == 0 ? Blocks.AIR : newBlock);
        event.setOldBlock(currentBlock);
        event.invoke();

        User user = getUser();

        if (event.isCancelled() || (mode == 1 && newBlock == Blocks.BEDROCK && !user.isOp())) {
            SPacket06SetBlock setblock = new SPacket06SetBlock(server, getUser());
            setblock.setX(posx);
            setblock.setY(posy);
            setblock.setZ(posz);
            setblock.setBlock(currentBlock.getId());
            setblock.send();
            return;
        }

        if (mode == (byte)0) {
            user.getLevel().setBlockNotify(posx, posy, posz, Blocks.AIR);
        } else {
            user.getLevel().setBlockNotify(posx, posy, posz, Blocks.values()[block]);
        }
    }
}
