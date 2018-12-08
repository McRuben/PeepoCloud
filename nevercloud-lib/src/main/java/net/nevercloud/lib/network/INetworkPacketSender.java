package net.nevercloud.lib.network;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import net.nevercloud.lib.network.packet.FilePacket;
import net.nevercloud.lib.network.packet.Packet;

import java.io.File;
import java.nio.file.Path;

public interface INetworkPacketSender {

    void sendPacket(Packet packet);

    default void sendFile(int packetId, File file) {
        this.sendPacket(new FilePacket(packetId, file));
    }

    default void sendFile(int packetId, Path path) {
        this.sendPacket(new FilePacket(packetId, path));
    }

    default void sendFile(int packetId, byte[] bytes, boolean isDirectory) {
        this.sendPacket(new FilePacket(packetId, bytes, isDirectory));
    }

    void sendPacketSync(Packet packet);

}
