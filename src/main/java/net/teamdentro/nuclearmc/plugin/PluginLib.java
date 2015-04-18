package net.teamdentro.nuclearmc.plugin;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        pluginLib.set("getWorkingDirectory", new GetWorkingDirectory(this));
        pluginLib.set("resolve", new Resolve(this));
        pluginLib.set("run", new Run(this));

        return pluginLib;
    }

    private String getWorkingDirectory() {
        return plugin.getWorkingDirectory();
    }

    private LuaValue run(String file) {
        try {
            return plugin.run(file, false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LuaError e) {
            throw new LuaError("Run: " + e.getMessage());
        }

        return null;
    }

    private class GetWorkingDirectory extends ZeroArgFunction {
        private PluginLib plugin;

        public GetWorkingDirectory(PluginLib plugin) {
            this.plugin = plugin;

        }

        @Override
        public LuaValue call() {
            return LuaValue.valueOf(plugin.getWorkingDirectory());
        }
    }

    private class Resolve extends OneArgFunction {
        private PluginLib plugin;

        public Resolve(PluginLib plugin) {
            this.plugin = plugin;
        }

        @Override
        public LuaValue call(LuaValue arg) {
            String path = arg.checkjstring();
            Path p1 = Paths.get(plugin.getWorkingDirectory());
            Path p2 = Paths.get(path);
            Path result = Paths.get(p1.toString(), p2.toString());
            return LuaValue.valueOf(result.toString());
        }
    }

    private class Run extends OneArgFunction {
        private PluginLib plugin;

        public Run(PluginLib plugin) {
            this.plugin = plugin;
        }

        @Override
        public LuaValue call(LuaValue arg) {
            return plugin.run(arg.checkjstring());
        }
    }
}
