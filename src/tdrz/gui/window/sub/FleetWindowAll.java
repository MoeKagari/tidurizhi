package tdrz.gui.window.sub;

import java.util.stream.IntStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
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
	private final FleetWindow[] fleetWindows;

	public FleetWindowAll(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		Composite fleetComposite = new Composite(this.getCenterComposite(), SWT.NONE);
		fleetComposite.setLayout(SwtUtils.makeGridLayout(2, 2, 2, 0, 0));
		fleetComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.fleetWindows = IntStream.range(0, 4).map(index -> index + 1)//
				.mapToObj(id -> new FleetWindow(id, new Composite(fleetComposite, SWT.BORDER)))//
				.toArray(FleetWindow[]::new);
	}

	public FleetWindow[] getFleetWindows() {
		return this.fleetWindows;
	}

	@Override
	public Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(410, 502));
	}
}
