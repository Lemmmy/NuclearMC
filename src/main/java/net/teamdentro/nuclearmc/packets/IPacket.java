package net.teamdentro.nuclearmc.packets;

public interface IPacket {
    /**
     * The ID of this packet, as a byte
     *
     * @return The ID of this packet, as a byte
     */
    public byte getID();
}
