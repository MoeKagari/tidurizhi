package tdrz.gui.window.sup;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import tdrz.core.util.SwtUtils;
import tdrz.gui.window.WindowResource;

/** 和 {@link AbstractCompositeBase} 一样,只是为了分层而分离的super class ,编写窗口类应使用 {@link AbstractWindow} 或其子类 */
public abstract class AbstractShellBase {
	public final Shell shell;
	public final Menu menuBar;

	public AbstractShellBase() {
		this.shell = new Shell(new Shell(WindowResource.DISPLAY), this.getShellStyle());
		this.shell.setText(this.defaultTitle());
		this.shell.setImage(WindowResource.LOGO);
		this.shell.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		this.shell.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.menuBar = SwtUtils.makeBarMenu(this.shell);
	}

	/** 默认shell style */
	protected abstract int getShellStyle();

	public abstract String defaultTitle();
}
