package tdrz.gui.window.sup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import tdrz.core.util.SwtUtils;
import tdrz.gui.window.WindowResource;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.context.data.DataType;

/**
 * 呼出式窗口的super class
 * @author MoeKagari
 */
public abstract class WindowBase extends AbstractWindow {
	private final ApplicationMain main;

	/**
	 * @param main
	 * @param menuItem null可
	 * @param title 窗口标题
	 */
	public WindowBase(ApplicationMain main, String title) {
		super(new Shell(WindowResource.DISPLAY, SWT.TOOL), title);
		this.main = main;
		this.shell.addListener(SWT.Close, ev -> {
			ev.doit = false;
			this.hiddenWindow();
		});
	}

	public ApplicationMain getMain() {
		return this.main;
	}

	@Override
	public void update(DataType type) {}

	/** 是否启用最大化,默认false */
	protected boolean canMaxSize() {
		return false;
	}

	@Override
	public Point defaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

	@Override
	protected final int getShellStyle() {
		return SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.MIN | (this.canMaxSize() ? SWT.MAX : SWT.NONE);
	}
}
