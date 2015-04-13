package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.IOException;

public class SPacket03LevelData extends ServerPacket {
    private short length;
    private byte[] chunk;
    private int progress;
    public SPacket03LevelData(Server server, User client) {
        super(server, client);
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public void setChunk(byte[] chunk) {
        this.chunk = chunk;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public byte getID() {
        return 0x03;
    }

    @Override
    public void send() throws IOException {
        getWriter().writeByte(getID());
        getWriter().writeShort(getLength());
        getWriter().write(getChunk());
        getWriter().writeByte((byte) getProgress());

        flush(/*your dad*/);
    }
}
