package tdrz.config;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppConfig {
	private static final Logger LOG = LogManager.getLogger(AppConfig.class);
	private static final File file = new File(AppConstants.APPCONFIGS_FILEPATH);
	private static AppConfig config;

	public static AppConfig get() {
		if (config == null) {
			config = new AppConfig();
		}
		return config;
	}

	public static void load() {
		try (XMLDecoder de = new XMLDecoder(new FileInputStream(file))) {
			Object obj = de.readObject();
			if (obj instanceof AppConfig) {
				config = (AppConfig) obj;
			}
		} catch (FileNotFoundException e) {

		}
	}

	public static void store() {
		try (XMLEncoder en = new XMLEncoder(new FileOutputStream(file))) {
			en.writeObject(config);
		} catch (FileNotFoundException e) {
			LOG.warn("app配置保存失败", e);
		}
	}

	private int listenPort = 22222;
	private boolean allowOnlyFromLocalhost = false;
	private boolean closeOutsidePort = true;
	private boolean useProxy = false;
	private String proxyHost = "127.0.0.1";
	private int proxyPort = 8099;

	private boolean noticeDeckmission = true;
	private boolean noticeNdock = true;
	private boolean noticeAkashi = true;
	private boolean noticeCond = true;
	private int noticeCondWhen = 40;
	private boolean noticeCondOnlyMainFleet = false;

	private boolean showEventMapHPInConsole = true;
	private boolean showNameOnTitle = true;
	private boolean checkDoit = true;
	private boolean minimizedToTray = false;
	private boolean notCalcuExpForLevel1Ship = true;
	private boolean notCalcuExpForLevel99Ship = true;
	private boolean notCalcuExpForLevel155Ship = false;
	private boolean noticeDeckmissionAgain = true;
	private boolean autoUpdateBattleFlow = true;

	private boolean useCache = true;

	public boolean isNoticeDeckmission() {
		return this.noticeDeckmission;
	}

	public void setNoticeDeckmission(boolean noticeDeckmission) {
		this.noticeDeckmission = noticeDeckmission;
	}

	public boolean isNoticeNdock() {
		return this.noticeNdock;
	}

	public void setNoticeNdock(boolean noticeNdock) {
		this.noticeNdock = noticeNdock;
	}

	public boolean isNoticeAkashi() {
		return this.noticeAkashi;
	}

	public void setNoticeAkashi(boolean noticeAkashi) {
		this.noticeAkashi = noticeAkashi;
	}

	public boolean isNoticeCond() {
		return this.noticeCond;
	}

	public void setNoticeCond(boolean noticeCond) {
		this.noticeCond = noticeCond;
	}

	public boolean isNoticeCondOnlyMainFleet() {
		return this.noticeCondOnlyMainFleet;
	}

	public void setNoticeCondOnlyMainFleet(boolean noticeCondOnlyMainFleet) {
		this.noticeCondOnlyMainFleet = noticeCondOnlyMainFleet;
	}

	public boolean isAllowOnlyFromLocalhost() {
		return this.allowOnlyFromLocalhost;
	}

	public void setAllowOnlyFromLocalhost(boolean allowOnlyFromLocalhost) {
		this.allowOnlyFromLocalhost = allowOnlyFromLocalhost;
	}

	public boolean isCloseOutsidePort() {
		return this.closeOutsidePort;
	}

	public void setCloseOutsidePort(boolean closeOutsidePort) {
		this.closeOutsidePort = closeOutsidePort;
	}

	public int getListenPort() {
		return this.listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public boolean isUseProxy() {
		return this.useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public String getProxyHost() {
		return this.proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean isShowNameOnTitle() {
		return this.showNameOnTitle;
	}

	public void setShowNameOnTitle(boolean showNameOnTitle) {
		this.showNameOnTitle = showNameOnTitle;
	}

	public boolean isCheckDoit() {
		return this.checkDoit;
	}

	public void setCheckDoit(boolean checkDoit) {
		this.checkDoit = checkDoit;
	}

	public boolean isNotCalcuExpForLevel1Ship() {
		return this.notCalcuExpForLevel1Ship;
	}

	public void setNotCalcuExpForLevel1Ship(boolean notCalcuExpForLevel1Ship) {
		this.notCalcuExpForLevel1Ship = notCalcuExpForLevel1Ship;
	}

	public boolean isNotCalcuExpForLevel99Ship() {
		return this.notCalcuExpForLevel99Ship;
	}

	public void setNotCalcuExpForLevel99Ship(boolean notCalcuExpForLevel99Ship) {
		this.notCalcuExpForLevel99Ship = notCalcuExpForLevel99Ship;
	}

	public boolean isNotCalcuExpForLevel155Ship() {
		return this.notCalcuExpForLevel155Ship;
	}

	public void setNotCalcuExpForLevel155Ship(boolean notCalcuExpForLevel155Ship) {
		this.notCalcuExpForLevel155Ship = notCalcuExpForLevel155Ship;
	}

	public boolean isNoticeDeckmissionAgain() {
		return this.noticeDeckmissionAgain;
	}

	public void setNoticeDeckmissionAgain(boolean noticeDeckmissionAgain) {
		this.noticeDeckmissionAgain = noticeDeckmissionAgain;
	}

	public boolean isShowEventMapHPInConsole() {
		return this.showEventMapHPInConsole;
	}

	public void setShowEventMapHPInConsole(boolean showEventMapHPInConsole) {
		this.showEventMapHPInConsole = showEventMapHPInConsole;
	}

	public boolean isMinimizedToTray() {
		return this.minimizedToTray;
	}

	public void setMinimizedToTray(boolean minimizedToTray) {
		this.minimizedToTray = minimizedToTray;
	}

	public boolean isAutoUpdateBattleFlow() {
		return this.autoUpdateBattleFlow;
	}

	public void setAutoUpdateBattleFlow(boolean autoUpdateBattleFlow) {
		this.autoUpdateBattleFlow = autoUpdateBattleFlow;
	}

	public int getNoticeCondWhen() {
		return this.noticeCondWhen;
	}

	public void setNoticeCondWhen(int noticeCondWhen) {
		this.noticeCondWhen = noticeCondWhen;
	}

	public boolean isUseCache() {
		return this.useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}
}
