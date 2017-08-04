package tdrz.gui.window.listener;

public interface WindowCanBeOperated {
	public boolean canVisibleOperation();

	public boolean canOpacityOperation();

	public boolean canMinimizedOperation();

	public boolean canTopOperation();

	public boolean canTopMostOperation();

	public boolean canTitleBarOperation();
}
