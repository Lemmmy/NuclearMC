package net.teamdentro.nuclearmc;

import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.command.CommandSender;
import net.teamdentro.nuclearmc.packets.SPacket07SpawnPlayer;
import net.teamdentro.nuclearmc.packets.SPacket08Teleport;
import net.teamdentro.nuclearmc.packets.SPacket0CDespawnPlayer;
import net.teamdentro.nuclearmc.packets.SPacket0DChatMessage;
import net.teamdentro.nuclearmc.util.Position;
import org.luaj.vm2.LuaValue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class User implements CommandSender {
    private InetSocketAddress sender;
    private int port;
    private String username;
    private Channel socket;
    private byte playerID;
    private Position pos;
    private boolean op = false;

    private Level level;

    private Map<String, Object> properties = new HashMap<>();

    /**
     * Instantiates a new user
     *
     * @param username Their username
     * @param sender   The InetSocketAddress of the user
     * @param port     The port they're connected from
     * @param socket   The socket their computer is plugged into. You know, so we know if they experience freedom or not.
     */
    public User(String username, InetSocketAddress sender, int port, Channel socket) {
        this.sender = sender;
        this.port = port;
        this.socket = socket;
        this.username = username;
    }

    /**
     * Despawns a player.
     */
    public void disconnect() {
        SPacket0CDespawnPlayer packet = new SPacket0CDespawnPlayer(Server.instance, this);
        packet.setPlayerID(getPlayerID());
        Server.instance.broadcast(packet, false, getLevel());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }

        User user = (User) obj;
        return user.getUsername().equals(username) && user.getPlayerID() == playerID;
    }


    /**
     * Gets whether the user is a server operator.
     *
     * @return Whether the user is a server operator.
     */
    public boolean isOp() {
        return op;
    }

    /**
     * Gets the user's username
     *
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get's the user's InetSocketAddress
     *
     * @return The user's socket address
     */
    public InetSocketAddress getAddress() {
        return sender;
    }

    /**
     * Get's the users channel
     *
     * @return The user's channel
     */
    public Channel getChannel() {
        return socket;
    }

    /**
     * Get's the port the user is connected from
     *
     * @return The port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sends a chat message to the user
     *
     * @param message The message to send
     */
    public void sendMessage(String message) {
        sendMessage(message, Byte.MAX_VALUE);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    /**
     * Sends a chat message to the user
     *
     * @param message  The message to send
     * @param playerID Some bullshit
     */
    public void sendMessage(String message, byte playerID) {
        SPacket0DChatMessage msg = new SPacket0DChatMessage(Server.instance, this);
        msg.setMessage(message);
        msg.setPlayerID(playerID);
        msg.setRecipient(this);
        try {
            msg.send();
        } catch (java.io.IOException e) {
        }
    }

    /**
     * Sets this user's position
     *
     * @param pos      The new position
     * @param teleport Whether this is a teleport or not
     */
    public void setPosition(Position pos, boolean teleport) {
        this.pos = pos;

        SPacket08Teleport packet = new SPacket08Teleport(Server.instance, this);
        packet.setPos(pos);
        packet.setPlayer(getPlayerID());
        Server.instance.broadcast(packet, false, getLevel());

        if (teleport) {
            packet = new SPacket08Teleport(Server.instance, this);
            packet.setPos(pos);
            packet.setPlayer((byte) -1);
            try {
                packet.send();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Gets the user's position
     *
     * @return The user's position
     */
    public Position getPosition() {
        return pos;
    }

    /**
     * Gets the user's PlayerID
     *
     * @return The user's ID
     */
    public byte getPlayerID() {
        return playerID;
    }

    /**
     * Sets the user's PlayerID
     *
     * @param playerID The new ID
     */
    public void setPlayerID(byte playerID) {
        this.playerID = playerID;
    }

    /**
     * Gets the level the user is currently on
     *
     * @return The level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Sets the level the user is currently on, spawn other players and this user in that level.
     *
     * @param level The new level
     */
    public void setLevel(Level level) {
        try {
            if (getLevel() != null) {
                SPacket0CDespawnPlayer packet = new SPacket0CDespawnPlayer(Server.instance, this);
                packet.setPlayerID(getPlayerID());
                Server.instance.broadcast(packet, false, getLevel());
            }

            level.sendToUser(Server.instance, this);

            this.level = level;

            Position spawnPos = new Position((short) (getLevel().getSpawnX() * 32),
                    (short) (getLevel().getSpawnY() * 32 + 51),
                    (short) (getLevel().getSpawnZ() * 32),
                    (byte) 0, (byte) 0);

            SPacket07SpawnPlayer spawn = new SPacket07SpawnPlayer(Server.instance, this);
            spawn.setPos(spawnPos);
            spawn.setPlayerID(getPlayerID());
            spawn.setName(getUsername());
            Server.instance.broadcast(spawn, false, getLevel());

            SPacket08Teleport teleport = new SPacket08Teleport(Server.instance, this);
            teleport.setPos(spawnPos);
            teleport.setPlayer((byte) -1);
            teleport.send();

            for (User u : getLevel().getUsers()) {
                if (u.getPlayerID() == getPlayerID()) continue;
                SPacket07SpawnPlayer s = new SPacket07SpawnPlayer(Server.instance, this);
                s.setPos(u.getPosition());
                s.setPlayerID(u.getPlayerID());
                s.setName(u.getUsername());
                s.send();
            }
        } catch (IOException ignored) {
        }
    }

    // excuse me intellij, this is fucking used
    public void setProperty(String property, Object value) {
        properties.put(property, value);
    }

    public Object getProperty(String property) {
        return properties.get(property);
    }
}
