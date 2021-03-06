package tdrz.gui.window.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import server.ServerConfig;
import tdrz.core.config.AppConfig;
import tdrz.core.config.ShipGroup;
import tdrz.core.config.WindowConfig;
import tdrz.core.internal.ApplicationLock;
import tdrz.core.internal.AsyncExecApplicationMain;
import tdrz.gui.window.WindowResource;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.server.TDRZServerSevlet;
import tool.function.FunctionUtils;

public class MainStart {
	public static TDRZServerSevlet server = null;
	public static final ApplicationLock applicationLock = new ApplicationLock();
	public static final Logger LOG = LogManager.getLogger(ApplicationMain.class);

	public static void main(String[] args) {
		//多重启动检查之后启动
		FunctionUtils.ifRunnable(applicationLockCheck(), MainStart::start);
	}

	/** 没有锁住(false),代表本次启动为多重启动 */
	private static boolean applicationLockCheck() {
		if (!applicationLock.isError() && applicationLock.isLocked()) return true;

		{
			Shell shell = new Shell(WindowResource.DISPLAY, SWT.TOOL);
			{
				MessageBox mes = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				mes.setText("多重启动");
				mes.setMessage("请勿多重启动");
				mes.open();
			}
			shell.dispose();
		}

		applicationLock.release();
		return false;
	}

	private static void start() {
		try {
			AppConfig.load();
			WindowConfig.load();
			GlobalContext.load();
			ShipGroup.load();

			ApplicationMain.main = new ApplicationMain();
			new AsyncExecApplicationMain(ApplicationMain.main).start();
			server = new TDRZServerSevlet(new ServerConfig(AppConfig.get()::getListenPort, AppConfig.get()::isUseProxy, AppConfig.get()::getProxyHost, AppConfig.get()::getProxyPort));

			server.start();
			ApplicationMain.main.start();//程序堵塞在这里
		} catch (Throwable e) {
			LOG.fatal("main thread 异常中止", e);
		} finally {
			WindowResource.DISPLAY.dispose();
			applicationLock.release();

			try {
				server.end();
			} catch (Exception e) {
				e.printStackTrace();
			}

			AppConfig.store();
			WindowConfig.store();
			GlobalContext.store();
			ShipGroup.store();
		}
	}
}
