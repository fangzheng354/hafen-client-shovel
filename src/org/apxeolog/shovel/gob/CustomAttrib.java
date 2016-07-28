package org.apxeolog.shovel.gob;

import haven.*;
import org.apxeolog.shovel.highlight.HighlightOption;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by APXEOLOG on 15.09.2015.
 */
public class CustomAttrib extends GAttrib {
    public CustomAttrib(Gob gob) {
        super(gob);
    }

    public void draw(GOut g, Coord position) {

    }

    public static class ReadyHideOrTubeAttrib extends CustomAttrib {
        private static final TexI cachedFont;
        public int height = 0;

        static {
            cachedFont = Utils.renderOutlinedFont(Text.std, "100%", new Color(0, 153, 153, 255), Color.BLACK, 2);
        }

        public ReadyHideOrTubeAttrib(Gob gob) {
            this(gob, 100);
        }

        @Override
        public void draw(GOut g, Coord position) {
            g.aimage(cachedFont, position, 0.5, 1);
        }

        public ReadyHideOrTubeAttrib(Gob gob, int val) {
            super(gob);
            height = val;
        }
    }

    public static class HighlightAttrib extends CustomAttrib {
        public HighlightOption highlightOption;

        public HighlightAttrib(Gob gob, HighlightOption highlightOption) {
            super(gob);
            this.highlightOption = highlightOption;
        }
    }

    public static class CropStageAttrib extends CustomAttrib {
        private static final HashMap<Integer, TexI> stageCache = new HashMap<>();
        public int stage;

        public CropStageAttrib(Gob gob, int stage) {
            super(gob);
            this.stage = stage;
        }

        @Override
        public void draw(GOut g, Coord position) {
            TexI cachedFont = stageCache.get(stage);
            if (cachedFont == null) {
                cachedFont = Utils.renderTextPatch(Text.std, String.valueOf(stage), new Color(0, 255, 0, 255), new Coord(18, 18));
                stageCache.put(stage, cachedFont);
            }
            g.aimage(cachedFont, position, 0.5, 1);
        }

        @Override
        public Object staticp() {
            return null;
        }
    }
}
