package net.teamdentro.nuclearmc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Lignum on 12/04/2015.
 */
public class User {
    private InetAddress sender;
    private int port;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String username;
    private Socket socket;

    public User(String username, InetAddress sender, int port, Socket socket) {
        this.sender = sender;
        this.port = port;

        if (socket != null) {
            try {
                this.inputStream = new DataInputStream(socket.getInputStream());
                this.outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.socket = socket;
        this.username = username;
    }

    public Socket getSocket() {
        return socket;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }

        User user = (User)obj;
        return user.getUsername().equals(username);
    }
}
