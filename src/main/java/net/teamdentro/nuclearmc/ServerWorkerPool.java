package net.teamdentro.nuclearmc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ServerWorkerPool {
    private ServerWorker[] workers;

    public ServerWorkerPool(Server server, int workers) {
        this.workers = new ServerWorker[workers];

        NuclearMC.getLogger().info("Initialising worker pool with " + workers + " worker(s)");

        for (int i = 0; i < workers; ++i) {
            this.workers[i] = new ServerWorker(server);
        }
    }

    public void processPacket(byte id, ByteBuf packet, Channel client) {
        for (ServerWorker worker : workers) {
            if (!worker.isBusy()) {
                worker.process(id, packet, client);
            }
        }
    }
}
