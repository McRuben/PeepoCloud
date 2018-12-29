package net.peepocloud.plugin.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.peepocloud.lib.config.json.SimpleJsonObject;
import net.peepocloud.plugin.PeepoCloudPlugin;
import net.peepocloud.lib.server.minecraft.MinecraftServerInfo;
import net.peepocloud.plugin.api.bungee.PeepoCloudBungeeAPI;
import net.peepocloud.plugin.network.packet.in.PacketInAPISignSelector;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class PeepoBungeePlugin extends PeepoCloudPlugin implements PeepoCloudBungeeAPI {
    private BungeeLauncher plugin;

    PeepoBungeePlugin(BungeeLauncher plugin) {
        super(Paths.get("nodeInfo.json"));
        this.plugin = plugin;

        this.packetManager.registerPacket(new PacketInAPISignSelector());
    }

    @Override
    public void registerServerInfo(MinecraftServerInfo serverInfo) {
        if(!this.plugin.getProxy().getServers().containsKey(serverInfo.getComponentName())) {
            ServerInfo newServer = this.plugin.getProxy().constructServerInfo(serverInfo.getComponentName(),
                    new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort()), serverInfo.getMotd(), false);
            this.plugin.getProxy().getServers().put(serverInfo.getComponentName(), newServer);
        }
    }

    @Override
    public void unregisterServerInfo(MinecraftServerInfo serverInfo) {
        this.plugin.getProxy().getServers().remove(serverInfo.getComponentName());
    }

    @Override
    public Runnable handleConnected() {
        return () -> {
            System.out.println(new SimpleJsonObject().append("ds", super.getMinecraftServers().complete()).toPrettyJson());
            super.getMinecraftServers().complete().forEach(this::registerServerInfo);
        };
    }

    @Override
    public boolean isBungee() {
        return true;
    }

    @Override
    public boolean isBukkit() {
        return false;
    }


    @Override
    public BungeeLauncher getPlugin() {
        return plugin;
    }
}
