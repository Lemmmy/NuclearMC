package net.teamdentro.nuclearmc;

import net.teamdentro.nuclearmc.packets.SPacket0DChatMessage;

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
    private byte playerID;

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

    public byte getPlayerID() {
        return playerID;
    }

    public void setPlayerID(byte playerID) {
        this.playerID = playerID;
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

    public void sendMessage(String message) {
        sendMessage(message, Byte.MAX_VALUE);
    }

    public void sendMessage(String message, byte playerID) {
        SPacket0DChatMessage msg = new SPacket0DChatMessage(Server.instance, this);
        msg.setMessage(message);
        msg.setPlayerID(playerID);
        msg.setRecipient(this);
        msg.send();
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
