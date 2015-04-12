package net.teamdentro.nuclearmc;

import java.net.DatagramPacket;

public class ServerWorkerPool {
	private ServerWorker[] workers;
	
	public ServerWorkerPool(int workers) {
		this.workers = new ServerWorker[workers];
		
		NuclearMC.getLogger().info("Initialising worker pool with " + workers + " worker(s).");
		
		for (int i = 0; i < workers; ++i) {
			this.workers[i] = new ServerWorker();
		}
	}
	
	public void processPacket(DatagramPacket packet) {
		for (ServerWorker worker : workers) {
			if (!worker.isBusy()) {
				worker.process(packet);
			}
		}
	}
}
