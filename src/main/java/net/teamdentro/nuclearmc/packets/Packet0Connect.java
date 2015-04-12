package net.teamdentro.nuclearmc.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;

public class Packet0Connect extends Packet {
	private byte protVersion;
	private String username;
	private String key;
	private byte userdata;
	
	public Packet0Connect(Server server, Socket client) {
		super(server, client);
	}
	

	public byte getProtVersion() {
		return protVersion;
	}

	public String getUsername() {
		return username;
	}

	public String getKey() {
		return key;
	}
	
	public byte getUserdata() {
		return userdata;
	}

	@Override
	public byte getID() {
		return (byte)0x0;
	}

	@Override
	public void handle() {
		try {
			protVersion = data.readByte();
			username = readString(64).trim(); // WHO DID THIS AND WHY?
			key = readString(64).trim();
			userdata = data.readByte();
			
			NuclearMC.getLogger().info("Player " + username + " connected to the server.");

            User user = new User(username, client.getInetAddress(), client.getPort(), data, new DataOutputStream(client.getOutputStream()));
            SPacket0EDisconnect dc = new SPacket0EDisconnect(server, user);
            dc.setReason("This is a test server! Sorry :(");
            dc.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
