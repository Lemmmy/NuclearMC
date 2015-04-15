package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

public class SPacket04LevelFinalise extends ServerPacket {
    private int width;
    private int height;
    private int depth;

    public SPacket04LevelFinalise(Server server, User client) {
        super(server, client);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public byte getID() {
        return 0x04;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeShort((short) width);
        getWriter().writeShort((short) height);
        getWriter().writeShort((short) depth);
        flush();
    }
}
