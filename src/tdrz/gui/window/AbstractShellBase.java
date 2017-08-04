package tdrz.gui.window;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.utils.SwtUtils;

/** 和 {@link AbstractCompositeBase} 一样,只是为了分层而分离的super class ,编写窗口类应使用 {@link AbstractWindow} 或其子类 */
public abstract class AbstractShellBase {
	private final Image logo;
	private final String defaultTitle;
	private final Shell shell;
	private final MenuItem menuItem;
	private Menu menuBar;

	public AbstractShellBase(Shell parent, String title, Image logo, MenuItem menuItem) {
		this.shell = new Shell(parent, this.getShellStyle());
		this.defaultTitle = title;
		this.logo = logo;
		this.menuItem = menuItem;
		this.init();
	}

	public AbstractShellBase(Display parent, String title, InputStream logoInputStream) {
		this.shell = new Shell(parent, this.getShellStyle());
		this.defaultTitle = title;
		this.logo = new Image(parent, logoInputStream);
		this.menuItem = null;
		this.init();
	}

	private void init() {
		this.shell.setText(this.defaultTitle);
		this.shell.setImage(this.logo);
		this.shell.setSize(this.getDefaultSize());
		this.shell.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.shell.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(this.menuBar);

		if (this.menuItem != null) this.menuItem.addSelectionListener(new ControlSelectionListener(ev -> {
			if (this.menuItem.getSelection()) {
				this.displayWindow();
			} else {
				this.hiddenWindow();
			}
		}));
	}

	public final Shell getShell() {
		return this.shell;
	}

	public final Image getLogo() {
		return this.logo;
	}

	public final String getDefaultTitle() {
		return this.defaultTitle;
	}

	public final Menu getMenuBar() {
		return this.menuBar;
	}

	/** 默认size */
	protected abstract Point getDefaultSize();

	/** 默认shellstyle */
	protected abstract int getShellStyle();

	/*------------------------------------------------------------------------------------------------------------*/

	public void hiddenWindow() {
		this.setVisible(false);
	}

	public void displayWindow() {
		this.setVisible(true);
	}

	private void setVisible(boolean visible) {
		if (this.menuItem != null) {
			this.menuItem.setSelection(visible);
		}
		this.shell.setVisible(visible);
	}
}
