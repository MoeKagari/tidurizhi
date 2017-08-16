package tdrz.gui.window.listener;

import tdrz.gui.window.sub.WindowOperationWindow;
import tdrz.gui.window.sup.AbstractWindowSuper;

/** {@link AbstractWindowSuper}实现 , {@link WindowOperationWindow}中操作 */
public interface WindowCanBeOperated {
	public boolean canVisibleBeOperated();

	public boolean canOpacityBeOperated();

	public boolean canMinimizedBeOperated();

	public boolean canTopBeOperated();

	public boolean canTopMostBeOperated();

	public boolean canTitleBarBeOperated();
}
