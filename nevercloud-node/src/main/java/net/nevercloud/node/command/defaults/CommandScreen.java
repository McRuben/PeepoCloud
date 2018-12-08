package net.nevercloud.node.command.defaults;
/*
 * Created by Mc_Ruben on 27.11.2018
 */

import net.nevercloud.lib.server.bungee.BungeeCordProxyInfo;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.command.Command;
import net.nevercloud.node.command.CommandSender;
import net.nevercloud.node.command.TabCompletable;
import net.nevercloud.node.network.participant.NodeParticipant;
import net.nevercloud.node.screen.EnabledScreen;
import net.nevercloud.node.server.process.BungeeProcess;
import net.nevercloud.node.server.process.CloudProcess;
import net.nevercloud.node.server.process.ServerProcess;

import java.util.*;
import java.util.stream.Collectors;

public class CommandScreen extends Command implements TabCompletable {
    public CommandScreen() {
        super("screen");
    }

    private EnabledScreen enabledScreen;

    @Override
    public void execute(CommandSender sender, String commandLine, String[] args) {
        if (args.length < 1) {
            sendUsage(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "server":
            {
                if (args.length != 2) {
                    sender.sendMessage("screen <server> <name>");
                    return;
                }
                MinecraftServerInfo serverInfo = NeverCloudNode.getInstance().getMinecraftServerInfo(args[1]);
                if (serverInfo == null) {
                    sender.createLanguageMessage("command.screen.server.notFound").replace("%name%", args[1]).send();
                    return;
                }

                if (this.enabledScreen != null) {
                    NeverCloudNode.getInstance().getScreenManager().disableScreen(enabledScreen);
                }

                this.enabledScreen = NeverCloudNode.getInstance().getScreenManager().enableScreen(serverInfo, s -> {
                    sender.sendMessage("SCREEN -> " + serverInfo.getComponentName() + ": " + s);
                });
                sender.createLanguageMessage("command.screen.server.successful").replace("%name%", args[1]).send();
            }
            break;

            case "proxy":
            {
                if (args.length != 2) {
                    sender.sendMessage("screen proxy <name>");
                    return;
                }
                BungeeCordProxyInfo serverInfo = NeverCloudNode.getInstance().getBungeeProxyInfo(args[1]);
                if (serverInfo == null) {
                    sender.createLanguageMessage("command.screen.proxy.notFound").replace("%name%", args[1]).send();
                    return;
                }

                if (this.enabledScreen != null) {
                    NeverCloudNode.getInstance().getScreenManager().disableScreen(enabledScreen);
                }

                this.enabledScreen = NeverCloudNode.getInstance().getScreenManager().enableScreen(serverInfo, s -> {
                    sender.sendMessage("SCREEN -> " + serverInfo.getComponentName() + ": " + s);
                });
                sender.createLanguageMessage("command.screen.proxy.successful").replace("%name%", args[1]).send();
            }
            break;

            case "leave":
            {
                if (args.length != 1) {
                    sender.sendMessage("screen leave");
                    return;
                }

                if (this.enabledScreen == null) {
                    sender.sendMessageLanguageKey("command.screen.leave.notEnabled");
                    return;
                }

                NeverCloudNode.getInstance().getScreenManager().disableScreen(this.enabledScreen);
                this.enabledScreen = null;
            }
            break;

            case "write":
            {
                if (args.length < 2) {
                    sender.sendMessage("screen write <command>");
                    return;
                }
                if (this.enabledScreen == null) {
                    sender.sendMessageLanguageKey("command.screen.write.notEnabled");
                    return;
                }

                StringBuilder command = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    command.append(args[i]).append(' ');
                }
                this.enabledScreen.write(command.substring(0, command.length() - 1));
                sender.createLanguageMessage("command.screen.write.success").replace("%name%", this.enabledScreen.getComponentName()).send();
            }
            break;

            default:
                sendUsage(sender);
                break;
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(
                "screen server <name",
                "screen proxy <name>",
                "screen leave",
                "screen write"
        );
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String commandLine, String[] args) {
        return args.length == 1 ? Arrays.asList("server", "proxy", "leave", "write") :
                args.length == 2 ?
                        args[0].equalsIgnoreCase("server") ? this.findServers() :
                                args[0].equalsIgnoreCase("proxy") ? this.findProxies() :
                                        Collections.emptyList() :
                        Collections.emptyList();
    }

    private Collection<String> findServers() {
        Collection<String> a = new ArrayList<>(NeverCloudNode.getInstance().getServersOnThisNode().keySet());
        a.addAll(NeverCloudNode.getInstance().getProcessManager().getProcesses().values().stream().filter(process -> process instanceof ServerProcess).map(CloudProcess::getName).collect(Collectors.toList()));
        for (NodeParticipant value : NeverCloudNode.getInstance().getServerNodes().values()) {
            a.addAll(value.getServers().keySet());
            a.addAll(value.getStartingServers().keySet());
        }
        return a;
    }

    private Collection<String> findProxies() {
        Collection<String> a = new ArrayList<>(NeverCloudNode.getInstance().getProxiesOnThisNode().keySet());
        a.addAll(NeverCloudNode.getInstance().getProcessManager().getProcesses().values().stream().filter(process -> process instanceof BungeeProcess).map(CloudProcess::getName).collect(Collectors.toList()));
        for (NodeParticipant value : NeverCloudNode.getInstance().getServerNodes().values()) {
            a.addAll(value.getProxies().keySet());
            a.addAll(value.getStartingProxies().keySet());
        }
        return a;
    }
}
