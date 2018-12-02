package net.nevercloud.node.api.event.network.minecraftserver;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.api.event.internal.Event;

@Getter
/**
 * Called when a server is started on this node instance
 */
public class ServerStartEvent extends NetworkServerEvent {
    public ServerStartEvent(MinecraftServerInfo serverInfo) {
        super(serverInfo);
    }
}
