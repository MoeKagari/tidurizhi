package tdrz.gui.window.main;

import java.text.SimpleDateFormat;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TrayItem;

import tdrz.config.AppConfig;
import tdrz.config.AppConstants;
import tdrz.dto.word.BasicDto;
import tdrz.dto.word.MapinfoDto;
import tdrz.dto.word.MapinfoDto.OneMap;
import tdrz.dto.word.MaterialDto;
import tdrz.gui.window.AbstractWindow;
import tdrz.gui.window.sub.BattleWindow;
import tdrz.gui.window.sub.FleetWindow;
import tdrz.gui.window.sub.FleetWindowAll;
import tdrz.gui.window.sub.FleetWindowOut;
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
import tdrz.gui.window.sub.table.ShipListTable;
import tdrz.gui.window.sub.table.UserItemListTable;
import tdrz.logic.DeckBuilder;
import tdrz.logic.TimeString;
import tdrz.update.GlobalContext;
import tdrz.update.data.DataType;
import tdrz.utils.SwtUtils;
import tool.FunctionUtils;

public class ApplicationMain extends AbstractWindow {
	public static ApplicationMain main;
	private static final Logger userLogger = LogManager.getLogger("user");

	/*------------------------------------------------------------------------------------------------------*/

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

	private final AbstractWindow[] windows;
	/*------------------------------------------------------------------------------------------------------*/

	private static final Display display = new Display();
	private final Composite contentComposite;
	private TrayItem trayItem;

	private Composite leftComposite;//左面板
	private Button itemList;
	private Button shipList;
	private Group resourceGroup;
	private Label[] resourceLabels = new Label[8];
	private Group deckGroup;
	private Label[] deckNameLabels = new Label[4];
	private Label[] deckTimeLabels = new Label[4];
	private Group ndockGroup;
	private Label[] ndockNameLabels = new Label[4];
	private Label[] ndockTimeLabels = new Label[4];
	private Label akashiTimerLabel;
	private org.eclipse.swt.widgets.List console;

	protected ApplicationMain() {
		super(display, AppConstants.MAINWINDOWNAME, ApplicationMain.class.getResourceAsStream(AppConstants.LOGO));

		this.getShell().addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent ev) {
				if (AppConfig.get().isCheckDoit()) {
					MessageBox box = new MessageBox(ApplicationMain.this.getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION | SWT.ON_TOP);
					box.setText("退出");
					box.setMessage("要退出航海日志吗?");
					ev.doit = box.open() == SWT.YES;
				}
			}

