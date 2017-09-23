package tdrz.gui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import tdrz.core.util.SwtUtils;
import tool.function.FunctionUtils;

/**
 * 和 {@link AbstractShellBase} 一样,只是为了分层而分离的super class ,编写窗口类应使用
 * {@link AbstractWindowBase} 或其子类
 */
public abstract class AbstractCompositeBase extends AbstractShellBase {
	public final Composite mainComposite;

	/**
	 * 需要先将标志置为true,否则null
	 * 
	 * @see AbstractCompositeBase#haveLeftComposite()
	 */
	public final Composite leftComposite;

	/**
	 * 需要先将标志置为true,否则null
	 * 
	 * @see AbstractCompositeBase#haveTopComposite()
	 */
	public final Composite topComposite;

	public final Composite centerComposite;

	/**
	 * 需要先将标志置为true,否则null
	 * 
	 * @see AbstractCompositeBase#haveBottomComposite()
	 */
	public final Composite bottomComposite;

	/**
	 * 需要先将标志置为true,否则null
	 * 
	 * @see AbstractCompositeBase#haveRightComposite()
	 */
	public final Composite rightComposite;

	public AbstractCompositeBase() {
		int numColumns = (this.haveLeftComposite() ? 1 : 0) + 1 + (this.haveRightComposite() ? 1 : 0);

		this.mainComposite = new Composite(this.shell, SWT.NONE);
		this.mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.mainComposite.setLayout(SwtUtils.makeGridLayout(numColumns, 0, 0, 0, 0));
		{
			if (this.haveTopComposite()) {
				this.topComposite = new Composite(this.mainComposite, SWT.NONE);
				this.topComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, numColumns, 1));
				this.topComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			} else {
				this.topComposite = null;
			}

			if (this.haveLeftComposite()) {
				this.leftComposite = new Composite(this.mainComposite, SWT.NONE);
				this.leftComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
				this.leftComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			} else {
				this.leftComposite = null;
			}

			this.centerComposite = new Composite(this.mainComposite, SWT.NONE);
			this.centerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.centerComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));

			if (this.haveRightComposite()) {
				this.rightComposite = new Composite(this.mainComposite, SWT.NONE);
				this.rightComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
				this.rightComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			} else {
				this.rightComposite = null;
			}

			if (this.haveBottomComposite()) {
				this.bottomComposite = new Composite(this.mainComposite, SWT.NONE);
				this.bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, numColumns, 1));
				this.bottomComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			} else {
				this.bottomComposite = null;
			}
		}
	}

	public final void layout() {
		this.mainComposite.layout();
		FunctionUtils.forEach(this.mainComposite.getChildren(), child -> {
			if (child instanceof Composite) {
				((Composite) child).layout();
			}
		});
	}

	/**
	 * 是否有此Composite
	 * 
	 * @see AbstractCompositeBase#leftComposite
	 */
	public boolean haveLeftComposite() {
		return false;
	}

	/**
	 * 是否有此Composite
	 * 
	 * @see AbstractCompositeBase#topComposite
	 */
	public boolean haveTopComposite() {
		return false;
	}

	/**
	 * 是否有此Composite
	 * 
	 * @see AbstractCompositeBase#bottomComposite
	 */
	public boolean haveBottomComposite() {
		return false;
	}

	/**
	 * 是否有此Composite
	 * 
	 * @see AbstractCompositeBase#rightComposite
	 */
	public boolean haveRightComposite() {
		return false;
	}

	/** 更新窗口(延迟redraw,只对于 center composite ) */
	protected final void updateWindowRedraw(Runnable run) {
		this.centerComposite.setRedraw(false);
		run.run();
		this.centerComposite.setRedraw(true);
	}
}
