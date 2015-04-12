package net.teamdentro.nuclearmc.packets;

import java.io.DataInputStream;
import java.io.IOException;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;

public class Packet0Connect extends Packet {
	private byte protVersion;
	private String username;
	private String key;
	private byte userdata;
	
	public Packet0Connect(Server server, DataInputStream data) {
		super(server, data);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
