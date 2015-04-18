package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.Blocks;
import net.teamdentro.nuclearmc.NuclearMC;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class PluginGlobals {
    public static void set(Plugin plugin, Globals lua) {
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
        lua.set("Utils", lua.load(new UtilsLib()));
        lua.set("Plugin", lua.load(new PluginLib(plugin)));
        lua.set("Blocks", lua.load(new BlocksLib()));
        lua.set("BlockMode", lua.load(new BlockModeLib()));
    }

    public static LuaTable getLuaBlock(Blocks b) {
        return BlocksLib.blockTable.get(b.toString()).checktable();
    }

    protected static LuaTable makeLuaBlock(Blocks b) {
        LuaTable t = new LuaTable();
        t.set("id", b.getId());
        t.set("name", b.toString());

        LuaTable aliases = new LuaTable();

        int i = 1;
        for (String alias : b.getAliases()) {
            aliases.set(i, LuaValue.valueOf(alias));
            i++;
        }

        t.set("aliases", aliases);

        LuaTable metatable = new LuaTable();
        metatable.set("__eq", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue tbl1, LuaValue tbl2) {
                return BlocksLib.areEqual(tbl1, tbl2);
            }
        });

        t.setmetatable(metatable);
        return t;
    }
}