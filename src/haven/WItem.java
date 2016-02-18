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

import org.apxeolog.shovel.Settings;
import org.apxeolog.shovel.Shovel;
import org.apxeolog.shovel.info.ItemQualityInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static haven.Inventory.sqsz;

public class WItem extends Widget implements DTarget {
    public static final Resource missing = Resource.local().loadwait("gfx/invobjs/missing");
    public final GItem item;
    private Resource cspr = null;
    private Message csdt = Message.nil;
    
    public WItem(GItem item) {
	super(sqsz);
	this.item = item;
    }

    public void drawmain(GOut g, GSprite spr) {
	spr.draw(g);
    }

    public static BufferedImage shorttip(List<ItemInfo> info) {
	return(ItemInfo.shorttip(info));
    }
    
    public static BufferedImage longtip(GItem item, List<ItemInfo> info) {
	BufferedImage img = ItemInfo.longtip(info);
	Resource.Pagina pg = item.res.get().layer(Resource.pagina);
	if(pg != null)
	    img = ItemInfo.catimgs(0, img, RichText.render("\n" + pg.text, 200).img);
	return(img);
    }
    
    public BufferedImage longtip(List<ItemInfo> info) {
	return(longtip(item, info));
    }
    
    public class ItemTip implements Indir<Tex> {
	private final TexI tex;
	
	public ItemTip(BufferedImage img) {
	    if(img == null)
		throw(new Loading());
	    tex = new TexI(img);
	}
	
	public GItem item() {
	    return(item);
	}
	
	public Tex get() {
	    return(tex);
	}
    }
    
    public class ShortTip extends ItemTip {
	public ShortTip(List<ItemInfo> info) {super(shorttip(info));}
    }
    
    public class LongTip extends ItemTip {
	public LongTip(List<ItemInfo> info) {super(longtip(info));}
    }

    private long hoverstart;
    private ItemTip shorttip = null, longtip = null;
    private List<ItemInfo> ttinfo = null;
    public Object tooltip(Coord c, Widget prev) {
		long now = System.currentTimeMillis();
		if (prev == this) {
		} else if (prev instanceof WItem) {
			long ps = ((WItem) prev).hoverstart;
			if (now - ps < 1000)
				hoverstart = now;
			else
				hoverstart = ps;
		} else {
			hoverstart = now;
		}
		try {
			List<ItemInfo> info = item.info();
			if (info.size() < 1)
				return (null);
			if (info != ttinfo) {
				shorttip = longtip = null;
				ttinfo = info;
			}
			if (longtip == null)
				longtip = new LongTip(info);
			return (longtip);
		} catch (Loading e) {
			return ("...");
		}
	}

    public volatile static int cacheseq = 0;
    public abstract class AttrCache<T> {
	private List<ItemInfo> forinfo = null;
	private T save = null;
	private int forseq = -1;
	
	public T get() {
	    try {
		List<ItemInfo> info = item.info();
		if((cacheseq != forseq) || (info != forinfo)) {
		    save = find(info);
		    forinfo = info;
		    forseq = cacheseq;
		}
	    } catch(Loading e) {
		return(null);
	    }
	    return(save);
	}
	
	protected abstract T find(List<ItemInfo> info);
    }
    
    public final AttrCache<Color> olcol = new AttrCache<Color>() {
	protected Color find(List<ItemInfo> info) {
	    Color ret = null;
	    for(ItemInfo inf : info) {
		if(inf instanceof GItem.ColorInfo) {
		    Color c = ((GItem.ColorInfo)inf).olcol();
		    if(c != null)
			ret = (ret == null)?c:Utils.preblend(ret, c);
		}
	    }
	    return(ret);
	}
    };
    
