package tdrz.gui.window;

import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import tdrz.gui.window.listener.MouseDragListener;
import tdrz.gui.window.listener.WindowCanBeOperated;
import tdrz.update.data.ApiDataListener;
import tool.FunctionUtils;

/** 仅仅是为了分离 {@link AbstractWindow} 的功能部分 */
public abstract class AbstractWindowImplements extends AbstractCompositeBase implements ApiDataListener, WindowCanBeOperated {
	private final MouseDragListener mouseDragListener = new MouseDragListener(this);

	public AbstractWindowImplements(Shell parent, String title, Image logo, MenuItem menuItem) {
		super(parent, title, logo, menuItem);
	}

	public AbstractWindowImplements(Display parent, String title, InputStream logoStream) {
		super(parent, title, logoStream);
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
	public boolean canTitleBarOperation() {
		return true;
	}

	@Override
	public boolean canTopMostOperation() {
		return true;
	}

	/*------------------------------------------------------------------------------------------------------------*/

	/** 改变窗口透明度 */
	public void changeOpacity(int opacity) {
		this.getShell().setAlpha(opacity);
	}

	/** 设置总在前 */
	public void setTopMost(boolean topMost) {
		long hWndInsertAfter = topMost ? OS.HWND_TOPMOST : OS.HWND_NOTOPMOST;
		OS.SetWindowPos(this.getShell().handle, hWndInsertAfter, 0, 0, 0, 0, OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE);
	}

	/** 切换有无标题栏 */
	public void toggleTitlebar(boolean showTitleBar) {
		int style = OS.GetWindowLong(this.getShell().handle, OS.GWL_STYLE);
		if (showTitleBar) {
			style |= OS.WS_CAPTION;
		} else {
			style &= ~OS.WS_CAPTION;
		}
		OS.SetWindowLong(this.getShell().handle, OS.GWL_STYLE, style);
		OS.SetWindowPos(this.getShell().handle, 0L, 0, 0, 0, 0, OS.SWP_DRAWFRAME | OS.SWP_NOMOVE | OS.SWP_NOSIZE | OS.SWP_NOZORDER | OS.SWP_NOACTIVATE);
	}

	/** 设置窗口置顶 */
	public final void setTop() {
		OS.SetWindowPos(this.getShell().handle, OS.HWND_TOP, 0, 0, 0, 0, OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE);
	}

	/** 鼠标按下之后拖动,窗口跟随着移动 */
	protected final void allowMouseDrag(Control con) {
		con.addMouseListener(this.mouseDragListener);
		con.addMouseMoveListener(this.mouseDragListener);
	}

	/** 鼠标按下之后拖动,窗口跟随着移动 */
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
