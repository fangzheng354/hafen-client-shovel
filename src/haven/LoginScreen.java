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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apxeolog.shovel.ALS;
import org.apxeolog.shovel.Shovel;
import org.apxeolog.shovel.widget.ConfigurationWnd;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class LoginScreen extends Widget {
    Login cur;
    Text error;
    IButton btn;
    Button optbtn;
    OptWnd opts;
    static Text.Foundry textf, textfs;
    static Tex bg = Resource.loadtex("gfx/loginscr");
    Text progress = null;

    static {
	textf = new Text.Foundry(Text.sans, 16).aa(true);
	textfs = new Text.Foundry(Text.sans, 14).aa(true);

    }

    public LoginScreen() {
	super(bg.sz());
	setfocustab(true);
	add(new Img(bg), Coord.z);
	optbtn = adda(new Button(100, "Options"), 10, sz.y - 10, 0, 1);
		createAccountButtons(null);
		checkForNewReleases();
    }

    private static abstract class Login extends Widget {
	abstract Object[] data();
	abstract boolean enter();
    }

    private class Pwbox extends Login {
	TextEntry user, pass;
	CheckBox savepass;

	private Pwbox(String username, boolean save) {
	    setfocustab(true);
	    add(new Label("User name", textf), Coord.z);
	    add(user = new TextEntry(150, username), new Coord(0, 20));
	    add(new Label("Password", textf), new Coord(0, 50));
	    add(pass = new TextEntry(150, ""), new Coord(0, 70));
	    pass.pw = true;
	    add(savepass = new CheckBox("Remember me", true), new Coord(0, 100));
	    savepass.a = save;
	    if(user.text.equals(""))
		setfocus(user);
	    else
		setfocus(pass);
	    resize(new Coord(150, 150));
	    LoginScreen.this.add(this, new Coord(575, 310));
	}

	public void wdgmsg(Widget sender, String name, Object... args) {
	}

	Object[] data() {
	    return(new Object[] {new AuthClient.NativeCred(user.text, pass.text), savepass.a});
	}

	boolean enter() {
	    if(user.text.equals("")) {
		setfocus(user);
		return(false);
	    } else if(pass.text.equals("")) {
		setfocus(pass);
		return(false);
	    } else {
		return(true);
	    }
	}

	public boolean globtype(char k, KeyEvent ev) {
	    if((k == 'r') && ((ev.getModifiersEx() & (KeyEvent.META_DOWN_MASK | KeyEvent.ALT_DOWN_MASK)) != 0)) {
		savepass.set(!savepass.a);
		return(true);
	    }
	    return(false);
	}
    }

    private class Tokenbox extends Login {
	Text label;
	Button btn;
		
	private Tokenbox(String username) {
	    label = textfs.render("Identity is saved for " + username, java.awt.Color.WHITE);
	    add(btn = new Button(100, "Forget me"), new Coord(75, 30));
	    resize(new Coord(250, 100));
	    LoginScreen.this.add(this, new Coord(495, 330));
	}
		
	Object[] data() {
	    return(new Object[0]);
	}
		
	boolean enter() {
	    return(true);
	}
		
	public void wdgmsg(Widget sender, String name, Object... args) {
	    if(sender == btn) {
		LoginScreen.this.wdgmsg("forget");
		return;
	    }
	    super.wdgmsg(sender, name, args);
	}
		
	public void draw(GOut g) {
	    g.image(label.tex(), new Coord((sz.x / 2) - (label.sz().x / 2), 0));
	    super.draw(g);
	}
	
	public boolean globtype(char k, KeyEvent ev) {
	    if((k == 'f') && ((ev.getModifiersEx() & (KeyEvent.META_DOWN_MASK | KeyEvent.ALT_DOWN_MASK)) != 0)) {
		LoginScreen.this.wdgmsg("forget");
		return(true);
	    }
	    return(false);
	}
    }

    private void mklogin() {
	synchronized(ui) {
	    adda(btn = new IButton("gfx/hud/buttons/login", "u", "d", "o") {
		    protected void depress() {Audio.play(Button.lbtdown.stream());}
		    protected void unpress() {Audio.play(Button.lbtup.stream());}
		}, 719, 510, 1, 1);
	    progress(null);
	}
    }

    private void error(String error) {
	synchronized(ui) {
	    if(this.error != null)
		this.error = null;
	    if(error != null)
		this.error = textf.render(error, java.awt.Color.RED);
	}
    }

    private void progress(String p) {
	synchronized(ui) {
	    if(progress != null)
		progress = null;
	    if(p != null)
		progress = textf.render(p, java.awt.Color.WHITE);
	}
    }

    private void clear() {
	if(cur != null) {
	    ui.destroy(cur);
	    cur = null;
	    ui.destroy(btn);
	    btn = null;
	}
	progress(null);
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
	if(sender == btn) {
	    if(cur.enter()) {
			if (cur instanceof Pwbox) {
				Pwbox box = (Pwbox) cur;
				addAccount(box.user.text, box.pass.text);
			}
			super.wdgmsg("login", cur.data());
		}
	    return;
	} else if(sender == optbtn) {
	    if(opts == null) {
		opts = adda(new OptWnd(false) {
			public void hide() {
			    /* XXX */
			    reqdestroy();
			}
		    }, sz.div(2), 0.5, 0.5);
	    } else {
		opts.reqdestroy();
		opts = null;
	    }
	    return;
	} else if(sender == opts) {
	    opts.reqdestroy();
	    opts = null;
	}
	super.wdgmsg(sender, msg, args);
    }

    public void cdestroy(Widget ch) {
	if(ch == opts) {
	    opts = null;
	}
    }

    public void uimsg(String msg, Object... args) {
	synchronized(ui) {
	    if(msg == "passwd") {
		clear();
		cur = new Pwbox((String)args[0], (Boolean)args[1]);
		mklogin();
	    } else if(msg == "token") {
		clear();
		cur = new Tokenbox((String)args[0]);
		mklogin();
	    } else if(msg == "error") {
		error((String)args[0]);
	    } else if(msg == "prg") {
		error(null);
		clear();
		progress((String)args[0]);
	    }
	}
    }

    public void presize() {
	c = parent.sz.div(2).sub(sz.div(2));
    }

    protected void added() {
	presize();
	parent.setfocus(this);
    }

    public void draw(GOut g) {
	super.draw(g);
	if(error != null)
	    g.image(error.tex(), new Coord(420 - (error.sz().x / 2), 450));
	if(progress != null)
	    g.image(progress.tex(), new Coord(420 - (progress.sz().x / 2), 350));
    }

    public boolean type(char k, KeyEvent ev) {
	if(k == 10) {
	    if((cur != null) && cur.enter())
			if (cur instanceof Pwbox) {
				Pwbox box = (Pwbox) cur;
				addAccount(box.user.text, box.pass.text);
			}
		wdgmsg("login", cur.data());
	    return(true);
	}
	return(super.type(k, ev));
    }

	private LinkedList<Widget> customWidgets = new LinkedList<>();

	private HashMap<String, String> loadAccounts() {
		HashMap<String, String> map = new HashMap<>();
		File accountFile = new File(Shovel.getUserDirectory(), "accounts.json");
		if (!accountFile.exists()) return map;
		try {
			Gson gson = new Gson();
			return gson.fromJson(new FileReader(accountFile), map.getClass());
		} catch (Exception ex) {
			ALS.alDebugPrint("Cannot read account file:", ex.getMessage());
		}
		return map;
	}

	private void saveAccounts(HashMap<String, String> accounts) {
		File accountFile = new File(Shovel.getUserDirectory(), "accounts.json");
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			Files.write(accountFile.toPath(), gson.toJson(accounts).getBytes(Charset.forName("utf-8")), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (Exception ex) {
			ALS.alDebugPrint("Cannot write account file:", ex.getMessage());
		}
	}

	private void addAccount(String username, String password) {
		// Load existing and merge new into it
		HashMap<String, String> accounts = loadAccounts();
		accounts.put(username, password);
		saveAccounts(accounts);
		createAccountButtons(accounts);
	}

	private void removeAccount(String username) {
		HashMap<String, String> accounts = loadAccounts();
		accounts.remove(username);
		saveAccounts(accounts);
		createAccountButtons(accounts);
	}

	private void createAccountButtons(HashMap<String, String> accounts) {
		for (Widget widget : customWidgets) {
			widget.destroy();
		}
		customWidgets.clear();

		HashMap<String, String> accountData = accounts == null ? loadAccounts() : accounts;
		List<String> accountNames = new LinkedList<>();
		accountNames.addAll(accountData.keySet());
		Collections.sort(accountNames, (o1, o2) -> o1.compareToIgnoreCase(o2));
        int x = 0; int y = 0;
        for (int i = 0; i < accountNames.size(); i++) {
            final String username = accountNames.get(i);
            final String password = accountData.get(username);
            Button btnAcc = new Button(140, username) {
                @Override
                public void click() {
                    parent.wdgmsg("forget");
                    parent.wdgmsg(parent, "login", new AuthClient.NativeCred(username, password), false);
                }
            };
            add(btnAcc, new Coord(x, y));
            Button btnDel = new Button(15, "X") {
                public void click() {
                    removeAccount(username);
                }
            };
            add(btnDel, new Coord(x + 140, y + 6));
            customWidgets.add(btnAcc);
            customWidgets.add(btnDel);

            y += 30;
            if (y >= 500) {
                y = 0;
                x += 160;
            }
        }
	}

	private void checkForNewReleases() {
		String result = Shovel.checkForNewReleases();
		if (result != null) {
			Window window = new Window(Coord.z, "Update");
			RichTextBox textBox = new RichTextBox(new Coord(200, 200), "", HelpWnd.fnd);
			textBox.settext(result);
			textBox.pack();
			window.add(textBox, Coord.z);
			window.pack();
			add(window, new Coord(550, 0));
		}
	}
}