    public final AttrCache<Tex> itemnum = new AttrCache<Tex>() {
	protected Tex find(List<ItemInfo> info) {
	    GItem.NumberInfo ninf = ItemInfo.find(GItem.NumberInfo.class, info);
	    if(ninf == null) return(null);
	    return(new TexI(Utils.outline2(Text.render(Integer.toString(ninf.itemnum()), Color.WHITE).img, Utils.contrast(Color.WHITE))));
	}
    };

    private GSprite lspr = null;
    public void tick(double dt) {
	/* XXX: This is ugly and there should be a better way to
	 * ensure the resizing happens as it should, but I can't think
	 * of one yet. */
	GSprite spr = item.spr();
	if((spr != null) && (spr != lspr)) {
	    Coord sz = new Coord(spr.sz());
	    if((sz.x % sqsz.x) != 0)
		sz.x = sqsz.x * ((sz.x / sqsz.x) + 1);
	    if((sz.y % sqsz.y) != 0)
		sz.y = sqsz.y * ((sz.y / sqsz.y) + 1);
	    resize(sz);
	    lspr = spr;
	}
    }

    public void draw(GOut g) {
		GSprite spr = item.spr();
		if (spr != null) {
			Coord sz = spr.sz();
			g.defstate();
			if (olcol.get() != null)
				g.usestate(new ColorMask(olcol.get()));
			drawmain(g, spr);
			g.defstate();
			if (item.num >= 0) {
				g.atext(Integer.toString(item.num), sz, 0, 1);
			} else if (itemnum.get() != null) {
				g.imageDock(itemnum.get(), sz, 0, 1);
			}
			if (item.meter > 0) {
				g.imageDock(Utils.renderOutlinedFont(Text.std, Integer.toString(item.meter) + "%", Color.WHITE, Color.BLACK, 1), sz, 0.5, 0.5);
			}
			if (Shovel.getSettings().showQuality && item.ready()) {
				ItemQualityInfo qualityInfo = item.getItemQualityInfo();
				if (qualityInfo != null) {
					if (Shovel.getSettings().qualityDisplayType == Settings.QualityDisplayType.MAX) {
						g.imageDock(qualityInfo.textCache, sz, 1, 1);
					} else {
						g.imageDock(qualityInfo.averageTextCache, sz, 1, 1);
					}
				}
			}
		} else {
			g.image(missing.layer(Resource.imgc).tex(), Coord.z, sz);
		}
	}

	private ISBox getStockpile() {
		Window swnd = GameUI.instance.findWindow("Stockpile");

		if (swnd == null)
			return null;

		ISBox ret = swnd.findchild(ISBox.class);
		return ret;
	}

    public boolean mousedown(Coord c, int btn) {
	if(btn == 1) {
	    if(ui.modshift)
		item.wdgmsg("transfer", c);
		else if(ui.modctrl)
		item.wdgmsg("drop", c);
	    else
		item.wdgmsg("take", c);
	    return(true);
	} else if(btn == 3) {
		if (Shovel.getSettings().enableGroupHotkeys) {
			if (ui.modshift) {
				if (ui.modmeta) {
					wdgmsg("transfer_all_asc", item.getItemName());
				} else {
					ISBox stockpile = getStockpile();
					if (stockpile == null) {
						wdgmsg("transfer_all", item.getItemName());
					} else {
						for (int  i = 0; i < 56; ++i)
							stockpile.wdgmsg("xfer2", 1, 1);
					}
				}
			} else if (ui.modctrl) {
				if (ui.modmeta)
					wdgmsg("drop_all_asc", item.getItemName());
				else
					wdgmsg("drop_all", item.getItemName());
			} else
				item.wdgmsg("iact", c, ui.modflags());
		} else
			item.wdgmsg("iact", c, ui.modflags());
	    return(true);
	}
	return(false);
    }

    public boolean drop(Coord cc, Coord ul) {
	return(false);
    }
	
    public boolean iteminteract(Coord cc, Coord ul) {
	item.wdgmsg("itemact", ui.modflags());
	return(true);
    }
}
