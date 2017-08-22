package tdrz.gui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;

import tdrz.core.util.SwtUtils;
import tdrz.gui.window.sub.BattleFlowWindow;
import tdrz.gui.window.sub.BattleWindow;
import tool.function.FunctionUtils;

/**
 * {@link BattleWindow}和{@link BattleFlowWindow}中的scrolled composite
 * @author MoeKagari
 */
public class BattleScrolledComposite extends ScrolledComposite {
	public final Composite contentComposite;

	public BattleScrolledComposite(Composite composite, int space) {
		super(composite, SWT.V_SCROLL);
		this.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.setExpandHorizontal(true);
		this.setExpandVertical(true);
		this.setAlwaysShowScrollBars(true);

		this.contentComposite = new Composite(this, SWT.NONE);
		this.contentComposite.setLayout(SwtUtils.makeGridLayout(1, 0, space, 0, 0, 5, 5));
		this.contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.setContent(this.contentComposite);
	}

	public void layoutContent(boolean auto_scroll) {
		this.setMinSize(this.contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.contentComposite.layout();
		if (auto_scroll) {
			ScrollBar bar = this.getVerticalBar();
			bar.setSelection(bar.getMaximum());
		}
		this.layout();
	}

	public void clearWindow() {
		FunctionUtils.forEach(this.contentComposite.getChildren(), Control::dispose);
		this.layoutContent(true);
	}
}
