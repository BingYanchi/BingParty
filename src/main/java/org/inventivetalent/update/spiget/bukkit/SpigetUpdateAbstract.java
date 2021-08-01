/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package org.inventivetalent.update.spiget.bukkit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.inventivetalent.update.spiget.bukkit.comparator.VersionComparator;

public abstract class SpigetUpdateAbstract {
    public static final String RESOURCE_INFO = "https://api.spiget.org/v2/resources/%s?ut=%s";
    public static final String RESOURCE_VERSION = "https://api.spiget.org/v2/resources/%s/versions/latest?ut=%s";
    protected final int resourceId;
    protected final String currentVersion;
    protected final Logger log;
    protected String userAgent = "SpigetResourceUpdater";
    protected VersionComparator versionComparator = VersionComparator.EQUAL;
    protected ResourceInfo latestResourceInfo;

    public SpigetUpdateAbstract(int resourceId, String currentVersion, Logger log) {
        this.resourceId = resourceId;
        this.currentVersion = currentVersion;
        this.log = log;
    }

    public SpigetUpdateAbstract setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public SpigetUpdateAbstract setVersionComparator(VersionComparator comparator) {
        this.versionComparator = comparator;
        return this;
    }

    public ResourceInfo getLatestResourceInfo() {
        return this.latestResourceInfo;
    }

    protected abstract void dispatch(Runnable var1);

    public boolean isVersionNewer(String oldVersion, String newVersion) {
        return this.versionComparator.isNewer(oldVersion, newVersion);
    }

    public void checkForUpdate(final UpdateCallback callback) {
        this.dispatch(new Runnable(){

            @Override
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection)new URL(String.format(SpigetUpdateAbstract.RESOURCE_INFO, SpigetUpdateAbstract.this.resourceId, System.currentTimeMillis())).openConnection();
                    connection.setRequestProperty("User-Agent", SpigetUpdateAbstract.this.getUserAgent());
                    JsonObject jsonObject = new JsonParser().parse((Reader)new InputStreamReader(connection.getInputStream())).getAsJsonObject();
                    SpigetUpdateAbstract.this.latestResourceInfo = (ResourceInfo)new Gson().fromJson((JsonElement)jsonObject, ResourceInfo.class);
                    connection = (HttpURLConnection)new URL(String.format(SpigetUpdateAbstract.RESOURCE_VERSION, SpigetUpdateAbstract.this.resourceId, System.currentTimeMillis())).openConnection();
                    connection.setRequestProperty("User-Agent", SpigetUpdateAbstract.this.getUserAgent());
                    jsonObject = new JsonParser().parse((Reader)new InputStreamReader(connection.getInputStream())).getAsJsonObject();
                    SpigetUpdateAbstract.this.latestResourceInfo.latestVersion = (ResourceVersion)new Gson().fromJson((JsonElement)jsonObject, ResourceVersion.class);
                    if (SpigetUpdateAbstract.this.isVersionNewer(SpigetUpdateAbstract.this.currentVersion, SpigetUpdateAbstract.this.latestResourceInfo.latestVersion.name)) {
                        callback.updateAvailable(SpigetUpdateAbstract.this.latestResourceInfo.latestVersion.name, "https://spigotmc.org/" + SpigetUpdateAbstract.this.latestResourceInfo.file.url, !SpigetUpdateAbstract.this.latestResourceInfo.external && !SpigetUpdateAbstract.this.latestResourceInfo.premium);
                    } else {
                        callback.upToDate();
                    }
                }
                catch (Exception e) {
                    SpigetUpdateAbstract.this.log.log(Level.WARNING, "Failed to get resource info from spiget.org", e);
                }
            }
        });
    }
}

