package tdrz.gui.window.main;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import tdrz.core.config.AppConfig;
import tdrz.core.config.AppConstants;
import tdrz.core.logic.DeckBuilder;
import tdrz.core.logic.TimeString;
import tdrz.core.util.SwtUtils;
import tdrz.gui.composite.FleetWindow;
import tdrz.gui.other.ControlSelectionListener;
import tdrz.gui.other.WindowConfigChangedAdapter;
import tdrz.gui.window.AbstractWindow;
import tdrz.gui.window.AbstractWindowBase;
import tdrz.gui.window.WindowResource;
import tdrz.gui.window.sub.BattleFlowWindow;
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
import tdrz.gui.window.sub.table.RemodleRecordTable;
import tdrz.gui.window.sub.table.ShipGroupTable;
import tdrz.gui.window.sub.table.ShipListTable;
import tdrz.gui.window.sub.table.UserItemListTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.GlobalContextUpdater;
import tdrz.update.context.data.DataType;
import tdrz.update.dto.word.ResourceDto;
import tool.function.FunctionUtils;

public final class ApplicationMain extends AbstractWindowBase {
	public static ApplicationMain main;

	/*------------------------------------------------------------------------------------------------------*/

	/** 舰队面板-全 */
	private final FleetWindowAll fleetWindowAll;
	/** 舰队面板-单 */
	private final FleetWindowOut[] fleetWindowOuts;

	/** 经验计算器 */
	private final CalcuExpTable calcuExpTable;
	/** 演习经验计算器 */
	private final CalcuPracticeExpTable calcuPracticeExpTable;

	/** 战斗窗口 */
	private final BattleWindow battleWindow;
	/** 战斗流程 */
	private final BattleFlowWindow battleFlowWindow;
	/** 地图详情 */
	private final MapListTable mapListTable;

	/** 开发记录 */
	private final CreateItemTable createItemTable;
	/** 建造记录 */
	private final CreateShipTable createShipTable;
	/** 远征记录 */
	private final MissionResultTable missionResultTable;
	/** 资源记录 */
	private final MaterialRecordTable materialRecordTable;

	/** 解体记录 */
	private final DestroyShipTable destroyShipTable;
	/** 废弃记录 */
	private final DestroyItemTable destroyItemTable;
	/** 改修记录 */
	private final RemodleRecordTable remodleRecordTable;

	/** 战斗记录 */
	private final BattleListTable battleListTable;
	/** 掉落记录 */
	private final DropListTable dropListTable;

	/** 所有舰娘(信息) */
	private final ShipListTable shipListTable1;
	/** 所有舰娘(属性) */
	private final ShipListTable shipListTable2;
	/** 所有舰娘(综合) */
	private final ShipListTable shipListTable3;
	/** 所有装备 */
	private final ItemListTable itemListTable;
	/** 所有任务 */
	private final QuestListTable questListTable;
	/** 所有道具 */
	private final UserItemListTable userItemListTable;

	private final ShipGroupTable shipGroupTable;
	/** 窗口操作 */
	private final WindowOperationWindow windowOperationWindow;
	/** 设置 */
	private final ConfigWindow configWindow;
	/** 舰娘图鉴 */
	private final BookShipWindow bookShipWindow;
	/** 装备图鉴 */
	private final BookItemWindow bookItemWindow;

	/*------------------------------------------------------------------------------------------------------*/

	private TrayItem trayItem;

	private ShipItemButtonComposite shipItemButtonComposite;
	private ResourceGroup resourceGroup;
	private NameTimeGroup deckGroup, ndockGroup;
	private AkashiTimerComposite akashiTimerComposite;
	private MessageList messageList;

