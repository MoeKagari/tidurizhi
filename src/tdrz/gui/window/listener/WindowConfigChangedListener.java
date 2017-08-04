package tdrz.gui.window.listener;

import org.eclipse.swt.graphics.Point;

public interface WindowConfigChangedListener {
	public void sizeChanged(Point size);

	public void locationChanged(Point location);

	public void visibleChanged(boolean visible);

	public void minimizedChanged(boolean minimized);
}
