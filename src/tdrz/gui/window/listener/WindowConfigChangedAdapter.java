package tdrz.gui.window.listener;

import org.eclipse.swt.graphics.Point;

public class WindowConfigChangedAdapter implements WindowConfigChangedListener {
	@Override
	public void sizeChanged(Point size) {}

	@Override
	public void locationChanged(Point location) {}

	@Override
	public void visibleChanged(boolean visible) {}

	@Override
	public void minimizedChanged(boolean minimized) {}
}
