package tdrz.gui.window.listener;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;

import tdrz.gui.window.AbstractWindowImplements;

/** shell跟随鼠标的拖动而移动 */
public class MouseDragListener extends MouseAdapter implements MouseMoveListener {
	private final AbstractWindowImplements window;
	private Point oldLocation = null;

	public MouseDragListener(AbstractWindowImplements window) {
		this.window = window;
	}

	@Override
	public void mouseMove(MouseEvent ev) {
		if (this.oldLocation != null) {
			Point shellLocation = this.window.getShell().getLocation();
			int x = shellLocation.x - this.oldLocation.x + ev.x;
			int y = shellLocation.y - this.oldLocation.y + ev.y;
			this.window.getShell().setLocation(x, y);
		}
	}

	@Override
	public void mouseDown(MouseEvent ev) {
		if (ev.button == 1) {
			this.oldLocation = new Point(ev.x, ev.y);
		}
	}

	@Override
	public void mouseUp(MouseEvent ev) {
		this.oldLocation = null;
	}
}
