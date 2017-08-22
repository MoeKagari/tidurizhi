package tdrz.gui.window.sup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

import tdrz.core.util.SwtUtils;
import tdrz.update.context.data.DataType;

/**
 * 呼出式窗口的super class
 * @author MoeKagari
 */
public abstract class WindowBase extends AbstractWindow {
	public WindowBase() {
		this.shell.addListener(SWT.Close, ev -> {
			ev.doit = false;
			this.hiddenWindow();
		});
	}

	@Override
	public void update(DataType type) {}

	@Override
	public Point defaultSize() {
		return SwtUtils.DPIAwareSize(new Point(400, 200));
	}
}