			@Override
			public void shellIconified(ShellEvent ev) {
				if (AppConfig.get().isMinimizedToTray()) {
					ApplicationMain.this.displayWindow();;
				}
			}
		});

		this.contentComposite = new Composite(this.getCenterComposite(), SWT.NONE);
		this.contentComposite.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
		this.contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.initLeftComposite();
		this.initRightComposite();
		this.initTrayItem();
		this.initMenuBar();

		this.windows = new AbstractWindow[] {//
				this,//
				this.fleetWindowAll, this.fleetWindowOuts[0], this.fleetWindowOuts[1], this.fleetWindowOuts[2], this.fleetWindowOuts[3],//
				this.calcuExpTable, this.calcuPracticeExpTable,//
				this.battleWindow, this.battleWindow.getBattleFlowWindow(), this.mapListTable,//
				this.createItemTable, this.createShipTable, this.missionResultTable, this.materialRecordTable,//
				this.destroyItemTable, this.destroyShipTable,//
				this.battleListTable, this.dropListTable,//
				this.shipListTable1, this.shipListTable2, this.shipListTable3,//
				this.itemListTable, this.questListTable, this.userItemListTable//
		};
		FunctionUtils.forEach(this.windows, AbstractWindow::restoreWindowConfig);
	}

	//左面板
	private void initLeftComposite() {
		this.leftComposite = new Composite(this.contentComposite, SWT.NONE);
		this.leftComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 1, 1));
		this.leftComposite.setLayoutData(SwtUtils.makeGridData(GridData.FILL_VERTICAL, 210));//左边面板的宽度在此控制
		{
			Composite buttonComposite = new Composite(this.leftComposite, SWT.NONE);
			buttonComposite.setLayout(new FillLayout());
			buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				this.itemList = new Button(buttonComposite, SWT.PUSH);
				this.itemList.setText("装备(0/0)");

				this.shipList = new Button(buttonComposite, SWT.PUSH);
				this.shipList.setText("舰娘(0/0)");
			}
		}
		{
			this.resourceGroup = new Group(this.leftComposite, SWT.NONE);
			this.resourceGroup.setText("资源");
			this.resourceGroup.setLayout(SwtUtils.makeGridLayout(4, 0, 0, 0, 0));
			this.resourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			String[] resourceStrings = { "油", "钢", "高速修复材", "开发资材", "弹", "铝", "螺丝", "高速建造材" };
			for (int i = 0; i < 8; i++) {
				Label resourceLabel = this.resourceLabels[i] = new Label(this.resourceGroup, SWT.RIGHT);
				SwtUtils.setToolTipText(resourceLabel, resourceStrings[i]);
				SwtUtils.initLabel(resourceLabel, "0", new GridData(GridData.FILL_HORIZONTAL));
			}
		}
		{
			this.deckGroup = new Group(this.leftComposite, SWT.NONE);
			this.deckGroup.setText("远征");
			this.deckGroup.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.deckGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			for (int i = 0; i < 4; i++) {
				this.deckNameLabels[i] = new Label(this.deckGroup, SWT.NONE);
				SwtUtils.initLabel(this.deckNameLabels[i], String.format("%s远征", AppConstants.DEFAULT_FLEET_NAME[i]), new GridData(GridData.FILL_HORIZONTAL));

				this.deckTimeLabels[i] = new Label(this.deckGroup, SWT.RIGHT);
				SwtUtils.initLabel(this.deckTimeLabels[i], "00时00分00秒", new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1), 78);
			}
		}
		{
			this.ndockGroup = new Group(this.leftComposite, SWT.NONE);
			this.ndockGroup.setText("入渠");
			this.ndockGroup.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.ndockGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			for (int i = 0; i < 4; i++) {
				this.ndockNameLabels[i] = new Label(this.ndockGroup, SWT.NONE);
				SwtUtils.initLabel(this.ndockNameLabels[i], String.format("渠%d", i + 1), new GridData(GridData.FILL_HORIZONTAL));

				this.ndockTimeLabels[i] = new Label(this.ndockGroup, SWT.RIGHT);
				SwtUtils.initLabel(this.ndockTimeLabels[i], "00时00分00秒", new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1), 78);
			}
		}
		{
			Composite akashiTimerComposite = new Composite(this.leftComposite, SWT.NONE);
			akashiTimerComposite.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0, 0, 0, 3, 3));
			akashiTimerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				SwtUtils.initLabel(new Label(akashiTimerComposite, SWT.LEFT), "泊地修理", new GridData(), 48);
				this.akashiTimerLabel = new Label(akashiTimerComposite, SWT.RIGHT);
				SwtUtils.initLabel(this.akashiTimerLabel, "??秒", new GridData(GridData.FILL_HORIZONTAL));
			}
		}
		{
			this.console = new org.eclipse.swt.widgets.List(this.leftComposite, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
			this.console.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.console.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (e.button == 3) {
						ApplicationMain.this.console.deselectAll();
					}
				}
			});
		}
	}

	//右面板
	private void initRightComposite() {
		int fleetLength = 1;
		Composite rightComposite = new Composite(this.contentComposite, SWT.NONE);
		rightComposite.setLayout(SwtUtils.makeGridLayout(new int[] { 0, 1, 1, 2, 2 }[fleetLength], 2, 2, 1, 1));
		rightComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		IntStream.range(0, fleetLength).map(index -> index + 1).forEach(id -> new FleetWindow(id, new Composite(rightComposite, SWT.BORDER)));
	}

	//托盘图标
	private void initTrayItem() {
		this.trayItem = new TrayItem(display.getSystemTray(), SWT.NONE);
		this.trayItem.setImage(this.getLogo());
		this.trayItem.addListener(SWT.Selection, ev -> this.displayWindow());
		this.trayItem.addMenuDetectListener(new TrayItemMenuListener());
	}

	//菜单栏
	private void initMenuBar() {
		MenuItem cmdMenuItem = new MenuItem(this.getMenuBar(), SWT.CASCADE);
		cmdMenuItem.setText("主菜单");
		Menu cmdMenu = new Menu(cmdMenuItem);
		cmdMenuItem.setMenu(cmdMenu);
		{
			MenuItem ship = new MenuItem(cmdMenu, SWT.CHECK);
			ship.setText("所有舰娘(信息)");
			this.shipListTable1 = new ShipListTable(this, ship, ship.getText()) {
				@Override
				protected ShipListTableMode getMode() {
					return ShipListTableMode.INFORMATION;
				}
			};
			this.shipList.addSelectionListener(new ControlSelectionListener(this.shipListTable1::displayWindow));

			MenuItem ship2 = new MenuItem(cmdMenu, SWT.CHECK);
			ship2.setText("所有舰娘(属性)");
			this.shipListTable2 = new ShipListTable(this, ship2, ship2.getText()) {
				@Override
				protected ShipListTableMode getMode() {
					return ShipListTableMode.PARAMENTER;
				}
			};

			MenuItem ship3 = new MenuItem(cmdMenu, SWT.CHECK);
			ship3.setText("所有舰娘(综合)");
			this.shipListTable3 = new ShipListTable(this, ship3, ship3.getText()) {
				@Override
				protected ShipListTableMode getMode() {
					return ShipListTableMode.ALL;
				}
			};

			MenuItem item = new MenuItem(cmdMenu, SWT.CHECK);
			item.setText("所有装备");
			this.itemListTable = new ItemListTable(this, item, item.getText());
			this.itemList.addSelectionListener(new ControlSelectionListener(this.itemListTable::displayWindow));

			MenuItem quest = new MenuItem(cmdMenu, SWT.CHECK);
			quest.setText("所有任务");
			this.questListTable = new QuestListTable(this, quest, quest.getText());

			MenuItem useitem = new MenuItem(cmdMenu, SWT.CHECK);
			useitem.setText("所有道具");
			this.userItemListTable = new UserItemListTable(this, useitem, useitem.getText());

			new MenuItem(cmdMenu, SWT.SEPARATOR);

			MenuItem battle = new MenuItem(cmdMenu, SWT.CHECK);
			battle.setText("出击");
			this.battleWindow = new BattleWindow(this, battle, battle.getText());

			MenuItem mapinfo = new MenuItem(cmdMenu, SWT.CHECK);
			mapinfo.setText("地图详情");
			this.mapListTable = new MapListTable(this, mapinfo, mapinfo.getText());

			new MenuItem(cmdMenu, SWT.SEPARATOR);

			MenuItem expcalu = new MenuItem(cmdMenu, SWT.CHECK);
			expcalu.setText("经验计算器");
			this.calcuExpTable = new CalcuExpTable(this, expcalu, expcalu.getText());

			MenuItem practiceexpcalu = new MenuItem(cmdMenu, SWT.CHECK);
			practiceexpcalu.setText("演习经验计算器");
			this.calcuPracticeExpTable = new CalcuPracticeExpTable(this, practiceexpcalu, practiceexpcalu.getText());

			new MenuItem(cmdMenu, SWT.SEPARATOR);

			MenuItem dispose = new MenuItem(cmdMenu, SWT.NONE);
			dispose.setText("退出");
			dispose.addSelectionListener(new ControlSelectionListener(this.getShell()::close));
		}

		MenuItem recordMenuItem = new MenuItem(this.getMenuBar(), SWT.CASCADE);
		recordMenuItem.setText("记录");
		Menu recordMenu = new Menu(recordMenuItem);
		recordMenuItem.setMenu(recordMenu);
		{
			MenuItem createship = new MenuItem(recordMenu, SWT.CHECK);
			createship.setText("建造记录");
			this.createShipTable = new CreateShipTable(this, createship, createship.getText());

			MenuItem createitem = new MenuItem(recordMenu, SWT.CHECK);
			createitem.setText("开发记录");
			this.createItemTable = new CreateItemTable(this, createitem, createitem.getText());

			MenuItem mission = new MenuItem(recordMenu, SWT.CHECK);
			mission.setText("远征记录");
			this.missionResultTable = new MissionResultTable(this, mission, mission.getText());

			MenuItem material = new MenuItem(recordMenu, SWT.CHECK);
			material.setText("资源记录");
			this.materialRecordTable = new MaterialRecordTable(this, material, material.getText());

			new MenuItem(recordMenu, SWT.SEPARATOR);

			MenuItem destroyShip = new MenuItem(recordMenu, SWT.CHECK);
			destroyShip.setText("解体记录");
			this.destroyShipTable = new DestroyShipTable(this, destroyShip, destroyShip.getText());

			MenuItem destroyItem = new MenuItem(recordMenu, SWT.CHECK);
			destroyItem.setText("废弃记录");
			this.destroyItemTable = new DestroyItemTable(this, destroyItem, destroyItem.getText());

			new MenuItem(recordMenu, SWT.SEPARATOR);

			MenuItem battle = new MenuItem(recordMenu, SWT.CHECK);
			battle.setText("出击记录");
			this.battleListTable = new BattleListTable(this, battle, battle.getText());

			MenuItem drop = new MenuItem(recordMenu, SWT.CHECK);
			drop.setText("掉落记录");
			this.dropListTable = new DropListTable(this, drop, drop.getText());
		}

		MenuItem fleetMenuItem = new MenuItem(this.getMenuBar(), SWT.CASCADE);
		fleetMenuItem.setText("舰队");
		Menu fleetMenu = new Menu(fleetMenuItem);
		fleetMenuItem.setMenu(fleetMenu);
		{//外置舰队面板
			MenuItem fleetWindowAllMenuItem = new MenuItem(fleetMenu, SWT.CHECK);
			fleetWindowAllMenuItem.setText("全舰队");
			this.fleetWindowAll = new FleetWindowAll(this, fleetWindowAllMenuItem, fleetWindowAllMenuItem.getText());

			this.fleetWindowOuts = IntStream.range(0, 4).mapToObj(index -> {
				MenuItem fleetWindowOutMenuItem = new MenuItem(fleetMenu, SWT.CHECK);
				fleetWindowOutMenuItem.setText(AppConstants.DEFAULT_FLEET_NAME[index]);
				return new FleetWindowOut(this, fleetWindowOutMenuItem, index + 1) {
					@Override
					public int getId() {
						return index;
					}
				};
			}).toArray(FleetWindowOut[]::new);
		}

		MenuItem etcMenuItem = new MenuItem(this.getMenuBar(), SWT.CASCADE);
		etcMenuItem.setText("其它");
		Menu etcMenu = new Menu(etcMenuItem);
		etcMenuItem.setMenu(etcMenu);
		{
			MenuItem topmost = new MenuItem(etcMenu, SWT.CHECK);
			topmost.setText("总在前");
			topmost.addSelectionListener(new ControlSelectionListener(ev -> this.setTopMost(topmost.getSelection())));

			MenuItem deckbuilder = new MenuItem(etcMenu, SWT.PUSH);
			deckbuilder.setText("DeckBuilder");
			deckbuilder.addSelectionListener(new ControlSelectionListener(ev -> new Clipboard(display).setContents(new Object[] { DeckBuilder.build() }, new Transfer[] { TextTransfer.getInstance() })));
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public Display getDisplay() {
		return display;
	}

	public TrayItem getTrayItem() {
		return this.trayItem;
	}

	public Button getItemList() {
		return this.itemList;
	}

	public Button getShipList() {
		return this.shipList;
	}

	public Group getDeckGroup() {
		return this.deckGroup;
	}

	public Group getNdockGroup() {
		return this.ndockGroup;
	}

	public Label[] getResourceLabel() {
		return this.resourceLabels;
	}

	public Label[] getDeckNameLabel() {
		return this.deckNameLabels;
	}

	public Label[] getDeckTimeLabel() {
		return this.deckTimeLabels;
	}

	public Label[] getNdockNameLabel() {
		return this.ndockNameLabels;
	}

	public Label[] getNdockTimeLabel() {
		return this.ndockTimeLabels;
	}

	public Label getAkashiTimerLabel() {
		return this.akashiTimerLabel;
	}

	public AbstractWindow[] getWindows() {
		return this.windows;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		if (FunctionUtils.isFalse(this.getShell().isDisposed())) {
			//更新主面板的 所有舰娘 按钮
			//更新主面板的 所有装备 按钮
			BasicDto basic = GlobalContext.getBasicInformation();
			if (basic != null) {
				String text = String.format("舰娘(%d/%d)", GlobalContext.getShipMap().size(), basic.getMaxChara());
				if (FunctionUtils.isFalse(StringUtils::equals, this.shipList.getText(), text)) {
					this.shipList.setText(text);
				}

				text = String.format("装备(%d/%d)", GlobalContext.getItemMap().size(), basic.getMaxSlotItem());
				if (FunctionUtils.isFalse(StringUtils::equals, this.itemList.getText(), text)) {
					this.itemList.setText(text);
				}
			}

			//更新标题 
			String title = AppConstants.MAINWINDOWNAME;
			if (AppConfig.get().isShowNameOnTitle() && basic != null) {
				title = String.format("%s - %s", basic.getUserName(), title);
			}
			if (FunctionUtils.isFalse(StringUtils::equals, this.getShell().getText(), title)) {
				this.getShell().setText(title);
			}

			//更新主面板的 资源
			MaterialDto currentMaterial = GlobalContext.getCurrentMaterial();
			if (currentMaterial != null) {
				int[] resources = currentMaterial.getMaterialForWindow();
				FunctionUtils.forEach(this.resourceLabels, resources, (label, resource) -> SwtUtils.setText(label, String.valueOf(resource)));
			}

			//print活动海域HP到console
			if (type == DataType.MAPINFO) {
				MapinfoDto mapinfo = GlobalContext.getMapinfo();
				if (mapinfo != null) {
					mapinfo.getMaps().stream().filter(OneMap::isEventMap).filter(map -> map.getHP()[0] != 0).forEach(map -> {
						String message = String.format("%d-%d-%s: [%d,%d]", map.getArea(), map.getNo(), map.getEventMap().getRank(), map.getHP()[0], map.getHP()[1]);
						this.printMessage(message, false);
					});
				}
			}
		}
	}

	public void printMessage(String mes, boolean printToLog) {
		String message = mes;
		if (printToLog) {
			userLogger.info(mes);
			message = AppConstants.CONSOLE_TIME_FORMAT.format(TimeString.getCurrentTime()) + "  " + message;
		}
		display.asyncExec(FunctionUtils.getRunnable(this::printMessage, message));
	}

	public void printNewDay(long time) {
		this.printMessage(new SimpleDateFormat("yyyy-MM-dd").format(time));
	}

	private void printMessage(String message) {
		if (this.console.isDisposed()) return;

		if (this.console.getItemCount() >= 200) this.console.remove(0);
		this.console.add(message);
		this.console.setSelection(this.console.getItemCount() - 1);
		this.console.deselectAll();
	}

	public void start() {
		this.printNewDay(TimeString.getCurrentTime());
		this.printMessage(AppConstants.MAINWINDOWNAME + "启动", true);
		this.resourceGroup.forceFocus();
		while (FunctionUtils.isFalse(this.getShell().isDisposed())) {
			FunctionUtils.ifNotConsumer(display, Display::readAndDispatch, Display::sleep);
		}
		display.dispose();
	}

	@Override
	protected Point getDefaultSize() {
		return SwtUtils.DPIAwareSize(new Point(413, 524));
	}

	@Override
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.RESIZE;
	}

	public class TrayItemMenuListener implements MenuDetectListener {
		private Menu menu;

		public TrayItemMenuListener() {
			this.menu = new Menu(ApplicationMain.this.getShell());

			MenuItem dispose = new MenuItem(this.menu, SWT.NONE);
			dispose.setText("退出");
			dispose.addSelectionListener(new ControlSelectionListener(ApplicationMain.this.getShell()::close));
		}

		@Override
		public void menuDetected(MenuDetectEvent e) {
			this.menu.setVisible(true);
		}
	}
}