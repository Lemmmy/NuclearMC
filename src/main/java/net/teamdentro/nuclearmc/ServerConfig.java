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
		configFile = new File("config.lua");
		
		if (!configFile.exists()) {
			try {
				InputStream defaultFile = getClass().getClassLoader().getResourceAsStream("config.default.lua");
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
		return config.get(key) != null && config.get(key).isint() ? config.get(key).toint() : def;
	}
	
	public boolean getBoolean(String key, boolean def) {
		return config.get(key) != null && config.get(key).isboolean()  ? config.get(key).toboolean() : def;
	}

	public int getType(String key) {
		return config.get(key) != null ? config.get(key).type() : LuaValue.TNIL;
	}
}
