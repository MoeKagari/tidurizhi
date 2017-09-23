package tdrz.core.util;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import tdrz.gui.other.ControlSelectionListener;
import tdrz.gui.window.WindowResource;
import tool.function.FunctionUtils;

public final class SwtUtils {
	private final static int DPI_BASE = 96;
	private final static Point DPI = WindowResource.DISPLAY.getDPI();

	public static int DPIAwareWidth(int width) {
		return (width * DPI.x) / DPI_BASE;
	}

	public static int DPIAwareHeight(int height) {
		return (height * DPI.y) / DPI_BASE;
	}

	public static Point DPIAwareSize(Point size) {
		return new Point(DPIAwareWidth(size.x), DPIAwareHeight(size.y));
	}

	public static Point DPIAwareSize(int width, int height) {
		return new Point(DPIAwareWidth(width), DPIAwareHeight(height));
	}

	public static void layoutRecursively(Composite composite) {
		FunctionUtils.forEach(composite.getChildren(), control -> {
			if (control instanceof Composite) {
				layoutRecursively((Composite) control);
			}
		});
		composite.layout();
	}

	public static void setMenuRecursively(Composite composite, Menu menu) {
		composite.setMenu(menu);
		FunctionUtils.forEach(composite.getChildren(), child -> {
			if (child instanceof Composite) {
				setMenuRecursively((Composite) child, menu);
			}
			child.setMenu(menu);
		});
	}

	public static Menu makeBarMenu(Shell parent) {
		Menu menuBar = new Menu(parent, SWT.BAR);
		parent.setMenuBar(menuBar);
		return menuBar;
	}

	public static Menu makeNormalMenu(Control parent) {
		Menu menu = new Menu(parent);
		parent.setMenu(menu);
		return menu;
	}

	public static Menu makeCasacdeMenu(Menu parent, String text) {
		MenuItem menuItem = new MenuItem(parent, SWT.CASCADE);
		menuItem.setText(text);
		Menu menu = new Menu(menuItem);
		menuItem.setMenu(menu);
		return menu;
	}

	public static MenuItem makeSeparatorMenuItem(Menu parent) {
		return new MenuItem(parent, SWT.SEPARATOR);
	}

	public static MenuItem makeMenuItem(Menu parent, int style, String text, SelectionListener listener) {
		MenuItem menuItem = new MenuItem(parent, style);
		menuItem.setText(text);
		menuItem.addSelectionListener(listener);
		return menuItem;
	}

	public static MenuItem makeMenuItem(Menu parent, int style, String text, Runnable handler) {
		return makeMenuItem(parent, style, text, new ControlSelectionListener(handler));
	}

	public static MenuItem makeMenuItem(Menu parent, int style, String text, Consumer<SelectionEvent> handler) {
		return makeMenuItem(parent, style, text, new ControlSelectionListener(handler));
	}

	public static GridData makeGridData(int style, int width) {
		return makeGridData(new GridData(style), width);
	}

	public static GridData makeGridData(GridData gd, int width) {
		gd.widthHint = SwtUtils.DPIAwareWidth(width);
		return gd;
	}

	public static GridLayout makeGridLayout(int numColumns, int[] params) {
		GridLayout gl = new GridLayout(numColumns, false);
		switch (params.length) {
			default:
				throw new RuntimeException("参数长度不符合");
			case 8:
				gl.marginRight = params[7];
				gl.marginLeft = params[6];
			case 6:
				gl.marginBottom = params[5];
				gl.marginTop = params[4];
			case 4:
				gl.marginHeight = params[3];
				gl.marginWidth = params[2];
				gl.verticalSpacing = params[1];
				gl.horizontalSpacing = params[0];
		}
		return gl;
	}

	public static GridLayout makeGridLayout(int numColumns, int horizontalSpacing, int verticalSpacing, int marginWidth, int marginHeight) {
		GridLayout gl = new GridLayout(numColumns, false);
		gl.horizontalSpacing = horizontalSpacing;
		gl.verticalSpacing = verticalSpacing;
		gl.marginWidth = marginWidth;
		gl.marginHeight = marginHeight;
		return gl;
	}

	public static GridLayout makeGridLayout(int numColumns, int horizontalSpacing, int verticalSpacing, int marginWidth, int marginHeight, int marginTop, int marginBottom) {
		GridLayout gl = makeGridLayout(numColumns, horizontalSpacing, verticalSpacing, marginWidth, marginHeight);
		gl.marginTop = marginTop;
		gl.marginBottom = marginBottom;
		return gl;
	}

	public static GridLayout makeGridLayout(int numColumns, int horizontalSpacing, int verticalSpacing, int marginWidth, int marginHeight, int marginTop, int marginBottom, int marginLeft, int marginRight) {
		GridLayout gl = makeGridLayout(numColumns, horizontalSpacing, verticalSpacing, marginWidth, marginHeight, marginTop, marginBottom);
		gl.marginLeft = marginLeft;
		gl.marginRight = marginRight;
		return gl;
	}

	public static Label initLabel(Label label, String text, GridData gd) {
		label.setText(text);
		label.setLayoutData(gd);
		return label;
	}

	public static Label initLabel(Label label, String text, GridData gd, Color background) {
		label.setBackground(background);
		initLabel(label, text, gd);
		return label;
	}

	public static Label initLabel(Label label, String text, GridData gd, int width) {
		gd.widthHint = SwtUtils.DPIAwareWidth(width);
		initLabel(label, text, gd);
		return label;
	}

	public static Label initLabel(Label label, String text, GridData gd, int width, Color background) {
		label.setBackground(background);
		gd.widthHint = SwtUtils.DPIAwareWidth(width);
		initLabel(label, text, gd);
		return label;
	}

	public static void insertBlank(Composite composite) {
		initLabel(new Label(composite, SWT.NONE), "", new GridData(GridData.FILL_HORIZONTAL));
	}

	public static void insertBlank(Composite composite, int width) {
		initLabel(new Label(composite, SWT.NONE), "", new GridData(), width);
	}

	public static void insertHSeparator(Composite composite) {
		Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public static void insertVSeparator(Composite composite) {
		Label separator = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		separator.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	}

	public static Label setText(Label label, String text) {
		if (FunctionUtils.isFalse(StringUtils::equals, text, label.getText())) {
			label.setText(text);
		}
		return label;
	}

	public static Label setToolTipText(Label label, String text) {
		if (FunctionUtils.isFalse(StringUtils::equals, text, label.getToolTipText())) {
			label.setToolTipText(text);
		}
		return label;
	}
}
