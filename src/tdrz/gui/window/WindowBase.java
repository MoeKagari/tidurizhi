package tdrz.gui.window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import tdrz.gui.window.main.ApplicationMain;
import tdrz.utils.SwtUtils;

/**
 * 呼出式窗口的super class
 * @author MoeKagari
 */
public class WindowBase extends AbstractWindow {
	private final List<Consumer<ShellEvent>> handlersAfterHidden = new ArrayList<>();

	public WindowBase(ApplicationMain main, MenuItem menuItem, String title) {
		super(new Shell(main.getDisplay(), SWT.TOOL), title, main.getLogo(), menuItem);
		this.initShellListener();
	}

	public WindowBase(ApplicationMain main, String title) {
		super(new Shell(main.getDisplay(), SWT.TOOL), title, main.getLogo());
		this.initShellListener();
	}

	private void initShellListener() {
		this.getShell().addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent ev) {
				ev.doit = false;
				WindowBase.this.hiddenWindow();
				WindowBase.this.handlersAfterHidden.forEach(handler -> handler.accept(ev));
			}
		});
	}

	/*------------------------------------------------------------------------------------------------------------*/

	public void addHandlerAfterHidden(Consumer<ShellEvent> handler) {
		this.handlersAfterHidden.add(handler);
	}

	public void addHandlerAfterHidden(Runnable run) {
		this.handlersAfterHidden.add(ev -> run.run());
	}

	/*------------------------------------------------------------------------------------------------------------*/

	@Override
	protected Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}

	@Override
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MIN;
	}
}
