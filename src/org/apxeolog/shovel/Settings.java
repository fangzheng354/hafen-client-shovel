package org.apxeolog.shovel;

import haven.Coord;

import java.util.HashMap;

/**
 * Created by APXEOLOG on 31/08/2015.
 * Settings class
 */
public class Settings {
    public boolean dumpMinimaps = true;
    public boolean alwaysShowNickname = true;
    public boolean showFlavor = false;
    public boolean nightvision = true;
    public boolean showQuality = true;
    public boolean showFriendNotifications = true;
    public boolean enableGroupHotkeys = true;
    public boolean showReadyHideAndLeather = true;
    public boolean showObjectsHealth = true;
    public boolean allowLookSuspiciously = true;
    public boolean drawMinimapGrid = true;
    public boolean drawMinimapFOV = true;
    public boolean enableSimpleCrops = true;
    public boolean showCropStages = true;
    public boolean debugWidgets = false;
    public boolean studyAtMinimap = false;
    public boolean showServerTime = false;
    public boolean showMeterValues = false;
    public boolean showPlayersPaths = false;
    public boolean showRadiuses = false;
    public boolean disableClouds = false;
    public boolean disableCrops = false;
    public boolean showFepAndHunger = false;
    public boolean lockStudy = false;
    public boolean autoStudy = false;
    public boolean simpleTiles = false;
    public boolean tileCentering = false;
    public boolean enableHide = false;
    public boolean restrictAnimations = false;
    public QualityDisplayType qualityDisplayType = QualityDisplayType.MAX;

    // Window position and size
    public HashMap<String, WindowData> windows;

    // Runtime options, don't save them
    transient public boolean debugMode = false;
    transient public boolean checkedForNewReleases = false;

    public static class WindowData {
        public Coord position;
        public Coord size;
    }

    public void setWindowData(String name, Coord position, Coord size) {
        WindowData data = windows.get(name);
        if (data == null) {
            data = new WindowData();
            windows.put(name, data);
        }
        if (position != null)
            data.position = position;
        if (size != null)
            data.size = size;
    }

    public enum QualityDisplayType {
        MAX, AVG
    }

    public Settings() {
        windows = new HashMap<>();
    }
}
