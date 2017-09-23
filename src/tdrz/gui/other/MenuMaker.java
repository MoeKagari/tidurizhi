package tdrz.gui.other;

import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import tdrz.core.util.SwtUtils;

public class MenuMaker {
	private final Menu menu;

	public MenuMaker(Menu menu) {
		this.menu = menu;
	}

	public Menu getMenu() {
		return this.menu;
	}

	public MenuItem makeMenuItem(Function<Menu, MenuItem> fun) {
		return fun.apply(this.menu);
	}

	public MenuMaker toCascadeMenuMaker(String text) {
		return new MenuMaker(SwtUtils.makeCasacdeMenu(this.menu, text));
	}

	public MenuMaker makeSeparatorMenuItem() {
		SwtUtils.makeSeparatorMenuItem(this.menu);
		return this;
	}

	public MenuMaker makeMenuItem(int style, String text, SelectionListener listener) {
		SwtUtils.makeMenuItem(this.menu, style, text, listener);
		return this;
	}

	public MenuMaker makeMenuItem(int style, String text, Runnable handler) {
		return this.makeMenuItem(style, text, new ControlSelectionListener(handler));
	}

	public MenuMaker makeMenuItem(int style, String text, Consumer<SelectionEvent> handler) {
		return this.makeMenuItem(style, text, new ControlSelectionListener(handler));
	}

	/*-------------------------------------------------------------------------------------------------------------------------------------------------*/

	public static MenuMaker makeCasacdeMenuMaker(Menu parent, String text) {
		return new MenuMaker(SwtUtils.makeCasacdeMenu(parent, text));
	}

	public static MenuMaker makeNormalMenuMaker(Control parent) {
		return new MenuMaker(SwtUtils.makeNormalMenu(parent));
	}

	public static MenuMaker makeBarMenuMaker(Shell parent) {
		return new MenuMaker(SwtUtils.makeBarMenu(parent));
	}
}
