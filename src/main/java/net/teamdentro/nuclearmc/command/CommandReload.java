package net.teamdentro.nuclearmc.command;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;

/**
 * Created by Lignum on 19/04/2015.
 */
public class CommandReload extends Command {
    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender) {
        NuclearMC.getLogger().info("Reloading...");

        Server.instance.getServerConfig().loadConfig();

        if (Server.instance.getPluginManager().reloadPlugins()) {
            sender.sendMessage("&aPlugins reloaded.");
        } else {
            sender.sendMessage("&cPlugins reloaded with errors. Check the console.");
        }

        NuclearMC.getLogger().info("Reload complete.");
        return true;
    }
}
