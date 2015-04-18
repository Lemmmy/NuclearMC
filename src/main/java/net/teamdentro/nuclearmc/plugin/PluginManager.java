package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.NuclearMC;
import org.apache.commons.io.FilenameUtils;
import org.luaj.vm2.LuaError;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Lignum on 15/04/2015.
 */
public class PluginManager implements Closeable {
    private List<Plugin> loadedPlugins = new ArrayList<Plugin>();

    public void loadPlugins(String dir) {
        File d = new File(dir).getAbsoluteFile();
        if (!d.isDirectory()) {
            throw new IllegalArgumentException("tried to load plugins from non-directory file!");
        }

        NuclearMC.getLogger().info("Loading plugins...");

        try {
            File defDir = new File(d.getParentFile(), "default");
            File assetsDir = defDir.getParentFile();

            if (!assetsDir.exists()) {
                assetsDir.mkdir();
            }

            Plugin plugin = loadAnyPlugin(defDir.getAbsolutePath(), false);
            plugin.def = true;
            plugin.run("init.lua", true);

            if (plugin != null) {
                NuclearMC.getLogger().info("\t- [\u2713] Default");
            } else {
                NuclearMC.getLogger().info("\t- [X] Default");
            }
        } catch (LuaError err) {
            NuclearMC.getLogger().severe("Failed to load default plugin: " + err.getMessage());
            NuclearMC.getLogger().info("\t- [X] Default");
        } catch (Exception e) {
            NuclearMC.getLogger().log(Level.SEVERE, e.getClass().getName() + " while loading default plugin: " + e.getMessage(), e);
        }

        for (File f : d.listFiles()) {
            try {
                if (f.isDirectory()) {
                    Plugin p = loadPlugin(f.getAbsolutePath(), true);
                    String c = "\u2713";
                    if (p == null) {
                        c = "X";
                    }
                    NuclearMC.getLogger().info("\t- [" + c + "] " + f.getName());
                } else {
                    if (f.getName().endsWith(".zip") || f.getName().endsWith(".plugin")) {
                        String name = f.getName();
                        int pos = name.lastIndexOf('.');
                        if (pos > 0) {
                            name = name.substring(0, pos);
                        }

                        Plugin p = loadPluginFromZIP(f.getAbsolutePath(), true);
                        String c = "\u2713";
                        if (p == null) {
                            c = "X";
                        }

                        NuclearMC.getLogger().info("\t- [" + c + "] " + name);
                    }
                }
            } catch (Exception e) {
                NuclearMC.getLogger().log(Level.SEVERE, "Error while loading plugin from file " + f.getName(), e);
            }
        }
    }

    public Plugin loadAnyPlugin(String file, boolean runInit) throws IOException {
        File f = new File(file);

        if (f.isDirectory()) {
            return loadPlugin(f.getAbsolutePath(), runInit);
        } else {
            if (f.getName().endsWith(".zip") || f.getName().endsWith(".plugin")) {
                String name = f.getName();
                int pos = name.lastIndexOf('.');
                if (pos > 0) {
                    name = name.substring(0, pos);
                }

                return loadPluginFromZIP(f.getAbsolutePath(), runInit);
            }
        }

        return null;
    }

    public Plugin loadPlugin(String dir, boolean runInit) throws IOException {
        String name = FilenameUtils.removeExtension(FilenameUtils.getName(dir));

        try {
            BasicPlugin plugin = new BasicPlugin(dir);
            plugin.name = name;
            loadedPlugins.add(plugin);

            if (runInit) {
                plugin.run("init.lua", true);
            }

            return plugin;
        } catch (LuaError err) {
            NuclearMC.getLogger().severe("Failed to load plugin " + name + ": " + err.getMessage());
        }

        return null;
    }

    public Plugin loadPluginFromZIP(String zip, boolean runInit) throws IOException {
        String name = FilenameUtils.removeExtension(FilenameUtils.getName(zip));

        try {
            ZIPPlugin plugin = new ZIPPlugin(zip);
            plugin.name = name;
            loadedPlugins.add(plugin);

            if (runInit) {
                plugin.run("init.lua", true);
            }

            return plugin;
        } catch (LuaError err) {
            NuclearMC.getLogger().severe("Failed to load plugin " + name + ": " + err.getMessage());
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        for (Plugin plugin : loadedPlugins) {
            try {
                plugin.run("shutdown.lua", true);
            } catch (LuaError e) {
                NuclearMC.getLogger().severe("Error in plugin " + plugin.getName() + " during shutdown: " + e.getMessage());
            }
            plugin.close();
        }
    }
}
