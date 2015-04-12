package net.teamdentro.nuclearmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerConfig {
	private File configFile;
	private Properties props;
	
	public ServerConfig() {
		props = new Properties();
		configFile = new File("config.txt");
		
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				
				props.setProperty("ServerPort", 		"25565");
				props.setProperty("ServerIP", 			"127.0.0.1");
				
				props.setProperty("MaxPlayers", 		"32");
				props.setProperty("Name", 				"My NuclearMC Server");
				
				props.setProperty("HeartbeatInterval", 	"45");
				props.setProperty("MaxWorkers", 		"1");
				
				props.setProperty("MakeFancyLogs", 		"false");
				
				try (FileOutputStream fos = new FileOutputStream(configFile)) {
					props.store(fos, null);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try (FileInputStream fis = new FileInputStream(configFile)) {
			props.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getValue(String key, String def) {
		return props.getProperty(key, def);
	}
	
	public int getInt(String key, int def) {
		try {
			return Integer.parseInt(props.getProperty(key, String.valueOf(def)));
		} catch (NumberFormatException e) {
			return def;
		}
	}
	
	public boolean getBool(String key, boolean def) {
		switch (props.getProperty(key, String.valueOf(def)).toLowerCase()) {
		case "true":
			return true;
			
		case "false":
			return false;
			
		default:
			return def;
		}
	}
}
