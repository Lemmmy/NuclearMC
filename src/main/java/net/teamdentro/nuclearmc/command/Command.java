package net.teamdentro.nuclearmc.command;

import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lignum on 19/04/2015.
 */
public abstract class Command {
    protected static Map<String, Command> commands = new HashMap<>();

    static {
        // Register commands
        registerCommands();
    }

    public static void registerCommands() {
        registerCommand(CommandNMCInfo.class);
        registerCommand(CommandReload.class);
        registerCommand(CommandHelp.class);
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

    public static void clearCommands() {
        commands.clear();
    }

    public static String[] listCategories() {
        List<String> categories = new ArrayList<>();

        for (Map.Entry<String, Command> e : commands.entrySet()) {
            Command cmd = e.getValue();
            if (!categories.contains(cmd.getCategory())) {
                categories.add(cmd.getCategory());
            }
        }

        return categories.toArray(new String[0]);
    }

    public static Command[] listCommands() {
        Command[] cmds = new Command[commands.size()];

        int i = 0;
        for (Map.Entry<String, Command> e : commands.entrySet()) {
            i++;
            cmds[i] = e.getValue();
        }

        return cmds;
    }

    public static Command[] listCommands(String category) {
        List<Command> cmds = new ArrayList<Command>();

        int i = 0;
        for (Map.Entry<String, Command> e : commands.entrySet()) {
            if (e.getValue().getCategory().toLowerCase().startsWith(category.toLowerCase())) {
                i++;
                cmds.add(e.getValue());
            }
        }

        return cmds.toArray(new Command[0]);
    }

    public abstract String[] getAliases();

    public abstract String getName();

    public abstract String getUsage();

    public abstract String getCategory();

    public boolean luaExecute(CommandSender sender, LuaTable args) {
        List<String> largs = new ArrayList<>();

        for (int i = 1; i <= args.length(); ++i) {
            largs.add(args.get(i).checkjstring());
        }

        return execute(sender, largs.toArray(new String[0]));
    }

    public abstract boolean execute(CommandSender sender, String[] args);
}
