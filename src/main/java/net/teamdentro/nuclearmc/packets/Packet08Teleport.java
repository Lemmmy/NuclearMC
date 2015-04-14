package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.util.Position;

public class Packet08Teleport extends Packet {
    public Packet08Teleport(Server server, Channel client, ByteBuf data) {
        super(server, client, data);
    }

    @Override
    public byte getID() {
        return 0x08;
    }

    @Override
    public void handle() {
        byte player = data.readByte(); // player
        if (player != (byte) 255) return;
        short posx = data.readShort();
        short posy = data.readShort();
        short posz = data.readShort();
        byte yaw = data.readByte();
        byte pitch = data.readByte();

        Position pos = new Position(posx, posy, posz, yaw, pitch);

        getUser().setPos(pos, false);
    }
}
