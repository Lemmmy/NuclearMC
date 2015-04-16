package net.teamdentro.nuclearmc.plugin;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

/**
 * Created by Lignum on 16/04/2015.
 */
public class PluginLib extends OneArgFunction {
    private Plugin plugin;

    public PluginLib(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public LuaValue call(LuaValue arg) {
        LuaTable pluginLib = tableOf();



        return pluginLib;
    }

    private String getWorkingDirectory() {
        return plugin.getWorkingDirectory();
    }

    private class GetWorkingDirectory {

    }
}
