package tdrz.gui.window.main;

import tdrz.gui.window.WindowResource;
import tdrz.update.context.GlobalContext;

public class MainStartTest {
	public static void main(String[] args) {
		GlobalContext.load();
		ApplicationMain.main = new ApplicationMain();
		ApplicationMain.main.start();//程序堵塞在这里
		WindowResource.DISPLAY.dispose();
	}
}
