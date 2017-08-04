package tdrz.gui.window;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import tdrz.config.WindowConfig;
import tdrz.gui.window.listener.MouseDragListener;
import tdrz.gui.window.listener.WindowCanBeOperated;
import tdrz.gui.window.listener.WindowConfigChangedListener;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.GlobalContextUpdater;
import tdrz.update.data.ApiDataListener;
import tool.FunctionUtils;

/**
 * 所有窗口的super class
 * @author MoeKagari
 */
public abstract class AbstractWindow extends AbstractCompositeBase implements ApiDataListener, WindowCanBeOperated {
	protected final static List<AbstractWindow> ACTIVE_WINDOWS = new ArrayList<>();
	private WindowConfig windowConfig = null;
	private final MouseDragListener mouseDragListener = new MouseDragListener(this);
	private final List<WindowConfigChangedListener> windowConfigChangedListeners = new ArrayList<>();

	public AbstractWindow(ApplicationMain main, String title, MenuItem menuItem) {
		super(new Shell(main.getDisplay(), SWT.TOOL), title, main.getLogo(), menuItem);
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
				AbstractWindow.this.setTopMost(AbstractWindow.this.windowConfig.isTopMost());//topmost失效问题,重复topmost
			}
		});

		this.getShell().addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent ev) {
				Point size = AbstractWindow.this.getShell().getSize();
				AbstractWindow.this.windowConfig.setSize(size);
				System.out.println("size change : " + size);
			}

			@Override
			public void controlMoved(ControlEvent ev) {
				Point location = AbstractWindow.this.getShell().getLocation();
				AbstractWindow.this.windowConfig.setLocation(location);
				System.out.println("location change : " + location);
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
	public boolean canTopOperation() {
		return true;
	}

	@Override
	public void displayWindow() {
		super.displayWindow();
		this.windowConfig.setVisible(true);
		this.windowConfigChangedListeners.forEach(listener -> listener.visibleChanged(true));
		this.setTopMost(this.windowConfig.isTopMost());//topmost失效问题,重复topmost
	}

	@Override
	public void hiddenWindow() {
		super.hiddenWindow();
		this.windowConfig.setVisible(false);
		this.windowConfigChangedListeners.forEach(listener -> listener.visibleChanged(false));
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
				this.windowConfig = new WindowConfig(this.getShell().getSize(), this.getShell().getLocation(), this.getShell().isVisible(), this.getShell().getMinimized(), false, true, 255);
				WindowConfig.get().put(this.getWindowConfigKey(), this.windowConfig);
				return;
			}
		}

		//setLocation最先操作,因为下面的一些操作会产生 controlMoved 事件,从而使 windowConfig 的 location 重置
		this.getShell().setLocation(this.windowConfig.getLocation());//自身也会产生 controlMoved 事件
		//setSize先操作,因为下面的一些操作会产生 controlResized 事件,从而使 windowConfig 的 size 重置
		this.getShell().setSize(this.windowConfig.getSize());//会产生 controlMoved controlResized 事件
		this.getShell().setMinimized(this.windowConfig.isMinimized());
		this.changeOpacity(this.windowConfig.getOpacity());
		this.toggleTitlebar(this.windowConfig.isShowTitleBar());
		if (this.windowConfig.isVisible()) {
			this.displayWindow();
		} else {
			this.hiddenWindow();
		}
		this.setTopMost(this.windowConfig.isTopMost());
	}

	/*------------------------------------------------------------------------------------------------------------*/

	public final void changeOpacity(int opacity) {
		this.windowConfig.setOpacity(opacity);
		this.getShell().setAlpha(opacity);
	}

	/** 设置总在前 */
	public final void setTopMost(boolean topMost) {
		this.windowConfig.setTopMost(topMost);
		long hWndInsertAfter = topMost ? OS.HWND_TOPMOST : OS.HWND_NOTOPMOST;
		OS.SetWindowPos(this.getShell().handle, hWndInsertAfter, 0, 0, 0, 0, OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE);
	}

	/** 切换有无标题栏 */
	public final void toggleTitlebar(boolean showTitleBar) {
		this.windowConfig.setShowTitleBar(showTitleBar);
		int style = OS.GetWindowLong(this.getShell().handle, OS.GWL_STYLE);
		if (showTitleBar) {
			style |= OS.WS_CAPTION;
		} else {
			style &= ~OS.WS_CAPTION;
		}
		OS.SetWindowLong(this.getShell().handle, OS.GWL_STYLE, style);
		OS.SetWindowPos(this.getShell().handle, 0L, 0, 0, 0, 0, OS.SWP_DRAWFRAME | OS.SWP_NOMOVE | OS.SWP_NOSIZE | OS.SWP_NOZORDER | OS.SWP_NOACTIVATE);
	}

	public final void setTop() {
		OS.SetWindowPos(this.getShell().handle, OS.HWND_TOP, 0, 0, 0, 0, OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE);
	}

	/** 鼠标按下之后拖动,窗口跟随着移动 */
	protected final void allowMouseDrag(Control con) {
		con.addMouseListener(this.mouseDragListener);
		con.addMouseMoveListener(this.mouseDragListener);
	}

	protected final void allowMouseDragRecursively(Composite com) {
		this.allowMouseDrag(com);
		FunctionUtils.forEach(com.getChildren(), con -> {
			if (con instanceof Composite) {
				this.allowMouseDragRecursively((Composite) con);
			}
			this.allowMouseDrag(con);
		});
	}

	/** 更新窗口(延迟redraw,只对于 center composite ) */
	protected final void updateWindowRedraw(Runnable run) {
		this.getCenterComposite().setRedraw(false);
		run.run();
		this.getCenterComposite().setRedraw(true);
	}
}
