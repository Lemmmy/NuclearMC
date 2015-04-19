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

    /**
     * Load the configuration file, or create it if it doesn't exist.
     */
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
                LuaValue.valueOf("config.default.lua")).checktable();
    }

    /**
     * Gets a boolean from the configuration file
     *
     * @param key The key of the boolean
     * @param def The default value if it is invalid or doesn't exist
     * @return The value or default value
     */
    public boolean getBoolean(String key, boolean def) {
        for (int i = 1; i < config.length() + 1; i++) {
            LuaTable t = config.get(i).checktable();
            if (!t.get(1).tojstring().equalsIgnoreCase(key)) continue;

            return t.get(2) != null && t.get(2).isboolean() ? t.get(2).isboolean() : def;
        }
        return def;
    }

    /**
     * Gets the configuration file
     *
     * @return The configuration file
     */
    public LuaTable getConfig() {
        return config;
    }

    /**
     * Gets an integer from the configuration file
     *
     * @param key The key of the integer
     * @param def The default value if it is invalid or doesn't exist
     * @return The value or default value
     */
    public int getInt(String key, int def) {
        for (int i = 1; i < config.length() + 1; i++) {
            LuaTable t = config.get(i).checktable();
            if (!t.get(1).tojstring().equalsIgnoreCase(key)) continue;

            return t.get(2) != null && t.get(2).isint() ? t.get(2).toint() : def;
        }
        return def;
    }

    /**
     * Gets a string from the configuration file
     *
     * @param key The key of the string
     * @param def The default value if it is invalid or doesn't exist
     * @return The value or default value
     */
    public String getValue(String key, String def) {
        for (int i = 1; i < config.length() + 1; i++) {
            LuaTable t = config.get(i).checktable();
            if (!t.get(1).tojstring().equalsIgnoreCase(key)) continue;

            return t.get(2) != null ? t.get(2).tojstring() : def;
        }
        return def;
    }
}
