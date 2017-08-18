package tdrz.gui.window.sub.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import tdrz.core.config.AppConfig;
import tdrz.core.translator.ShipDtoTranslator;
import tdrz.core.util.SwtUtils;
import tdrz.core.util.ToolUtils;
import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

public class CalcuExpTable extends CalcuTable<CalcuExpTable.CalcuExpData> {
	private final Button updateShipDataButton;
	private final Button secretaryButton;
	private final Button flagshipButton;
	private final Button mvpButton;
	private final Combo evalCombo;

	private final ScrolledComposite scrolledComposite;
	private final Composite shipCompositeList;

	private final Label gaizhaoLevelLabel;

	private final DefaultDataComposite defaultShip;
	private final List<ShipDataComposite> shipComposites = new ArrayList<>();
	private AbstractDataComposite selected;

	public CalcuExpTable(ApplicationMain main, String title) {
		super(main, title);

		Composite buttonComposite = new Composite(this.leftComposite, SWT.NONE);
		buttonComposite.setLayout(SwtUtils.makeGridLayout(3, 0, 0, 0, 0));
		buttonComposite.setLayoutData(SwtUtils.makeGridData(GridData.FILL_HORIZONTAL, 250));
		{
			this.updateShipDataButton = new Button(buttonComposite, SWT.PUSH);
			this.updateShipDataButton.setLayoutData(SwtUtils.makeGridData(0, 34));
			this.updateShipDataButton.setText("更新");
			this.updateShipDataButton.addSelectionListener(new ControlSelectionListener(ev -> {
				if (this.updateShipData()) {
					this.getUpdateTableListener().widgetSelected(ev);
				}
			}));

			this.secretaryButton = new Button(buttonComposite, SWT.NONE);
			this.secretaryButton.setLayoutData(SwtUtils.makeGridData(0, 50));
			this.secretaryButton.setText("秘书舰");
			this.secretaryButton.addSelectionListener(new ControlSelectionListener(ev -> {
				if (this.slectSecretaryShip()) {
					this.getUpdateTableListener().widgetSelected(ev);
				}
			}));

			Composite booleanComposite = new Composite(buttonComposite, SWT.NONE);
			booleanComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			booleanComposite.setLayout(SwtUtils.makeGridLayout(3, 0, 0, 0, 0));
			{
				this.flagshipButton = new Button(booleanComposite, SWT.CHECK);
				this.flagshipButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
				this.flagshipButton.setSelection(true);
				this.flagshipButton.setText("旗舰");
				this.flagshipButton.addSelectionListener(this.getUpdateTableListener());

				this.mvpButton = new Button(booleanComposite, SWT.CHECK);
				this.mvpButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
				this.mvpButton.setSelection(true);
				this.mvpButton.setText("MVP");
				this.mvpButton.addSelectionListener(this.getUpdateTableListener());

				this.evalCombo = new Combo(booleanComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
				this.evalCombo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
				this.evalCombo.setItems(ToolUtils.toStringArray(Eval.values(), eval -> eval.name));
				this.evalCombo.select(0);
				this.evalCombo.addSelectionListener(this.getUpdateTableListener());
			}
		}

		Composite gaizhaoLevelComposite = new Composite(this.leftComposite, SWT.BORDER);
		gaizhaoLevelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gaizhaoLevelComposite.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
		{
			new Label(gaizhaoLevelComposite, SWT.CENTER).setText("改造等级 : ");

			this.gaizhaoLevelLabel = new Label(gaizhaoLevelComposite, SWT.LEFT);
			this.gaizhaoLevelLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		this.defaultShip = new DefaultDataComposite();
		this.defaultShip.select();

		this.scrolledComposite = new ScrolledComposite(this.leftComposite, SWT.V_SCROLL);
		this.scrolledComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 4, 0, 0));
		this.scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.scrolledComposite.setExpandHorizontal(true);
		this.scrolledComposite.setExpandVertical(true);
		this.scrolledComposite.setAlwaysShowScrollBars(true);
		{
			this.shipCompositeList = new Composite(this.scrolledComposite, SWT.NONE);
			this.shipCompositeList.setLayout(SwtUtils.makeGridLayout(1, 2, 0, 0, 0));
			this.shipCompositeList.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.scrolledComposite.setContent(this.shipCompositeList);
		}
	}