	public ApplicationMain() {
		this.shell.addListener(SWT.Iconify, ev -> this.hiddenWindow());
		this.shell.addListener(SWT.Close, ev -> {
			if (AppConfig.get().isCheckDoit()) {
				Shell parent = new Shell(WindowResource.DISPLAY, SWT.TOOL | SWT.TOP | SWT.ON_TOP);
				{
					MessageBox box = new MessageBox(parent, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					box.setText("退出");
					box.setMessage("要退出提督日志吗?");
					ev.doit = box.open() == SWT.YES;
				}
				parent.dispose();
			}
		});
		this.addWindowConfigChangedListener(new WindowConfigChangedAdapter() {
			@Override
			public void displayBefore() {
				ApplicationMain.this.shell.setMinimized(false);
			}
		});

		//左边信息面板
		Composite centerContentComposite = new Composite(this.centerComposite, SWT.NONE);
		centerContentComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		centerContentComposite.setLayoutData(SwtUtils.makeGridData(GridData.FILL_VERTICAL, 210));//左边面板的宽度在此控制
		{
			this.shipItemButtonComposite = new ShipItemButtonComposite(centerContentComposite);
			this.resourceGroup = new ResourceGroup(centerContentComposite);
			this.deckGroup = new NameTimeGroup(centerContentComposite, "远征");
			this.ndockGroup = new NameTimeGroup(centerContentComposite, "入渠");
			this.akashiTimerComposite = new AkashiTimerComposite(centerContentComposite);
			this.messageList = new MessageList(centerContentComposite);
		}

		//右边舰队面板
		int fleetLength = 1;
		Composite rightContentComposite = new Composite(this.rightComposite, SWT.NONE);
		rightContentComposite.setLayout(SwtUtils.makeGridLayout(new int[] { 0, 1, 1, 2, 2 }[fleetLength], 2, 2, 1, 1));
		rightContentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		for (int index = 0; index < fleetLength; index++) {
			new FleetWindow(index + 1, new Composite(rightContentComposite, SWT.BORDER));
		}

		//菜单栏
		{
			Menu cmdMenu = SwtUtils.makeCasacdeMenu(this.menuBar, "主菜单");
			{
				this.shipListTable1 = this.addMenuItemForWindow(cmdMenu, new ShipListTable() {
					@Override
					protected ShipListTableMode getMode() {
						return ShipListTableMode.INFORMATION;
					}
				});
				this.shipListTable2 = this.addMenuItemForWindow(cmdMenu, new ShipListTable() {
					@Override
					protected ShipListTableMode getMode() {
						return ShipListTableMode.PARAMENTER;
					}
				});
				this.shipListTable3 = this.addMenuItemForWindow(cmdMenu, new ShipListTable() {
					@Override
					protected ShipListTableMode getMode() {
						return ShipListTableMode.ALL;
					}
				});
				this.itemListTable = this.addMenuItemForWindow(cmdMenu, new ItemListTable());
				this.questListTable = this.addMenuItemForWindow(cmdMenu, new QuestListTable());
				this.userItemListTable = this.addMenuItemForWindow(cmdMenu, new UserItemListTable());
				SwtUtils.makeSeparatorMenuItem(cmdMenu);

				this.battleFlowWindow = new BattleFlowWindow();
				this.battleWindow = this.addMenuItemForWindow(cmdMenu, new BattleWindow(this.battleFlowWindow));
				this.addMenuItemForWindow(cmdMenu, this.battleFlowWindow);

				this.mapListTable = this.addMenuItemForWindow(cmdMenu, new MapListTable());
				SwtUtils.makeSeparatorMenuItem(cmdMenu);
				this.calcuExpTable = this.addMenuItemForWindow(cmdMenu, new CalcuExpTable());
				this.calcuPracticeExpTable = this.addMenuItemForWindow(cmdMenu, new CalcuPracticeExpTable());
				SwtUtils.makeSeparatorMenuItem(cmdMenu);
				SwtUtils.makeMenuItem(cmdMenu, SWT.NONE, "退出", this.shell::close);
			}

			Menu recordMenu = SwtUtils.makeCasacdeMenu(this.menuBar, "记录");
			{
				this.createShipTable = this.addMenuItemForWindow(recordMenu, new CreateShipTable());
				this.createItemTable = this.addMenuItemForWindow(recordMenu, new CreateItemTable());
				this.missionResultTable = this.addMenuItemForWindow(recordMenu, new MissionResultTable());
				this.materialRecordTable = this.addMenuItemForWindow(recordMenu, new MaterialRecordTable());
				SwtUtils.makeSeparatorMenuItem(recordMenu);
				this.destroyShipTable = this.addMenuItemForWindow(recordMenu, new DestroyShipTable());
				this.destroyItemTable = this.addMenuItemForWindow(recordMenu, new DestroyItemTable());
				this.remodleRecordTable = this.addMenuItemForWindow(recordMenu, new RemodleRecordTable());
				SwtUtils.makeSeparatorMenuItem(recordMenu);
				this.battleListTable = this.addMenuItemForWindow(recordMenu, new BattleListTable());
				this.dropListTable = this.addMenuItemForWindow(recordMenu, new DropListTable());
			}

			Menu fleetMenu = SwtUtils.makeCasacdeMenu(this.menuBar, "舰队");
			{
				this.fleetWindowAll = this.addMenuItemForWindow(fleetMenu, new FleetWindowAll());

				this.fleetWindowOuts = IntStream.rangeClosed(1, 4)//
						.mapToObj(id -> this.addMenuItemForWindow(fleetMenu,//
								new FleetWindowOut() {
									@Override
									public int getId() {
										return id;
									}
								}//
						)).toArray(FleetWindowOut[]::new);
			}

			Menu etcMenu = SwtUtils.makeCasacdeMenu(this.menuBar, "其它");
			{
				SwtUtils.makeMenuItem(etcMenu, SWT.PUSH, "DeckBuilder", ev -> new Clipboard(WindowResource.DISPLAY).setContents(new Object[] { DeckBuilder.build() }, new Transfer[] { TextTransfer.getInstance() }));
				SwtUtils.makeSeparatorMenuItem(etcMenu);
				this.shipGroupTable = this.addMenuItemForWindow(etcMenu, new ShipGroupTable());
				this.configWindow = this.addMenuItemForWindow(etcMenu, new ConfigWindow());
				this.windowOperationWindow = this.addMenuItemForWindow(etcMenu, new WindowOperationWindow());
				SwtUtils.makeSeparatorMenuItem(etcMenu);
				this.bookShipWindow = this.addMenuItemForWindow(etcMenu, new BookShipWindow());
				this.bookItemWindow = this.addMenuItemForWindow(etcMenu, new BookItemWindow());
			}
		}

		//左边信息面板 的 两个按钮
		this.shipItemButtonComposite.shipList.addSelectionListener(new ControlSelectionListener(this.shipListTable1::displayWindow));
		this.shipItemButtonComposite.itemList.addSelectionListener(new ControlSelectionListener(this.itemListTable::displayWindow));

		//托盘
		this.trayItem = new TrayItem(WindowResource.DISPLAY.getSystemTray(), SWT.NONE);
		this.trayItem.setImage(WindowResource.LOGO);
		this.trayItem.addSelectionListener(new ControlSelectionListener(ev -> {
			if (this.getWindowConfig().isVisible()) {
				this.hiddenWindow();
			} else {
				this.displayWindow();
			}
		}));
		this.trayItem.addMenuDetectListener(new TrayItemMenuListener());
		this.trayItem.setToolTipText(this.defaultTitle());

		//所有窗口
		//null为 windowOperationWindow 中用的占位符
		List<AbstractWindowBase> windows = Arrays.asList(//
				this, this.fleetWindowAll, null, null,    //
				this.fleetWindowOuts[0], this.fleetWindowOuts[1], this.fleetWindowOuts[2], this.fleetWindowOuts[3],//
				this.calcuExpTable, this.calcuPracticeExpTable, null, null, //
				this.mapListTable, this.battleWindow, this.battleFlowWindow, null, //
				this.createItemTable, this.createShipTable, this.missionResultTable, this.materialRecordTable, //
				this.destroyItemTable, this.destroyShipTable, this.battleListTable, this.dropListTable, //
				this.shipListTable1, this.shipListTable2, this.shipListTable3, null, //
				this.itemListTable, this.userItemListTable, this.questListTable, null, //
				this.shipGroupTable, this.configWindow, this.windowOperationWindow, null, //
				this.bookShipWindow, this.bookItemWindow, this.remodleRecordTable, null//
		);

		//添加窗口到 [窗口操作] 窗口
		windows.forEach(this.windowOperationWindow::addWindow);

		//恢复窗口配置
		windows.forEach(window -> {
			if (window != null) {
				GlobalContextUpdater.addListener(window);
				window.layout();
				window.restoreWindowConfig();
			}
		});
	}

	private <T extends AbstractWindow> T addMenuItemForWindow(Menu parent, T window) {
		MenuItem menuItem = new MenuItem(parent, SWT.CHECK);
		menuItem.setText(window.defaultTitle());
		menuItem.addSelectionListener(new ControlSelectionListener(ev -> {
			if (menuItem.getSelection()) {
				window.displayWindow();
			} else {
				window.hiddenWindow();
			}
		}));
		window.addWindowConfigChangedListener(new WindowConfigChangedAdapter() {
			@Override
			public void visibleChangedBefore(boolean visible) {
				menuItem.setSelection(visible);
			}
		});
		return window;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public TrayItem getTrayItem() {
		return this.trayItem;
	}

	public NameTimeGroup getDeckGroup() {
		return this.deckGroup;
	}

	public NameTimeGroup getNdockGroup() {
		return this.ndockGroup;
	}

	public AkashiTimerComposite getAkashiTimerComposite() {
		return this.akashiTimerComposite;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void update(DataType type) {
		if (FunctionUtils.isFalse(this.shell.isDisposed())) {
			Optional.ofNullable(GlobalContext.getBasicInformation()).ifPresent(basic -> {
				//更新主面板的 [舰娘] 按钮
				String text = String.format("舰娘(%d/%d)", GlobalContext.getShipMap().size(), basic.getMaxChara());
				if (FunctionUtils.isFalse(StringUtils::equals, this.shipItemButtonComposite.shipList.getText(), text)) {
					this.shipItemButtonComposite.shipList.setText(text);
				}

				//更新主面板的 [装备] 按钮
				text = String.format("装备(%d/%d)", GlobalContext.getItemMap().size(), basic.getMaxSlotItem());
				if (FunctionUtils.isFalse(StringUtils::equals, this.shipItemButtonComposite.itemList.getText(), text)) {
					this.shipItemButtonComposite.itemList.setText(text);
				}
			});

			//更新标题 
			this.updateTitle();

			//更新主面板的 资源
			Optional.ofNullable(GlobalContext.getCurrentMaterial().getMaterial()).ifPresent(currentMaterial -> {
				FunctionUtils.forEach(//
						this.resourceGroup.labels, currentMaterial.getResourceForApplicationMain(),//
						(label, resource) -> SwtUtils.setText(label, String.valueOf(resource))//
				);
			});
		}
	}

	/** 更新标题 */
	public void updateTitle() {
		Optional.ofNullable(GlobalContext.getBasicInformation()).ifPresent(basic -> {
			String title = AppConstants.MAINWINDOWNAME;
			if (AppConfig.get().isShowNameOnTitle()) {
				title = String.format("%s - %s", basic.getUserName(), title);
			}
			if (FunctionUtils.isFalse(StringUtils::equals, this.shell.getText(), title)) {
				this.shell.setText(title);
			}
		});
	}

	public void printMessage(String mes, boolean printToLog) {
		String message = mes;
		if (printToLog) {
			LogManager.getLogger("user").info(mes);
			message = AppConstants.CONSOLE_TIME_FORMAT.format(TimeString.getCurrentTime()) + "  " + message;
		}
		WindowResource.DISPLAY.asyncExec(FunctionUtils.getRunnable(this::printMessage, message));
	}

	public void printNewDay(long time) {
		this.printMessage(new SimpleDateFormat("yyyy-MM-dd").format(time));
	}

	private void printMessage(String message) {
		if (this.messageList.isDisposed()) return;

		if (this.messageList.getItemCount() >= 200) this.messageList.remove(0);
		this.messageList.add(message);
		this.messageList.setSelection(this.messageList.getItemCount() - 1);
		this.messageList.deselectAll();
	}

	public void start() {
		this.printNewDay(TimeString.getCurrentTime());
		this.printMessage(AppConstants.MAINWINDOWNAME + "启动", true);
		while (FunctionUtils.isFalse(this.shell.isDisposed())) {
			FunctionUtils.ifNotConsumer(WindowResource.DISPLAY, Display::readAndDispatch, Display::sleep);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public boolean defaultVisible() {
		return true;
	}

	@Override
	public boolean canMinimizedBeOperated() {
		return false;
	}

	@Override
	public boolean haveRightComposite() {
		return true;
	}

	@Override
	public Point defaultSize() {
		return SwtUtils.DPIAwareSize(new Point(406, 524));
	}

	@Override
	public String defaultTitle() {
		return AppConstants.MAINWINDOWNAME;
	}

	private class TrayItemMenuListener implements MenuDetectListener {
		private final Menu menu = new Menu(ApplicationMain.this.shell);

		TrayItemMenuListener() {
			Menu haveMenu = SwtUtils.makeCasacdeMenu(this.menu, "拥有");
			{
				this.makeNewMenuItem(haveMenu, ApplicationMain.this.shipListTable1);
				this.makeNewMenuItem(haveMenu, ApplicationMain.this.shipListTable2);
				this.makeNewMenuItem(haveMenu, ApplicationMain.this.shipListTable3);
				this.makeNewMenuItem(haveMenu, ApplicationMain.this.itemListTable);
				this.makeNewMenuItem(haveMenu, ApplicationMain.this.userItemListTable);
			}

			Menu fleetMenu = SwtUtils.makeCasacdeMenu(this.menu, "舰队");
			{
				this.makeNewMenuItem(fleetMenu, ApplicationMain.this.fleetWindowAll);
				this.makeNewMenuItem(fleetMenu, ApplicationMain.this.fleetWindowOuts[0]);
				this.makeNewMenuItem(fleetMenu, ApplicationMain.this.fleetWindowOuts[1]);
				this.makeNewMenuItem(fleetMenu, ApplicationMain.this.fleetWindowOuts[2]);
				this.makeNewMenuItem(fleetMenu, ApplicationMain.this.fleetWindowOuts[3]);
			}

			Menu recordMenu = SwtUtils.makeCasacdeMenu(this.menu, "记录");
			{
				this.makeNewMenuItem(recordMenu, ApplicationMain.this.createShipTable);
				this.makeNewMenuItem(recordMenu, ApplicationMain.this.createItemTable);
				this.makeNewMenuItem(recordMenu, ApplicationMain.this.missionResultTable);
				this.makeNewMenuItem(recordMenu, ApplicationMain.this.materialRecordTable);
				SwtUtils.makeSeparatorMenuItem(recordMenu);
				this.makeNewMenuItem(recordMenu, ApplicationMain.this.destroyShipTable);
				this.makeNewMenuItem(recordMenu, ApplicationMain.this.destroyItemTable);
				this.makeNewMenuItem(recordMenu, ApplicationMain.this.remodleRecordTable);
				SwtUtils.makeSeparatorMenuItem(recordMenu);
				this.makeNewMenuItem(recordMenu, ApplicationMain.this.battleListTable);
				this.makeNewMenuItem(recordMenu, ApplicationMain.this.dropListTable);
			}

			Menu calcuMenu = SwtUtils.makeCasacdeMenu(this.menu, "计算器");
			{
				this.makeNewMenuItem(calcuMenu, ApplicationMain.this.calcuExpTable);
				this.makeNewMenuItem(calcuMenu, ApplicationMain.this.calcuPracticeExpTable);
			}

			SwtUtils.makeSeparatorMenuItem(this.menu);
			this.makeNewMenuItem(this.menu, ApplicationMain.this.questListTable);
			SwtUtils.makeSeparatorMenuItem(this.menu);
			this.makeNewMenuItem(this.menu, ApplicationMain.this.battleWindow);
			this.makeNewMenuItem(this.menu, ApplicationMain.this.battleFlowWindow);
			this.makeNewMenuItem(this.menu, ApplicationMain.this.mapListTable);
			SwtUtils.makeSeparatorMenuItem(this.menu);
			this.makeNewMenuItem(this.menu, ApplicationMain.this.shipGroupTable);
			this.makeNewMenuItem(this.menu, ApplicationMain.this.configWindow);
			this.makeNewMenuItem(this.menu, ApplicationMain.this.windowOperationWindow);
			SwtUtils.makeSeparatorMenuItem(this.menu);
			this.makeNewMenuItem(this.menu, ApplicationMain.this.bookShipWindow);
			this.makeNewMenuItem(this.menu, ApplicationMain.this.bookItemWindow);
			SwtUtils.makeSeparatorMenuItem(this.menu);
			SwtUtils.makeMenuItem(this.menu, SWT.NONE, "退出", ApplicationMain.this.shell::close);
		}

		private MenuItem makeNewMenuItem(Menu parent, AbstractWindow window) {
			return SwtUtils.makeMenuItem(parent, SWT.NONE, window.defaultTitle(), window::displayWindow);
		}

		@Override
		public void menuDetected(MenuDetectEvent ev) {
			this.menu.setVisible(true);
		}
	}

	public class ShipItemButtonComposite extends Composite {
		public final Button shipList, itemList;

		ShipItemButtonComposite(Composite showContentComposite) {
			super(showContentComposite, SWT.NONE);
			this.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				this.shipList = new Button(this, SWT.PUSH);
				this.shipList.setText("舰娘(0/0)");
				this.shipList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				this.itemList = new Button(this, SWT.PUSH);
				this.itemList.setText("装备(0/0)");
				this.itemList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			}
		}
	}

	public class ResourceGroup extends Group {
		public final Label[] labels;

		ResourceGroup(Composite showContentComposite) {
			super(showContentComposite, SWT.NONE);
			this.setText("资源");
			this.setLayout(SwtUtils.makeGridLayout(4, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.labels = Arrays.stream(ResourceDto.getResourceTextForApplicationMain()).map(text -> {
				Label label = new Label(this, SWT.RIGHT);
				label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				label.setText("0");
				label.setToolTipText(text);
				return label;
			}).toArray(Label[]::new);
		}

		@Override
		protected void checkSubclass() {}
	}

	public class NameTimeGroup extends Group {
		public final NameTimeComposite[] nameTimeComposites;

		NameTimeGroup(Composite showContentComposite, String text) {
			super(showContentComposite, SWT.NONE);
			this.setText(text);
			this.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.nameTimeComposites = IntStream.range(0, 4)//
					.mapToObj(index -> new NameTimeComposite(String.format("----")))//
					.toArray(NameTimeComposite[]::new);
		}

		@Override
		protected void checkSubclass() {}

		public class NameTimeComposite extends Composite {
			public final Label nameLabel;
			public final Label timeLabel;

			NameTimeComposite(String nameLabelText) {
				super(NameTimeGroup.this, SWT.NONE);
				this.setLayout(SwtUtils.makeGridLayout(2, 4, 0, 0, 0));
				this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				this.nameLabel = new Label(this, SWT.LEFT);
				this.nameLabel.setText(nameLabelText);
				this.nameLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				this.timeLabel = new Label(this, SWT.RIGHT);
				this.timeLabel.setText("00时00分00秒");
			}
		}
	}

	public class AkashiTimerComposite extends Composite {
		public final Label timeLabel;

		AkashiTimerComposite(Composite showContentComposite) {
			super(showContentComposite, SWT.NONE);
			this.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0, 0, 0, 3, 3));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			new Label(this, SWT.LEFT).setText("泊地修理");

			this.timeLabel = new Label(this, SWT.RIGHT);
			this.timeLabel.setText("??秒");
			this.timeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
	}

	public class MessageList extends org.eclipse.swt.widgets.List {
		MessageList(Composite showContentComposite) {
			super(showContentComposite, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
			this.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (e.button == 3) {
						MessageList.this.deselectAll();
					}
				}
			});
		}

		@Override
		protected void checkSubclass() {}
	}
}
