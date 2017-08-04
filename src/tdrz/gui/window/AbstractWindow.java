package tdrz.gui.window;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import tdrz.config.WindowConfig;
import tdrz.gui.window.listener.MouseDragListener;
import tdrz.gui.window.listener.WindowCanBeOperated;
import tdrz.gui.window.listener.WindowConfigChangedListener;
import tdrz.update.GlobalContextUpdater;
import tdrz.update.data.ApiDataListener;

/**
 * 所有窗口的super class
 * @author MoeKagari
 */
public abstract class AbstractWindow extends AbstractCompositeBase implements ApiDataListener, WindowCanBeOperated {
	protected final static List<AbstractWindow> ACTIVE_WINDOWS = new ArrayList<>();
	private WindowConfig windowConfig = null;
	private final MouseDragListener mouseDragListener = new MouseDragListener(this);
	private final List<WindowConfigChangedListener> windowConfigChangedListeners = new ArrayList<>();

	public AbstractWindow(Shell parent, String title, Image logo, MenuItem menuItem) {
		super(parent, title, logo, menuItem);
		this.init();
	}

	public AbstractWindow(Display parent, String title, InputStream logoStream) {
		super(parent, title, logoStream);
		this.init();
	}

	private void init() {
		this.getShell().addListener(SWT.Activate, ev -> {
			ACTIVE_WINDOWS.remove(this);
			ACTIVE_WINDOWS.add(0, this);//倒序添加
		});

		this.getShell().addShellListener(new ShellAdapter() {
			@Override
			public void shellIconified(ShellEvent ev) {
				AbstractWindow.this.windowConfig.setMinimized(true);
				AbstractWindow.this.windowConfigChangedListeners.forEach(listener -> listener.minimizedChanged(true));
			}

			@Override
			public void shellDeiconified(ShellEvent ev) {
				AbstractWindow.this.windowConfig.setMinimized(false);
				AbstractWindow.this.windowConfigChangedListeners.forEach(listener -> listener.minimizedChanged(false));
			}
		});

		this.getShell().addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent ev) {
				Point size = AbstractWindow.this.getShell().getSize();
				AbstractWindow.this.windowConfig.setSize(size);
				AbstractWindow.this.windowConfigChangedListeners.forEach(listener -> listener.sizeChanged(size));
			}

			@Override
			public void controlMoved(ControlEvent ev) {
				Point location = AbstractWindow.this.getShell().getLocation();
				System.out.println("config.setlocation");
				AbstractWindow.this.windowConfig.setLocation(location);
				AbstractWindow.this.windowConfigChangedListeners.forEach(listener -> listener.locationChanged(location));
			}
		});

		GlobalContextUpdater.addListener(this);
	}

	@Override
	public boolean canOpacityOperation() {
		return true;
	}

	@Override
	public boolean canVisibleOperation() {
		return true;
	}

	@Override
	public boolean canMinimizedOperation() {
		return true;
	}

	@Override
	public void displayWindow() {
		this.windowConfig.setVisible(true);
		this.windowConfigChangedListeners.forEach(listener -> listener.visibleChanged(true));
		super.displayWindow();
	}

	@Override
	public void hiddenWindow() {
		this.windowConfig.setVisible(false);
		this.windowConfigChangedListeners.forEach(listener -> listener.visibleChanged(false));
		super.hiddenWindow();
	}

	public void addWindowConfigChangedListener(WindowConfigChangedListener listener) {
		this.windowConfigChangedListeners.add(listener);
	}

	/*------------------------------------------------------------------------------------------------------------*/

	/** 存储当前窗口的配置时所需的key */
	public String getWindowConfigKey() {
		return this.getClass().getName();
	}

	/** 窗口配置 */
	public final WindowConfig getWindowConfig() {
		return this.windowConfig;
	}

	/** 恢复当前窗口的配置 */
	public final void restoreWindowConfig() {
		if (this.windowConfig == null) {
			this.windowConfig = WindowConfig.get().get(this.getWindowConfigKey());
			if (this.windowConfig == null) {
				System.out.println("无");
				this.windowConfig = new WindowConfig(this.getShell().getSize(), this.getShell().getLocation(), this.getShell().isVisible(), this.getShell().getMinimized(), false, true, 255);
				WindowConfig.get().put(this.getWindowConfigKey(), this.windowConfig);
			} else {
				System.out.println("有");
			}
			System.out.println(this.windowConfig);
			System.out.println(this.getWindowConfigKey());
		}

		//setLocation最先操作,因为下面的一些操作会产生 controlMoved 事件,从而使 windowConfig 的 location 重置
		this.getShell().setLocation(this.windowConfig.getLocation());
		this.setTopMost(this.windowConfig.isTopMost());
		this.toggleTitlebar(this.windowConfig.isShowTitleBar());
		this.changeOpacity(this.windowConfig.getOpacity());
		this.getShell().setSize(this.windowConfig.getSize());
		this.getShell().setMinimized(this.windowConfig.isMinimized());
		//显示窗口在最后
		if (this.windowConfig.isVisible()) {
			this.displayWindow();
		} else {
			this.hiddenWindow();
		}
	}

	/*------------------------------------------------------------------------------------------------------------*/

	public final void changeOpacity(int opacity) {
		this.windowConfig.setOpacity(opacity);
		this.getShell().setAlpha(opacity);
	}

	/** 设置总在前 */
	public final void setTopMost(boolean topMost) {
		this.windowConfig.setTopMost(topMost);
		int SWP_NOSIZE = 0x0001;
		int SWP_NOMOVE = 0x0002;
		int hWndInsertAfter = topMost ? OS.HWND_TOPMOST : OS.HWND_NOTOPMOST;
		OS.SetWindowPos(this.getShell().handle, hWndInsertAfter, 0, 0, 0, 0, SWP_NOSIZE | SWP_NOMOVE);
	}

	public final void toggleTitlebar(boolean showTitleBar) {
		this.windowConfig.setShowTitleBar(showTitleBar);
		int GWL_STYLE = -16;
		int WS_CAPTION = 0x00C00000;
		int style = OS.GetWindowLong(this.getShell().handle, GWL_STYLE);
		if (showTitleBar) {
			style |= WS_CAPTION;
		} else {
			style &= ~WS_CAPTION;
		}
		OS.SetWindowLong(this.getShell().handle, GWL_STYLE, style);
		OS.SetWindowPos(this.getShell().handle, 0, 0, 0, 0, 0, 39);// 39 = SWP_DRAWFRAME |SWP_NOMOVE |SWP_NOSIZE |SWP_NOZORDER
	}

	/** 鼠标按下之后拖动,窗口跟随着移动 */
	protected final void allowMouseDrag(Control con) {
		con.addMouseListener(this.mouseDragListener);
		con.addMouseMoveListener(this.mouseDragListener);
	}

	/** 更新窗口(延迟redraw,只对于 center composite ) */
	protected final void updateWindowRedraw(Runnable run) {
		this.getCenterComposite().setRedraw(false);
		run.run();
		this.getCenterComposite().setRedraw(true);
	}
}
