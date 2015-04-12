package net.teamdentro.nuclearmc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		work = null;
	}
}
