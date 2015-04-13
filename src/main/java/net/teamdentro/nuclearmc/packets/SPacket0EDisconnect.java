package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

public class SPacket0EDisconnect extends ServerPacket {
    private String reason;

    public SPacket0EDisconnect(Server server, User target) {
        super(server, target);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public byte getID() {
        return 0x0E;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        writeString(reason);

        NuclearMC.getLogger().info("Disconnected " + client.getUsername() + " [" + client.getAddress().toString() + ":" + client.getPort() + "] for reason \"" + reason + "\"");

        flush();
    }
}