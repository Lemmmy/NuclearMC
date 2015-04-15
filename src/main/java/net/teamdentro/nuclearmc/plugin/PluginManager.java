package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.NuclearMC;
import org.luaj.vm2.LuaError;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Lignum on 15/04/2015.
 */
public class PluginManager implements Closeable {
    private List<Plugin> loadedPlugins = new ArrayList<Plugin>();

    public void loadPlugins(String dir) {
        File d = new File(dir);
        if (!d.isDirectory()) {
            throw new IllegalArgumentException("tried to load plugins from non-directory file!");
        }

        NuclearMC.getLogger().info("Loading plugins...");

        try {
            File defDir = new File(d.getParentFile(), "default");
            Plugin plugin = loadAnyPlugin(defDir.getAbsolutePath());
            if (plugin != null) {
                NuclearMC.getLogger().info("\t- [\u2713] Default");
            } else {
                NuclearMC.getLogger().info("\t- [X] Default");
            }
        } catch (Exception e) {
            NuclearMC.getLogger().log(Level.SEVERE, "Error while loading default plugin!", e);
        }

        for (File f : d.listFiles()) {
            try {
                if (f.isDirectory()) {
                    Plugin p = loadPlugin(f.getAbsolutePath());
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

                        Plugin p = loadPluginFromZIP(f.getAbsolutePath());
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

    public Plugin loadAnyPlugin(String file) throws IOException {
        File f = new File(file);

        if (f.isDirectory()) {
            return loadPlugin(f.getAbsolutePath());
        } else {
            if (f.getName().endsWith(".zip") || f.getName().endsWith(".plugin")) {
                String name = f.getName();
                int pos = name.lastIndexOf('.');
                if (pos > 0) {
                    name = name.substring(0, pos);
                }

                return loadPluginFromZIP(f.getAbsolutePath());
            }
        }

        return null;
    }

    public Plugin loadPlugin(String dir) throws IOException {
        try {
            BasicPlugin plugin = new BasicPlugin(dir);
            loadedPlugins.add(plugin);
            plugin.run("init.lua", true);
            return plugin;
        } catch (LuaError err) {
            NuclearMC.getLogger().severe("Failed to load plugin from directory " + Paths.get(dir).getFileName() + ": " + err.getMessage());
        }

        return null;
    }

    public Plugin loadPluginFromZIP(String zip) throws IOException {
        try {
            ZIPPlugin plugin = new ZIPPlugin(zip);
            loadedPlugins.add(plugin);
            plugin.run("init.lua", true);
            return plugin;
        } catch (LuaError err) {
            NuclearMC.getLogger().severe("Failed to load plugin from file " + Paths.get(zip).getFileName() + ": " + err.getMessage());
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        for (Plugin plugin : loadedPlugins) {
            plugin.run("shutdown.lua", true);
            plugin.close();
        }
    }
}
