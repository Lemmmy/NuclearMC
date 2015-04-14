package net.teamdentro.nuclearmc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.teamdentro.nuclearmc.packets.*;

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
        registerPacket(Packet08Teleport.class);
    }

    private ServerConfig config;
    private ServerWorkerPool workerPool;
    private String salt;
    private boolean running;
    private ChannelFuture f;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private List<User> users = new ArrayList<>();
    private int heartbeatInterval;
    private String serverName;
    private String motd;
    private byte lastPlayerID = Byte.MIN_VALUE;
    private Level level;
    private float heartbeatTimer;
    private String serverURL = "";
    public Server() {
        running = false;
        config = new ServerConfig();

        setupLogFiles();

        workerPool = new ServerWorkerPool(this, config.getInt("MaxWorkers", 1));

        heartbeatInterval = (int) (heartbeatTimer = config.getInt("HeartbeatInterval", 45));
        serverName = config.getValue("Name", "My NuclearMC Server");
        motd = config.getValue("MOTD", "Welcome to my NuclearMC Server!");

        level = new Level();
    }

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

    public static String generateSalt() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();

        char[] buf = new char[16];

        for (int i = 0; i < 16; i++) {
            buf[i] = chars.charAt(rand.nextInt(chars.length()));
        }
        return new String(buf);
    }

    public boolean isRunning() {
        return running;
    }

    public ServerWorkerPool getWorkerPool() {
        return workerPool;
    }

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

    public User[] getOnlineUsers() {
        return users.toArray(new User[0]);
    }

    public void addUser(User user) {
        users.add(user);
    }

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

    public void kickPlayer(String player, String reason) {
        for (User user : users) {
            if (user.getUsername().equals(player)) {
                kickUser(user, reason);
            }
        }
    }

    public byte makeUniquePlayerID() {
        return ++lastPlayerID;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getServerName() {
        return serverName;
    }

    public String getMotd() {
        return motd;
    }

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

        for (int i = 0; i < users.size(); ++i) {
            User user = users.get(i);
            if (!user.getChannel().isOpen()) {
                users.remove(i);
                NuclearMC.getLogger().info(user.getUsername() + " has disconnected.");
            }
        }
    }

    /**
     * Broadcast a packet to all users on the server
     * @param packet The packet to send
     * @param includeOriginal Should the packet be sent to the original recipient?
     */
    public void broadcast(ServerPacket packet, boolean includeOriginal) {
        User originalUser = packet.getRecipient();

        for (User user : users) {
            if (!includeOriginal && user.equals(originalUser))
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
     * @param msg The chat message to send
     */
    public void broadcastMessage(String msg) {
        for (User user : users) {
            user.sendMessage(msg);
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

    public void closeServer() {
        NuclearMC.getLogger().info("Server closing down");

        try {
            NuclearMC.getLogger().info("Closing Netty");

            f.channel().closeFuture().sync();

            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        } catch (InterruptedException e) {
            NuclearMC.getLogger().info("Problem closing Netty");
            e.printStackTrace();
        }
    }

    public ServerConfig getServerConfig() {
        return config;
    }
}
