package tdrz.gui.window.sub;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import tdrz.core.util.SwtUtils;
import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.gui.window.listener.WindowConfigChangedAdapter;
import tdrz.gui.window.sup.AbstractWindow;
import tdrz.gui.window.sup.WindowBase;

public class WindowOperationWindow extends WindowBase {
	private final Composite contentComposite;
	private final List<AbstractOperationComposite> aocs = new ArrayList<>();

	public WindowOperationWindow() {
		this.contentComposite = new Composite(this.centerComposite, SWT.NONE);
		this.contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.contentComposite.setLayout(SwtUtils.makeGridLayout(4, 0, 0, 0, 0, 2, 2, 2, 2));
		this.allowMouseDragRecursively(this.contentComposite);
	}

	public void addWindow(AbstractWindow window) {
		if (window != null) {
			this.aocs.add(new WindowOperationComposite(window));
		} else {
			this.aocs.add(new EmptyOperationComposite());
		}
	}

	@Override
	public boolean canIgnoreMouseBeOperated() {
		return false;
	}

	@Override
	public boolean canOpacityBeOperated() {
		return false;
	}

	@Override
	public String defaultTitle() {
		return "窗口操作";
	}

	@Override
	public boolean defaultVisible() {
		return true;
	}

	@Override
	public Point defaultSize() {
		return SwtUtils.DPIAwareSize(new Point(900, 961));
	}

	private abstract class AbstractOperationComposite extends Composite {
		public AbstractOperationComposite(int style) {
			super(WindowOperationWindow.this.contentComposite, style);
			WindowOperationWindow.this.allowMouseDragRecursively(this);
		}
	}

	private class EmptyOperationComposite extends AbstractOperationComposite {
		public EmptyOperationComposite() {
			super(SWT.NONE);
		}
	}

	private class WindowOperationComposite extends AbstractOperationComposite {
		private final AbstractWindow window;

		public WindowOperationComposite(AbstractWindow window) {
			super(SWT.BORDER);
			this.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.window = window;

			this.initComposite_0();
			this.initComposite_1();
			this.initComposite_2();
		}

		private void initComposite_0() {
			Composite composite = new Composite(this, SWT.NONE);
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			composite.setLayout(SwtUtils.makeGridLayout(4, 0, 0, 0, 0));
			WindowOperationWindow.this.allowMouseDragRecursively(composite);
			{
				Label nameLabel = new Label(composite, SWT.BORDER);
				nameLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
				nameLabel.setText(this.window.defaultTitle());
				WindowOperationWindow.this.allowMouseDragRecursively(nameLabel);

				SwtUtils.insertBlank(composite);

				Button showTitleBar = new Button(composite, SWT.CHECK);
				showTitleBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
				showTitleBar.setText("标题栏");
				showTitleBar.setEnabled(this.window.canTitleBarBeOperated());
				if (this.window.canTitleBarBeOperated()) {
					showTitleBar.setSelection(this.window.getWindowConfig().isShowTitleBar());
					showTitleBar.addSelectionListener(new ControlSelectionListener(ev -> {
						this.window.toggleTitlebar(showTitleBar.getSelection());
					}));
				}

				Button ignoreMouse = new Button(composite, SWT.CHECK);
				ignoreMouse.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
				ignoreMouse.setText("鼠标穿透");
				ignoreMouse.setEnabled(this.window.canIgnoreMouseBeOperated());
				if (this.window.canIgnoreMouseBeOperated()) {
					ignoreMouse.setSelection(this.window.getWindowConfig().isIgnoreMouse());
					ignoreMouse.addSelectionListener(new ControlSelectionListener(ev -> {
						this.window.ignoreMouse(ignoreMouse.getSelection());
					}));
				}
			}
		}

		private void initComposite_1() {
			Composite composite = new Composite(this, SWT.NONE);
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			composite.setLayout(SwtUtils.makeGridLayout(4, 0, 0, 0, 0));
			WindowOperationWindow.this.allowMouseDragRecursively(composite);
			{
				Button display = new Button(composite, SWT.CHECK);
				display.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
				display.setText("显示");
				display.setEnabled(this.window.canVisibleBeOperated());
				if (this.window.canVisibleBeOperated()) {
					display.setSelection(this.window.getWindowConfig().isVisible());
					display.addSelectionListener(new ControlSelectionListener(ev -> {
						if (display.getSelection()) {
							this.window.displayWindow();
						} else {
							this.window.hiddenWindow();
						}
					}));
					this.window.addWindowConfigChangedListener(new WindowConfigChangedAdapter() {
						@Override
						public void displayBefore() {
							display.setSelection(true);
						}

						@Override
						public void hiddenAfter() {
							display.setSelection(false);
						}
					});
				}

				Button minimized = new Button(composite, SWT.CHECK);
				minimized.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
				minimized.setText("最小化");
				minimized.setEnabled(this.window.canMinimizedBeOperated());
				if (this.window.canMinimizedBeOperated()) {
					minimized.setSelection(this.window.getWindowConfig().isMinimized());
					minimized.addSelectionListener(new ControlSelectionListener(ev -> {
						this.window.shell.setMinimized(minimized.getSelection());
					}));
					this.window.addWindowConfigChangedListener(new WindowConfigChangedAdapter() {
						@Override
						public void minimizedChanged(boolean mini) {
							minimized.setSelection(mini);
						}
					});
				}

				Button topmost = new Button(composite, SWT.CHECK);
				topmost.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
				topmost.setText("总在前");
				topmost.setEnabled(this.window.canTopMostBeOperated());
				if (this.window.canTopMostBeOperated()) {
					topmost.setSelection(this.window.getWindowConfig().isTopMost());
					topmost.addSelectionListener(new ControlSelectionListener(ev -> {
						this.window.setTopMost(topmost.getSelection());
					}));
				}

				Button top = new Button(composite, SWT.PUSH);
				top.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
				top.setText("置顶");
				top.setToolTipText("使窗口置顶,非总在前");
				top.setEnabled(this.window.canTopBeOperated());
				if (this.window.canTopBeOperated()) {
					top.addSelectionListener(new ControlSelectionListener(this.window::setTop));
				}
			}
		}

		private void initComposite_2() {
			Composite composite = new Composite(this, SWT.NONE);
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			WindowOperationWindow.this.allowMouseDragRecursively(composite);
			{
				Scale opacity = new Scale(composite, SWT.HORIZONTAL);
				opacity.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				opacity.setMinimum(0);
				opacity.setMaximum(255);
				opacity.setIncrement(1);
				opacity.setEnabled(this.window.canOpacityBeOperated());
				if (this.window.canOpacityBeOperated()) {
					opacity.setSelection(this.window.getWindowConfig().getOpacity());
					opacity.addSelectionListener(new ControlSelectionListener(ev -> {
						this.window.changeOpacity(opacity.getSelection());
					}));
				}
			}
		}
	}
}
