package net.teamdentro.nuclearmc.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Lignum on 15/04/2015.
 */
public class PluginManager {
    private List<Plugin> loadedPlugins = new ArrayList<Plugin>();

    public void loadPlugin(String dir) {
        Path path = Paths.get(dir, "init.lua");
        Plugin.runFile(path.toString());
    }

    public void loadPluginFromZIP(String zip) {
        try (ZipFile zipFile = new ZipFile(zip)) {
            ZipEntry initFile = zipFile.getEntry("init.lua");

            InputStream stream = zipFile.getInputStream(initFile);
            Plugin.runFromStream(stream);
            stream.close();

            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
