package tdrz.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.MenuItem;

import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.data.DataType;
import tdrz.utils.SwtUtils;

/**
 * 呼出式窗口的super class
 * @author MoeKagari
 */
public abstract class WindowBase extends AbstractWindow {
	public WindowBase(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, title, menuItem);
		this.initShellListener();
	}

	public WindowBase(ApplicationMain main, String title) {
		super(main, title, null);
		this.initShellListener();
	}

	private void initShellListener() {
		this.getShell().addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent ev) {
				ev.doit = false;
				WindowBase.this.hiddenWindow();
			}
		});
	}

	@Override
	public void update(DataType type) {}

	/** 是否启用最大化,默认false */
	protected boolean canMaxSize() {
		return false;
	}

	/** 是否可以改变size,默认true */
	protected boolean canReSize() {
		return true;
	}

	@Override
	protected Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

	@Override
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | (this.canReSize() ? SWT.RESIZE : SWT.NONE) | SWT.MIN | (this.canMaxSize() ? SWT.MAX : SWT.NONE);
	}
}
