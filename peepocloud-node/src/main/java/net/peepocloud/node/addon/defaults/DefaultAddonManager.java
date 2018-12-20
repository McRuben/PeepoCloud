package net.peepocloud.node.addon.defaults;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import net.md_5.bungee.http.HttpClient;
import net.peepocloud.commons.config.json.SimpleJsonObject;
import net.peepocloud.commons.utility.SystemUtils;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.addon.node.NodeAddon;
import net.peepocloud.node.utility.FileDownloading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.function.Consumer;

public class DefaultAddonManager {

    public void getDefaultAddons(Consumer<Collection<DefaultAddonConfig>> consumer) {
        HttpClient.get(SystemUtils.CENTRAL_SERVER_URL + "addons", null, (s, throwable) -> {
            if (throwable != null) {
                consumer.accept(null);
                throwable.printStackTrace();
            } else {
                consumer.accept(SimpleJsonObject.GSON.fromJson(s, new TypeToken<Collection<DefaultAddonConfig>>() {
                }.getType()));
            }
        });
    }

    public void getDefaultAddon(String name, Consumer<DefaultAddonConfig> consumer) {
        getDefaultAddons(defaultAddonConfigs -> {
            for (DefaultAddonConfig defaultAddonConfig : defaultAddonConfigs) {
                if (defaultAddonConfig.getName().equalsIgnoreCase(name)) {
                    consumer.accept(defaultAddonConfig);
                    return;
                }
            }
            consumer.accept(null);
        });
    }

    public InstallAddonResult installAddon(DefaultAddonConfig defaultAddonConfig) {
        Path path = Paths.get("nodeAddons/" + defaultAddonConfig.getName() + "-" + defaultAddonConfig.getVersion() + ".jar");
        if (Files.exists(path))
            return InstallAddonResult.ADDON_ALREADY_INSTALLED;

        if (!FileDownloading.downloadFileWithProgressBar(PeepoCloudNode.getInstance().getLogger(), SystemUtils.CENTRAL_SERVER_URL + "file?addonName=" + defaultAddonConfig.getName() + "&addonVersion=" + defaultAddonConfig.getVersion(), path, null, null))
            return InstallAddonResult.DOWNLOAD_FAILED;
        if (!PeepoCloudNode.getInstance().getNodeAddonManager().loadAndEnableAddon(path))
            return InstallAddonResult.ADDON_LOAD_FAILED;
        return InstallAddonResult.SUCCESS;
    }

    public boolean uninstallAddon(DefaultAddonConfig defaultAddonConfig) {
        Path path = Paths.get("nodeAddons/" + defaultAddonConfig.getName() + "-" + defaultAddonConfig.getVersion() + ".jar");
        if (!Files.exists(path))
            return false;

        NodeAddon addon = PeepoCloudNode.getInstance().getNodeAddonManager().getAddonByFileName(path.getFileName().toString());

        if (addon == null)
            return false;

        PeepoCloudNode.getInstance().getNodeAddonManager().unloadAddon(addon);

        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public UpdateAddonResult updateAddon(DefaultAddonConfig defaultAddonConfig) {
        NodeAddon addon = PeepoCloudNode.getInstance().getNodeAddonManager().getAddonByName(defaultAddonConfig.getName());
        if (addon == null)
            return UpdateAddonResult.ADDON_NOT_FOUND;
        if (addon.getAddonConfig().getVersion().equals(defaultAddonConfig.getVersion()))
            return UpdateAddonResult.ADDON_UP_TO_DATE;

        {
            PeepoCloudNode.getInstance().getNodeAddonManager().unloadAddon(addon);
            Path path = Paths.get("nodeAddons/" + addon.getAddonConfig().getFileName());
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        {
            Path path = Paths.get("nodeAddons/" + defaultAddonConfig.getName() + "-" + defaultAddonConfig.getVersion() + ".jar");
            if (Files.exists(path))
                return UpdateAddonResult.ADDON_ALREADY_INSTALLED;

            if (!HttpClient.downloadFile(SystemUtils.CENTRAL_SERVER_URL + "file?addonName=" + defaultAddonConfig.getName() + "&addonVersion=" + defaultAddonConfig.getVersion(), path))
                return UpdateAddonResult.DOWNLOAD_FAILED;

            if (!PeepoCloudNode.getInstance().getNodeAddonManager().loadAndEnableAddon(path))
                return UpdateAddonResult.ADDON_LOAD_FAILED;
        }

        return UpdateAddonResult.SUCCESS;
    }

    @AllArgsConstructor
    public static enum UpdateAddonResult {

        ADDON_NOT_FOUND("addons.defaults.update.notInstalled"),
        ADDON_UP_TO_DATE("addons.defaults.update.alreadyUpToDate"),
        DOWNLOAD_FAILED("addons.defaults.update.downloadFailed"),
        ADDON_ALREADY_INSTALLED("addons.defaults.update.alreadyInstalled"),
        ADDON_LOAD_FAILED("addons.defaults.update.loadFailed"),
        SUCCESS("addons.defaults.update.success");

        private String key;

        public String formatMessage(DefaultAddonConfig addonConfig) {
            String message = PeepoCloudNode.getInstance().getLanguagesManager().getMessage(key);

            if (this == DOWNLOAD_FAILED) {
                return String.format(message, addonConfig.getName(), addonConfig.getVersion());
            }
            return message;
        }

    }

    @AllArgsConstructor
    public static enum InstallAddonResult {

        ADDON_ALREADY_INSTALLED("addons.defaults.install.alreadyInstalled"),
        DOWNLOAD_FAILED("addons.defaults.install.downloadFailed"),
        ADDON_LOAD_FAILED("addons.defaults.install.loadFailed"),
        SUCCESS("addons.defaults.install.success");

        private String key;

        public String formatMessage(DefaultAddonConfig addonConfig) {
            String message = PeepoCloudNode.getInstance().getLanguagesManager().getMessage(key);

            if (this == DOWNLOAD_FAILED) {
                return String.format(message, addonConfig.getName(), addonConfig.getVersion());
            }
            return message;
        }

    }

}
