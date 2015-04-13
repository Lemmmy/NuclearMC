package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

/**
 * Created by Lignum on 12/04/2015.
 */
public class SPacket02LevelInitialise extends ServerPacket {
    public SPacket02LevelInitialise(Server server, User client) {
        super(server, client);
    }

    @Override
    public byte getID() {
        return 0x02;
    }

    @Override
    public void send() {
        write(getID());
        flush();
    }
}
