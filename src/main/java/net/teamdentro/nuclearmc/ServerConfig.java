package net.teamdentro.nuclearmc;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

public class ServerConfig {
	private File configFile;
	private LuaTable config;
	
	public ServerConfig() {
		props = new HashMap<>();
		configFile = new File("config.lua");
		
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();

				InputStream defaultFile = NuclearMC.class.getResourceAsStream("config.default.lua");
				Files.copy(defaultFile, configFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		LuaValue lua = JsePlatform.standardGlobals();
		config = lua.get("dofile").call(
				LuaValue.valueOf("./config.default.lua")).checktable();
	}
	
	public String getValue(String key, String def) {
		return config.get(key) != null ? config.get(key).tojstring() : def;
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
