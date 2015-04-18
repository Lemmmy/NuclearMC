package net.teamdentro.nuclearmc.plugin;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

/**
 * Created by Lignum on 18/04/2015.
 */
public class BlockModeLib extends OneArgFunction {
    protected static LuaTable modes = new LuaTable();

    static {
        modes.set("PLACE", 0x1);
        modes.set("DESTROY", 0x0);
    }

    @Override
    public LuaValue call(LuaValue arg) {
        return modes;
    }
}
