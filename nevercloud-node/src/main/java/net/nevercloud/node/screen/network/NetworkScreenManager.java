package net.nevercloud.node.screen.network;
/*
 * Created by Mc_Ruben on 26.11.2018
 */

import lombok.Getter;
import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.network.packet.out.screen.NetworkScreen;
import net.nevercloud.node.network.packet.out.screen.PacketOutDispatchProxyCommand;
import net.nevercloud.node.network.packet.out.screen.PacketOutDispatchServerCommand;
import net.nevercloud.node.network.packet.out.screen.PacketOutToggleScreen;
import net.nevercloud.node.network.participant.NodeParticipant;
import net.nevercloud.node.screen.EnabledScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class NetworkScreenManager {

    private Map<String, NetworkScreen> screens = new HashMap<>();

    public EnabledScreen enableScreen(BungeeCordProxyInfo proxyInfo, Consumer<String> consumer) {
        return enableScreen(consumer, proxyInfo.getComponentName(), proxyInfo.getParentComponentName());
    }

    public EnabledScreen enableScreen(Consumer<String> consumer, String componentName, String parentComponentName) {
        UUID uniqueId = UUID.randomUUID();
        if (this.screens.containsKey(componentName)) {
            this.screens.get(componentName).getConsumers().put(uniqueId, consumer);
            return createScreen(componentName, parentComponentName, uniqueId);
        }
        NodeParticipant nodeParticipant = NeverCloudNode.getInstance().getServerNodes().get(parentComponentName);
        if (nodeParticipant == null)
            return null;
        nodeParticipant.sendPacket(new PacketOutToggleScreen(componentName, true));
        this.screens.put(componentName, new NetworkScreen(nodeParticipant));
        this.screens.get(componentName).getConsumers().put(uniqueId, consumer);
        return createScreen(componentName, parentComponentName, uniqueId);
    }

    private EnabledScreen createScreen(String componentName, String parentComponentName, UUID uniqueId) {
        return new EnabledScreen(componentName, uniqueId) {
            @Override
            public void write(String line) {
                NodeParticipant participant = NeverCloudNode.getInstance().getServerNodes().get(parentComponentName);
                if (participant == null)
                    return;
                if (participant.getServers().containsKey(componentName) || participant.getWaitingServers().containsKey(componentName) || participant.getStartingServers().containsKey(componentName)) {
                    participant.sendPacket(new PacketOutDispatchServerCommand(componentName, line));
                } else if (participant.getProxies().containsKey(componentName) || participant.getWaitingProxies().containsKey(componentName) || participant.getStartingProxies().containsKey(componentName)) {
                    participant.sendPacket(new PacketOutDispatchProxyCommand(componentName, line));
                }
            }
        };
    }

    public EnabledScreen enableScreen(MinecraftServerInfo serverInfo, Consumer<String> consumer) {
        return enableScreen(consumer, serverInfo.getComponentName(), serverInfo.getParentComponentName());
    }

    public boolean disableScreen(String componentName, UUID uniqueId) {
        NetworkScreen screen = this.screens.get(componentName);
        if (screen == null)
            return false;
        if (!screen.getConsumers().containsKey(uniqueId))
            return false;
        screen.getConsumers().remove(uniqueId);
        if (screen.getConsumers().isEmpty()) {
            screen.getParticipant().sendPacket(new PacketOutToggleScreen(componentName, false));
            this.screens.remove(componentName);
        }
        return true;
    }

    public boolean disableScreen(EnabledScreen enabledScreen) {
        return this.disableScreen(enabledScreen.getComponentName(), enabledScreen.getUniqueId());
    }

    public void handleNodeDisconnect(NodeParticipant participant) {
        new HashMap<>(this.screens).forEach((s, networkScreen) -> { //prevent ConcurrentModificationException
            if (networkScreen.getParticipant().equals(participant)) {
                this.screens.remove(s);
            }
        });
    }

    public void dispatchScreenInput(NodeParticipant participant, String name, String line) {
        if (line == null)
            return;
        if (!this.screens.containsKey(name)) {
            participant.sendPacket(new PacketOutToggleScreen(name, false));
            return;
        }

        this.screens.get(name).call(line);
    }

}
