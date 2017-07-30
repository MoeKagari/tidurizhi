package tdrz.gui.window.sub;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import tdrz.gui.window.WindowBase;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.utils.SwtUtils;

/**
 * 舰队面板-全
 * @author MoeKagari
 */
public class FleetWindowAll extends WindowBase {
	private FleetWindow[] fleetWindows;

	public FleetWindowAll(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
		this.initFleetWindows();
	}

	private void initFleetWindows() {
		Composite fleetComposite = new Composite(this.getCenterComposite(), SWT.NONE);
		GridLayout fleetCompositeGridLayout = new GridLayout(2, true);
		fleetCompositeGridLayout.horizontalSpacing = 2;
		fleetCompositeGridLayout.verticalSpacing = 2;
		fleetCompositeGridLayout.marginWidth = 0;
		fleetCompositeGridLayout.marginHeight = 0;
		fleetComposite.setLayout(fleetCompositeGridLayout);
		fleetComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		int fleetLength = 4;
		this.fleetWindows = new FleetWindow[fleetLength];
		for (int i = 0; i < fleetLength; i++) {
			this.fleetWindows[i] = new FleetWindow(i + 1, new Composite(fleetComposite, SWT.BORDER));
		}
	}

	public FleetWindow[] getFleetWindows() {
		return this.fleetWindows;
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(410, 502));
	}
}
