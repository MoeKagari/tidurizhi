package tdrz.gui.window;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import tdrz.core.config.AppConstants;
import tdrz.gui.window.main.ApplicationMain;

public class WindowResource {
	public static final Display DISPLAY = Display.getDefault();
	public static final Image LOGO = new Image(DISPLAY, ApplicationMain.class.getResourceAsStream(AppConstants.LOGO));
}
