package tdrz.gui.window;

import java.io.InputStream;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import tdrz.config.WindowConfig;
import tdrz.update.GlobalContextUpdater;
import tdrz.update.data.ApiDataListener;
import tdrz.update.data.DataType;
import tdrz.utils.SwtUtils;
import tool.FunctionUtils;

/**
 * 所有窗口的super class
 * @author MoeKagari
 */
public abstract class AbstractWindow implements ApiDataListener {
	private final Image logo;
	private final String defaultTitle;
	private final Shell shell;
	private final MenuItem menuItem;

	private Menu menuBar;
	private Composite leftComposite;
	private Composite topComposite;
	private Composite centerComposite;
	private Composite bottomComposite;
	private Composite rightComposite;

	private WindowConfig windowConfig = null;
	private final MouseDragListener mouseDragListener = new MouseDragListener();

	public AbstractWindow(Shell parent, String title, Image logo, MenuItem menuItem) {
		this.shell = new Shell(parent, this.getShellStyle());
		this.defaultTitle = title;
		this.logo = logo;
		this.menuItem = menuItem;
		this.init();
		this.menuItem.addSelectionListener(new ControlSelectionListener(ev -> {
			if (this.menuItem.getSelection()) {
				this.displayWindow();
			} else {
				this.hiddenWindow();
			}
		}));
	}

	public AbstractWindow(Shell parent, String title, Image logo) {
		this.shell = new Shell(parent, this.getShellStyle());
		this.defaultTitle = title;
		this.logo = logo;
		this.menuItem = null;
		this.init();
	}

	public AbstractWindow(Display parent, String title, InputStream logoInputStream) {
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
		this.shell.setLayout(SwtUtils.makeGridLayout(1 + (this.haveLeftComposite() ? 1 : 0) + (this.haveRightComposite() ? 1 : 0), 0, 0, 0, 0));
		this.shell.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(this.menuBar);

		if (this.haveLeftComposite()) {
			this.leftComposite = new Composite(this.shell, SWT.NONE);
			this.leftComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
			this.leftComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		}

		Composite subComposite = new Composite(this.shell, SWT.NONE);
		subComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		subComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		{
			if (this.haveTopComposite()) {
				this.topComposite = new Composite(subComposite, SWT.NONE);
				this.topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				this.topComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			}

			this.centerComposite = new Composite(subComposite, SWT.NONE);
			this.centerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.centerComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));

