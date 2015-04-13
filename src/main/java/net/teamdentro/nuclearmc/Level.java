package net.teamdentro.nuclearmc;

import net.teamdentro.nuclearmc.packets.SPacket02LevelInitialise;
import net.teamdentro.nuclearmc.packets.SPacket03LevelData;
import net.teamdentro.nuclearmc.packets.SPacket04LevelFinalise;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

public class Level {
    private byte[] blocks;
    private int width, height, depth;
    private int spawnX, spawnY, spawnZ;
    private Random rand = new Random();

    public Level() {
        width = 128;
        height = 64;
        depth = 128;

        blocks = new byte[width * height * depth];

        generateFlatland();
    }

    public int getBlock(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < width && y < depth && z < height ? blocks[getIndex(x, y, z)] : 0;
    }

    public void setBlock(int x, int y, int z, int id) {
        blocks[getIndex(x, y, z)] = (byte) id;
    }

    public int getIndex(int x, int y, int z) {
        return (y * depth + z) * width + x;
    }

    public void generateFlatland() {
        for (int y = 0; y < 8; ++y) {
            for (int z = 0; z < depth; ++z) {
                for (int x = 0; x < width; ++x) {
                    setBlock(x, y, z, rand.nextInt(49));
                }
            }
        }

        spawnX = 8;
        spawnY = 10;
        spawnZ = 8;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public void setSpawnZ(int spawnZ) {
        this.spawnZ = spawnZ;
    }

    public void sendToUser(Server server, User user) {
        SPacket02LevelInitialise init = new SPacket02LevelInitialise(server, user);
        try {
            init.send();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

                SPacket03LevelData packet = new SPacket03LevelData(server, user);
                packet.setLength(length);
                packet.setChunk(chunk);
                packet.setProgress((int) ((sent / chunks) * 255));
                packet.send();

                sent++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SPacket04LevelFinalise finalise = new SPacket04LevelFinalise(server, user);
        finalise.setWidth(width);
        finalise.setHeight(height);
        finalise.setDepth(depth);
        try {
            finalise.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
