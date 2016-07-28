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

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class RootWidget extends ConsoleHost {
    public static final Resource defcurs = Resource.local().loadwait("gfx/hud/curs/arw");
    Profile guprof, grprof, ggprof;
    boolean afk = false;
	
    public RootWidget(UI ui, Coord sz) {
	super(ui, new Coord(0, 0), sz);
	setfocusctl(true);
	hasfocus = true;
	cursor = defcurs.indir();
    }
	
    public boolean globtype(char key, KeyEvent ev) {
	if(!super.globtype(key, ev)) {
        boolean ctrl = (((ev.getModifiers() & InputEvent.CTRL_MASK) != 0));
        int code = ev.getKeyCode();
	    if(key == '`') {
		GameUI gi = findchild(GameUI.class);
		if(Config.profile) {
		    add(new Profwnd(guprof, "UI profile"), new Coord(100, 100));
		    add(new Profwnd(grprof, "GL profile"), new Coord(450, 100));
		    if((gi != null) && (gi.map != null))
			add(new Profwnd(gi.map.prof, "Map profile"), new Coord(100, 250));
		}
		if(Config.profilegpu) {
		    add(new Profwnd(ggprof, "GPU profile"), new Coord(450, 250));
		}
	    } else if(key == ':') {
		entercmd();
	    } else if((code == KeyEvent.VK_N)&&ctrl) {
			Shovel.getSettings().nightvision = !Shovel.getSettings().nightvision;
			Shovel.saveSettings();
		} else if ((code == KeyEvent.VK_F9) && ctrl) {
			Shovel.getSettings().debugWidgets = !Shovel.getSettings().debugWidgets;
			System.out.println("Widget creation debugging toggled. Press Ctrl+F9 to toggle widget debugging.");
		} else if ((code == KeyEvent.VK_F6)) {
            Shovel.getSettings().enableHide = !Shovel.getSettings().enableHide;
        } else if ((code == KeyEvent.VK_D) && ctrl) {
			Shovel.getSettings().debugMode = !Shovel.getSettings().debugMode;
		} else if ((code == KeyEvent.VK_W) && ev.isShiftDown()) {
			Shovel.getSettings().qualityDisplayType = Shovel.getSettings().qualityDisplayType == Settings.QualityDisplayType.AVG ?
					Settings.QualityDisplayType.MAX : Settings.QualityDisplayType.AVG;
			Shovel.saveSettings();
		} else if(key != 0) {
		wdgmsg("gk", (int) key);
	    }
	}
	return(true);
    }

    public void draw(GOut g) {
	super.draw(g);
	drawcmd(g, new Coord(20, sz.y - 20));
    }
    
    public void error(String msg) {
    }
}
