package net.nevercloud.lib.network.packet.handler;

import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.packet.Packet;
import java.util.function.Consumer;

public interface PacketHandler {

    void handlePacket(NetworkParticipant networkParticipant, Packet packet, Consumer<Packet> queryResult);

    void onException(NetworkParticipant networkParticipant, Throwable throwable);

    void onConnect(NetworkParticipant networkParticipant);

    void onDisconnect(NetworkParticipant networkParticipant);

}
