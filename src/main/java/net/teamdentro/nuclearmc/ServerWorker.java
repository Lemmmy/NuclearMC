package net.teamdentro.nuclearmc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.teamdentro.nuclearmc.packets.IPacket;
import net.teamdentro.nuclearmc.packets.Packet;

public class ServerWorker implements Runnable {
	private Thread workerThread;
	private ByteBuf work;
    private Server server;
    private Channel client;

    private byte id;

	public ServerWorker(Server server) {
		this.server = server;
        workerThread = new Thread(this);
	}
	
	public void process(byte id, ByteBuf packet, Channel client) {
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
                return;
            }

            if (!packetClass.getSuperclass().equals(Packet.class)) {
                NuclearMC.getLogger().warning("Server tried to process non-client packet!!");
                NuclearMC.getLogger().warning("## Type: " + packetClass.toString());
                NuclearMC.getLogger().warning("## Superclass: " + packetClass.getSuperclass().toString());
            }

            Packet p = ((Class<? extends Packet>)packetClass).getDeclaredConstructor(Server.class, Channel.class, ByteBuf.class).newInstance(server, client, work);
            p.handle();
        } catch (Exception e) {
            if (!e.getMessage().toLowerCase().contains("connection was forcibly closed by the remote host"))
                e.printStackTrace();
        }

        work = null;
	}
}
