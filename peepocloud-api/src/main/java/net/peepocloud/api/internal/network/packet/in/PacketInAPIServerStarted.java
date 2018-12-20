package net.peepocloud.api.internal.network.packet.in;


import net.peepocloud.api.internal.NodeChildAPI;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.network.NetworkParticipant;
import net.peepocloud.lib.network.packet.JsonPacket;
import net.peepocloud.lib.network.packet.Packet;
import net.peepocloud.lib.network.packet.handler.JsonPacketHandler;

import java.util.function.Consumer;

public class PacketInAPIServerStarted extends JsonPacketHandler {


    @Override
    public int getId() {
        return 100;
    }

    @Override
    public void handlePacket(NetworkParticipant networkParticipant, JsonPacket packet, Consumer<Packet> queryResponse) {
        if(packet.getSimpleJsonObject() == null || !packet.getSimpleJsonObject().contains("serverInfo"))
            return;
        if(NodeChildAPI.getInstance().isBungee())
            NodeChildAPI.getInstance().toBungee().registerServerInfo(packet.getSimpleJsonObject().getObject("serverInfo", MinecraftServerInfo.class));
    }
}
