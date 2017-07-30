package tdrz.gui.window.sub.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;

import tdrz.config.AppConfig;
import tdrz.dto.translator.ShipDtoTranslator;
import tdrz.dto.word.ShipDto;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.GlobalContext;
import tdrz.utils.SwtUtils;
import tdrz.utils.ToolUtils;

public class CalcuExpTable extends CalcuTable<CalcuExpTable.CalcuExpData> {
	private final ScrolledComposite scrolledComposite;

	private final Button updateShipDataButton;
	private final Button secretaryButton;
	private final Button flagshipButton;
	private final Button mvpButton;
	private final Combo evalCombo;

	private final Composite shipsListComposite;

	private final NullDataComposite defaultShip;
	private final List<ShipDataComposite> ships = new ArrayList<>();

	public CalcuExpTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		Composite buttonComposite = new Composite(this.getLeftComposite(), SWT.NONE);
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

		this.defaultShip = new NullDataComposite(this.getLeftComposite());
		this.defaultShip.check.setSelection(true);

		this.scrolledComposite = new ScrolledComposite(this.getLeftComposite(), SWT.V_SCROLL);
		this.scrolledComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 4, 0, 0));
		this.scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.scrolledComposite.setExpandHorizontal(true);
		this.scrolledComposite.setExpandVertical(true);
		this.scrolledComposite.setAlwaysShowScrollBars(true);

		this.shipsListComposite = new Composite(this.scrolledComposite, SWT.NONE);
		this.shipsListComposite.setLayout(SwtUtils.makeGridLayout(1, 2, 0, 0, 0));
		this.shipsListComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.scrolledComposite.setContent(this.shipsListComposite);
	}

	/**
	 * 更新左侧的data
	 * @return 是否重新更新table
	 */
	private boolean updateShipData() {
		boolean defaultSlected = true;
		int shipID = -1;
		int currentExp = -1;
		AbstractDataComposite slected = this.getSlected();
		if (slected != this.defaultShip) {
			ShipDataComposite sdc = (ShipDataComposite) slected;
			defaultSlected = false;
			shipID = sdc.shipID;
			currentExp = sdc.currentExp;
		}

		this.ships.forEach(Composite::dispose);
		this.ships.clear();

		List<ShipDto> datas = new ArrayList<>(GlobalContext.getShipMap().values());
		if (AppConfig.get().isNotCalcuExpForLevel1Ship()) {
			datas.removeIf(data -> data.getLevel() == 1);
		}
		if (AppConfig.get().isNotCalcuExpForLevel99Ship()) {
			datas.removeIf(data -> data.getLevel() == 99);
		}
		if (AppConfig.get().isNotCalcuExpForLevel155Ship()) {
			datas.removeIf(data -> data.getLevel() == 155);
		}
		Collections.sort(datas, (a, b) -> Integer.compare(b.getLevel(), a.getLevel()));//等级从大到小

		for (int i = 0; i < datas.size(); i++) {
			this.ships.add(new ShipDataComposite(this.shipsListComposite, i + 1, datas.get(i)));
		}

		//layout
		this.scrolledComposite.setMinSize(this.shipsListComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.shipsListComposite.layout();
		this.scrolledComposite.layout();

		if (defaultSlected) {//先前为默认选择,则返回false
			return false;
		}

		for (ShipDataComposite ship : this.ships) {
			if (ship.shipID == shipID) {//先前选择的舰娘被除籍,则不会有true
				ship.check.setSelection(true);
				return ship.currentExp != currentExp;//舰娘的经验是否发生了变化
			}
		}

		//未发现与先前选择一样的,则默认选择,并返回true
		this.defaultShip.check.setSelection(true);
		return true;
	}

	private boolean slectSecretaryShip() {
		ShipDto secretaryShip = GlobalContext.getSecretaryShip();
		if (secretaryShip != null) {
			for (ShipDataComposite adc : this.ships) {
				if (adc.shipID == secretaryShip.getId()) {
					return this.slect(adc);
				}
			}
		}
		return false;
	}

	private boolean slect(AbstractDataComposite adc) {
		AbstractDataComposite slected = this.getSlected();

		this.defaultShip.check.setSelection(false);
		this.ships.forEach(ship -> ship.check.setSelection(false));
		adc.check.setSelection(true);

		return slected != adc;
	}

	private AbstractDataComposite getSlected() {
		if (this.defaultShip.check.getSelection()) {
			return this.defaultShip;
		} else {
			return this.ships.stream().filter(ship -> ship.check.getSelection()).findFirst().get();
		}
	}

	@Override
	protected boolean haveLeftComposite() {
		return true;
	}

	@Override
	protected boolean disposeAndUpdate() {
		return false;
	}

	@Override
	protected boolean isTableSortable() {
		return false;
	}

	@Override
	protected boolean haveNo() {
		return false;
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("目标等级", rd -> rd.targetLevel));
		tcms.add(new TableColumnManager("升级所需", rd -> rd.targetExp - rd.currentExp));
		SeaExp.SEAEXPMAP.forEach((sea, exp) -> tcms.add(new TableColumnManager(sea, rd -> {
			int baseExp = this.calcuBaseExp(exp, this.flagshipButton.getSelection(), this.mvpButton.getSelection(), Eval.EVALMAP.get(this.evalCombo.getText()).value);
			int needExp = rd.targetExp - rd.currentExp;
			int count = (needExp / baseExp) + ((needExp % baseExp) != 0 ? 1 : 0);
			return Arrays.toString(new int[] { baseExp, count });
		})));
	}

	@Override
	protected void updateData(List<CalcuExpData> datas) {
		AbstractDataComposite adc = this.getSlected();
		IntStream.rangeClosed(adc.getLevel() + 1, adc.getMaxLevel()).mapToObj(targetLevel -> new CalcuExpData(adc.getCurrentExp(), targetLevel)).forEach(datas::add);
	}

	private int calcuBaseExp(int seaExp, boolean flagship, boolean mvp, double eval) {
		double exp = seaExp;

		if (flagship) exp *= 1.5;
		if (mvp) exp *= 2;
		exp *= eval;

		return (int) Math.floor(exp);
	}

	private abstract class AbstractDataComposite extends Composite {
		protected final Button check;

		public AbstractDataComposite(Composite parent, String text) {
			super(parent, SWT.BORDER);
			this.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.check = new Button(this, SWT.CHECK | SWT.LEFT);
			this.check.setLayoutData(SwtUtils.makeGridData(0, 40));
			this.check.setText(text);
			this.check.addSelectionListener(new ControlSelectionListener(ev -> {
				if (CalcuExpTable.this.slect(this)) {
					CalcuExpTable.this.getUpdateTableListener().widgetSelected(ev);
				}
			}));
		}

		public int getMaxLevel() {
			return ShipExp.getMaxLevel();
		}

		public abstract int getLevel();

		public abstract int getCurrentExp();
	}

	private class NullDataComposite extends AbstractDataComposite {
		protected final Spinner spinner;

		public NullDataComposite(Composite parent) {
			super(parent, "默认");

			this.spinner = new Spinner(this, SWT.LEFT);
			this.spinner.setLayoutData(SwtUtils.makeGridData(new GridData(SWT.CENTER, SWT.CENTER, true, true), 40));
			this.spinner.setMinimum(1);
			this.spinner.setMaximum(this.getMaxLevel());
			this.spinner.addSelectionListener(new ControlSelectionListener(ev -> {
				if (this.check.getSelection()) {
					CalcuExpTable.this.getUpdateTableListener().widgetSelected(ev);
				}
			}));
		}

		@Override
		public int getLevel() {
			return this.spinner.getSelection();
		}

		@Override
		public int getCurrentExp() {
			return ShipExp.getExp(this.getLevel());
		}
	}

	private class ShipDataComposite extends AbstractDataComposite {
		private final int shipID;
		private final int level;
		private final int currentExp;

		private final Label levelLabel;
		private final Label nameLabel;

		public ShipDataComposite(Composite parent, int index, ShipDto ship) {
			super(parent, String.valueOf(index));

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

			this.shipID = ship.getId();
			this.level = ship.getLevel();
			this.currentExp = ship.getCurrentExp();
		}

		@Override
		public int getLevel() {
			return this.level;
		}

		@Override
		public int getCurrentExp() {
			return this.currentExp;
		}
	}

	protected class CalcuExpData {
		private final int currentExp;
		private final int targetLevel;
		private final int targetExp;

		public CalcuExpData(int currentExp, int targetLevel) {
			this.currentExp = currentExp;
			this.targetLevel = targetLevel;
			this.targetExp = ShipExp.getExp(targetLevel);
		}
	}
}
