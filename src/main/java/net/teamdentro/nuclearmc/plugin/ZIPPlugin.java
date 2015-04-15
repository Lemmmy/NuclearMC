package net.teamdentro.nuclearmc.plugin;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Lignum on 15/04/2015.
 */
public class ZIPPlugin extends Plugin {
    private ZipFile file;

    public ZIPPlugin(String filename) throws IOException {
        this(new ZipFile(filename));
    }

    public ZIPPlugin(ZipFile zip) {
        file = zip;
    }

    public LuaValue run(String filename, boolean ignoreFileExistence) throws IOException, LuaError {
        ZipEntry entry = file.getEntry(filename);

        if (ignoreFileExistence && entry == null) {
            return LuaValue.NIL;
        }

        try (InputStream in = file.getInputStream(entry)) {
            return runFromStream(in);
        }
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
