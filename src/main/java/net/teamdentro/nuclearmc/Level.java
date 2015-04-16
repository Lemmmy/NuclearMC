package net.teamdentro.nuclearmc;

import net.teamdentro.nuclearmc.packets.SPacket02LevelInitialise;
import net.teamdentro.nuclearmc.packets.SPacket03LevelData;
import net.teamdentro.nuclearmc.packets.SPacket04LevelFinalise;
import net.teamdentro.nuclearmc.packets.SPacket06SetBlock;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

public class Level {
    private byte[] blocks;
    private int width, height, depth;
    private int spawnX, spawnY, spawnZ;
    private Random rand = new Random();
    private String name;

    /**
     * Create a new level with the specified dimensions
     *
     * @param w The width of the level (x axis). Must be a power of two
     * @param h The height of the level (y axis). Must be a power of two.
     * @param d The depth of the level (z axis). Must be a power of two.
     */
    public Level(String name, int w, int h, int d) {
        this.name = name;
        width = w;
        height = h;
        depth = d;

        blocks = new byte[width * height * depth];

        generateFlatland();
    }

    /**
     * Generates a flat level, with the grass at sea level (height / 2) and the spawn point at the centre of the map
     */
    public void generateFlatland() {
        for (int y = 0; y < height / 2; ++y) {
            for (int z = 0; z < depth; ++z) {
                for (int x = 0; x < width; ++x) {
                    setBlock(x, y, z, y == height / 2 - 1 ? Blocks.GRASS : Blocks.DIRT);
                }
            }
        }

        spawnX = width / 2;
        spawnY = height / 2 + 1;
        spawnZ = depth / 2;
    }

    /**
     * Sets a block at the specified coordinates
     *
     * @param x     The X position
     * @param y     The Y position
     * @param z     The Z position
     * @param block The Blocks block to set at the specified coordinates
     * @see #setBlockNotify(int, int, int, Blocks)
     */
    public void setBlock(int x, int y, int z, Blocks block) {
        blocks[getIndex(x, y, z)] = (byte) block.getId();
    }

    /**
     * Gets the index of the specified coordinates
     *
     * @param x The X position
     * @param y The Y position
     * @param z The Z position
     * @return The index of the specified coordinates
     */
    public int getIndex(int x, int y, int z) {
        return (y * depth + z) * width + x;
    }

    /**
     * Gets the block at the specified coordinates
     *
     * @param x The X position
     * @param y The Y position
     * @param z The Z position
     * @return The Blocks block at the specified coordinates
     */
    public Blocks getBlock(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < width && y < depth && z < height ?
                Blocks.values()[blocks[getIndex(x, y, z)]] : Blocks.AIR;
    }

    /**
     * Gets the X coordinate of the level's spawn point
     *
     * @return The X coordinate of the level's spawn point
     */
    public int getSpawnX() {
        return spawnX;
    }

    /**
     * Sets the X coordinate of the level's spawn point
     *
     * @param spawnX The X coordinate of the level's spawn point
     */
    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    /**
     * Gets the Y coordinate of the level's spawn point
     *
     * @return The Y coordinate of the level's spawn point
     */
    public int getSpawnY() {
        return spawnY;
    }

    /**
     * Sets the X coordinate of the level's spawn point
     *
     * @param spawnY The Y coordinate of the level's spawn point
     */
    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    /**
     * Gets the Z coordinate of the level's spawn point
     *
     * @return The Z coordinate of the level's spawn point
     */
    public int getSpawnZ() {
        return spawnZ;
    }

    /**
     * Sets the Z coordinate of the level's spawn point
     *
     * @param spawnZ The Z coordinate of the level's spawn point
     */
    public void setSpawnZ(int spawnZ) {
        this.spawnZ = spawnZ;
    }

    /**
     * Gets a list of users in this level
     *
     * @return A list of users in this level
     */
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();

        for (User user : Server.instance.getOnlineUsers()) {
            if (user.getLevel().equals(this)) {
                users.add(user);
            }
        }

        return users;
    }

    /**
     * Sends the level to a user
     *
     * @param server The server instance
     * @param user   The user to send the level to
     */
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

    /**
     * LOUDLY SETS A BLOCK (sets a block and sends a packet to all users in the level)
     *
     * @param x     The X position
     * @param y     The Y position
     * @param z     The Z position
     * @param block The Blocks block to set at the specified coordinates
     * @see #setBlock(int, int, int, Blocks)
     */
    public void setBlockNotify(int x, int y, int z, Blocks block) {
        setBlock(x, y, z, block);

        SPacket06SetBlock packet = new SPacket06SetBlock(Server.instance, null);
        packet.setX((short) x);
        packet.setY((short) y);
        packet.setZ((short) z);
        packet.setBlock(block.getId());

        Server.instance.broadcast(packet, true, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
