package tdrz.gui.window.sub;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import tdrz.config.AppConstants;
import tdrz.gui.window.WindowBase;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.utils.SwtUtils;

/**
 * 舰队面板-单
 * @author MoeKagari
 */
public abstract class FleetWindowOut extends WindowBase {
	private final FleetWindow fleetWindow;

	public FleetWindowOut(ApplicationMain main, MenuItem menuItem, int id) {
		super(main, menuItem, AppConstants.DEFAULT_FLEET_NAME[id - 1]);
		this.fleetWindow = new FleetWindow(id, new Composite(this.getCenterComposite(), SWT.BORDER));
	}

	public FleetWindow getFleetWindow() {
		return this.fleetWindow;
	}

	public abstract int getId();

	@Override
	public String getWindowConfigKey() {
		return FleetWindowOut.class.getName() + this.getId();
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(250, 269));
	}
}
