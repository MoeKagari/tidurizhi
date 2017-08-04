package tdrz.gui.window.listener;

import java.util.function.Consumer;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ControlSelectionListener extends SelectionAdapter {
	private final Consumer<SelectionEvent> handler;

	public ControlSelectionListener(Consumer<SelectionEvent> handler) {
		this.handler = handler;
	}

	public ControlSelectionListener(Runnable run) {
		this.handler = ev -> run.run();
	}

	public ControlSelectionListener andThen(Consumer<SelectionEvent> after) {
		return new ControlSelectionListener(this.handler.andThen(after));
	}

	public ControlSelectionListener andThen(ControlSelectionListener after) {
		return new ControlSelectionListener(this.handler.andThen(after.handler));
	}

	@Override
	public void widgetSelected(SelectionEvent ev) {
		this.handler.accept(ev);
	}
}