package tdrz.gui.window.listener;

import tdrz.gui.window.AbstractWindowImplements;
import tdrz.gui.window.sub.WindowOperationWindow;

/** {@link AbstractWindowImplements}实现 , {@link WindowOperationWindow}中操作 */
public interface WindowCanBeOperated {
	public boolean canBeOperated();

	public boolean canVisibleBeOperated();

	public boolean canOpacityBeOperated();

	public boolean canMinimizedBeOperated();

	public boolean canTopBeOperated();

	public boolean canTopMostBeOperated();

	public boolean canTitleBarBeOperated();
}
