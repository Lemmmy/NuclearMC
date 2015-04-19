package net.teamdentro.nuclearmc.command;

import net.teamdentro.nuclearmc.NuclearMC;

/**
 * Created by Lignum on 19/04/2015.
 */
public class CommandNMCInfo extends Command {
    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "nmcinfo";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("&aThis server is running NuclearMC v" + NuclearMC.getVersion());
        return true;
    }
}
