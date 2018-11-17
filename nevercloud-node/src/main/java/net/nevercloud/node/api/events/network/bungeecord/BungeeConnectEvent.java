package net.nevercloud.node.api.events.network.bungeecord;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.nevercloud.node.api.events.internal.Event;
import net.nevercloud.node.network.participants.BungeeCordParticipant;

@Getter
@AllArgsConstructor
public class BungeeConnectEvent extends Event {
    private BungeeCordParticipant participant;
}
