package net.nevercloud.node.updater;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThreadLocalRandom;
import net.md_5.bungee.http.HttpClient;
import net.nevercloud.lib.config.json.SimpleJsonObject;
import net.nevercloud.lib.utility.SystemUtils;
import net.nevercloud.node.NeverCloudNode;
import net.nevercloud.node.utility.FileDownloading;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AutoUpdaterManager {

    public void checkUpdates(Consumer<UpdateCheckResponse> consumer) {
        HttpClient.get(SystemUtils.CENTRAL_SERVER_URL + "updatecheck?version=" + SystemUtils.getCurrentVersion(), null, (s, throwable) -> {
            if (throwable != null) {
                consumer.accept(null);
                throwable.printStackTrace();
                return;
            }
            SimpleJsonObject jsonObject = new SimpleJsonObject(s);
            if (jsonObject.getBoolean("success")) {
                UpdateCheckResponse response = new UpdateCheckResponse(
                        jsonObject.getInt("versionsBehind"),
                        jsonObject.getString("newestVersion"),
                        jsonObject.getBoolean("upToDate")
                );
                consumer.accept(response);
            } else {
                consumer.accept(null);
                System.out.println(NeverCloudNode.getInstance().getLanguagesManager().getMessage("autoupdate.checkFailed").replace("%reason%", jsonObject.getString("reason")));
            }
        });
    }

    public void update(BiConsumer<Boolean, Path> consumer) {
        if (PlatformDependent.isWindows()) {
            Path path = Paths.get(SystemUtils.getPathOfInternalJarFile().replaceFirst(".jar", "") + "-update-" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE) + ".jar");
            consumer.accept(FileDownloading.downloadFileWithProgressBar(NeverCloudNode.getInstance().getLogger(), SystemUtils.CENTRAL_SERVER_URL + "updatenode", path,
                    () -> {
                    }, () -> {
                    }), path);
        } else {
            consumer.accept(FileDownloading.downloadFileWithProgressBar(NeverCloudNode.getInstance().getLogger(), SystemUtils.CENTRAL_SERVER_URL + "updatenode", Paths.get(SystemUtils.getPathOfInternalJarFile()),
                    () -> {
                    }, () -> {
                    }), null);
        }

    }

}
