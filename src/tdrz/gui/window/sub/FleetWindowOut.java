package tdrz.gui.window.sub;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import tdrz.core.config.AppConstants;
import tdrz.core.util.SwtUtils;
import tdrz.gui.composite.FleetWindow;
import tdrz.gui.window.AbstractWindowBase;

/**
 * 舰队面板-单
 * @author MoeKagari
 */
public abstract class FleetWindowOut extends AbstractWindowBase {
	private final FleetWindow fleetWindow;

	public FleetWindowOut() {
		this.fleetWindow = new FleetWindow(this.getId(), new Composite(this.centerComposite, SWT.BORDER));
	}

	public FleetWindow getFleetWindow() {
		return this.fleetWindow;
	}

	public abstract int getId();

	@Override
	public String defaultTitle() {
		return AppConstants.DEFAULT_FLEET_NAME[this.getId() - 1];
	}

	@Override
	public String getWindowConfigKey() {
		return FleetWindowOut.class.getName() + this.getId();
	}

	@Override
	public Point defaultSize() {
		return SwtUtils.DPIAwareSize(new Point(250, 269));
	}
}
