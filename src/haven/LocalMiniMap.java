/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import static haven.MCache.cmaps;
import static haven.MCache.tilesz;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import haven.resutil.Ridges;
import org.apxeolog.shovel.ALS;
import org.apxeolog.shovel.Settings;
import org.apxeolog.shovel.Shovel;
import org.apxeolog.shovel.gob.CustomAttrib;
import org.apxeolog.shovel.highlight.HighlightManager;
import org.apxeolog.shovel.highlight.HighlightOption;

public class LocalMiniMap extends Window {
    public final MapView mv;
    private Coord playerCoordinates = null;
    private BufferedImage gridCellImage = null;

    private static final Text.Foundry borkaF = new Text.Foundry(Text.monob, 16).aa(true);

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (!"close".equals(msg)) super.wdgmsg(sender, msg, args);
    }

    public LocalMiniMap(Coord sz, MapView mv) {
        super(sz, "Minimap");
        this.mv = mv;
        gridCellImage = TexI.mkbuf(cmaps);
        Graphics2D graphics2D = gridCellImage.createGraphics();
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawRect(0, 0, cmaps.x, cmaps.y);

        IButton btn = new IButton(
            Resource.loadRawImage("apxeolog/ui/resize-normal").layer(Resource.Image.class).img,
            Resource.loadRawImage("apxeolog/ui/resize-down").layer(Resource.Image.class).img,
            Resource.loadRawImage("apxeolog/ui/resize-hover").layer(Resource.Image.class).img
        ) {
            private Coord lastCoord = null;
            private Coord initialSize = null;

            @Override
            public boolean mousedown(Coord c, int button) {
                lastCoord = ui.mc;
                initialSize = ((Window) parent).asz;
                return super.mousedown(c, button);
            }

            @Override
            public boolean mouseup(Coord c, int button) {
                lastCoord = null;
                return super.mouseup(c, button);
            }

            @Override
            public void mousemove(Coord c) {
                if (lastCoord != null) {
                    parent.resize(initialSize.add(ui.mc.sub(lastCoord)));
                }
            }

            @Override
            public void presize() {
                c = ((Window) parent).asz;
            }

            @Override
            public void click() {

            }
        };
        add(btn, sz);
    }

    @Override
    public void resize(Coord s) {
        super.resize(s);
        Shovel.getSettings().setWindowData("Minimap", null, asz);
        Shovel.saveSettings();
    }

    @Override
    public void dragEnd() {
        super.dragEnd();
        Shovel.getSettings().setWindowData("Minimap", c, asz);
        Shovel.saveSettings();
    }

    public Coord p2c(Coord pc) {
        return (pc.div(tilesz).sub(playerCoordinates).add(asz.div(2)));
    }

    public Coord c2p(Coord c) {
        return (c.sub(asz.div(2)).add(playerCoordinates).mul(tilesz).add(tilesz.div(2)));
    }

    public void drawicons(GOut g) {
        OCache oc = ui.sess.glob.oc;
        // Cache party ids to remove them from render
        ArrayList<Long> partyIds = new ArrayList<>(10);
        synchronized (ui.sess.glob.party.memb) {
            for (Party.Member m : ui.sess.glob.party.memb.values()) {
                if (m.getgob() != null) {
                    partyIds.add(m.getgob().id);
                    try {
                        Coord ptc = m.getc();
                        ptc = p2c(ptc);
                        //g.chcolor(m.col.getRed(), m.col.getGreen(), m.col.getBlue(), 200);
                        //g.image(MiniMap.plx.layer(Resource.imgc).tex(), ptc.add(MiniMap.plx.layer(Resource.negc).cc.inv()));
                        g.image(Text.renderstroked("x", m.col, Color.BLACK, borkaF).tex(), ptc.add(MiniMap.plx.layer(Resource.negc).cc.inv()).add(0, -8));
                        //g.chcolor();
                    } catch (Exception ex) {

                    }
                }
            }
        }
        synchronized (oc) {
            for (Gob gob : oc) {
                try {
                    GobIcon icon = gob.getattr(GobIcon.class);
                    if (icon != null) {
                        Coord gc = p2c(gob.rc);
                        Tex tex = icon.tex();
                        g.image(tex, gc.sub(tex.sz().div(2)));
                    }
                } catch (Loading l) {
                }
            }
            for (Gob gob : oc) {
                try {
                    if (partyIds.contains(gob.id)) continue;
                    CustomAttrib.HighlightAttrib highlightAttrib = gob.getattr(CustomAttrib.HighlightAttrib.class);
                    if (highlightAttrib != null) {
                        if (highlightAttrib.highlightOption.enabled) {
                            if (highlightAttrib.highlightOption.color != null) {
                                Coord dotPosition = p2c(gob.rc).sub(3, 3);
                                g.chcolor(Color.BLACK);
                                g.fellipse(dotPosition, new Coord(5, 5));
                                g.chcolor(highlightAttrib.highlightOption.color);
                                g.fellipse(dotPosition, new Coord(4, 4));
                                g.chcolor();
                            } else if (highlightAttrib.highlightOption.icon != null) {
                                Coord dotPosition = p2c(gob.rc);
                                try {
                                    //g.aimage(Resource.loadtex(highlightAttrib.highlightOption.icon), dotPosition, 0.5, 0.5);
                                    g.image(Resource.loadtex(highlightAttrib.highlightOption.icon), dotPosition.sub(10, 10), new Coord(20, 20));
                                } catch (Exception ex) {
                                }
                            }
                        }
                    }
                    Composite composite = gob.getattr(Composite.class);
                    if (composite != null) {
                        String resourceName = composite.getBaseName();
                        if ("gfx/borka/body".equals(resourceName)) {
                            // Players
                            Color color = new Color(255, 0, 0);
                            KinInfo kinInfo = gob.getattr(KinInfo.class);
                            if (kinInfo != null) {
                                color = BuddyWnd.gc[kinInfo.group];
                            }
                            Coord dotPosition = p2c(gob.rc).sub(3, 3);
                            g.chcolor(Color.BLACK);
                            g.fellipse(dotPosition, new Coord(5, 5));
                            g.chcolor(color);
                            g.fellipse(dotPosition, new Coord(4, 4));
                            g.chcolor();
                        }
                    }
                } catch (Loading l) {
                }
            }
        }
    }

    public Gob findicongob(Coord c) {
        OCache oc = ui.sess.glob.oc;
        synchronized (oc) {
            for (Gob gob : oc) {
                try {
                    boolean highlighted = false;
                    CustomAttrib.HighlightAttrib highlightAttrib = gob.getattr(CustomAttrib.HighlightAttrib.class);
                    if (highlightAttrib != null && highlightAttrib.highlightOption.enabled)
                        highlighted = true;

                    GobIcon icon = gob.getattr(GobIcon.class);
                    if (icon != null || highlighted) {
                        Coord gc = p2c(gob.rc);
                        Coord sz;
                        if (icon != null)
                            sz = icon.tex().sz();
                        else
                            sz = new Coord(20, 20);
                        if (c.isect(gc.sub(sz.div(2)), sz))
                            return (gob);
                    }
                } catch (Loading l) {
                }
            }
        }
        return (null);
    }

    public void tick(double dt) {
        Gob pl = ui.sess.glob.oc.getgob(mv.plgob);
        if (pl == null) {
            this.playerCoordinates = mv.cc.div(tilesz);
            return;
        }
        this.playerCoordinates = pl.rc.div(tilesz);
    }

    @Override
    public void cdraw(GOut g) {
        if (playerCoordinates == null) return;
        Coord playerGrid = playerCoordinates.div(cmaps);
        Coord gridIterator = new Coord(0, 0);
        for (gridIterator.x = -1; gridIterator.x < 2; gridIterator.x++) {
            for (gridIterator.y = -1; gridIterator.y < 2; gridIterator.y++) {
                Coord translate = playerCoordinates.sub(playerGrid.add(gridIterator).mul(cmaps));
                try {
                    MCache.Grid grid = ui.sess.glob.map.getgrid(playerGrid.add(gridIterator));
                    if (grid != null && grid.getGridImage() != null) {
                        g.image(grid.getGridImage(), asz.div(2).sub(translate));
                    }
                } catch (Loading ex) {

                }
                if (Shovel.getSettings().drawMinimapGrid)
                    g.image(gridCellImage, asz.div(2).sub(translate));
            }
        }
        if (Shovel.getSettings().drawMinimapFOV) {
            g.chcolor(255, 255, 255, 64);
            g.frect(asz.div(2).sub(44, 44), new Coord(88, 88));
            g.chcolor();
        }
        drawicons(g);
    }

    public boolean mousedown(Coord c, int button) {
        if (c.isect(atl, asz)) {
            c = c.sub(atl);
            if (playerCoordinates == null)
                return (false);
            Gob gob = findicongob(c);
            if (gob == null)
                mv.wdgmsg("click", rootpos().add(c), c2p(c), button, ui.modflags());
            else
                mv.wdgmsg("click", rootpos().add(c), c2p(c), button, ui.modflags(), 0, (int) gob.id, gob.rc, 0, -1);
            return (true);
        } else {
            return super.mousedown(c, button);
        }
    }
}
