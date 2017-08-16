package tdrz.gui.window.sup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import tdrz.core.config.WindowConfig;
import tdrz.gui.window.listener.WindowConfigChangedListener;
import tdrz.update.context.GlobalContextUpdater;

/**
 * 所有窗口的super class
 * @author MoeKagari
 */
public abstract class AbstractWindow extends AbstractWindowSuper {
	private WindowConfig windowConfig = null;
	private final List<WindowConfigChangedListener> windowConfigChangedListeners = new ArrayList<>();

	public AbstractWindow(Shell parent, String title) {
		super(parent, title);
		this.restoreConfigAndAddShellListener();
		GlobalContextUpdater.addListener(this);
	}

	private void restoreConfigAndAddShellListener() {
		this.windowConfig = WindowConfig.get().get(this.getWindowConfigKey());
		if (this.windowConfig == null) {
			this.windowConfig = new WindowConfig(this.defaultSize(), this.shell.getLocation(), this.defaultVisible(), this.shell.getMinimized(), false, true, 255);
			WindowConfig.get().put(this.getWindowConfigKey(), this.windowConfig);
		}

		//在添加监听Resize和Move的listener前,恢复location,size
		this.shell.setSize(this.windowConfig.getSize());//会产生Resize事件
		this.shell.setLocation(this.windowConfig.getLocation());//会产生Resize和Move事件

		this.shell.addListener(SWT.Iconify, ev -> {//[最小化]事件
			this.windowConfig.setMinimized(true);
			this.windowConfigChangedListeners.forEach(listener -> listener.minimizedChanged(true));
		});

		this.shell.addListener(SWT.Deiconify, ev -> {//[从最小化中恢复]事件
			this.windowConfig.setMinimized(false);
			this.windowConfigChangedListeners.forEach(listener -> listener.minimizedChanged(false));
		});

		this.shell.addListener(SWT.Resize, ev -> {//[改变窗口大小]事件
			this.windowConfig.setSize(this.shell.getSize());
		});

		this.shell.addListener(SWT.Move, ev -> {//[移动窗口]事件
			this.windowConfig.setLocation(this.shell.getLocation());
		});
	}

	/*------------------------------------------------------------------------------------------------------------*/

	/** 隐藏窗口 */
	public void hiddenWindow() {
		if (this.shell.isVisible()) {
			this.windowConfig.setVisible(false);
			this.windowConfigChangedListeners.forEach(listener -> listener.visibleChanged(false));
			this.shell.setVisible(false);
		}
	}

	/** 显示窗口 */
	public void displayWindow() {
		if (this.shell.isVisible() == false) {
			this.windowConfig.setVisible(true);
			this.windowConfigChangedListeners.forEach(listener -> listener.visibleChanged(true));
			this.shell.setVisible(true);
		}
		this.setTop();
		this.setTopMost(this.windowConfig.isTopMost());//解决topmost失效问题,重复setTopMost
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
		this.shell.setMinimized(this.windowConfig.isMinimized());
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
