package tdrz.gui.window.sub;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import tdrz.core.config.AppConstants;
import tdrz.core.util.SwtUtils;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sup.WindowBase;

/**
 * 舰队面板-单
 * @author MoeKagari
 */
public abstract class FleetWindowOut extends WindowBase {
	private final FleetWindow fleetWindow;

	public FleetWindowOut(ApplicationMain main, int id) {
		super(main, AppConstants.DEFAULT_FLEET_NAME[id - 1]);
		this.fleetWindow = new FleetWindow(id, new Composite(this.centerComposite, SWT.BORDER));
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
	public Point defaultSize() {
		return SwtUtils.DPIAwareSize(new Point(250, 269));
	}
}
