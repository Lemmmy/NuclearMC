package net.teamdentro.nuclearmc.packets;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Lignum on 12/04/2015.
 */
public class SPacket03LevelData extends ServerPacket {
    public SPacket03LevelData(Server server, User client) {
        super(server, client);
    }

    private byte[] blocks;

    public byte[] getBlocks() {
        return blocks;
    }

    public void setBlocks(byte[] blocks) {
        this.blocks = blocks;
    }

    private int completeness;

    public int getCompleteness() {
        return completeness;
    }

    public void setCompleteness(int completeness) {
        this.completeness = completeness;
    }

    @Override
    public byte getID() {
        return 0x03;
    }

    @Override
    public void send() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            DataOutputStream dos = new DataOutputStream(gzip);

            dos.writeInt(blocks.length);
            dos.write(blocks);

            dos.close();
            gzip.close();
            bos.close();

            byte[] data = bos.toByteArray();
            float chunks = data.length / 1024;
            float sent = 0;

            for (int i = 0; i < data.length; i += 1024) {
                byte[] chunk = new byte[1024];

                short length = 1024;
                if (data.length - i < length) {
                    length = (short) (data.length - i);
                }

                System.arraycopy(data, i, chunk, 0, length);

                write(getID());
                write(length);
                write(chunk);
                write((int) (sent / chunks) * 255);

                sent++;
            }

            flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
