package net.teamdentro.nuclearmc.command;

/**
 * Created by Lignum on 19/04/2015.
 */
public interface CommandSender {
    void sendMessage(String message);

    boolean isPlayer();
}
