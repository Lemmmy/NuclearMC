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
        return true;
    }
}
