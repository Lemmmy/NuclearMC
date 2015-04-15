package net.teamdentro.nuclearmc;

import net.teamdentro.nuclearmc.gui.GUI;
import net.teamdentro.nuclearmc.gui.GUILogHandler;

import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NuclearMC {
    public static long MAIN_THREAD_ID;
    private static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static GUI gui;

    /**
     * Get the GUI instance
     *
     * @return The GUI instance or null
     */
    public static GUI getGUI() {
        return gui;
    }

    /**
     * Is the server running in GUI mode?
     *
     * @return Is the server running in GUI mode?
     */
    public static boolean isGUI() {
        return gui != null;
    }

    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);

        MAIN_THREAD_ID = Thread.currentThread().getId();

        getLogger().setLevel(Level.ALL);
        getLogger().setUseParentHandlers(false);

        for (Handler handler : getLogger().getHandlers()) {
            getLogger().removeHandler(handler);
        }

        if (arguments.contains("-g") || arguments.contains("--gui")) {
            gui = new GUI();
            GUILogHandler handler = new GUILogHandler();
            handler.setLevel(Level.ALL);
            handler.setTextArea(gui.getTextArea());
            getLogger().addHandler(handler);
        } else {
            ConsoleHandler chandler = new ConsoleHandler();
            chandler.setFormatter(new ConsoleFormatter());
            chandler.setLevel(Level.ALL);
            getLogger().addHandler(chandler);
        }

        Server.instance = new Server();
        Server.instance.run();
    }

    /**
     * Get the logger
     *
     * @return The logger
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Shut the server down
     */
    public static void shutDown() {
        Server.instance.closeServer();
        if (gui != null) gui.dispose();
        System.exit(0);
    }
}
