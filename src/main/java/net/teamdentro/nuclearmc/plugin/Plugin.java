package net.teamdentro.nuclearmc.plugin;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.nio.file.Paths;

/**
 * Created by Lignum on 15/04/2015.
 */
public abstract class Plugin implements Closeable {
    protected Globals lua;
    protected PluginLib pluginLib;
    protected boolean def = false;
    protected String name;

    static {
    }

    public Plugin() {
        lua = JsePlatform.standardGlobals();
        PluginGlobals.set(this, lua);
    }

    /**
     * Is this the default plugin?
     */
    public boolean isDefault() {
        return def;
    }

    public String getName() {
        return name;
    }

    public abstract String getWorkingDirectory();

    public Globals getGlobals() {
        return lua;
    }

    public void setGlobal(String key, LuaValue value) {
        lua.set(key, value);
    }

    public abstract LuaValue run(String filename, boolean ignoreFileExistence) throws IOException, LuaError;

    protected LuaValue runFile(String filename) throws LuaError {
        try (FileInputStream fis = new FileInputStream(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            return lua.load(reader, Paths.get(filename).getFileName().toString()).call();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return LuaValue.NIL;
    }

    protected LuaValue runFromStream(InputStream stream, String chunkName) throws IOException, LuaError {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return lua.load(reader, chunkName).call();
        }
    }
}
