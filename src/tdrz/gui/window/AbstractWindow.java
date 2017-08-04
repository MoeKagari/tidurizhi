package tdrz.gui.window;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import tdrz.config.WindowConfig;
import tdrz.gui.window.listener.WindowConfigChangedListener;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.GlobalContextUpdater;

/**
 * 所有窗口的super class
 * @author MoeKagari
 */
public abstract class AbstractWindow extends AbstractWindowImplements {
	protected final static List<AbstractWindow> ACTIVE_WINDOWS = new ArrayList<>();
	private WindowConfig windowConfig = null;
	private final List<WindowConfigChangedListener> windowConfigChangedListeners = new ArrayList<>();

	public AbstractWindow(ApplicationMain main, String title, MenuItem menuItem) {
		super(new Shell(main.getDisplay(), SWT.TOOL), title, main.getLogo(), menuItem);
		this.restoreConfigAndAddShellListener();
	}

	public AbstractWindow(Display parent, String title, InputStream logoStream) {
		super(parent, title, logoStream);
		this.restoreConfigAndAddShellListener();
	}

	private void restoreConfigAndAddShellListener() {
		this.windowConfig = WindowConfig.get().get(this.getWindowConfigKey());
		if (this.windowConfig == null) {
			this.windowConfig = new WindowConfig(this.getShell().getSize(), this.getShell().getLocation(), this.getShell().isVisible(), this.getShell().getMinimized(), false, true, 255);
			WindowConfig.get().put(this.getWindowConfigKey(), this.windowConfig);
		} else {
			//在添加监听controlMoved和controlResized的listener前,恢复location,size
			this.getShell().setLocation(this.windowConfig.getLocation());
			this.getShell().setSize(this.windowConfig.getSize());
		}

		this.getShell().addListener(SWT.Activate, ev -> {//[窗口激活]事件
			ACTIVE_WINDOWS.remove(this);
			ACTIVE_WINDOWS.add(0, this);//倒序添加
		});

		this.getShell().addListener(SWT.Iconify, ev -> {//[最小化]事件
			this.windowConfig.setMinimized(true);
			this.windowConfigChangedListeners.forEach(listener -> listener.minimizedChanged(true));
		});

		this.getShell().addListener(SWT.Deiconify, ev -> {//[从最小化中恢复]事件
			this.windowConfig.setMinimized(false);
			this.windowConfigChangedListeners.forEach(listener -> listener.minimizedChanged(false));
			this.setTopMost(AbstractWindow.this.windowConfig.isTopMost());//topmost失效问题,重复topmost
		});

		this.getShell().addListener(SWT.Resize, ev -> {//[改变窗口大小]事件
			Point size = this.getShell().getSize();
			this.windowConfig.setSize(size);
		});

		this.getShell().addListener(SWT.Move, ev -> {//[移动窗口]事件
			Point location = this.getShell().getLocation();
			this.windowConfig.setLocation(location);
		});

		GlobalContextUpdater.addListener(this);
	}

	@Override
	public void displayWindow() {
		this.windowConfig.setVisible(true);
		this.windowConfigChangedListeners.forEach(listener -> listener.visibleChanged(true));
		super.displayWindow();
		this.setTopMost(this.windowConfig.isTopMost());//解决topmost失效问题,重复topmost
	}

	@Override
	public void hiddenWindow() {
		this.windowConfig.setVisible(false);
		this.windowConfigChangedListeners.forEach(listener -> listener.visibleChanged(false));
		super.hiddenWindow();
	}

	public final void addWindowConfigChangedListener(WindowConfigChangedListener listener) {
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

	/** 恢复窗口配置 */
	public final void restoreWindowConfig() {
		this.getShell().setMinimized(this.windowConfig.isMinimized());
		this.changeOpacity(this.windowConfig.getOpacity());
		this.toggleTitlebar(this.windowConfig.isShowTitleBar());
		if (this.windowConfig.isVisible()) {
			this.displayWindow();
		} else {
			this.hiddenWindow();
		}
		//等显示窗口之后再topmost
		this.setTopMost(this.windowConfig.isTopMost());
	}

	/*------------------------------------------------------------------------------------------------------------*/

	/** 改变窗口透明度 */
	@Override
	public final void changeOpacity(int opacity) {
		this.windowConfig.setOpacity(opacity);
		super.changeOpacity(opacity);
	}

	/** 设置总在前 */
	@Override
	public final void setTopMost(boolean topMost) {
		this.windowConfig.setTopMost(topMost);
		super.setTopMost(topMost);
	}

	/** 切换有无标题栏 */
	@Override
	public final void toggleTitlebar(boolean showTitleBar) {
		this.windowConfig.setShowTitleBar(showTitleBar);
		super.toggleTitlebar(showTitleBar);
	}
}
