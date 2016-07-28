package org.apxeolog.shovel.widget;

import haven.*;
import haven.Button;
import haven.Label;
import haven.Window;
import org.apxeolog.shovel.ALS;
import org.apxeolog.shovel.Settings;
import org.apxeolog.shovel.Shovel;
import org.apxeolog.shovel.highlight.HighlightGroup;
import org.apxeolog.shovel.highlight.HighlightManager;
import org.apxeolog.shovel.highlight.HighlightOption;

import java.awt.*;

/**
 * Created by APXEOLOG on 31/08/2015.
 */
public class ConfigurationWnd extends Window {
    public ConfigurationWnd() {
        super(new Coord(400, 450), "Shovel Settings");

        final Tabs tabs = new Tabs(new Coord(15, 50), Coord.z, this);
        Tabs.Tab generalSettings;
        {
            generalSettings = tabs.add();
            generalSettings.add(new CheckBox("Always show nicknames", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().alwaysShowNickname = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().alwaysShowNickname), new Coord(15, 25));
            generalSettings.add(new CheckBox("Save minimaps", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().dumpMinimaps = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().dumpMinimaps), new Coord(15, 50));
            generalSettings.add(new CheckBox("Enable flavor objects", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showFlavor = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().showFlavor), new Coord(15, 75));
            generalSettings.add(new CheckBox("Enable friends notification", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showFriendNotifications = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().showFriendNotifications), new Coord(15, 100));
            generalSettings.add(new CheckBox("Show items quality", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showQuality = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().showQuality), new Coord(15, 125));
            generalSettings.add(new CheckBox("Enable group hotkeys", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().enableGroupHotkeys = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().enableGroupHotkeys), new Coord(15, 150));
            generalSettings.add(new CheckBox("Show dried hide and leather", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showReadyHideAndLeather = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().showReadyHideAndLeather), new Coord(15, 175));
            generalSettings.add(new CheckBox("Show objects health", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showObjectsHealth = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().showObjectsHealth), new Coord(15, 200));
            generalSettings.add(new CheckBox("Allow look suspiciously :D", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().allowLookSuspiciously = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().allowLookSuspiciously), new Coord(15, 225));
            generalSettings.add(new CheckBox("Enable simple crops (need restart)", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().enableSimpleCrops = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().enableSimpleCrops), new Coord(15, 250));
            generalSettings.add(new CheckBox("Draw minimap grid", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().drawMinimapGrid = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().drawMinimapGrid), new Coord(15, 275));
            generalSettings.add(new CheckBox("Draw minimap FOV", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().drawMinimapFOV = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().drawMinimapFOV), new Coord(15, 300));
            generalSettings.add(new CheckBox("Show crops/trees/bushes stages", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showCropStages = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().showCropStages), new Coord(15, 325));

            CheckBox chckQMax = new CheckBox("Show MAX quality", false) {
                @Override
                public void changed(boolean val) {
                    if (val == true) {
                        if (linked.size() > 0) linked.get(0).a = false;
                        Shovel.getSettings().qualityDisplayType = Settings.QualityDisplayType.MAX;
                        Shovel.saveSettings();
                    } else {
                        if (linked.size() > 0) linked.get(0).a = true;
                        Shovel.getSettings().qualityDisplayType = Settings.QualityDisplayType.AVG;
                        Shovel.saveSettings();
                    }
                }
            }.chainSet(Shovel.getSettings().qualityDisplayType == Settings.QualityDisplayType.MAX);
            generalSettings.add(chckQMax, new Coord(200, 25));
            CheckBox chckQAvg = new CheckBox("Show AVG quality", false) {
                @Override
                public void changed(boolean val) {
                    if (val == true) {
                        if (linked.size() > 0) linked.get(0).a = false;
                        Shovel.getSettings().qualityDisplayType = Settings.QualityDisplayType.AVG;
                        Shovel.saveSettings();
                    } else {
                        if (linked.size() > 0) linked.get(0).a = true;
                        Shovel.getSettings().qualityDisplayType = Settings.QualityDisplayType.MAX;
                        Shovel.saveSettings();
                    }
                }
            }.chainSet(Shovel.getSettings().qualityDisplayType == Settings.QualityDisplayType.AVG);
            generalSettings.add(chckQAvg, new Coord(200, 50));
            generalSettings.add(new CheckBox("Study at bottom left (need restart)", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().studyAtMinimap = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().studyAtMinimap), new Coord(200, 75));
            generalSettings.add(new CheckBox("Display ingame time", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showServerTime = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().showServerTime), new Coord(200, 100));
            generalSettings.add(new CheckBox("Show meters values", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showMeterValues = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().showMeterValues), new Coord(200, 125));
            generalSettings.add(new CheckBox("Show players paths", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showPlayersPaths = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().showPlayersPaths), new Coord(200, 150));
            generalSettings.add(new CheckBox("Disable clouds", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().disableClouds = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().disableClouds), new Coord(200, 175));
            generalSettings.add(new CheckBox("Show FEP and Hunger bars", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().showFepAndHunger = val;
                    Shovel.saveSettings();
                    if (val) {
                        if (GameUI.instance != null) {
                            if (GameUI.instance.fepMeter != null)
                                GameUI.instance.fepMeter.show();
                            if (GameUI.instance.hungerMeter != null)
                                GameUI.instance.hungerMeter.show();
                        }
                    } else {
                        if (GameUI.instance != null) {
                            if (GameUI.instance.fepMeter != null)
                                GameUI.instance.fepMeter.hide();
                            if (GameUI.instance.hungerMeter != null)
                                GameUI.instance.hungerMeter.hide();
                        }
                    }
                }
            }.chainSet(Shovel.getSettings().showFepAndHunger), new Coord(200, 200));
            generalSettings.add(new CheckBox("Simple tiles (on map load)", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().simpleTiles = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().simpleTiles), new Coord(200, 225));
            generalSettings.add(new CheckBox("Restrict animations (need restart)", false) {
                @Override
                public void changed(boolean val) {
                    Shovel.getSettings().restrictAnimations = val;
                    Shovel.saveSettings();
                }
            }.chainSet(Shovel.getSettings().restrictAnimations), new Coord(200, 250));
            chckQMax.linked.add(chckQAvg);
            chckQAvg.linked.add(chckQMax);
        }
        Tabs.Tab highlightSettings;
        {
            highlightSettings = tabs.add();
            highlightSettings.add(new Label("Select group"), new Coord(10, 0));
            highlightSettings.add(new Label("Enable or disable icon (dblclick works too)"), new Coord(140, 0));
            TextEntry colorPicker = new TextEntry(130, "");
            HighlightGroup[] highlightGroups = HighlightManager.getHighlightGroups();
            HighlightOptionList highlightOptionList = new HighlightOptionList(210, 10) {
                @Override
                public void change(HighlightOption item) {
                    if (item != null) {
                        colorPicker.settext(HighlightManager.formatColor(
                                item.color != null ? item.color : Color.BLACK));
                    }
                    super.change(item);
                }
            };
            highlightSettings.add(highlightOptionList, new Coord(130, 20));
            HighlightGroupList highlightGroupList = new HighlightGroupList(120, 10, highlightGroups) {
                @Override
                public void change(HighlightGroup item) {
                    if (item != null) {
                        highlightOptionList.setHighlightOptions(item.options);
                        highlightOptionList.setActiveItem(0);
                        highlightOptionList.sb.val = 0;
                    }
                    super.change(item);
                }
            };
            highlightGroupList.setActiveItem(0);
            highlightSettings.add(highlightGroupList, new Coord(0, 20));
            highlightSettings.add(new Button(100, "Enable Group") {
                @Override
                public void click() {
                    HighlightGroup highlightGroup = highlightGroupList.getCurrentItem();
                    if (highlightGroup != null) {
                        for (int i = 0; i < highlightGroup.options.length; i++) {
                            highlightGroup.options[i].enabled = true;
                        }
                        HighlightManager.saveHighlightGroups();
                    }
                }
            }, new Coord(10, 330));
            highlightSettings.add(new Button(100, "Disable Group") {
                @Override
                public void click() {
                    HighlightGroup highlightGroup = highlightGroupList.getCurrentItem();
                    if (highlightGroup != null) {
                        for (int i = 0; i < highlightGroup.options.length; i++) {
                            highlightGroup.options[i].enabled = false;
                        }
                        HighlightManager.saveHighlightGroups();
                    }
                }
            }, new Coord(120, 330));
            highlightSettings.add(new Button(100, "Toggle Item") {
                @Override
                public void click() {
                    HighlightOption highlightOption = highlightOptionList.getCurrentItem();
                    if (highlightOption != null) {
                        highlightOption.enabled = !highlightOption.enabled;
                        HighlightManager.saveHighlightGroups();
                    }
                }
            }, new Coord(230, 330));
            highlightSettings.add(colorPicker, new Coord(0, 370));
            highlightSettings.add(new Button(100, "Set for Group") {
                @Override
                public void click() {
                    HighlightGroup highlightGroup = highlightGroupList.getCurrentItem();
                    if (highlightGroup != null) {
                        Color parsed = HighlightManager.parseColor(colorPicker.text);
                        for (int i = 0; i < highlightGroup.options.length; i++) {
                            highlightGroup.options[i].color = parsed;
                        }
                        HighlightManager.saveHighlightGroups();
                    }
                }
            }, new Coord(140, 360));
            highlightSettings.add(new Button(100, "Set for Current") {
                @Override
                public void click() {
                    HighlightOption highlightOption = highlightOptionList.getCurrentItem();
                    if (highlightOption != null) {
                        Color parsed = HighlightManager.parseColor(colorPicker.text);
                        highlightOption.color = parsed;
                        HighlightManager.saveHighlightGroups();
                    }
                }
            }, new Coord(250, 360));
        }
        tabs.pack();
        add(new Button(100, "General") {
            @Override
            public void click() {
                tabs.showtab(generalSettings);
            }
        }, new Coord(15, 10));
        add(new Button(100, "Highlight") {
            @Override
            public void click() {
                tabs.showtab(highlightSettings);
            }
        }, new Coord(125, 10));
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == cbtn) {
            destroy();
        }
    }

    public static class HighlightGroupList extends Listbox<HighlightGroup> {
        private HighlightGroup[] highlightGroups;
        private HighlightGroup currentItem = null;

        public HighlightGroupList(int w, int h, HighlightGroup[] highlightGroups) {
            super(w, h, 30);
            this.highlightGroups = highlightGroups;
        }

        @Override
        protected HighlightGroup listitem(int i) {
            return highlightGroups[i];
        }

        @Override
        public void change(HighlightGroup item) {
            currentItem = item;
        }

        public void setActiveItem(int index) {
            if (index >= highlightGroups.length) return;
            currentItem = highlightGroups[index];
            change(currentItem);
        }

        public HighlightGroup getCurrentItem() {
            return currentItem;
        }

        @Override
        protected int listitems() {
            return highlightGroups.length;
        }

        @Override
        protected void drawitem(GOut g, HighlightGroup item, int i) {
            if (currentItem == item) {
                g.chcolor(64, 64, 64, 255);
                g.frect(Coord.z, new Coord(sz.x, 30));
                g.chcolor();
            }
            g.image(CharWnd.attrf.render(item.name).tex(), new Coord(10, 3));
        }
    }

    public static class HighlightOptionList extends Listbox<HighlightOption> {
        private HighlightOption[] highlightOptions;
        private HighlightOption currentItem = null;

        public HighlightOptionList(int w, int h) {
            super(w, h, 30);
            this.highlightOptions = new HighlightOption[0];
        }

        public void setHighlightOptions(HighlightOption[] highlightOptions) {
            this.highlightOptions = highlightOptions;
        }

        @Override
        protected HighlightOption listitem(int i) {
            return highlightOptions[i];
        }

        @Override
        public void change(HighlightOption item) {
            currentItem = item;
        }

        public void setActiveItem(int index) {
            if (index >= highlightOptions.length) return;
            currentItem = highlightOptions[index];
            change(currentItem);
        }

        public HighlightOption getCurrentItem() {
            return currentItem;
        }

        @Override
        protected int listitems() {
            return highlightOptions.length;
        }

        @Override
        protected void drawitem(GOut g, HighlightOption item, int i) {
            if (currentItem == item) {
                g.chcolor(64, 64, 64, 255);
                g.frect(Coord.z, new Coord(sz.x, 30));
                g.chcolor();
            }
            if (item.color != null) {
                g.chcolor(item.color);
                g.fellipse(new Coord(20, 15), new Coord(6, 6));
                g.chcolor();
            } else if (item.icon != null) {
                Tex icon = null;
                try {
                    icon = Resource.loadtex(item.icon);
                } catch (Exception ex) {
                    icon = WItem.missing.layer(Resource.imgc).tex();
                }
                g.image(icon, new Coord(6, 1), new Coord(28, 28));
            }
            if (item.enabled)
                g.chcolor(0, 102, 0, 255);
            g.image(CharWnd.attrf.render(item.name).tex(), new Coord(40, 3));
            if (item.enabled)
                g.chcolor();
        }

        private long lastClickTime = 0;

        private void dblclick(HighlightOption item) {
            if (currentItem != null) {
                currentItem.enabled = !currentItem.enabled;
                HighlightManager.saveHighlightGroups();
            }
        }

        @Override
        protected void itemclick(HighlightOption item, int button) {
            if (button == 1) {
                if (item != null && currentItem == item && (System.currentTimeMillis() - lastClickTime) < 1000) {
                    dblclick(item);
                    lastClickTime = 0;
                } else {
                    change(item);
                    lastClickTime = System.currentTimeMillis();
                }
            }
        }
    }
}
