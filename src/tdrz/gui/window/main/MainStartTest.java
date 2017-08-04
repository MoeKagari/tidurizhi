package tdrz.gui.window.main;

public class MainStartTest {
	public static void main(String[] args) {
		ApplicationMain.main = new ApplicationMain();
		ApplicationMain.main.start();//程序堵塞在这里
	}
}
