package tdrz.gui.window.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import tdrz.core.config.AppConfig;
import tdrz.core.util.SwtUtils;
import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.gui.window.listener.WindowConfigChangedAdapter;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.main.MainStart;
import tdrz.gui.window.sup.WindowBase;

public class ConfigWindow extends WindowBase {
	private final TabFolder tabFolder;

	public ConfigWindow() {
		this.tabFolder = new TabFolder(this.centerComposite, SWT.TOP);
		this.tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button saveButton = new Button(this.centerComposite, SWT.PUSH);
		saveButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		saveButton.setText("保存");
		saveButton.addSelectionListener(new ControlSelectionListener(ev -> {
			//保存配置
			Arrays.stream(ConfigWindow.this.tabFolder.getItems())//
					.map(item -> (AbstractTabItemComposite) item.getControl())//
					.flatMap(atc -> atc.saveActions.stream())//
					.forEach(Runnable::run);
			//关闭窗口
			this.hiddenWindow();
			//重启Server
			Optional.ofNullable(MainStart.server).ifPresent(server -> {
				if (server.isConfigChanged()) {
					try {
						server.restart();
						ApplicationMain.main.printMessage("服务器配置变更成功", true);
					} catch (Exception ex) {
						ApplicationMain.main.printMessage("服务器配置变更失败", true);
					}
				}
			});
			//更新主窗口标题
			ApplicationMain.main.updateTitle();
		}));

		this.newTabItem("通讯", new ProxyComposite());
		this.newTabItem("窗口", new WindowComposite());
		this.newTabItem("其它", new OthersComposite());

		this.addWindowConfigChangedListener(new WindowConfigChangedAdapter() {
			@Override
			public void displayBefore() {
				//显示之前重置窗口中的配置,使之以 AppConfig 相同
				Arrays.stream(ConfigWindow.this.tabFolder.getItems())//
						.map(item -> (AbstractTabItemComposite) item.getControl())//
						.flatMap(atc -> atc.defaultActions.stream())//
						.forEach(Runnable::run);
			}
		});
	}

	private TabItem newTabItem(String name, Control control) {
		TabItem tabItem = new TabItem(this.tabFolder, SWT.NONE);
		tabItem.setText(name);
		tabItem.setControl(control);
		return tabItem;
	}

	@Override
	public String defaultTitle() {
		return "设置";
	}

	@Override
	public Point defaultSize() {
		return SwtUtils.DPIAwareSize(new Point(380, 400));
	}

	private abstract class AbstractTabItemComposite extends Composite {
		protected final List<Runnable> defaultActions = new ArrayList<>();
		protected final List<Runnable> saveActions = new ArrayList<>();

		public AbstractTabItemComposite() {
			super(ConfigWindow.this.tabFolder, SWT.NONE);
			this.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.setLayout(SwtUtils.makeGridLayout(1, 0, 4, 0, 0, 4, 4, 4, 4));
		}
	}

