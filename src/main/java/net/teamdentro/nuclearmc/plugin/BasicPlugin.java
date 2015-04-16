package net.teamdentro.nuclearmc.plugin;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Lignum on 15/04/2015.
 */
public class BasicPlugin extends Plugin {
    private File file;

    public BasicPlugin(String filename) {
        this(new File(filename));
    }

    public BasicPlugin(File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("basic plugin file must be a directory!");
        }

        this.file = file;

        getGlobals().set("path", file.getPath());
    }

    @Override
    public LuaValue run(String filename, boolean ignoreFileExistence) throws IOException, LuaError {
        Path path = Paths.get(file.getAbsolutePath(), filename);

        if (ignoreFileExistence) {
            File theFile = new File(path.toString());
            if (!theFile.exists()) {
                return LuaValue.NIL;
            }
        }

        return runFile(path.toString());
    }

    @Override
    public void close() throws IOException {
    }
}
