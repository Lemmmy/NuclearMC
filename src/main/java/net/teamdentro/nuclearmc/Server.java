package net.teamdentro.nuclearmc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.teamdentro.nuclearmc.packets.*;
import net.teamdentro.nuclearmc.plugin.PluginManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.FileHandler;

public class Server implements Runnable {
    public static int BUFFER_SIZE = 2048;
    public static Server instance;
    protected static Map<Byte, Class<? extends IPacket>> packetRegistry = new HashMap<>();
    private static Random rand = new Random();

    static {
        registerPacket(Packet0Connect.class);
        registerPacket(Packet0DMessage.class);
        registerPacket(Packet05SetBlock.class);
        registerPacket(Packet08Teleport.class);
    }

    private ServerConfig config;
    private String salt;
    private boolean running;
    private ChannelFuture f;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private List<User> users = new ArrayList<>();
    private int heartbeatInterval;
    private String serverName;
    private String motd;
    private byte lastPlayerID = (byte) 0;
    private Map<String, Level> loadedLevels;
    private float heartbeatTimer;
    private String serverURL = "";
    private PluginManager pluginManager = new PluginManager();

    public Server() {
        running = false;
        config = new ServerConfig();

        setupLogFiles();

        heartbeatInterval = (int) (heartbeatTimer = config.getInt("HeartbeatInterval", 45));
        serverName = config.getValue("Name", "My NuclearMC Server");
        motd = config.getValue("MOTD", "Welcome to my NuclearMC Server!");

        loadedLevels = new HashMap<>();
        // if (!loadedLevels.containsKey(config.getValue("MainWorld")))
        // create it and load it, else load it
        loadedLevels.put(config.getValue("MainWorld", "main"), new Level("main", 64, 64, 64));
    }

