package net.peepocloud.plugin.bungee;
/*
 * Created by Mc_Ruben on 22.12.2018
 */

import lombok.AllArgsConstructor;
import net.peepocloud.lib.player.PeepoPlayer;
import net.peepocloud.plugin.api.network.handler.NetworkAPIHandler;
import net.peepocloud.lib.server.bungee.BungeeCordProxyInfo;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.lib.users.User;

@AllArgsConstructor
public class BungeeNetworkHandler implements NetworkAPIHandler {

    private PeepoBungeePlugin bungeeAPI;

    @Override
    public void handleServerAdd(MinecraftServerInfo serverInfo) {
        this.bungeeAPI.registerServerInfo(serverInfo);
    }

    @Override
    public void handleServerStop(MinecraftServerInfo serverInfo) {
        this.bungeeAPI.unregisterServerInfo(serverInfo);
    }

    @Override
    public void handleServerUpdate(MinecraftServerInfo oldInfo, MinecraftServerInfo newInfo) {
        this.bungeeAPI.registerServerInfo(newInfo);
    }

    @Override
    public void handleServerQueued(MinecraftServerInfo serverInfo) {

    }

    @Override
    public void handleProxyAdd(BungeeCordProxyInfo proxyInfo) {

    }

    @Override
    public void handleProxyStop(BungeeCordProxyInfo proxyInfo) {

    }

    @Override
    public void handleProxyUpdate(BungeeCordProxyInfo oldInfo, BungeeCordProxyInfo newInfo) {

    }

    @Override
    public void handleProxyQueued(BungeeCordProxyInfo proxyInfo) {

    }

    @Override
    public void handleUserAdd(User user) {

    }

    @Override
    public void handleUserRemove(User user) {

    }

    @Override
    public void handleUserUpdate(User oldUser, User newUser) {

    }

    @Override
    public void handlePlayerLogin(PeepoPlayer player) {

    }

    @Override
    public void handlePlayerLogout(PeepoPlayer player) {

    }

    @Override
    public void handlePlayerUpdate(PeepoPlayer oldPlayer, PeepoPlayer newPlayer) {

    }
}
