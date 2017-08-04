package tdrz.logic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class HPMessage {
	public static final String ESCAPE_STRING = "退避";

	private static Color RED;//大破
	private static Color GRAY;//中破
	private static Color BROWN;//击沉
	private static Color CYAN;//小破
	private static Color ESCAPE_COLOR;//退避

	public static String getString(double percent) {
		if (percent == 1.00) {
			return "完好";
		}

		if (percent < 1.00 && percent > 0.75) {
			return "擦伤";
		}

		if (percent <= 0.75 && percent > 0.50) {
			return "小破";
		}

		if (percent <= 0.50 && percent > 0.25) {
			return "中破";
		}

		if (percent <= 0.25 && percent > 0.00) {
			return "大破";
		}

		return "击沉";
	}

	public static Color getColor(String state) {
		switch (state) {
			case "击沉":
				return BROWN;
			case "大破":
				return RED;
			case "中破":
				return GRAY;
			case "小破":
				return CYAN;
			case HPMessage.ESCAPE_STRING:
				return ESCAPE_COLOR;
			default:
				return null;
		}
	}

	public static void initColor() {
		RED = new Color(Display.getDefault(), new RGB(255, 85, 17));
		GRAY = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
		BROWN = new Color(Display.getDefault(), new RGB(119, 102, 34));
		CYAN = Display.getDefault().getSystemColor(SWT.COLOR_CYAN);
		ESCAPE_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	}
}
