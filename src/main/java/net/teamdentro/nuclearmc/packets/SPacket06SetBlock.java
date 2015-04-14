package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.util.Position;

import java.io.IOException;

public class SPacket06SetBlock extends ServerPacket {
    public SPacket06SetBlock(Server server, User client) {
        super(server, client);
    }

    @Override
    public byte getID() {
        return 0x06;
    }

    @Override
    public void send() throws IOException {
        flush();
    }
}
