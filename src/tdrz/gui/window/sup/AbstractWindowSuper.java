package tdrz.gui.window.sup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import tdrz.gui.window.listener.DefaultWindowConfig;
import tdrz.gui.window.listener.MouseDragListener;
import tdrz.gui.window.listener.WindowCanBeOperated;
import tdrz.update.context.data.ApiDataListener;
import tool.function.FunctionUtils;

/** 仅仅是为了分离 {@link AbstractWindow} 的功能部分 */
public abstract class AbstractWindowSuper extends AbstractCompositeBase implements ApiDataListener, WindowCanBeOperated, DefaultWindowConfig {
	private final MouseDragListener mouseDragListener = new MouseDragListener(this);

	/** 是否启用最大化,默认false */
	protected boolean canMaxSize() {
		return false;
	}

	@Override
	protected final int getShellStyle() {
		return SWT.SHELL_TRIM | SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.MIN | (this.canMaxSize() ? SWT.MAX : SWT.NONE);
	}

	/*--------------------------------DefaultWindowConfig----------------------------------------------------------------------------*/

	@Override
	public abstract Point defaultSize();

	@Override
	public boolean defaultVisible() {
		return false;
	}

	/*----------------------------------WindowCanBeOperated--------------------------------------------------------------------------*/

	@Override
	public boolean canIgnoreMouseBeOperated() {
		return true;
	}

	@Override
	public boolean canOpacityBeOperated() {
		return true;
	}

	@Override
	public boolean canVisibleBeOperated() {
		return true;
	}

	@Override
	public boolean canMinimizedBeOperated() {
		return true;
	}

	@Override
	public boolean canTopBeOperated() {
		return true;
	}

	@Override
	public boolean canTitleBarBeOperated() {
		return true;
	}

	@Override
	public boolean canTopMostBeOperated() {
		return true;
	}

	/*------------------------------------------------------------------------------------------------------------*/

	/** 将窗口变成 鼠标可以穿透 的窗口 */
	public void ignoreMouse(boolean ignoreMouse) {
		int exstyle = OS.GetWindowLong(this.shell.handle, OS.GWL_EXSTYLE);
		if (ignoreMouse) {
			exstyle |= OS.WS_EX_TRANSPARENT;
			exstyle |= OS.WS_EX_LAYERED;
		} else {
			exstyle &= ~OS.WS_EX_TRANSPARENT;
			exstyle &= ~OS.WS_EX_LAYERED;
		}
		int alpha = this.shell.getAlpha();
		OS.SetWindowLong(this.shell.handle, OS.GWL_EXSTYLE, exstyle);
		this.shell.setAlpha(alpha);
	}

	/** 改变窗口透明度 */
	public void changeOpacity(int opacity) {
		this.shell.setAlpha(opacity);
	}

	/** 设置总在前 */
	public void setTopMost(boolean topMost) {
		long hWndInsertAfter = topMost ? OS.HWND_TOPMOST : OS.HWND_NOTOPMOST;
		OS.SetWindowPos(this.shell.handle, hWndInsertAfter, 0, 0, 0, 0, OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE);
	}

	/** 切换有无标题栏 */
	public void toggleTitlebar(boolean showTitleBar) {
		int style = OS.GetWindowLong(this.shell.handle, OS.GWL_STYLE);
		if (showTitleBar) {
			style |= OS.WS_CAPTION;
		} else {
			style &= ~OS.WS_CAPTION;
		}
		OS.SetWindowLong(this.shell.handle, OS.GWL_STYLE, style);
		OS.SetWindowPos(this.shell.handle, 0L, 0, 0, 0, 0, OS.SWP_DRAWFRAME | OS.SWP_NOMOVE | OS.SWP_NOSIZE | OS.SWP_NOZORDER | OS.SWP_NOACTIVATE);
	}

	/** 设置窗口置顶(非总在前) */
	public final void setTop() {
		OS.SetWindowPos(this.shell.handle, OS.HWND_TOP, 0, 0, 0, 0, OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE);
	}

	/** 鼠标按下之后拖动,窗口跟随着移动 */
	protected final void allowMouseDrag(Control con) {
		con.addMouseListener(this.mouseDragListener);
		con.addMouseMoveListener(this.mouseDragListener);
	}

	/** 鼠标按下之后拖动,窗口跟随着移动 */
	protected final void allowMouseDragRecursively(Control con) {
		this.allowMouseDrag(con);
		if (con instanceof Composite) {
			FunctionUtils.forEach(((Composite) con).getChildren(), this::allowMouseDragRecursively);
		}
	}
}