	/**
	 * 更新左侧的data
	 * @return 是否重新更新table
	 */
	private boolean updateShipData() {
		List<ShipDto> datas = new ArrayList<>(GlobalContext.getShipMap().values());
		Predicate<ShipDto> dataFilter = data -> false;
		if (AppConfig.get().isNotCalcuExpForLevel1Ship()) {
			dataFilter.or(data -> data.getLevel() == 1);
		}
		if (AppConfig.get().isNotCalcuExpForLevel99Ship()) {
			dataFilter.or(data -> data.getLevel() == 99);
		}
		if (AppConfig.get().isNotCalcuExpForLevel165Ship()) {
			dataFilter.or(data -> data.getLevel() == 165);
		}
		datas.removeIf(dataFilter);
		datas.sort(Comparator.comparingInt(ShipDto::getLevel).reversed());//等级从大到小

		this.shipComposites.forEach(Composite::dispose);
		this.shipComposites.clear();
		FunctionUtils.toListUseIndex(datas, (index, data) -> new ShipDataComposite(index + 1, data)).forEach(this.shipComposites::add);

		//layout
		this.scrolledComposite.setMinSize(this.shipCompositeList.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.shipCompositeList.layout();
		this.scrolledComposite.layout();

		//先前为默认选择,则返回false
		if (this.selected == this.defaultShip) {
			return false;
		}

		int shipID = this.selected.getShipId();
		int currentExp = this.selected.getCurrentExp();

		Optional<ShipDataComposite> op = this.shipComposites.stream().filter(shipComposite -> shipComposite.shipId == shipID).findFirst();
		if (op.isPresent()) {
			//先前选择的舰娘被除籍,则不会有true
			ShipDataComposite shipComposite = op.get();
			shipComposite.select();
			return shipComposite.currentExp != currentExp;//舰娘的经验是否发生了变化
		} else {
			//未发现与先前选择一样的,则默认选择,并返回true
			this.defaultShip.select();
			return true;
		}
	}

	private boolean slectSecretaryShip() {
		ShipDto secretaryShip = GlobalContext.getSecretaryShip();
		if (secretaryShip != null) {
			if (this.selected.getShipId() == secretaryShip.getId()) {
				return false;
			}

			for (ShipDataComposite shipComposite : this.shipComposites) {
				if (shipComposite.getShipId() == secretaryShip.getId()) {
					this.selected.notSelect();
					shipComposite.select();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean haveLeftComposite() {
		return true;
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("目标等级", rd -> rd.targetLevel));
		tcms.add(new TableColumnManager("升级所需", rd -> {
			if (rd.targetIsCurrent()) {
				return String.format("当前经验:%d", rd.currentExp);
			} else {
				return String.valueOf(rd.needExp());
			}
		}));
		FunctionUtils.toMapForEach(//
				Arrays.stream(AppConfig.get().getCalcuExpArea().split(",")).filter(SeaExp.SEAEXPMAP::containsKey),//
				FunctionUtils::returnSelf, SeaExp.SEAEXPMAP::get, //
				(sea, exp) -> tcms.add(new TableColumnManager(sea, rd -> {
					int baseExp = this.calcuBaseExp(exp);
					if (rd.targetIsCurrent()) return String.format("基础经验:%d", baseExp);
					int needExp = rd.needExp();
					int count = (needExp / baseExp) + ((needExp % baseExp) != 0 ? 1 : 0);
					return String.valueOf(count);
				}))//
		);
	}

	@Override
	protected void updateData(List<CalcuExpData> datas) {
		IntStream.rangeClosed(this.selected.getCurrentLevel(), this.selected.getMaxLevel())//
				.mapToObj(targetLevel -> new CalcuExpData(this.selected.getCurrentLevel(), this.selected.getCurrentExp(), targetLevel))//
				.forEach(datas::add);
	}

	private int calcuBaseExp(int seaExp) {
		double exp = seaExp;

		if (this.flagshipButton.getSelection()) exp *= 1.5;
		if (this.mvpButton.getSelection()) exp *= 2;
		exp *= Eval.EVALMAP.get(this.evalCombo.getText()).value;

		return (int) Math.floor(exp);
	}

	private abstract class AbstractDataComposite extends Composite {
		private final Button check;

		AbstractDataComposite(Composite parent, String text) {
			super(parent, SWT.BORDER);
			this.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.check = new Button(this, SWT.RADIO | SWT.LEFT);
			this.check.setLayoutData(SwtUtils.makeGridData(0, 40));
			this.check.setText(text);
			this.check.addSelectionListener(new ControlSelectionListener(ev -> {
				if (CalcuExpTable.this.selected != this) {
					//点击另外一个单元
					CalcuExpTable.this.selected.notSelect();//取消旧的选择的单元
					this.select();
					CalcuExpTable.this.getUpdateTableListener().widgetSelected(ev);//并更新table
				}
			}));
		}

		public final void select() {
			String text = FunctionUtils.ifFunction(this.getGaizhaoLevel(), gaizhaoLevel -> gaizhaoLevel != 0, String::valueOf, "");
			CalcuExpTable.this.gaizhaoLevelLabel.setText(text);
			this.check.setSelection(true);
			CalcuExpTable.this.selected = this;
		}

		public final void notSelect() {
			this.check.setSelection(false);
		}

		public final boolean getSelection() {
			return this.check.getSelection();
		}

		public final int getMaxLevel() {
			return ShipExp.EXPMAP.size();
		}

		public abstract int getCurrentLevel();

		public abstract int getCurrentExp();

		public abstract int getShipId();

		public abstract int getGaizhaoLevel();
	}

	private class DefaultDataComposite extends AbstractDataComposite {
		private final Spinner spinner;

		DefaultDataComposite() {
			super(CalcuExpTable.this.leftComposite, "默认");

			this.spinner = new Spinner(this, SWT.LEFT);
			this.spinner.setLayoutData(SwtUtils.makeGridData(new GridData(SWT.CENTER, SWT.CENTER, true, true), 40));
			this.spinner.setMinimum(1);
			this.spinner.setMaximum(this.getMaxLevel());
			this.spinner.addSelectionListener(new ControlSelectionListener(ev -> {
				if (this.getSelection()) {
					CalcuExpTable.this.getUpdateTableListener().widgetSelected(ev);
				}
			}));
		}

		@Override
		public int getCurrentLevel() {
			return this.spinner.getSelection();
		}

		@Override
		public int getCurrentExp() {
			return ShipExp.EXPMAP.get(this.getCurrentLevel());
		}

		@Override
		public int getShipId() {
			return -1;
		}

		@Override
		public int getGaizhaoLevel() {
			return 0;
		}
	}

	private class ShipDataComposite extends AbstractDataComposite {
		private final int shipId;
		private final int currentLevel;
		private final int currentExp;
		private final int gaizhaoLevel;

		private final Label levelLabel;
		private final Label nameLabel;

		ShipDataComposite(int index, ShipDto ship) {
			super(CalcuExpTable.this.shipCompositeList, String.valueOf(index));

			Composite labelComposite = new Composite(this, SWT.NONE);
			labelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			labelComposite.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			{
				this.levelLabel = new Label(labelComposite, SWT.LEFT);
				this.levelLabel.setLayoutData(SwtUtils.makeGridData(0, 42));
				this.levelLabel.setText(String.format("Lv.%d", ship.getLevel()));

				this.nameLabel = new Label(labelComposite, SWT.LEFT);
				this.nameLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				this.nameLabel.setText(ShipDtoTranslator.getName(ship));
			}

			this.shipId = ship.getId();
			this.currentLevel = ship.getLevel();
			this.currentExp = ship.getCurrentExp();
			this.gaizhaoLevel = FunctionUtils.notNull(ship.getMasterData(), md -> md.getGaizhaoLv(), 0);
		}

		@Override
		public int getCurrentLevel() {
			return this.currentLevel;
		}

		@Override
		public int getCurrentExp() {
			return this.currentExp;
		}

		@Override
		public int getShipId() {
			return this.shipId;
		}

		@Override
		public int getGaizhaoLevel() {
			return this.gaizhaoLevel;
		}
	}

	protected class CalcuExpData {
		private final int currentLevel;
		private final int currentExp;
		private final int targetLevel;
		private final int targetExp;

		public CalcuExpData(int currentLevel, int currentExp, int targetLevel) {
			this.currentLevel = currentLevel;
			this.currentExp = currentExp;
			this.targetLevel = targetLevel;
			this.targetExp = ShipExp.EXPMAP.get(targetLevel);
		}

		public boolean targetIsCurrent() {
			return this.targetLevel == this.currentLevel;
		}

		public int needExp() {
			return this.targetExp - this.currentExp;
		}
	}

	private static class SeaExp {
		private final static Map<String, Integer> SEAEXPMAP = new LinkedHashMap<>();

		static {
			SEAEXPMAP.put("1-1", 30);
			SEAEXPMAP.put("1-2", 50);
			SEAEXPMAP.put("1-3", 80);
			SEAEXPMAP.put("1-4", 100);
			SEAEXPMAP.put("1-5", 150);
			SEAEXPMAP.put("2-1", 120);
			SEAEXPMAP.put("2-2", 150);
			SEAEXPMAP.put("2-3", 200);
			SEAEXPMAP.put("2-4", 300);
			SEAEXPMAP.put("2-5", 250);
			SEAEXPMAP.put("3-1", 310);
			SEAEXPMAP.put("3-2", 320);
			SEAEXPMAP.put("3-3", 330);
			SEAEXPMAP.put("3-4", 350);
			SEAEXPMAP.put("3-5", 400);
			SEAEXPMAP.put("4-1", 310);
			SEAEXPMAP.put("4-2", 320);
			SEAEXPMAP.put("4-3", 330);
			SEAEXPMAP.put("4-4", 340);
			SEAEXPMAP.put("5-1", 360);
			SEAEXPMAP.put("5-2", 380);
			SEAEXPMAP.put("5-3", 400);
			SEAEXPMAP.put("5-4", 420);
			SEAEXPMAP.put("5-5", 450);
			SEAEXPMAP.put("6-1", 380);
			SEAEXPMAP.put("6-2", 420);
		}
	}
}
