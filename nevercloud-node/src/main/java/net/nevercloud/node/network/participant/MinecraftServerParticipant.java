package net.nevercloud.node.network.participant;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import io.netty.channel.Channel;
import lombok.Getter;
import net.nevercloud.lib.network.NetworkParticipant;
import net.nevercloud.lib.network.auth.Auth;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;

@Getter
public class MinecraftServerParticipant extends NetworkParticipant {
    private Auth auth;
    private MinecraftServerInfo serverInfo;

    public MinecraftServerParticipant(Channel channel, Auth auth) {
        super(auth.getComponentName(), channel);
        this.auth = auth;
        this.serverInfo = auth.getExtraData().getObject("serverInfo", MinecraftServerInfo.class);
    }


}
