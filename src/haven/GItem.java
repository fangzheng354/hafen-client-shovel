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

import haven.res.lib.Meat;
import org.apxeolog.shovel.ALS;
import org.apxeolog.shovel.info.ItemQualityInfo;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class GItem extends AWidget implements ItemInfo.SpriteOwner, GSprite.Owner {
	public Indir<Resource> res;
	public MessageBuf sdt;
	public int meter = 0;
	public int num = -1;
	private GSprite spr;
	private Object[] rawinfo;
	private List<ItemInfo> info = Collections.emptyList();

	private long lastTickTime = -1;
	private int tickDiff = 0;
	private int totalTime = 0;
	private int lastMeter = 0;
	private int remainingSeconds = 0;
	private int tickCount = 0;

	public boolean ready() {
		try {
			return spr != null && res.get().ready() && info() != null;
		} catch (Loading ex) {
			return false;
		}
	}

	@RName("item")
	public static class $_ implements Factory {
		public Widget create(Widget parent, Object[] args) {
			int res = (Integer)args[0];
			Message sdt = (args.length > 1)?new MessageBuf((byte[])args[1]):Message.nil;
			return(new GItem(parent.ui.sess.getres(res), sdt));
		}
	}

	public interface ColorInfo {
		public Color olcol();
	}

	public interface NumberInfo {
		public int itemnum();
	}

	public class Amount extends ItemInfo implements NumberInfo {
		private final int num;

		public Amount(int num) {
			super(GItem.this);
			this.num = num;
		}

		public int itemnum() {
			return(num);
		}
	}

	public GItem(Indir<Resource> res, Message sdt) {
		this.res = res;
		this.sdt = new MessageBuf(sdt);
	}

	public GItem(Indir<Resource> res) {
		this(res, Message.nil);
	}

	/**
	 * Returns item tooltip name or null
	 * @return
	 */
	public String getItemName() {
		List<ItemInfo> infoList = info();
		for (ItemInfo info : infoList) {
			if (info instanceof ItemInfo.Name) return ((ItemInfo.Name) info).str.text;
		}
		return null;
	}

	private  String formattedTime(int seconds) {
		String ret = new String();

		int hours = 0;
		int minutes = 0;
		int secs = 0;
		int tmp = 0;

		if (seconds > 3600) {
			hours = seconds / 3600;
			tmp = seconds % 3600;
		} else tmp = seconds;

		if (tmp > 60) {
			minutes = tmp / 60;
			tmp = tmp % 60;
		}

		secs = tmp;

		if (hours > 0)
			ret += (hours > 9 ? hours : "0"+hours) + ":";

		ret += (minutes > 9 ? minutes : "0"+minutes) + ":";
		ret += (secs > 9 ? secs : "0"+secs);

		return ret;
	}

	private Random rnd = null;
	public Random mkrandoom() {
		if(rnd == null)
			rnd = new Random();
		return(rnd);
	}
	public Resource getres() {return(res.get());}
	public Glob glob() {return(ui.sess.glob);}

	public GSprite spr() {
		GSprite spr = this.spr;
		if(spr == null) {
			try {
				spr = this.spr = GSprite.create(this, res.get(), sdt.clone());
			} catch(Loading l) {
			}
		}
		return(spr);
	}

	public void tick(double dt) {
		GSprite spr = spr();
		if(spr != null)
			spr.tick(dt);
	}

	public List<ItemInfo> info() {
		if (info == null) {
			info = ItemInfo.buildinfo(this, rawinfo);
			try {
				info.add(new ItemInfo.AdHoc(this, "Res: " + getres().name));
			} catch (Exception ex) {

			}
		}
		return (info);
	}

	private ItemQualityInfo itemQualityInfoCache = null;

	public ItemQualityInfo getItemQualityInfo() {
		if (itemQualityInfoCache == null) {
			List<ItemInfo> itemInfoList = null;
			try {
				itemInfoList = info();
			} catch (Exception ex) {
				// Ignore this and return null
				return null;
			}
			itemQualityInfoCache = new ItemQualityInfo();
			for (ItemInfo info : itemInfoList) {
				if (info instanceof ItemInfo.Contents && info != null) {
					for (ItemInfo infoContents : ((ItemInfo.Contents) info).sub) {
						if (infoContents.getClass().getSimpleName().equals("QBuff")) {
							try {
								String qname = (String) infoContents.getClass().getDeclaredField("name").get(infoContents);
								Double qval = (Double) infoContents.getClass().getDeclaredField("q").get(infoContents);
								itemQualityInfoCache.setByType(qname, qval);
							} catch (Exception ex) {
						}
						}
					}
					break;
				}
				if (info.getClass().getSimpleName().equals("QBuff")) {
					try {
						String qname = (String) info.getClass().getDeclaredField("name").get(info);
						//Integer qval = (Integer) info.getClass().getDeclaredField("q").get(info);
						Double qval = (Double) info.getClass().getDeclaredField("q").get(info);
						itemQualityInfoCache.setByType(qname, qval);
					} catch (Exception ex) {

					}
				}
			}
			itemQualityInfoCache.build();
		}
		return itemQualityInfoCache;
	}

	public Resource resource() {
		return(res.get());
	}

	public GSprite sprite() {
		if(spr == null)
			throw(new Loading("Still waiting for sprite to be constructed"));
		return(spr);
	}

	public void uimsg(String name, Object... args) {
		if(name == "num") {
			num = (Integer)args[0];
		} else if(name == "chres") {
			synchronized(this) {
				res = ui.sess.getres((Integer)args[0]);
				sdt = (args.length > 1)?new MessageBuf((byte[])args[1]):MessageBuf.nil;
				spr = null;
			}
		} else if(name == "tt") {
			info = null;
			rawinfo = args;

			if (tickDiff != 0 && tickCount > 1) {
				boolean f = false;
				for (int idx = 0; idx < rawinfo.length; ++idx) {
					Object obj = rawinfo[idx];
					if (obj instanceof String) {
						String st = (String)obj;
						if (st.startsWith("Rem. time:")) {
							f = true;
							rawinfo[idx] = new String("Rem. time: ~" + formattedTime(remainingSeconds) + " / " + formattedTime(totalTime));
							break;
						}
					}
				}
				if (f != true) {
					ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(rawinfo));
					temp.add(new String("Rem. time: ~" + formattedTime(remainingSeconds) + " / " + formattedTime(totalTime)));
					rawinfo = temp.toArray();
				}
			}
		} else if(name == "meter") {
			meter = (Integer)args[0];

			if (lastMeter == 0) {
				lastMeter = meter;
			}

			if (lastTickTime < 0) {
				lastTickTime = System.currentTimeMillis();
			} else {
				int meterDiff = meter - lastMeter;
				if (meterDiff > 0) {
					++tickCount;
					long ct = System.currentTimeMillis();
					tickDiff = (int) ((ct - lastTickTime) / 1000); //seconds
					totalTime = (tickDiff/meterDiff) * 100;
					lastTickTime = ct;

					if (tickDiff > 0 && tickCount > 1) {
						remainingSeconds = ((100 - meter) / meterDiff) * tickDiff;
						this.uimsg("tt", rawinfo);
					}

					lastMeter = meter;
				}
			}
		}
	}
}
