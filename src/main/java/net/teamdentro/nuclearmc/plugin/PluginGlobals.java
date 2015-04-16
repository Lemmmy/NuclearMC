package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.NuclearMC;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class PluginGlobals {
    public static void set(Globals lua) {
        lua.set("print", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String msg = arg.checkjstring();
                NuclearMC.getLogger().info(msg);
                return null;
            }
        });

        lua.set("warn", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String msg = arg.checkjstring();
                NuclearMC.getLogger().warning(msg);
                return null;
            }
        });

        lua.set("severe", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String msg = arg.checkjstring();
                NuclearMC.getLogger().severe(msg);
                return null;
            }
        });

        lua.set("Server", lua.load(new ServerLib()));
        lua.set("Event", lua.load(new EventLib()));
        lua.set("TextUtils", lua.load(new UtilsLib()));
    }
}
