package org.apxeolog.shovel.render;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by APXEOLOG on 13.04.2016.
 */
public class AnimationSuspendList {
    public ArrayList<String> forbiddenObjects;
    public transient ArrayList<Pattern> patterns;

    public void init(){
        patterns = new ArrayList<>(forbiddenObjects.size());
        for (String name : forbiddenObjects) {
            patterns.add(Pattern.compile(name));
        }
    }

    public static AnimationSuspendList getDefault() {
        AnimationSuspendList animationSuspendList = new AnimationSuspendList();
        animationSuspendList.forbiddenObjects = new ArrayList<>();
        animationSuspendList.forbiddenObjects.add("gfx/kritter/cattle/.*");
        animationSuspendList.forbiddenObjects.add("gfx/kritter/sheep/.*");
        animationSuspendList.forbiddenObjects.add("gfx/kritter/pig/.*");
        animationSuspendList.forbiddenObjects.add("gfx/terobjs/pow");
        animationSuspendList.forbiddenObjects.add("gfx/terobjs/dreca");
        animationSuspendList.forbiddenObjects.add("gfx/terobjs/beehive");
        animationSuspendList.forbiddenObjects.add("gfx/terobjs/villageidol");
        animationSuspendList.forbiddenObjects.add("gfx/terobjs/stockpile-trash");
        animationSuspendList.init();
        return animationSuspendList;
    }
}
