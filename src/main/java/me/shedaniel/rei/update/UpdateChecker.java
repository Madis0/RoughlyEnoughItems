package me.shedaniel.rei.update;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import me.shedaniel.rei.client.ConfigHelper;
import org.apache.commons.io.IOUtils;
import org.dimdev.riftloader.listener.InitializationListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateChecker implements InitializationListener {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Version CURRENT_VERSION = new Version("2.2.0.16");
    private static final String CURRENT_GAME_VERSION = "1.13";
    private static Version latestForGame = null;
    private static JsonVersionElement element;
    private static String VERSION_STRING = "https://raw.githubusercontent.com/shedaniel/RoughlyEnoughItems/1.13.2-rewrite/version.json";
    
    public static boolean isOutdated() {
        return latestForGame.compareTo(CURRENT_VERSION) == 1 && CURRENT_VERSION != null;
    }
    
    public static JsonVersionElement getElement() {
        return element;
    }
    
    public static UpdatePriority getUpdatePriority(List<Version> versions) {
        UpdatePriority p = UpdatePriority.NONE;
        List<UpdatePriority> priorities = Arrays.asList(UpdatePriority.values());
        for(UpdatePriority priority : versions.stream().map(UpdateChecker::getUpdatePriority).collect(Collectors.toList()))
            if (priority.compareTo(p) > 0)
                p = priority;
        return p;
    }
    
    public static UpdatePriority getUpdatePriority(Version version) {
        JsonArray array = element.getChangelogs().getRift();
        for(JsonElement element : array) {
            JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject.has("version") && jsonObject.get("version").getAsString().equals(version.toString()))
                return UpdatePriority.fromString(jsonObject.get("level").getAsString());
        }
        return UpdatePriority.NONE;
    }
    
    public static boolean checkUpdates() {
        return ConfigHelper.getInstance().checkUpdates();
    }
    
    public static Version getCurrentVersion() {
        return CURRENT_VERSION;
    }
    
    public static Version getLatestForGame() {
        return latestForGame;
    }
    
    static List<Version> getVersionsHigherThan(Version currentVersion) {
        List<Version> versions = Lists.newLinkedList();
        JsonArray array = element.getChangelogs().getRift();
        array.forEach(jsonElement -> {
            Version jsonVersion = new Version(jsonElement.getAsJsonObject().get("version").getAsString());
            if (jsonVersion.compareTo(currentVersion) > 0)
                versions.add(jsonVersion);
        });
        return versions;
    }
    
    private static InputStream downloadVersionString() {
        try {
            URL versionUrl = new URL(VERSION_STRING);
            return versionUrl.openStream();
        } catch (IOException e) {
            return new StringBufferInputStream("{}");
        }
    }
    
    private static String parseLatest(JsonVersionElement element, String gameVersion) {
        List<LatestVersionObject> objects = new LinkedList<>(element.getLatestVersions());
        for(int i = objects.size() - 1; i >= 0; i--)
            if (objects.get(i).getGameVersion().equals(gameVersion))
                return objects.get(i).getModVersion();
        return objects.get(objects.size() - 1).getModVersion();
    }
    
    @Override
    public void onInitialization() {
        if (!checkUpdates())
            return;
        InputStream downloadedStream = downloadVersionString();
        String downloadedString = null;
        try {
            downloadedString = IOUtils.toString(downloadedStream, StandardCharsets.UTF_8);
            element = GSON.fromJson(downloadedString, JsonVersionElement.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (downloadedString != null && !downloadedString.equalsIgnoreCase("{}"))
            latestForGame = new Version(parseLatest(element, CURRENT_GAME_VERSION));
        else
            latestForGame = new Version("0.0.0");
    }
    
    static class JsonVersionElement {
        @SerializedName("latest")
        private List<LatestVersionObject> latestVersions;
        private ChangelogObject changelogs;
        
        public JsonVersionElement() {
            this.latestVersions = Lists.newArrayList();
            changelogs = new ChangelogObject();
        }
        
        public List<LatestVersionObject> getLatestVersions() {
            return latestVersions;
        }
        
        public ChangelogObject getChangelogs() {
            return changelogs;
        }
    }
    
    static class LatestVersionObject {
        @SerializedName("game")
        private String gameVersion = "";
        @SerializedName("mod")
        private String modVersion = "";
        
        public String getGameVersion() {
            return gameVersion;
        }
        
        public String getModVersion() {
            return modVersion;
        }
        
        @Override
        public String toString() {
            return String.format("LatestVersion[%s] = %s", getGameVersion(), getModVersion());
        }
    }
    
    static class ChangelogObject {
        private JsonArray fabric = new JsonArray();
        private JsonArray rift = new JsonArray();
        
        public JsonArray getFabric() {
            return fabric;
        }
        
        public JsonArray getRift() {
            return rift;
        }
    }
    
}