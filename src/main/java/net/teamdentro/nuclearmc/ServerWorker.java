package net.teamdentro.nuclearmc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;

import net.teamdentro.nuclearmc.packets.IPacket;
import net.teamdentro.nuclearmc.packets.Packet;
import net.teamdentro.nuclearmc.packets.SPacket0EDisconnect;
import net.teamdentro.nuclearmc.packets.ServerPacket;

public class ServerWorker implements Runnable {
	private Thread workerThread;
	private DataInputStream work;
    private Server server;
    private Socket client;

	public ServerWorker(Server server) {
		this.server = server;
        workerThread = new Thread(this);
	}
	
	public void process(DataInputStream packet, Socket client) {
        work = packet;
        this.client = client;

		workerThread.run();
	}
	
	public boolean isBusy() {
		return work != null;
	}
	
	@Override
	public void run() {
        try {
            byte id = work.readByte();
            Class<? extends IPacket> packetClass = Server.packetRegistry.get(id);
            if (packetClass == null) {
                NuclearMC.getLogger().warning("Received invalid packet (0x" + Integer.toHexString(id) + ") from " + client.getInetAddress().toString() + ":" + client.getPort());
                return;
            }

            if (!packetClass.getSuperclass().equals(Packet.class)) {
                NuclearMC.getLogger().warning("Server tried to process non-client packet!!");
                NuclearMC.getLogger().warning("## Type: " + packetClass.toString());
                NuclearMC.getLogger().warning("## Superclass: " + packetClass.getSuperclass().toString());
            }

            Packet p = ((Class<? extends Packet>)packetClass).getDeclaredConstructor(Server.class, Socket.class).newInstance(server, client);
            p.handle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        work = null;
	}
}
