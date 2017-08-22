package tdrz.gui.window.listener;

public interface WindowConfigChangedListener {
	public default void visibleChangedBefore(boolean visible) {
		if (visible) {
			displayBefore();
		} else {
			hiddenBefore();
		}
	}

	public void displayBefore();

	public void hiddenBefore();

	public default void visibleChangedAfter(boolean visible) {
		if (visible) {
			displayAfter();
		} else {
			hiddenAfter();
		}
	}

	public void displayAfter();

	public void hiddenAfter();

	public void minimizedChanged(boolean minimized);
}
