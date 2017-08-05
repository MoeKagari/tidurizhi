package tdrz.gui.window;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import tdrz.utils.SwtUtils;

/** 和 {@link AbstractShellBase} 一样,只是为了分层而分离的super class ,编写窗口类应使用 {@link AbstractWindow} 或其子类 */
public abstract class AbstractCompositeBase extends AbstractShellBase {
	private Composite mainComposite;
	private Composite leftComposite;
	private Composite topComposite;
	private Composite centerComposite;
	private Composite bottomComposite;
	private Composite rightComposite;

	public AbstractCompositeBase(Shell parent, String title, Image logo, MenuItem menuItem) {
		super(parent, title, logo, menuItem);
		this.initComposite();
	}

	public AbstractCompositeBase(Display parent, String title, InputStream logoStream) {
		super(parent, title, logoStream);
		this.initComposite();
	}

	private void initComposite() {
		this.mainComposite = new Composite(this.getShell(), SWT.NONE);
		this.mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.mainComposite.setLayout(SwtUtils.makeGridLayout((this.haveLeftComposite() ? 1 : 0) + 1 + (this.haveRightComposite() ? 1 : 0), 0, 0, 0, 0));
		{
			if (this.haveLeftComposite()) {
				this.leftComposite = new Composite(this.mainComposite, SWT.NONE);
				this.leftComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
				this.leftComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			}

			Composite subComposite = new Composite(this.mainComposite, SWT.NONE);
			subComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			subComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			{
				if (this.haveTopComposite()) {
					this.topComposite = new Composite(subComposite, SWT.NONE);
					this.topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					this.topComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
				}

				this.centerComposite = new Composite(subComposite, SWT.NONE);
				this.centerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
				this.centerComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));

				if (this.haveBottomComposite()) {
					this.bottomComposite = new Composite(subComposite, SWT.NONE);
					this.bottomComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					this.bottomComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
				}
			}

			if (this.haveRightComposite()) {
				this.rightComposite = new Composite(this.mainComposite, SWT.NONE);
				this.rightComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
				this.rightComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			}
		}
	}

	protected boolean haveLeftComposite() {
		return false;
	}

	protected boolean haveTopComposite() {
		return false;
	}

	protected boolean haveBottomComposite() {
		return false;
	}

	protected boolean haveRightComposite() {
		return false;
	}

	public final Composite getCenterComposite() {
		return this.centerComposite;
	}

	public Composite getMainComposite() {
		return this.mainComposite;
	}

	/*------------------------------------------------------------------------------------------------------------*/

	public final Composite getLeftComposite() {
		return this.leftComposite;
	}

	public final Composite getTopComposite() {
		return this.topComposite;
	}

	public final Composite getBottomComposite() {
		return this.bottomComposite;
	}

	public final Composite getRightComposite() {
		return this.rightComposite;
	}
}
