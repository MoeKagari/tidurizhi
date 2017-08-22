package tdrz.gui.window;

import tdrz.gui.window.sub.BattleWindow;
import tdrz.gui.window.sub.BookItemWindow;
import tdrz.gui.window.sub.BookShipWindow;
import tdrz.gui.window.sub.ConfigWindow;
import tdrz.gui.window.sub.FleetWindowAll;
import tdrz.gui.window.sub.FleetWindowOut;
import tdrz.gui.window.sub.WindowOperationWindow;
import tdrz.gui.window.sub.table.BattleListTable;
import tdrz.gui.window.sub.table.CalcuExpTable;
import tdrz.gui.window.sub.table.CalcuPracticeExpTable;
import tdrz.gui.window.sub.table.CreateItemTable;
import tdrz.gui.window.sub.table.CreateShipTable;
import tdrz.gui.window.sub.table.DestroyItemTable;
import tdrz.gui.window.sub.table.DestroyShipTable;
import tdrz.gui.window.sub.table.DropListTable;
import tdrz.gui.window.sub.table.ItemListTable;
import tdrz.gui.window.sub.table.MapListTable;
import tdrz.gui.window.sub.table.MaterialRecordTable;
import tdrz.gui.window.sub.table.MissionResultTable;
import tdrz.gui.window.sub.table.QuestListTable;
import tdrz.gui.window.sub.table.ShipGroupTable;
import tdrz.gui.window.sub.table.ShipListTable;
import tdrz.gui.window.sub.table.UserItemListTable;

@SuppressWarnings("unused")
public class WindowManager {
	/** 舰队面板-全 */
	private FleetWindowAll fleetWindowAll;
	/** 舰队面板-单 */
	private FleetWindowOut[] fleetWindowOuts;

	/** 经验计算器 */
	private CalcuExpTable calcuExpTable;
	/** 演习经验计算器 */
	private CalcuPracticeExpTable calcuPracticeExpTable;

	/** 战斗窗口 */
	private BattleWindow battleWindow;
	/** 地图详情 */
	private MapListTable mapListTable;

	/** 开发记录 */
	private CreateItemTable createItemTable;
	/** 建造记录 */
	private CreateShipTable createShipTable;
	/** 远征记录 */
	private MissionResultTable missionResultTable;
	/** 资源记录 */
	private MaterialRecordTable materialRecordTable;

	/** 解体记录 */
	private DestroyShipTable destroyShipTable;
	/** 废弃记录 */
	private DestroyItemTable destroyItemTable;

	/** 战斗记录 */
	private BattleListTable battleListTable;
	/** 掉落记录 */
	private DropListTable dropListTable;

	/** 所有舰娘(信息) */
	private ShipListTable shipListTable1;
	/** 所有舰娘(属性) */
	private ShipListTable shipListTable2;
	/** 所有舰娘(综合) */
	private ShipListTable shipListTable3;
	/** 所有装备 */
	private ItemListTable itemListTable;
	/** 所有任务 */
	private QuestListTable questListTable;
	/** 所有道具 */
	private UserItemListTable userItemListTable;

	private ShipGroupTable shipGroupTable;
	/** 窗口操作 */
	private WindowOperationWindow windowOperationWindow;
	/** 设置 */
	private ConfigWindow configWindow;
	/** 舰娘图鉴 */
	private BookShipWindow bookShipWindow;
	/** 装备图鉴 */
	private BookItemWindow bookItemWindow;
}
