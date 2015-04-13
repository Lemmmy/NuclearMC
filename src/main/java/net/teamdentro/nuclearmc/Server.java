package net.teamdentro.nuclearmc;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;

import javax.net.ssl.HttpsURLConnection;

import net.teamdentro.nuclearmc.packets.*;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Server extends SimpleChannelHandler implements Runnable {
	//private DatagramSocket socket;
	private ServerConfig config;
	private ServerWorkerPool workerPool;

	private String salt;

	public static int BUFFER_SIZE = 2048;

	private boolean running;

	protected static Map<Byte, Class<? extends IPacket>> packetRegistry = new HashMap<>();

    public static Server instance;

	public static void registerPacket(Class<? extends Packet> packet) {
		Packet p;
		try {
			p = packet.getDeclaredConstructor(Server.class, Socket.class).newInstance(null, null);
			byte id = p.getID();
			packetRegistry.put(id, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static void registerServerPacket(Class<? extends ServerPacket> packet) {
        ServerPacket p;
        try {
            p = packet.getDeclaredConstructor(Server.class, User.class).newInstance(null, null);
            byte id = p.getID();
            packetRegistry.put(id, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public boolean isRunning() {
		return running;
	}

    private static Random rand = new Random();

    public static String generateSalt() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random rand = new Random();

		char[] buf = new char[16];

		for (int i=0; i<16; i++) {
			buf[i] = chars.charAt(rand.nextInt(chars.length()));
		}
		return new String(buf);
    }

	public void heartbeat(final HeartbeatCallback callback) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
                // Minecraft Heartbeat
                {
                    HttpsURLConnection connection = null;
                    try {
                        URL url = new URL("https://minecraft.net/heartbeat.jsp"
                                + "?port=" + config.getInt("ServerPort", 25565)
                                + "&max=" + config.getInt("MaxPlayers", 32)
                                + "&name=" + URLEncoder.encode(serverName, "UTF-8")
                                + "&public=" + config.getBool("Public", false)
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
                    HttpURLConnection connection = null;

                    try {
                        URL url = new URL("http://direct.worldofminecraft.com/hb.php"
                                + "?port=" + config.getInt("ServerPort", 25565)
                                + "&max=" + config.getInt("MaxPlayers", 32)
                                + "&name=" + URLEncoder.encode(serverName, "UTF-8")
                                + "&public=" + config.getBool("Public", false)
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

			boolean fancy = config.getBool("MakeFancyLogs", false);
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

    private List<User> users = new ArrayList<>();

	private int heartbeatInterval;
	private String serverName;
	private String motd;

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
        dc.send();

        user.getChannel().close();
    }

    public void kickPlayer(String player, String reason) {
        for (User user : users) {
            if (user.getUsername().equals(player)) {
                kickUser(user, reason);
            }
        }
    }

    static {
        registerPacket(Packet0Connect.class);
        registerPacket(Packet0DMessage.class);
        registerPacket(Packet08Teleport.class);
    }

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

    private byte lastPlayerID = Byte.MIN_VALUE;

    public byte makeUniquePlayerID() {
        return ++lastPlayerID;
    }

    private Level level;

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

	private float heartbeatTimer;
    private String serverURL = "";

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

    public void broadcast(ServerPacket packet) {
        User originalUser = packet.getRecipient();

        for (User user : users) {
            packet.setRecipient(user);
            packet.send();
        }

        packet.setRecipient(originalUser);
    }

    public void broadcastMessage(String msg) {
        for (User user : users) {
            user.sendMessage(msg);
        }
    }

	@Override
	public void run() {
		NuclearMC.getLogger().info("Starting server on port " + config.getInt("ServerPort", 25565));
		running = true;

        ChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()
        );

        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(Server.this);
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        bootstrap.bind(new InetSocketAddress(config.getValue("ServerIP", "0.0.0.0"), config.getInt("ServerPort", 25565)));
        System.out.println("Server Started!");

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
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();

        while(buf.readable()) {
            workerPool.processPacket(buf.readByte(), buf, e.getChannel());
        }
    }
}
