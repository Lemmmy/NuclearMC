package net.teamdentro.nuclearmc;

public interface HeartbeatCallback {
    /**
     * This will be ran when the Heartbeat is complete
     *
     * @param url The URL we beat
     */
    void run(String url);
}
