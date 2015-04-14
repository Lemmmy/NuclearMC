package net.teamdentro.nuclearmc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.teamdentro.nuclearmc.packets.IPacket;
import net.teamdentro.nuclearmc.packets.Packet;

import java.lang.reflect.InvocationTargetException;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf buf = (ByteBuf) msg;

            while (buf.isReadable()) {
                byte id = buf.readByte();

                Class<? extends IPacket> packetClass = Server.packetRegistry.get(id);
                if (packetClass == null) {
                    return;
                }

                if (!packetClass.getSuperclass().equals(Packet.class)) {
                    NuclearMC.getLogger().warning("Server tried to process non-client packet!!");
                    NuclearMC.getLogger().warning("## Type: " + packetClass.toString());
                    NuclearMC.getLogger().warning("## Superclass: " + packetClass.getSuperclass().toString());

                    return;
                }

                Packet p = ((Class<? extends Packet>) packetClass).getDeclaredConstructor(Server.class, Channel.class, ByteBuf.class).newInstance(Server.instance, ctx.channel(), buf);
                p.handle();
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // invalid packet
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
