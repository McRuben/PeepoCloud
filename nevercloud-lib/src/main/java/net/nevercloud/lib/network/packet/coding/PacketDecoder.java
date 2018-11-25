package net.nevercloud.lib.network.packet.coding;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.nevercloud.lib.network.packet.Packet;
import net.nevercloud.lib.network.packet.PacketInfo;
import net.nevercloud.lib.network.packet.PacketManager;
import net.nevercloud.lib.utility.network.PacketUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private PacketManager packetManager;

    public PacketDecoder(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(!(byteBuf instanceof EmptyByteBuf)) {
            byteBuf.resetReaderIndex();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);

            ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bytes);
            int id = byteArrayDataInput.readInt();
            boolean isQuery = byteArrayDataInput.readBoolean();

            PacketInfo packetInfo = this.packetManager.getPacketInfo(id);
            if (packetInfo == null)
                return;
            Class<? extends Packet> packetClass = packetInfo.getPacketClass();
            try {
                Packet packet = packetClass.getDeclaredConstructor(int.class).newInstance(id);

                if(isQuery)
                    this.packetManager.convertToQueryPacket(packet, PacketUtils.readUUID(byteArrayDataInput));

                byte[] b = new byte[byteArrayDataInput.readInt()];
                for (int i = 0; i < b.length; i++) {
                    b[i] = byteArrayDataInput.readByte();
                }
                ByteBuf buf = Unpooled.wrappedBuffer(b);
                packet.read(buf);
                list.add(packet);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
