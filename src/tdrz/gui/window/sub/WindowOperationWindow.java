package tdrz.gui.window.sub;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;

import tdrz.gui.window.AbstractWindow;
import tdrz.gui.window.WindowBase;
import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.gui.window.listener.WindowConfigChangedAdapter;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.utils.SwtUtils;

public class WindowOperationWindow extends WindowBase {
	private final Composite contentComposite;

	public WindowOperationWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		this.contentComposite = new Composite(this.getCenterComposite(), SWT.NONE);
		this.contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.contentComposite.setLayout(SwtUtils.makeGridLayout(3, 0, 0, 0, 0, 2, 2, 2, 2));
	}

	public void addWindow(AbstractWindow window) {
		new WindowOperationComposite(window);
	}

	@Override
	public boolean canOpacityOperation() {
		return false;
	}

	@Override
	public boolean canVisibleOperation() {
		return false;
	}

	@Override
	public boolean canMinimizedOperation() {
		return false;
	}

	public class WindowOperationComposite extends Composite {
		private static final int LENGTH = 3;

		public WindowOperationComposite(AbstractWindow window) {
			super(WindowOperationWindow.this.contentComposite, SWT.BORDER);
			this.setLayout(SwtUtils.makeGridLayout(LENGTH, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Label nameLabel = new Label(this, SWT.LEFT | SWT.BORDER);
			nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false, LENGTH, 1));
			nameLabel.setText(window.getDefaultTitle());

			Button display = new Button(this, SWT.CHECK);
			display.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
			display.setEnabled(window.canVisibleOperation());
			display.setText("显示");
			display.setSelection(window.getWindowConfig().isVisible());
			display.addSelectionListener(new ControlSelectionListener(ev -> {
				if (display.getSelection()) {
					window.displayWindow();
				} else {
					window.hiddenWindow();
				}
			}));
			window.addWindowConfigChangedListener(new WindowConfigChangedAdapter() {
				@Override
				public void visibleChanged(boolean visible) {
					display.setSelection(visible);
				}
			});

			Button minimized = new Button(this, SWT.CHECK);
			minimized.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
			minimized.setEnabled(window.canVisibleOperation());
			minimized.setText("最小化");
			minimized.setSelection(window.getWindowConfig().isVisible());
			minimized.addSelectionListener(new ControlSelectionListener(ev -> {
				window.getShell().setMinimized(minimized.getSelection());
			}));
			window.addWindowConfigChangedListener(new WindowConfigChangedAdapter() {
				@Override
				public void minimizedChanged(boolean mini) {
					minimized.setSelection(mini);
				}
			});

			Button topmost = new Button(this, SWT.CHECK);
			topmost.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
			topmost.setText("总在前");
			topmost.setSelection(window.getWindowConfig().isTopMost());
			topmost.addSelectionListener(new ControlSelectionListener(ev -> {
				window.setTopMost(topmost.getSelection());
			}));

			Button showTitleBar = new Button(this, SWT.CHECK);
			showTitleBar.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
			showTitleBar.setText("标题栏");
			showTitleBar.setSelection(window.getWindowConfig().isShowTitleBar());
			showTitleBar.addSelectionListener(new ControlSelectionListener(ev -> {
				window.toggleTitlebar(showTitleBar.getSelection());
			}));

			Scale opacity = new Scale(this, SWT.HORIZONTAL);
			opacity.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true, LENGTH - 1, 1));
			opacity.setEnabled(window.canOpacityOperation());
			opacity.setMinimum(0);
			opacity.setMaximum(255);
			opacity.setIncrement(1);
			opacity.setSelection(window.getWindowConfig().getOpacity());
			opacity.addSelectionListener(new ControlSelectionListener(ev -> {
				window.changeOpacity(opacity.getSelection());
			}));
		}
	}
}
