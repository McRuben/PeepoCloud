package net.peepocloud.node.network.packet.out.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import net.peepocloud.commons.config.json.SimpleJsonObject;
import net.peepocloud.api.network.packet.JsonPacket;
import net.peepocloud.api.server.bungee.BungeeCordProxyInfo;

public class PacketOutStartBungee extends JsonPacket {
    public PacketOutStartBungee(BungeeCordProxyInfo proxyInfo) {
        super(11);
        this.setSimpleJsonObject(new SimpleJsonObject().append("proxyInfo", proxyInfo));
    }
}
