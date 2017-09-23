package tdrz.gui.other;

import tdrz.gui.window.AbstractWindowSuper;
import tdrz.gui.window.sub.WindowOperationWindow;

/** {@link AbstractWindowSuper}实现 , {@link WindowOperationWindow}中操作 */
public interface WindowCanBeOperated {
	public boolean canIgnoreMouseBeOperated();

	public boolean canVisibleBeOperated();

	public boolean canOpacityBeOperated();

	public boolean canMinimizedBeOperated();

	public boolean canTopBeOperated();

	public boolean canTopMostBeOperated();

	public boolean canTitleBarBeOperated();
}