	private class ProxyComposite extends AbstractTabItemComposite {
		public ProxyComposite() {
			Group comuGroup = new Group(this, SWT.NONE);
			comuGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			comuGroup.setLayout(SwtUtils.makeGridLayout(1, 0, 4, 0, 0, 4, 4));
			comuGroup.setText("通讯");
			{
				Button saveJson = new Button(comuGroup, SWT.CHECK);
				saveJson.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				saveJson.setText("保存Api数据");
				this.defaultActions.add(() -> saveJson.setSelection(AppConfig.get().isSaveJson()));
				this.saveActions.add(() -> AppConfig.get().setSaveJson(saveJson.getSelection()));
			}

			Group proxyGroup = new Group(this, SWT.NONE);
			proxyGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			proxyGroup.setLayout(SwtUtils.makeGridLayout(2, 4, 4, 0, 0, 4, 4));
			proxyGroup.setText("代理");
			{
				new Label(proxyGroup, SWT.CENTER).setText("监听端口 : ");
				Spinner listenPort = new Spinner(proxyGroup, SWT.BORDER);
				listenPort.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				listenPort.setIncrement(1);
				listenPort.setMinimum(1);
				listenPort.setMaximum(65535);
				this.defaultActions.add(() -> listenPort.setSelection(AppConfig.get().getListenPort()));
				this.saveActions.add(() -> AppConfig.get().setListenPort(listenPort.getSelection()));

				Button useProxy = new Button(proxyGroup, SWT.CHECK);
				useProxy.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
				useProxy.setText("使用代理");
				this.defaultActions.add(() -> useProxy.setSelection(AppConfig.get().isUseProxy()));
				this.saveActions.add(() -> AppConfig.get().setUseProxy(useProxy.getSelection()));

				new Label(proxyGroup, SWT.CENTER).setText("代理主机 : ");
				Text proxyHost = new Text(proxyGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
				proxyHost.setLayoutData(SwtUtils.makeGridData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false), 150));
				this.defaultActions.add(() -> proxyHost.setText(AppConfig.get().getProxyHost()));
				this.saveActions.add(() -> AppConfig.get().setProxyHost(proxyHost.getText()));

				new Label(proxyGroup, SWT.CENTER).setText("代理端口 : ");
				Spinner proxyPort = new Spinner(proxyGroup, SWT.BORDER);
				proxyPort.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				proxyPort.setIncrement(1);
				proxyPort.setMinimum(1);
				proxyPort.setMaximum(65535);
				this.defaultActions.add(() -> proxyPort.setSelection(AppConfig.get().getProxyPort()));
				this.saveActions.add(() -> AppConfig.get().setProxyPort(proxyPort.getSelection()));
			}
		}
	}

	private class WindowComposite extends AbstractTabItemComposite {
		public WindowComposite() {
			Group mainWindow = new Group(this, SWT.NONE);
			mainWindow.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			mainWindow.setLayout(SwtUtils.makeGridLayout(1, 0, 4, 0, 0, 4, 4));
			mainWindow.setText("主窗口");
			{
				Button showNameOnTitle = new Button(mainWindow, SWT.CHECK);
				showNameOnTitle.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				showNameOnTitle.setText("标题栏显示提督名");
				this.defaultActions.add(() -> showNameOnTitle.setSelection(AppConfig.get().isShowNameOnTitle()));
				this.saveActions.add(() -> {
					AppConfig.get().setShowNameOnTitle(showNameOnTitle.getSelection());
				});

				Button checkDoit = new Button(mainWindow, SWT.CHECK);
				checkDoit.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				checkDoit.setText("退出时需确认");
				this.defaultActions.add(() -> checkDoit.setSelection(AppConfig.get().isCheckDoit()));
				this.saveActions.add(() -> AppConfig.get().setCheckDoit(checkDoit.getSelection()));
			}

			Group calcuExpWindow = new Group(this, SWT.NONE);
			calcuExpWindow.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			calcuExpWindow.setLayout(SwtUtils.makeGridLayout(2, 4, 4, 0, 0, 4, 4));
			calcuExpWindow.setText("经验计算");
			{
				Button notCalcuLv1 = new Button(calcuExpWindow, SWT.CHECK);
				notCalcuLv1.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
				notCalcuLv1.setText("不显示舰娘(Lv1)");
				this.defaultActions.add(() -> notCalcuLv1.setSelection(AppConfig.get().isNotCalcuExpForLevel1Ship()));
				this.saveActions.add(() -> AppConfig.get().setNotCalcuExpForLevel1Ship(notCalcuLv1.getSelection()));

				Button notCalcuLv99 = new Button(calcuExpWindow, SWT.CHECK);
				notCalcuLv99.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
				notCalcuLv99.setText("不显示舰娘(Lv99)");
				this.defaultActions.add(() -> notCalcuLv99.setSelection(AppConfig.get().isNotCalcuExpForLevel99Ship()));
				this.saveActions.add(() -> AppConfig.get().setNotCalcuExpForLevel99Ship(notCalcuLv99.getSelection()));

				Button notCalcuLv155 = new Button(calcuExpWindow, SWT.CHECK);
				notCalcuLv155.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
				notCalcuLv155.setText("不显示舰娘(Lv155)");
				this.defaultActions.add(() -> notCalcuLv155.setSelection(AppConfig.get().isNotCalcuExpForLevel165Ship()));
				this.saveActions.add(() -> AppConfig.get().setNotCalcuExpForLevel165Ship(notCalcuLv155.getSelection()));

				new Label(calcuExpWindow, SWT.CENTER).setText("计算海域 : ");
				Text calcuExpArea = new Text(calcuExpWindow, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
				calcuExpArea.setLayoutData(SwtUtils.makeGridData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false), 250));
				calcuExpArea.setToolTipText(String.join("\n", "格式例如 : ", "1-1,1-2,1-3,1-4,1-5", "只有12345海域的前五图(除了4-5),以及6-1,6-2"));
				this.defaultActions.add(() -> calcuExpArea.setText(AppConfig.get().getCalcuExpArea()));
				this.saveActions.add(() -> {
					String text = Arrays.stream(calcuExpArea.getText().split(",")).map(String::trim).reduce((a, b) -> String.join(",", a, b)).orElse("");
					AppConfig.get().setCalcuExpArea(text);
				});
			}

			Group battleWindow = new Group(this, SWT.NONE);
			battleWindow.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			battleWindow.setLayout(SwtUtils.makeGridLayout(1, 0, 4, 0, 0, 4, 4));
			battleWindow.setText("战斗");
			{
				Button autoUpdateBattleFlow = new Button(battleWindow, SWT.CHECK);
				autoUpdateBattleFlow.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				autoUpdateBattleFlow.setText("自动更新[战斗流程]窗口");
				this.defaultActions.add(() -> autoUpdateBattleFlow.setSelection(AppConfig.get().isAutoUpdateBattleFlow()));
				this.saveActions.add(() -> AppConfig.get().setAutoUpdateBattleFlow(autoUpdateBattleFlow.getSelection()));
			}
		}
	}

	private class OthersComposite extends AbstractTabItemComposite {
		public OthersComposite() {
			Group notify = new Group(this, SWT.NONE);
			notify.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			notify.setLayout(SwtUtils.makeGridLayout(1, 0, 4, 0, 0, 4, 4));
			notify.setText("提醒");
			{
				Button mission = new Button(notify, SWT.CHECK);
				mission.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				mission.setText("远征");
				this.defaultActions.add(() -> mission.setSelection(AppConfig.get().isNoticeDeckmission()));
				this.saveActions.add(() -> AppConfig.get().setNoticeDeckmission(mission.getSelection()));

				Button missionAgain = new Button(notify, SWT.CHECK);
				missionAgain.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				missionAgain.setText("远征再提醒");
				this.defaultActions.add(() -> missionAgain.setSelection(AppConfig.get().isNoticeDeckmissionAgain()));
				this.saveActions.add(() -> AppConfig.get().setNoticeDeckmissionAgain(missionAgain.getSelection()));

				Button ndock = new Button(notify, SWT.CHECK);
				ndock.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				ndock.setText("入渠");
				this.defaultActions.add(() -> ndock.setSelection(AppConfig.get().isNoticeNdock()));
				this.saveActions.add(() -> AppConfig.get().setNoticeNdock(ndock.getSelection()));

				Button akashiTimer = new Button(notify, SWT.CHECK);
				akashiTimer.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				akashiTimer.setText("泊地修理");
				this.defaultActions.add(() -> akashiTimer.setSelection(AppConfig.get().isNoticeAkashiTimer()));
				this.saveActions.add(() -> AppConfig.get().setNoticeAkashiTimer(akashiTimer.getSelection()));

				Button cond = new Button(notify, SWT.CHECK);
				cond.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				cond.setText("疲劳");
				this.defaultActions.add(() -> cond.setSelection(AppConfig.get().isNoticeCond()));
				this.saveActions.add(() -> AppConfig.get().setNoticeCond(cond.getSelection()));

				Button condOnlyMainFleet = new Button(notify, SWT.CHECK);
				condOnlyMainFleet.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
				condOnlyMainFleet.setText("疲劳(仅第一舰队)");
				this.defaultActions.add(() -> condOnlyMainFleet.setSelection(AppConfig.get().isNoticeCondOnlyMainFleet()));
				this.saveActions.add(() -> AppConfig.get().setNoticeCondOnlyMainFleet(condOnlyMainFleet.getSelection()));
			}
		}
	}
}
