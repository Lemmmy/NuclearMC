package net.teamdentro.nuclearmc;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public class Server implements Runnable {
	private DatagramSocket socket;
	private ServerConfig config;
	private ServerWorkerPool workerPool;
	
	public static int BUFFER_SIZE = 2048;
	
	private boolean running;
	
	public boolean isRunning() {
		return running;
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
	
	public Server() {
		running = false;
		config = new ServerConfig();
		
		setupLogFiles();
		
		workerPool = new ServerWorkerPool(config.getInt("MaxWorkers", 1));
	}
	
	@Override
	public void run() {
		NuclearMC.getLogger().info("Starting server...");
		running = true;
		
		try {
			socket = new DatagramSocket(config.getInt("ServerPort", 25565), InetAddress.getByName(config.getValue("ServerIP", "127.0.0.1")));
		} catch (IOException e) {
			NuclearMC.getLogger().log(Level.SEVERE, "Error while binding socket!", e);
		}
		
		while (running) {
			try {
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(/*&*/buffer, buffer.length);
				socket.receive(packet);
				
				workerPool.processPacket(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
