package net.teamdentro.nuclearmc;

import net.teamdentro.nuclearmc.packets.SPacket02LevelInitialise;
import net.teamdentro.nuclearmc.packets.SPacket03LevelData;
import net.teamdentro.nuclearmc.packets.SPacket04LevelFinalise;

import java.util.Random;

/**
 * Created by Lignum on 12/04/2015.
 */
public class Level {
    private byte[] blocks;
    private int width, height, depth;
    private int spawnX, spawnY, spawnZ;

    public Level() {
        width = 128;
        height = 64;
        depth = 128;

        blocks = new byte[width * height * depth];

        generateFlatland();
    }

    public int getBlock(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < width && y < depth && z < height ? blocks[getIndex(x, y, z)] & 255 : 0;
    }

    public void setBlock(int x, int y, int z, int id) {
        blocks[getIndex(x, y, z)] = (byte)id;
    }

    public int getIndex(int x, int y, int z) {
        return (y * height + z) * width + x;
    }

    private Random rand = new Random();

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
        init.send();

        SPacket03LevelData data = new SPacket03LevelData(server, user);
        data.setBlocks(blocks);
        data.send();

        SPacket04LevelFinalise finalise = new SPacket04LevelFinalise(server, user);
        finalise.setWidth(width);
        finalise.setHeight(height);
        finalise.setDepth(depth);
        finalise.send();
    }
}
