package tdrz.config;

import java.io.File;
import java.text.SimpleDateFormat;

public class AppConstants {
	public static final String MAINWINDOWNAME = "提督日志";
	public static final String[] DEFAULT_FLEET_NAME = { "第一舰队", "第二舰队", "第三舰队", "第四舰队" };
	public static final String[] EMPTY_NAMES = { "", "", "", "", "", "" };

	public static final SimpleDateFormat DECK_NDOCK_COMPLETE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat DECK_NDOCK_COMPLETE_TIME_FORMAT_LONG = new SimpleDateFormat("MM-dd HH:mm:ss");
	public static final SimpleDateFormat TABLE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat CONSOLE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	public static final File MASTERDATA_FILE = new File("MasterData.json").getAbsoluteFile();
	public static final String MASTERDATAFILE_BACKUP = "/resources/MasterData.json";

	/** 航海日志的图标 */
	public static final String LOGO = "/resources/logo.png";

	public static final String LOCKFILEPATH = "config/LOCK";
	/** 各个窗口的配置MAP(String,{@link WindowConfig}) */
	public static final String WINDOWCONFIGS_FILEPATH = "config/window.xml";
	/** 软件的各项设置{@link AppConfig} */
	public static final String APPCONFIGS_FILEPATH = "config/app.xml";

	/** 所有装备暂存文件 */
	public static final File ITEM_FILE = new File("item").getAbsoluteFile();

	/** 记录文件 */
	public static final File MEMORY_FILE = new File("memory").getAbsoluteFile();
}
