package net.teamdentro.nuclearmc.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.event.EventPostUserConnect;
import net.teamdentro.nuclearmc.event.EventUserMessage;
import net.teamdentro.nuclearmc.plugin.Commands;

import java.io.IOException;

public class Packet0DMessage extends Packet {
    private byte userdata;
    private String message;

    public Packet0DMessage(Server server, Channel client, ByteBuf data) {
        super(server, client, data);
    }

    @Override
    public byte getID() {
        return 0x0D;
    }

    @Override
    public void handle() {
        try {
            userdata = data.readByte();
            message = readString();

            User user = getUser();
            String cmdPrefix = server.getServerConfig().getValue("CommandPrefix", "/");

            if (user != null) {
                if (message.trim().startsWith(cmdPrefix)) {
                    String[] args = message.trim().substring(1).split(" ");
                    String cmd = args[0];

                    Commands.invokeCommand(cmd, getUser(), args);
                } else {
                    EventUserMessage eventUserMessage = new EventUserMessage();
                    eventUserMessage.setUser(user);
                    eventUserMessage.setMessage(message);
                    eventUserMessage.invoke();
                }
            }
        } catch (IOException ignored) {
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte getUserdata() {
        return userdata;
    }

    public void setUserdata(byte userdata) {
        this.userdata = userdata;
    }
}
