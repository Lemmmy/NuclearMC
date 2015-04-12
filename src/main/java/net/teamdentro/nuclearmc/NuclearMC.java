package net.teamdentro.nuclearmc;

import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NuclearMC {
	private static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static GUI gui;
	
	public static Logger getLogger() {
		return LOGGER;
	}
	
	public static long MAIN_THREAD_ID;
	
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

	public static boolean isGUI() {
		return gui!=null;
	}

	public static GUI getGUI() {
		return gui;
	}

	public static void shutDown() {
		// server.shutDown();
	}
}
