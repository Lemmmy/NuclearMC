package net.teamdentro.nuclearmc;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

public class ServerWorkerPool {
	private ServerWorker[] workers;
	
	public ServerWorkerPool(Server server, int workers) {
		this.workers = new ServerWorker[workers];
		
		NuclearMC.getLogger().info("Initialising worker pool with " + workers + " worker(s).");
		
		for (int i = 0; i < workers; ++i) {
			this.workers[i] = new ServerWorker(server);
		}
	}
	
	public void processPacket(byte id, ChannelBuffer packet, Channel client) {
		for (ServerWorker worker : workers) {
			if (!worker.isBusy()) {
				worker.process(id, packet, client);
			}
		}
	}
}
