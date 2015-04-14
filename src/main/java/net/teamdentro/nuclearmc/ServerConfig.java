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
        for (int i = 1; i < config.length() + 1; i++) {
            LuaTable t = config.get(i).checktable();
            if (!t.get(1).tojstring().equalsIgnoreCase(key)) continue;

            return t.get(2) != null ? t.get(2).tojstring() : def;
        }
        return def;
    }

    public int getInt(String key, int def) {
        for (int i = 1; i < config.length() + 1; i++) {
            LuaTable t = config.get(i).checktable();
            if (!t.get(1).tojstring().equalsIgnoreCase(key)) continue;

            return t.get(2) != null && t.get(2).isint() ? t.get(2).toint() : def;
        }
        return def;
    }

    public boolean getBoolean(String key, boolean def) {
        for (int i = 1; i < config.length() + 1; i++) {
            LuaTable t = config.get(i).checktable();
            if (!t.get(1).tojstring().equalsIgnoreCase(key)) continue;

            return t.get(2) != null && t.get(2).isboolean() ? t.get(2).isboolean() : def;
        }
        return def;
    }

    public LuaTable getConfig() {
        return config;
    }
}
