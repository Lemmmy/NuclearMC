package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.NuclearMC;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;

import java.io.*;

/**
 * Created by Lignum on 15/04/2015.
 */
public abstract class Plugin implements Closeable {
    protected static Globals lua;
    protected static LuaTable nmcApi;

    static {
        lua = JsePlatform.standardGlobals();
        lua.set("print", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String msg = arg.checkjstring();
                NuclearMC.getLogger().info(msg);
                return null;
            }
        });

        nmcApi = new LuaTable();
        nmcApi.set("getConfig", new ConfigLib.GetConfigFunc());

        lua.set("NMC", nmcApi);
    }

    public static Globals getGlobals() {
        return lua;
    }

    public abstract LuaValue run(String filename, boolean ignoreFileExistence) throws IOException, LuaError;

    protected static LuaValue runFile(String filename) throws LuaError {
        return lua.get("loadfile").call(LuaValue.valueOf(filename)).call();
    }

    protected static LuaValue runFromStream(InputStream stream) throws IOException, LuaError {
        String file = "";

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";

        while ((line = reader.readLine()) != null) {
            file += line;
        }

        return lua.get("load").call(LuaValue.valueOf(file)).call();
    }
}
