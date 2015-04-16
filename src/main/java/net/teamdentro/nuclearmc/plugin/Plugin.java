package net.teamdentro.nuclearmc.plugin;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;

/**
 * Created by Lignum on 15/04/2015.
 */
public abstract class Plugin implements Closeable {
    protected static Globals lua;
    protected static LuaTable nmcApi;

    static {
        lua = JsePlatform.standardGlobals();
        PluginGlobals.set(lua);
    }

    public static Globals getGlobals() {
        return lua;
    }

    public static void addGlobal(String key, LuaValue value) {
        lua.set(key, value);
    }

    public abstract LuaValue run(String filename, boolean ignoreFileExistence) throws IOException, LuaError;

    protected static LuaValue runFile(String filename) throws LuaError {
        return lua.loadfile(filename).call();
    }

    protected static LuaValue runFromStream(InputStream stream) throws IOException, LuaError {
        String file = "";

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";

        while ((line = reader.readLine()) != null) {
            file += line;
        }

        return lua.loadfile(file).call();
    }

    public class GetPathFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return null;
        }
    }
}
