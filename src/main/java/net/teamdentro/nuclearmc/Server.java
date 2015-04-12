package net.teamdentro.nuclearmc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;

import net.teamdentro.nuclearmc.packets.Packet;
import net.teamdentro.nuclearmc.packets.Packet0Connect;

public class Server implements Runnable {
	private DatagramSocket socket;
	private ServerConfig config;
	private ServerWorkerPool workerPool;

	private String salt;

	public static int BUFFER_SIZE = 2048;

	private boolean running;

	protected static Map<Byte, Class<? extends Packet>> packetRegistry = new HashMap<>();

	public static void registerPacket(Class<? extends Packet> packet) {
		Packet p;
		try {
			p = packet.getDeclaredConstructor(Server.class, DataInputStream.class).newInstance(null, null);
			byte id = p.getID();
			packetRegistry.put(id, packet);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
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

					try (	InputStream stream = connection.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(stream)) ) {
						String serverURL = reader.readLine();
						callback.run(serverURL);
					}
				} catch (IOException e) {
					e.printStackTrace();
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
			fhandler.setLevel(Level.CONFIG);

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

	private int heartbeatInterval;
	private String serverName;
	private String motd;

	public Server() {
		registerPacket(Packet0Connect.class);

		running = false;
		config = new ServerConfig();

		setupLogFiles();

		workerPool = new ServerWorkerPool(config.getInt("MaxWorkers", 1));

		heartbeatInterval = (int) (heartbeatTimer = config.getInt("HeartbeatInterval", 45));
		serverName = config.getValue("Name", "My NuclearMC Server");
		motd = config.getValue("MOTD", "Welcome to my NuclearMC Server!");
	}

	public void socketLoop() {
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket packet = new DatagramPacket(/*&*/buffer, buffer.length);
			socket.receive(packet);

            NuclearMC.getLogger().info("Got: " + new String(packet.getData()));

			workerPool.processPacket(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private float heartbeatTimer;
    private String serverURL = "";

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
		NuclearMC.getLogger().info("Starting server...");
		running = true;

		try {
			socket = new DatagramSocket(config.getInt("ServerPort", 25565), InetAddress.getByName(config.getValue("ServerIP", "0.0.0.0")));
		} catch (IOException e) {
			NuclearMC.getLogger().log(Level.SEVERE, "Error while binding socket!", e);
		}

		salt = generateSalt();

		Thread socketThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					socketLoop();
				}
			}
		});
		socketThread.start();

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
}
