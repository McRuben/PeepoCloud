package net.peepocloud.node.server.process.handler;
/*
 * Created by Mc_Ruben on 08.12.2018
 */

import lombok.RequiredArgsConstructor;
import net.peepocloud.lib.utility.SystemUtils;
import net.peepocloud.node.server.process.BungeeProcess;
import net.peepocloud.node.api.server.CloudProcess;
import net.peepocloud.node.server.process.CloudProcessImpl;
import net.peepocloud.node.server.process.ProcessManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ProcessLogHandler implements Runnable {
    private final ProcessManager processManager;

    private StringBuffer stringBuffer = new StringBuffer();

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            for (CloudProcessImpl value : this.processManager.getProcesses().values()) {
                this.readLog(value);
            }
            SystemUtils.sleepUninterruptedly(50);
        }
    }

    private void readLog(CloudProcessImpl process) {
        try {
            if (!process.isRunning())
                return;

            InputStream inputStream = process.getProcess().getInputStream();

            byte[] buf = new byte[1024];
            int len;
            while (inputStream.available() > 0 && (len = inputStream.read(buf, 0, buf.length)) != -1) {
                stringBuffer.append(new String(buf, 0, len, StandardCharsets.UTF_8));
            }

            for (String b : stringBuffer.toString().split("\n")) {
                for (String line : b.split("\r")) {
                    if (process instanceof BungeeProcess) {
                        if (line.startsWith(">"))
                            line = line.substring(1);
                    }
                    String a = line.trim();
                    if (a.isEmpty() || a.equals("\n"))
                        continue;
                    process.getCachedLog().add(line);
                    for (Consumer<String> value : process.getScreenHandlers().values()) {
                        value.accept(line);
                    }
                    if (process.getNetworkScreenHandler() != null) {
                        process.getNetworkScreenHandler().accept(line);
                    }
                }
            }
            stringBuffer.setLength(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
