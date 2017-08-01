package tdrz.gui.window.sub;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;

import tdrz.utils.SwtUtils;
import tool.FunctionUtils;

/**
 * {@link BattleWindow}和{@link BattleFlowWindow}中的scrolled composite
 * @author MoeKagari
 */
public class BattleScrolledComposite {
	private final ScrolledComposite sc;
	public final Composite contentComposite;

	public BattleScrolledComposite(Composite composite, int space) {
		this.sc = new ScrolledComposite(composite, SWT.V_SCROLL);
		this.sc.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.sc.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.sc.setExpandHorizontal(true);
		this.sc.setExpandVertical(true);
		this.sc.setAlwaysShowScrollBars(true);

		this.contentComposite = new Composite(this.sc, SWT.NONE);
		this.contentComposite.setLayout(SwtUtils.makeGridLayout(1, 0, space, 0, 0, 5, 5));
		this.contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.sc.setContent(this.contentComposite);
	}

	public void layout(boolean auto_scroll) {
		this.sc.setMinSize(this.contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.contentComposite.layout();
		if (auto_scroll) {
			ScrollBar bar = this.sc.getVerticalBar();
			bar.setSelection(bar.getMaximum());
		}
		this.sc.layout();
	}

	public void clearWindow() {
		FunctionUtils.forEach(this.contentComposite.getChildren(), Control::dispose);
		this.layout(true);
	}
}
