package net.teamdentro.nuclearmc.command;

public class CommandHelp extends Command {
    @Override
    public String[] getAliases() {
        return new String[]{"commands"};
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "[category]";
    }

    @Override
    public String getCategory() {
        return "server";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            String[] categories = Command.listCategories();

            sender.sendMessage("&e Here is a list of command categories:");
            for (String s : categories) {
                sender.sendMessage("&6/help &b" + s);
            }
        } else {
            Command[] commands = Command.listCommands(args[0]);
            StringBuilder builder = new StringBuilder();

            sender.sendMessage("&e Commands in &b" + args[0] + "&e category:");
            String prefix = "";
            for (Command cmd : commands) {
                builder.append(prefix);
                prefix = ", ";
                builder.append(cmd.getName());
            }

            for (int i = 0; i < builder.length(); i += 64) {
                sender.sendMessage(
                        builder.toString().substring(i,
                                Math.min(builder.length(), i + 64)));
            }
        }
        return true;
    }
}
