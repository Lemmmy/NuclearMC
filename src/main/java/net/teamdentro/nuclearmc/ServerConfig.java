package net.teamdentro.nuclearmc;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ServerConfig {
    private File configFile;
    private LuaTable config;

    public ServerConfig() {
        loadConfig();
    }

    public void loadConfig() {
        config = null;
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
        return config.get(key) != null && config.get(key).isboolean() ? config.get(key).toboolean() : def;
    }

    public LuaTable getConfig() {
        return config;
    }
}
