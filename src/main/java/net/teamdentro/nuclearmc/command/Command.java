package net.teamdentro.nuclearmc.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lignum on 19/04/2015.
 */
public abstract class Command {
    protected static Map<String, Command> commands = new HashMap<>();

    static {
        // Register commands
        registerCommand(CommandNMCInfo.class);
    }

    public static void registerCommand(Class<? extends Command> cmd) {
        try {
            Command command = cmd.newInstance();
            String name = command.getName();
            commands.put(name, command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerCommand(Command cmd) {
        commands.put(cmd.getName(), cmd);
    }

    public static Command getCommand(String name) {
        if (!commands.containsKey(name)) {
            return null;
        }

        return commands.get(name);
    }

    public abstract String[] getAliases();

    public abstract String getName();

    public abstract String getUsage();

    public abstract boolean execute(CommandSender sender);
}