    public void setupLogFiles() {
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            boolean fancy = config.getBoolean("MakeFancyLogs", false);
            NuclearMC.getLogger().config("MakeFancyLogs = " + fancy);

            FileHandler fhandler = new FileHandler("logs/server.log" + (fancy ? ".html" : ""));
            fhandler.setLevel(java.util.logging.Level.CONFIG);

            if (fancy) {
                fhandler.setFormatter(new FancyFormatter());
            } else {
                fhandler.setFormatter(new ConsoleFormatter());
            }

            NuclearMC.getLogger().addHandler(fhandler);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The server's plugin manager.
     */
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Register a packet for the server to recieve
     *
     * @param packet The class of the packet
     */
    public static void registerPacket(Class<? extends Packet> packet) {
        Packet p;
        try {
            p = packet.getDeclaredConstructor(Server.class, Channel.class, ByteBuf.class).newInstance(null, null, null);
            byte id = p.getID();
            packetRegistry.put(id, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @deprecated The IDs conflict so this function is never used
     */
    @Deprecated
    public static void registerServerPacket(Class<? extends ServerPacket> packet) {
        ServerPacket p;
        try {
            p = packet.getDeclaredConstructor(Server.class, User.class, ByteBuf.class).newInstance(null, null, null);
            byte id = p.getID();
            packetRegistry.put(id, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a user to the server
     *
     * @param user The user to add
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Generate a 16-character Base-62 string
     *
     * @return The generated salt
     */
    public static String generateSalt() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();

        char[] buf = new char[16];

        for (int i = 0; i < 16; i++) {
            buf[i] = chars.charAt(rand.nextInt(chars.length()));
        }
        return new String(buf);
    }

    /**
     * Broadcast a packet to all users on the server
     *
     * @param packet          The packet to send
     * @param includeOriginal Should the packet be sent to the original recipient?
     * @deprecated Most packets are world specific. In the event that you need to invoke it on all users, please
     * iterate over the levels instead.
     */
    @Deprecated
    public void broadcast(ServerPacket packet, boolean includeOriginal) {
        User originalUser = packet.getRecipient();

        for (User user : users) {
            if (!includeOriginal && user.equals(originalUser)) // temporary
                continue;

            packet.setRecipient(user);
            try {
                packet.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        packet.setRecipient(originalUser);
    }

    /**
     * Broadcast a packet to all users on a level
     *
     * @param packet          The packet to send
     * @param includeOriginal Should the packet be sent to the original recipient?
     * @param level           The level to send it to
     */
    public void broadcast(ServerPacket packet, boolean includeOriginal, Level level) {
        User originalUser = packet.getRecipient();

        for (User user : level.getUsers()) {
            if (!includeOriginal && user.equals(originalUser)) // temporary
                continue;

            packet.setRecipient(user);
            try {
                packet.send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        packet.setRecipient(originalUser);
    }

    /**
     * Broadcast a chat message to all users on the server
     *
     * @param msg The chat message to send
     */
    public void broadcastMessage(String msg) {
        NuclearMC.getLogger().info(msg);

        for (User user : users) {
            user.sendMessage(msg);
        }
    }

    /**
     * Beat the heart, smash it open, allow the blood flowing through the arteries to spill out, cause a mess
     * on the new carpet that the mailman just shat on so that you have to clean two people's messes
     * and a dead body.
     *
     * @param callback A callback to run when the process is complete.
     */
    public void heartbeat(final HeartbeatCallback callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Minecraft Heartbeat
                {
                    HttpsURLConnection connection;
                    try {
                        URL url = new URL("https://minecraft.net/heartbeat.jsp"
                                + "?port=" + config.getInt("ServerPort", 25565)
                                + "&max=" + config.getInt("MaxPlayers", 32)
                                + "&name=" + URLEncoder.encode(serverName, "UTF-8")
                                + "&public=" + config.getBoolean("Public", false)
                                + "&version=7"
                                + "&salt=" + salt
                                + "&users=0");
                        connection = (HttpsURLConnection) url.openConnection();

                        connection.setRequestMethod("GET");
                        connection.setDoInput(true);
                        connection.setDoOutput(false);
                        connection.setAllowUserInteraction(false);
                        connection.setRequestProperty("X-Clacks-Overhead", "GNU Terry Pratchett");

                        try (InputStream stream = connection.getInputStream();
                             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                            String serverURL = reader.readLine();
                            callback.run(serverURL);
                        }

                        connection.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                {
                    // WoM Heartbeat
                    HttpURLConnection connection;

                    try {
                        URL url = new URL("http://direct.worldofminecraft.com/hb.php"
                                + "?port=" + config.getInt("ServerPort", 25565)
                                + "&max=" + config.getInt("MaxPlayers", 32)
                                + "&name=" + URLEncoder.encode(serverName, "UTF-8")
                                + "&public=" + config.getBoolean("Public", false)
                                + "&version=7"
                                + "&salt=" + salt
                                + "&users=" + getOnlineUsers().length
                                + "&noforward=1");

                        connection = (HttpURLConnection) url.openConnection();

                        connection.setRequestMethod("GET");
                        connection.setDoInput(false);
                        connection.setDoOutput(false);
                        connection.setAllowUserInteraction(false);
                        connection.setRequestProperty("X-Clacks-Overhead", "GNU Terry Pratchett");
                        connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
                        connection.setUseCaches(false);

                        connection.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.run();
    }

    /**
     * Removes a user from the server
     *
     * @param user The user to remove
     */
    public void disconnectUser(User user) {
        users.remove(user);
    }

    /**
     * Removes a player from the server
     *
     * @param player The player to kick, by name
     */
    public void disconnectPlayer(String player) {
        for (User user : users) {
            if (user.getUsername().equals(player)) {
                users.remove(user);
            }
        }
    }

    /**
     * Gets a loaded level from its name
     *
     * @param levelName The level name
     * @return The level
     */
    public Level getLoadedLevel(String levelName) {
        return loadedLevels.get(levelName);
    }

    /**
     * Gets a list of loaded levels
     *
     * @return The list of loaded levels
     */
    public HashMap<String, Level> getLoadedLevels() {
        if (loadedLevels instanceof HashMap)
            return (HashMap<String, Level>) loadedLevels;
        else
            return null;
    }

    /**
     * Gets a list of online users
     *
     * @return The list of online users
     */
    public User[] getOnlineUsers() {
        return users.toArray(new User[0]);
    }

    /**
     * Gets the server's main level
     *
     * @return The main level
     */
    public Level getMainLevel() {
        return loadedLevels.get(config.getValue("MainWorld", "main"));
    }

    /**
     * Gets the message of the day
     *
     * @return The message of the day
     */
    public String getMotd() {
        return motd;
    }

    /**
     * Gets the server configuration
     *
     * @return The server configuration
     */
    public ServerConfig getServerConfig() {
        return config;
    }

    /**
     * Gets the server name
     *
     * @return The server name
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Is the server running?
     *
     * @return Is the server running?
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Kicks a player from the server
     *
     * @param player The player to kick, by name
     * @param reason The message to display to the kicked player, and in the console
     */
    public void kickPlayer(String player, String reason) {
        for (User user : users) {
            if (user.getUsername().equals(player)) {
                kickUser(user, reason);
            }
        }
    }

    /**
     * Kicks a user from the server
     *
     * @param user   The user to kick
     * @param reason The message to display to the kicked user, and in the console
     */
    public void kickUser(User user, String reason) {
        users.remove(user);

        SPacket0EDisconnect dc = new SPacket0EDisconnect(this, user);
        dc.setReason(reason);
        try {
            dc.send();
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.getChannel().close();
    }

    /**
     * Makes a unique player ID.
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * <p/>
     * I lied. It's currently not unique.
     * <p/>
     * Please revise.
     *
     * @return The fake fucking id
     */
    // TO-DO make this actually fucking unique
    public byte makeUniquePlayerID() {
        lastPlayerID++;
        return (byte) (lastPlayerID - 1);
    }

    /**
     * Updates the server
     *
     * @param dt time passed since last update
     */
    public void update(float dt) {
        heartbeatTimer += dt;
        if (heartbeatTimer >= heartbeatInterval) {
            heartbeatTimer = 0;
            heartbeat(new HeartbeatCallback() {
                @Override
                public void run(String url) {
                    if (!serverURL.equals(url)) {
                        serverURL = url;
                        NuclearMC.getLogger().info("Got Server URL " + url);
                    }
                }
            });
        }
    }


    @Override
    public void run() {
        String address = config.getValue("ServerIP", "0.0.0.0");
        int port = config.getInt("ServerPort", 25565);

        NuclearMC.getLogger().info("Starting server on port " + port);
        running = true;

        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            f = b.bind(address, port).sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        NuclearMC.getLogger().severe("Failed to bind to port");
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pluginManager.loadPlugins("plugins");

            salt = generateSalt();

            long lastTime = System.currentTimeMillis();

            while (running) {
                long now = System.currentTimeMillis();
                long dt = now - lastTime;

                update(dt / 1000.0f);

                lastTime = System.currentTimeMillis();

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            closeServer();
        }
    }

    /**
     * Kicks everyone out of the house and then bulldozes it.
     * <p/>
     * It seems rude at first, but it's actually pretty polite. You don't want to bulldoze the people
     * while they're inside of it, do you?
     */
    public void closeServer() {
        try {
            pluginManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NuclearMC.getLogger().info("Server closing down");

        for (int i = 0; i < users.size(); i++) {
            kickUser(users.get(i), config.getValue("ShutdownMessage", "Server is shutting down"));
        }
    }
}
