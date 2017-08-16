package tdrz.core.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolTip;

import tdrz.gui.window.main.ApplicationMain;
import tool.FunctionUtils;

public class TrayMessageBox {
	private Map<String, String> title_notice = null;

	public TrayMessageBox() {}

	public TrayMessageBox(String title, String notice) {
		this.title_notice = new LinkedHashMap<>();
		this.title_notice.put(title, notice);
	}

	public void add(String title, String notice) {
		if (this.title_notice == null) {
			this.title_notice = new LinkedHashMap<>();
		}
		this.title_notice.merge(title, notice, (value1, value2) -> String.format("%s\n%s", value1, value2));
	}

	public static void show(ApplicationMain main, TrayMessageBox box) {
		if (box.title_notice == null) return;
		if (box.title_notice.size() == 0) return;

		FunctionUtils.notNull(main.getTrayItem().getToolTip(), ToolTip::dispose);
		String text = StringUtils.join(box.title_notice.keySet(), "・");
		String message = StringUtils.join(box.title_notice.values(), "\r\n");

		ToolTip tip = new ToolTip(main.shell, SWT.BALLOON | SWT.ICON_INFORMATION);
		tip.setText(text);
		tip.setMessage(message);
		main.getTrayItem().setToolTip(tip);
		tip.setVisible(true);
	}
}
