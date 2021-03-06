package tdrz.core.config;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
		try (XMLDecoder de = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)))) {
			Object obj = de.readObject();
			if (obj instanceof AppConfig) {
				config = (AppConfig) obj;
			}
		} catch (FileNotFoundException e) {

		}
	}

	public static void store() {
		try (XMLEncoder en = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)))) {
			en.writeObject(config);
		} catch (FileNotFoundException e) {
			LOG.warn("app配置保存失败", e);
		}
	}

	private boolean saveJson = false;

	private int listenPort = 22222;
	private boolean useProxy = false;
	private String proxyHost = "127.0.0.1";
	private int proxyPort = 8099;

	private boolean noticeDeckmission = true;
	private boolean noticeDeckmissionAgain = true;
	private boolean noticeNdock = true;
	private boolean noticeAkashiTimer = true;
	private boolean noticeCond = true;
	private boolean noticeCondOnlyMainFleet = true;

	private boolean showNameOnTitle = true;
	private boolean checkDoit = true;
	private boolean autoUpdateBattleFlow = true;
	private boolean notCalcuExpForLevel1Ship = true;
	private boolean notCalcuExpForLevel99Ship = true;
	private boolean notCalcuExpForLevel165Ship = false;
	private String calcuExpArea = "1-5,2-3,3-2,4-3,5-1,5-3,5-4";

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

	public boolean isNoticeAkashiTimer() {
		return this.noticeAkashiTimer;
	}

	public void setNoticeAkashiTimer(boolean noticeAkashiTimer) {
		this.noticeAkashiTimer = noticeAkashiTimer;
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

	public boolean isNoticeDeckmissionAgain() {
		return this.noticeDeckmissionAgain;
	}

	public void setNoticeDeckmissionAgain(boolean noticeDeckmissionAgain) {
		this.noticeDeckmissionAgain = noticeDeckmissionAgain;
	}

	public boolean isAutoUpdateBattleFlow() {
		return this.autoUpdateBattleFlow;
	}

	public void setAutoUpdateBattleFlow(boolean autoUpdateBattleFlow) {
		this.autoUpdateBattleFlow = autoUpdateBattleFlow;
	}

	public boolean isSaveJson() {
		return this.saveJson;
	}

	public void setSaveJson(boolean saveJson) {
		this.saveJson = saveJson;
	}

	public String getCalcuExpArea() {
		return this.calcuExpArea;
	}

	public void setCalcuExpArea(String calcuExpArea) {
		this.calcuExpArea = calcuExpArea;
	}

	public boolean isNotCalcuExpForLevel165Ship() {
		return this.notCalcuExpForLevel165Ship;
	}

	public void setNotCalcuExpForLevel165Ship(boolean notCalcuExpForLevel165Ship) {
		this.notCalcuExpForLevel165Ship = notCalcuExpForLevel165Ship;
	}
}
