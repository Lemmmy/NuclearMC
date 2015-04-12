package net.teamdentro.nuclearmc;

import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NuclearMC {
	private static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static boolean gui;
	
	public static Logger getLogger() {
		return LOGGER;
	}
	
	public static long MAIN_THREAD_ID;
	
	public static void main(String[] args) {
		List<String> arguments = Arrays.asList(args);
		gui = arguments.contains("-g") || arguments.contains("--gui");

		MAIN_THREAD_ID = Thread.currentThread().getId();
		
		getLogger().setLevel(Level.ALL);
		getLogger().setUseParentHandlers(false);
		
		for (Handler handler : getLogger().getHandlers()) {
			getLogger().removeHandler(handler);
		}

		if (gui) {

		} else {
			ConsoleHandler chandler = new ConsoleHandler();
			chandler.setFormatter(new ConsoleFormatter());
			chandler.setLevel(Level.ALL);
			getLogger().addHandler(chandler);
		}

		Server server = new Server();
		server.run();
	}

	public static boolean isGUI() {
		return gui;
	}
}
