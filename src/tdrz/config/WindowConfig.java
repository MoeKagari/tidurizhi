package tdrz.config;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Point;

public class WindowConfig {
	private static final Logger LOG = LogManager.getLogger(WindowConfig.class);
	private static final File file = new File(AppConstants.WINDOWCONFIGS_FILEPATH);
	private static Map<String, WindowConfig> ALLWINDOWSCONFIG = new HashMap<>();

	public static Map<String, WindowConfig> get() {
		return ALLWINDOWSCONFIG;
	}

	public static void load() {
		try (XMLDecoder de = new XMLDecoder(new FileInputStream(file))) {
			Object obj = de.readObject();
			if (obj instanceof Map) {
				((Map<?, ?>) obj).forEach((key, value) -> {
					if (key instanceof String && value instanceof WindowConfig) {
						ALLWINDOWSCONFIG.put((String) key, (WindowConfig) value);
					}
				});
			}
		} catch (FileNotFoundException e) {

		}
	}

	public static void store() {
		try (XMLEncoder en = new XMLEncoder(new FileOutputStream(file))) {
			en.writeObject(ALLWINDOWSCONFIG);
		} catch (FileNotFoundException e) {
			LOG.warn("windows配置保存失败", e);
		}
	}

	private Point location;
	private Point size;
	private boolean visible;
	private boolean minimized;
	private boolean topMost;
	private boolean showTitleBar;
	private int opacity;

	public WindowConfig() {
		this(new Point(0, 0), new Point(0, 0), false, false, false, true, 255);
	}

	public WindowConfig(Point size, Point location, boolean visible, boolean minimized, boolean topMost, boolean showTitleBar, int opacity) {
		this.size = size;
		this.location = location;
		this.visible = visible;
		this.minimized = minimized;
		this.topMost = topMost;
		this.showTitleBar = showTitleBar;
		this.opacity = opacity;
	}

	public Point getLocation() {
		return this.location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Point getSize() {
		return this.size;
	}

	public void setSize(Point size) {
		this.size = size;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isMinimized() {
		return this.minimized;
	}

	public void setMinimized(boolean minimized) {
		this.minimized = minimized;
	}

	public boolean isTopMost() {
		return this.topMost;
	}

	public void setTopMost(boolean topMost) {
		this.topMost = topMost;
	}

	public boolean isShowTitleBar() {
		return this.showTitleBar;
	}

	public void setShowTitleBar(boolean showTitleBar) {
		this.showTitleBar = showTitleBar;
	}

	public int getOpacity() {
		return this.opacity;
	}

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	@Override
	public String toString() {
		return "WindowConfig [location=" + this.location + ", size=" + this.size + ", visible=" + this.visible + ", minimized=" + this.minimized + ", topMost=" + this.topMost + ", showTitleBar=" + this.showTitleBar + ", opacity="
				+ this.opacity + "]";
	}
}
