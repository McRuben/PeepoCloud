package net.nevercloud.node.api.event.player;
/*
 * Created by Mc_Ruben on 01.12.2018
 */

import lombok.*;
import net.nevercloud.node.api.event.internal.Cancellable;

@Getter
public class PlayerUpdateEvent extends OfflinePlayerEvent implements Cancellable {
    @Setter
    private boolean cancelled;
}
