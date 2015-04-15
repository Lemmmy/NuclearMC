package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.NuclearMC;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Lignum on 15/04/2015.
 */
public class Plugin {
    private static Globals lua;
    private static LuaTable nmcApi;

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

    protected static void runFile(String filename) {
        lua.get("loadfile").call(LuaValue.valueOf(filename)).call();
    }

    protected static void runFromStream(InputStream stream) throws IOException {
        String file = "";

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";

        while ((line = reader.readLine()) != null) {
            file += line;
        }

        lua.get("load").call(LuaValue.valueOf(file)).call();
    }
}
