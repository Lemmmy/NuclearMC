package net.teamdentro.nuclearmc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;

/**
 * Created by Lignum on 12/04/2015.
 */
public class User {
    private InetAddress sender;
    private int port;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String username;

    public User(String username, InetAddress sender, int port, DataInputStream dataStream, DataOutputStream dataOutStream) {
        this.sender = sender;
        this.port = port;
        this.inputStream = dataStream;
        this.outputStream = dataOutStream;
        this.username = username;
    }

    public InetAddress getAddress() {
        return sender;
    }

    public int getPort() {
        return port;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public String getUsername() {
        return username;
    }
}
