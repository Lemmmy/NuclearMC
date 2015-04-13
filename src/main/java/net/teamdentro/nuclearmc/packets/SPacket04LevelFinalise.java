package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

/**
 * Created by Lignum on 12/04/2015.
 */
public class SPacket04LevelFinalise extends ServerPacket {
    public SPacket04LevelFinalise(Server server, User client) {
        super(server, client);
    }

    private int width;
    private int height;
    private int depth;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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
