package org.apxeolog.shovel.render;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by APXEOLOG on 24.03.2016.
 */
public class HideList {
    public ArrayList<String> masks;
    public transient ArrayList<Pattern> patterns;

    public static HideList getDefault() {
        HideList hideList = new HideList();
        hideList.masks = new ArrayList<>();
        hideList.masks.add("gfx/kritter/.+");
        hideList.masks.add("gfx/terobjs/trees/.+");
        hideList.masks.add("gfx/terobjs/plants/.+");
        hideList.masks.add("gfx/terobjs/bushes/.+");
        hideList.init();
        return hideList;
    }

    public void init() {
        patterns = new ArrayList<>();
        if (masks == null) return;
        for (String mask : masks) {
            patterns.add(Pattern.compile(mask));
        }
    }
}
