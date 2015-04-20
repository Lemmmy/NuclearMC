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
        String[] categories = Command.listCategories();
        if (args.length == 0) {
            sender.sendMessage("&eHere is a list of command categories:");
            for (String s : categories) {
                sender.sendMessage("&6/help &b" + s);
            }
        } else {
            String cat = "";
            for (String s : categories) {
                if (s.trim().toLowerCase().startsWith(args[0].trim().toLowerCase())) {
                    cat = s;
                    break;
                }
            }
            if (cat == "") {
                sender.sendMessage("&cCategory &b" + args[0] + "&c not found");
                return true;
            }

            Command[] commands = Command.listCommands(args[0]);
            StringBuilder builder = new StringBuilder();

            sender.sendMessage("&eCommands in &b" + cat + "&e category:");
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