			if (this.haveBottomComposite()) {
				this.bottomComposite = new Composite(subComposite, SWT.NONE);
				this.bottomComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				this.bottomComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			}
		}

		if (this.haveRightComposite()) {
			this.rightComposite = new Composite(this.shell, SWT.NONE);
			this.rightComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
			this.rightComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		}

		GlobalContextUpdater.addListener(this);
	}

	public Shell getShell() {
		return this.shell;
	}

	public Image getLogo() {
		return this.logo;
	}

	public String getDefaultTitle() {
		return this.defaultTitle;
	}

	public Menu getMenuBar() {
		return this.menuBar;
	}

	public Composite getLeftComposite() {
		return this.leftComposite;
	}

	public Composite getTopComposite() {
		return this.topComposite;
	}

	public Composite getCenterComposite() {
		return this.centerComposite;
	}

	public Composite getBottomComposite() {
		return this.bottomComposite;
	}

	public Composite getRightComposite() {
		return this.rightComposite;
	}

	protected boolean haveLeftComposite() {
		return false;
	}

	public boolean haveTopComposite() {
		return false;
	}

	public boolean haveBottomComposite() {
		return false;
	}

	public boolean haveRightComposite() {
		return false;
	}

	/** 存储当前窗口的配置 */
	public void storeWindowConfig() {
		if (this.shell.isDisposed()) return;
		if (FunctionUtils.isFalse(this.shell.getMaximized())) {//最大化不记录
			this.windowConfig.setSize(this.shell.getSize());
			this.windowConfig.setLocation(this.shell.getLocation());
		}
		this.windowConfig.setMinimized(this.shell.getMinimized());
		this.windowConfig.setVisible(this.shell.isVisible());
	}

	/** 恢复当前窗口的配置 */
	public void restoreWindowConfig() {
		if (this.windowConfig == null) {
			this.windowConfig = WindowConfig.get().get(this.getWindowConfigKey());
			if (this.windowConfig == null) {
				this.windowConfig = new WindowConfig();
				this.windowConfig.setTopMost(this.defaultTopMost());
				this.storeWindowConfig();
				WindowConfig.get().put(this.getWindowConfigKey(), this.windowConfig);
				return;
			}
		}

		this.shell.setSize(this.windowConfig.getSize());
		this.shell.setLocation(this.windowConfig.getLocation());
		this.shell.setMinimized(this.windowConfig.getMinimized());
		this.setVisible(this.windowConfig.isVisible());
	}

	/** 更新窗口(延迟redraw) */
	protected void updateWindowRedraw(Runnable run) {
		this.centerComposite.setRedraw(false);
		run.run();
		this.centerComposite.setRedraw(true);
	}

	public void hiddenWindow() {
		this.setVisible(false);
	}

	public void displayWindow() {
		this.setVisible(true);
	}

	private void setVisible(boolean visible) {
		if (visible) this.shell.setMinimized(false);
		this.shell.setVisible(visible);
		FunctionUtils.ifRunnable(visible, this.shell::forceActive);
		FunctionUtils.notNull(this.menuItem, mi -> mi.setSelection(visible));

		this.setTopMost(this.windowConfig.isTopMost());
	}

	/** 设置总在前 */
	protected void setTopMost(boolean topMost) {
		this.windowConfig.setTopMost(topMost);

		int hWndInsertAfter = topMost ? OS.HWND_TOPMOST : OS.HWND_NOTOPMOST;
		Point location = this.shell.getLocation();
		Point size = this.shell.getSize();
		OS.SetWindowPos(this.shell.handle, hWndInsertAfter, location.x, location.y, size.x, size.y, SWT.NULL);
	}

	protected boolean defaultTopMost() {
		return false;
	}

	/** 鼠标按下之后拖动,窗口跟随着移动 */
	protected void allowMouseDrag(Control con) {
		con.addMouseListener(this.mouseDragListener);
		con.addMouseMoveListener(this.mouseDragListener);
	}

	/** 存储当前窗口的配置时所需的key */
	protected String getWindowConfigKey() {
		return this.getClass().getName();
	}

	/** 默认size */
	protected abstract Point getDefaultSize();

	protected WindowConfig getWindowConfig() {
		return this.windowConfig;
	}

	/** 默认shellstyle */
	protected abstract int getShellStyle();

	@Override
	public void update(DataType type) {}

	/** shell跟随鼠标的拖动而移动 */
	private class MouseDragListener implements MouseListener, MouseMoveListener {
		private boolean allowDrag = false;
		private Point oldLocation = null;

		@Override
		public void mouseMove(MouseEvent ev) {
			if (this.allowDrag) {
				Point shellLocation = AbstractWindow.this.shell.getLocation();
				int x = shellLocation.x - this.oldLocation.x + ev.x;
				int y = shellLocation.y - this.oldLocation.y + ev.y;
				AbstractWindow.this.shell.setLocation(x, y);
			}
		}

		@Override
		public void mouseDoubleClick(MouseEvent ev) {}

		@Override
		public void mouseDown(MouseEvent ev) {
			if (ev.button == 1) {
				this.allowDrag = true;
				this.oldLocation = new Point(ev.x, ev.y);
			}
		}

		@Override
		public void mouseUp(MouseEvent ev) {
			this.allowDrag = false;
		}
	}

	public static class ControlResizeListener extends ControlAdapter {
		private final Consumer<ControlEvent> handler;

		public ControlResizeListener(Consumer<ControlEvent> handler) {
			this.handler = handler;
		}

		public ControlResizeListener(Runnable run) {
			this.handler = ev -> run.run();
		}

		@Override
		public void controlResized(ControlEvent ev) {
			this.handler.accept(ev);
		}
	}

	public static class ControlSelectionListener extends SelectionAdapter {
		private final Consumer<SelectionEvent> handler;

		public ControlSelectionListener(Consumer<SelectionEvent> handler) {
			this.handler = handler;
		}

		public ControlSelectionListener(Runnable run) {
			this.handler = ev -> run.run();
		}

		public ControlSelectionListener andThen(Consumer<SelectionEvent> after) {
			return new ControlSelectionListener(this.handler.andThen(after));
		}

		public ControlSelectionListener andThen(ControlSelectionListener after) {
			return new ControlSelectionListener(this.handler.andThen(after.handler));
		}

		@Override
		public void widgetSelected(SelectionEvent ev) {
			this.handler.accept(ev);
		}
	}

	public static class SpinnerMouseWheelListener implements MouseWheelListener {
		private final Consumer<MouseEvent> handler;
		private final Spinner spinner;

		public SpinnerMouseWheelListener(Spinner spinner, Consumer<MouseEvent> handler) {
			this.spinner = spinner;
			this.handler = handler;
		}

		public SpinnerMouseWheelListener(Spinner spinner, Runnable run) {
			this.spinner = spinner;
			this.handler = ev -> run.run();
		}

		@Override
		public void mouseScrolled(MouseEvent ev) {
			int count = ev.count;
			if (count != 0) {
				int cur = this.spinner.getSelection();
				int next = cur + (count > 0 ? 1 : -1);
				if (next >= this.spinner.getMinimum() && next <= this.spinner.getMaximum()) {
					this.spinner.setSelection(next);
					this.handler.accept(ev);
				}
			}
		}
	}
}
