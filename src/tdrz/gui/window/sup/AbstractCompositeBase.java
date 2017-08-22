package tdrz.gui.window.sup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import tdrz.core.util.SwtUtils;

/** 和 {@link AbstractShellBase} 一样,只是为了分层而分离的super class ,编写窗口类应使用 {@link AbstractWindow} 或其子类 */
public abstract class AbstractCompositeBase extends AbstractShellBase {
	public final Composite mainComposite;

	/**
	 * 需要先将标志置为true,否则null
	 * @see AbstractCompositeBase#haveLeftComposite()
	 */
	public final Composite leftComposite;

	/**
	 * 需要先将标志置为true,否则null
	 * @see AbstractCompositeBase#haveTopComposite()
	 */
	public final Composite topComposite;

	public final Composite centerComposite;

	/**
	 * 需要先将标志置为true,否则null
	 * @see AbstractCompositeBase#haveBottomComposite()
	 */
	public final Composite bottomComposite;

	/**
	 * 需要先将标志置为true,否则null
	 * @see AbstractCompositeBase#haveRightComposite()
	 */
	public final Composite rightComposite;

	public AbstractCompositeBase() {
		this.mainComposite = new Composite(this.shell, SWT.NONE);
		this.mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.mainComposite.setLayout(SwtUtils.makeGridLayout((this.haveLeftComposite() ? 1 : 0) + 1 + (this.haveRightComposite() ? 1 : 0), 0, 0, 0, 0));
		{
			if (this.haveLeftComposite()) {
				this.leftComposite = new Composite(this.mainComposite, SWT.NONE);
				this.leftComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
				this.leftComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			} else {
				this.leftComposite = null;
			}

			Composite subComposite = new Composite(this.mainComposite, SWT.NONE);
			subComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			subComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			{
				if (this.haveTopComposite()) {
					this.topComposite = new Composite(subComposite, SWT.NONE);
					this.topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					this.topComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
				} else {
					this.topComposite = null;
				}

				this.centerComposite = new Composite(subComposite, SWT.NONE);
				this.centerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
				this.centerComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));

				if (this.haveBottomComposite()) {
					this.bottomComposite = new Composite(subComposite, SWT.NONE);
					this.bottomComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					this.bottomComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
				} else {
					this.bottomComposite = null;
				}
			}

			if (this.haveRightComposite()) {
				this.rightComposite = new Composite(this.mainComposite, SWT.NONE);
				this.rightComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
				this.rightComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			} else {
				this.rightComposite = null;
			}
		}
	}

	/**
	 * 是否有此Composite
	 * @see AbstractCompositeBase#leftComposite
	 */
	protected boolean haveLeftComposite() {
		return false;
	}

	/**
	 * 是否有此Composite
	 * @see AbstractCompositeBase#topComposite
	 */
	protected boolean haveTopComposite() {
		return false;
	}

	/**
	 * 是否有此Composite
	 * @see AbstractCompositeBase#bottomComposite
	 */
	protected boolean haveBottomComposite() {
		return false;
	}

	/**
	 * 是否有此Composite
	 * @see AbstractCompositeBase#rightComposite
	 */
	protected boolean haveRightComposite() {
		return false;
	}

	/** 更新窗口(延迟redraw,只对于 center composite ) */
	protected final void updateWindowRedraw(Runnable run) {
		this.centerComposite.setRedraw(false);
		run.run();
		this.centerComposite.setRedraw(true);
	}
}
