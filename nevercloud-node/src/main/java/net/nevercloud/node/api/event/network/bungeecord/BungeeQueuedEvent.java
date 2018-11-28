package net.nevercloud.node.api.event.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.node.api.event.internal.Cancellable;
import net.nevercloud.node.api.event.internal.Event;

@Getter
public class BungeeQueuedEvent extends NetworkBungeeEvent implements Cancellable {
    @Setter
    private boolean cancelled;

    public BungeeQueuedEvent(BungeeCordProxyInfo proxyInfo) {
        super(proxyInfo);
    }
}
