/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package org.inventivetalent.update.spiget.bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.update.spiget.bukkit.SpigetUpdateAbstract;
import org.inventivetalent.update.spiget.bukkit.comparator.VersionComparator;
import org.inventivetalent.update.spiget.bukkit.download.DownloadCallback;
import org.inventivetalent.update.spiget.bukkit.download.UpdateDownloader;

public class SpigetUpdate
extends SpigetUpdateAbstract {
    protected final Plugin plugin;
    protected DownloadFailReason failReason = DownloadFailReason.UNKNOWN;

    public SpigetUpdate(Plugin plugin, int resourceId) {
        super(resourceId, plugin.getDescription().getVersion(), plugin.getLogger());
        this.plugin = plugin;
        this.setUserAgent("SpigetResourceUpdater/Bukkit");
    }

    @Override
    public SpigetUpdate setUserAgent(String userAgent) {
        super.setUserAgent(userAgent);
        return this;
    }

    @Override
    public SpigetUpdate setVersionComparator(VersionComparator comparator) {
        super.setVersionComparator(comparator);
        return this;
    }

    @Override
    protected void dispatch(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    public boolean downloadUpdate() {
        boolean allowExternalDownload;
        if (this.latestResourceInfo == null) {
            this.failReason = DownloadFailReason.NOT_CHECKED;
            return false;
        }
        if (!this.isVersionNewer(this.currentVersion, this.latestResourceInfo.latestVersion.name)) {
            this.failReason = DownloadFailReason.NO_UPDATE;
            return false;
        }
        if (this.latestResourceInfo.external) {
            this.failReason = DownloadFailReason.NO_DOWNLOAD;
            return false;
        }
        File pluginFile = this.getPluginFile();
        if (pluginFile == null) {
            this.failReason = DownloadFailReason.NO_PLUGIN_FILE;
            return false;
        }
        File updateFolder = Bukkit.getUpdateFolderFile();
        if (!updateFolder.exists() && !updateFolder.mkdirs()) {
            this.failReason = DownloadFailReason.NO_UPDATE_FOLDER;
            return false;
        }
        final File updateFile = new File(updateFolder, pluginFile.getName());
        Properties properties = this.getUpdaterProperties();
        allowExternalDownload = properties != null && properties.containsKey("externalDownloads") && Boolean.valueOf(properties.getProperty("externalDownloads")) != false;
        if (!allowExternalDownload && this.latestResourceInfo.external) {
            this.failReason = DownloadFailReason.EXTERNAL_DISALLOWED;
            return false;
        }
        this.log.info("[SpigetUpdate] Downloading update...");
        this.dispatch(UpdateDownloader.downloadAsync(this.latestResourceInfo, updateFile, this.getUserAgent(), new DownloadCallback(){

            @Override
            public void finished() {
                SpigetUpdate.this.log.info("[SpigetUpdate] Update saved as " + updateFile.getPath());
            }

            @Override
            public void error(Exception exception) {
                SpigetUpdate.this.log.log(Level.WARNING, "[SpigetUpdate] Could not download update", exception);
            }
        }));
        return true;
    }

    public DownloadFailReason getFailReason() {
        return this.failReason;
    }

    public Properties getUpdaterProperties() {
        File file = new File(Bukkit.getUpdateFolderFile(), "spiget.properties");
        Properties properties = new Properties();
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return null;
                }
                properties.setProperty("externalDownloads", "false");
                properties.store(new FileWriter(file), "Configuration for the Spiget auto-updater. https://spiget.org | https://github.com/InventivetalentDev/SpigetUpdater\nUse 'externalDownloads' if you want to auto-download resources hosted on external sites\n");
            }
            catch (Exception ignored) {
                return null;
            }
        }
        try {
            properties.load(new FileReader(file));
        }
        catch (IOException e) {
            return null;
        }
        return properties;
    }

    private File getPluginFile() {
        if (!(this.plugin instanceof JavaPlugin)) {
            return null;
        }
        try {
            Method method = JavaPlugin.class.getDeclaredMethod("getFile", new Class[0]);
            method.setAccessible(true);
            return (File)method.invoke(this.plugin, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not get plugin file", e);
        }
    }

    public static enum DownloadFailReason {
        NOT_CHECKED,
        NO_UPDATE,
        NO_DOWNLOAD,
        NO_PLUGIN_FILE,
        NO_UPDATE_FOLDER,
        EXTERNAL_DISALLOWED,
        UNKNOWN;

    }
}

