package net.teamdentro.nuclearmc;

import java.net.Socket;

import net.teamdentro.nuclearmc.packets.IPacket;
import net.teamdentro.nuclearmc.packets.Packet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

public class ServerWorker implements Runnable {
	private Thread workerThread;
	private ChannelBuffer work;
    private Server server;
    private Channel client;

    private byte id;

	public ServerWorker(Server server) {
		this.server = server;
        workerThread = new Thread(this);
	}
	
	public void process(byte id, ChannelBuffer packet, Channel client) {
        work = packet;
        this.client = client;
        this.id = id;

		workerThread.run();
	}
	
	public boolean isBusy() {
		return work != null;
	}
	
	@Override
	public void run() {
        try {
            Class<? extends IPacket> packetClass = Server.packetRegistry.get(id);
            if (packetClass == null) {
                NuclearMC.getLogger().warning("Received invalid packet (0x" + Integer.toHexString(id) + ") from " + client.getRemoteAddress().toString());
                return;
            }

            if (!packetClass.getSuperclass().equals(Packet.class)) {
                NuclearMC.getLogger().warning("Server tried to process non-client packet!!");
                NuclearMC.getLogger().warning("## Type: " + packetClass.toString());
                NuclearMC.getLogger().warning("## Superclass: " + packetClass.getSuperclass().toString());
            }

            Packet p = ((Class<? extends Packet>)packetClass).getDeclaredConstructor(Server.class, Channel.class, ChannelBuffer.class).newInstance(server, client, work);
            p.handle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        work = null;
	}
}
