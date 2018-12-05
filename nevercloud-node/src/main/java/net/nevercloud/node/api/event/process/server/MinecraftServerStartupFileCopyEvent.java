package net.nevercloud.node.api.event.process.server;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import lombok.*;
import net.nevercloud.lib.server.minecraft.MinecraftServerInfo;
import net.nevercloud.node.api.event.process.ProcessEvent;
import net.nevercloud.node.server.process.CloudProcess;

import java.io.InputStream;

@Getter
/**
 * Called when the server.jar is copied into a new process, before it's starting up
 */
public class MinecraftServerStartupFileCopyEvent extends ProcessEvent {
    private MinecraftServerInfo serverInfo;
    @Setter
    private InputStream inputStream;

    public MinecraftServerStartupFileCopyEvent(CloudProcess cloudProcess, MinecraftServerInfo serverInfo, InputStream inputStream) {
        super(cloudProcess);
        this.serverInfo = serverInfo;
        this.inputStream = inputStream;
    }
}
