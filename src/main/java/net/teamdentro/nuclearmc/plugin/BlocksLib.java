package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.Blocks;
import net.teamdentro.nuclearmc.util.Util;
import org.apache.commons.lang3.ArrayUtils;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.Arrays;

/**
 * Created by Lignum on 18/04/2015.
 */
public class BlocksLib extends OneArgFunction {
    protected static LuaTable blockTable = new LuaTable();

    static {
        for (Blocks b : Blocks.values()) {
            blockTable.set(b.toString(), PluginGlobals.makeLuaBlock(b));
        }
    }

    @Override
    public LuaValue call(LuaValue arg) {
        LuaTable lib = tableOf();
        lib.set("areEqual", new AreEqual());
        lib.set("getBlock", new GetBlock());

        LuaTable metatable = new LuaTable();
        metatable.set("__index", blockTable);
        lib.setmetatable(metatable);
        return lib;
    }

    public static LuaBoolean areEqual(LuaValue vblock1, LuaValue vblock2) {
        LuaTable block1 = vblock1.checktable();
        LuaTable block2 = vblock2.checktable();

        return LuaValue.valueOf(block1.get("id").toint() == block2.get("id").toint());
    }

    private static boolean blockHasAlias(Blocks b, String alias) {
        for (String a : b.getAliases()) {
            if (a.equalsIgnoreCase(alias)) {
                return true;
            }
        }

        return false;
    }

    public static LuaValue getBlock(LuaValue vname) {
        String name = vname.checkjstring();

        for (Blocks b : Blocks.values()) {
            if (b.toString().equalsIgnoreCase(name) || blockHasAlias(b, name)) {
                return PluginGlobals.getLuaBlock(b);
            }
        }

        return LuaValue.NIL;
    }

    private class AreEqual extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue vblock1, LuaValue vblock2) {
            return areEqual(vblock1.checktable(), vblock2.checktable());
        }
    }

    private class GetBlock extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return getBlock(arg);
        }
    }
}