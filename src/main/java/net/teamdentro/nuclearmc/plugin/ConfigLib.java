package net.teamdentro.nuclearmc.plugin;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lignum on 15/04/2015.
 */
public class ConfigLib {
    public static class Config {
        private Map<String, String> properties = new HashMap<>();
        private String filename;

        public Config(String filename) {
            this.filename = filename;
        }

        public Config(File file) {
            this(file.getAbsolutePath());
        }

        public boolean isEmpty() {
            return properties.isEmpty();
        }

        public boolean hasKey(String key) {
            return properties.containsKey(key);
        }

        public void set(String key, String value) {
            properties.put(key, value);
        }

        public String get(String key, String defaultValue) {
            if (!properties.containsKey(key)) {
                return defaultValue;
            } else {
                return properties.get(key);
            }
        }

        public String get(String key) {
            return get(key, null);
        }

        public void copyToTable(LuaTable table) {
            for (Map.Entry<String, String> prop : properties.entrySet()) {
                table.set(prop.getKey(), prop.getValue());
            }
        }

        public void load() {
            File f = new File(filename);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }

            try (FileInputStream fis = new FileInputStream(filename);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] vals = line.split("=", 2);
                    String key = vals[0];
                    String value = vals[1];
                    set(key, value);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void save() {
            File f = new File(filename);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try (FileOutputStream fos = new FileOutputStream(filename);
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
                for (Map.Entry<String, String> prop : properties.entrySet()) {
                    writer.write(prop.getKey() + "=" + prop.getValue());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File configDir = new File("config");

    public static Config getConfig(String pluginName) {
        Config cfg = new Config(Paths.get(configDir.getAbsolutePath(), pluginName + ".cfg").toString());
        cfg.load();
        return cfg;
    }

    public static class GetConfigFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String plugin = arg.checkjstring();
            Config cfg = getConfig(plugin);
            return CoerceJavaToLua.coerce(cfg);
        }
    }
}
