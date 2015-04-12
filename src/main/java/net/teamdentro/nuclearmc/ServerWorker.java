package net.teamdentro.nuclearmc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;

import net.teamdentro.nuclearmc.packets.Packet;

public class ServerWorker implements Runnable {
	private Thread workerThread;
	private DatagramPacket work;

	public ServerWorker() {
		workerThread = new Thread(this);
	}
	
	public void process(DatagramPacket packet) {
		work = packet;
		workerThread.run();
	}
	
	public boolean isBusy() {
		return work != null;
	}
	
	@Override
	public void run() {
		byte[] data = work.getData();
		
		try (	ByteArrayInputStream bais = new ByteArrayInputStream(data);
				DataInputStream dis = new DataInputStream(bais)				) {
			byte id = dis.readByte();
			Class<? extends Packet> packetClass = Server.packetRegistry.get(id);
			if (packetClass == null) {
				NuclearMC.getLogger().warning("Received invalid packet (0x" + Integer.toHexString(id) + ") from " + work.getAddress().toString() + ":" + work.getPort());
				return;
			}
			
			Packet p = packetClass.getDeclaredConstructor(Server.class, DataInputStream.class).newInstance();
			p.handle();
		} catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		work = null;
	}
}
