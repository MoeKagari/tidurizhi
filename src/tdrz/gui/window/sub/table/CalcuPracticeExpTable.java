package tdrz.gui.window.sub.table;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;

import tdrz.dto.word.PracticeEnemyDto;
import tdrz.dto.word.PracticeEnemyDto.PracticeEnemyShip;
import tdrz.gui.window.listener.ControlSelectionListener;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.GlobalContext;
import tdrz.update.data.DataType;
import tdrz.utils.SwtUtils;
import tool.FunctionUtils;

public class CalcuPracticeExpTable extends CalcuTable<CalcuPracticeExpTable.CalcuPracticeExpData> {
	private final Spinner levelSpinner0, levelSpinner1;
	private final PracticeShipComposite[] shipComposites;
	private boolean notUpdateShipComposite = false;

	public CalcuPracticeExpTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		Composite contentComposite = new Composite(this.getLeftComposite(), SWT.NONE);
		contentComposite.setLayoutData(SwtUtils.makeGridData(new GridData(SWT.FILL, SWT.CENTER, true, true), 175));
		contentComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		{
			Composite mainLevelComposite = new Composite(contentComposite, SWT.NONE);
			mainLevelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			mainLevelComposite.setLayout(SwtUtils.makeGridLayout(2, 0, 0, 0, 0));
			{
				this.levelSpinner0 = new Spinner(mainLevelComposite, SWT.LEFT);
				this.levelSpinner0.setLayoutData(SwtUtils.makeGridData(new GridData(SWT.CENTER, SWT.CENTER, true, true), 40));
				this.levelSpinner0.setMinimum(1);
				this.levelSpinner0.setMaximum(ShipExp.EXPMAP.size());
				this.levelSpinner0.addSelectionListener(new ControlSelectionListener(ev -> {
					this.notUpdateShipComposite = true;
					this.getUpdateTableListener().widgetSelected(ev);
				}));

				this.levelSpinner1 = new Spinner(mainLevelComposite, SWT.LEFT);
				this.levelSpinner1.setLayoutData(SwtUtils.makeGridData(new GridData(SWT.CENTER, SWT.CENTER, true, true), 40));
				this.levelSpinner1.setMinimum(1);
				this.levelSpinner1.setMaximum(ShipExp.EXPMAP.size());
				this.levelSpinner1.addSelectionListener(new ControlSelectionListener(ev -> {
					this.notUpdateShipComposite = true;
					this.getUpdateTableListener().widgetSelected(ev);
				}));
			}

			this.shipComposites = IntStream.range(0, 6).mapToObj(index -> new PracticeShipComposite(contentComposite, index)).toArray(PracticeShipComposite[]::new);
		}
	}

	@Override
	protected boolean haveLeftComposite() {
		return true;
	}

	@Override
	protected boolean canMaxSize() {
		return false;
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return type == DataType.PRACTICE_ENEMYINFO;
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("", rd -> rd.fm.name));
		Arrays.stream(Eval.values()).map(eval -> new TableColumnManager(eval.name, rd -> rd.calcu(eval))).forEach(tcms::add);
	}

	@Override
	protected void updateData(List<CalcuPracticeExpData> datas) {
		int lv0, lv1;

		if (this.notUpdateShipComposite) {
			lv0 = this.levelSpinner0.getSelection();
			lv1 = this.levelSpinner1.getSelection();
			this.notUpdateShipComposite = false;
		} else {
			PracticeEnemyShip[] ships = FunctionUtils.notNull(GlobalContext.getPracticeEnemy(), PracticeEnemyDto::getShips, null);
			if (ships == null) {
				FunctionUtils.forEach(this.shipComposites, shipComposite -> shipComposite.update(null));

				lv0 = this.levelSpinner0.getSelection();
				lv1 = this.levelSpinner1.getSelection();
			} else {
				FunctionUtils.forEach(this.shipComposites, ships, PracticeShipComposite::update);

				lv0 = this.shipComposites[0].level;
				lv1 = this.shipComposites[1].level;

				this.levelSpinner0.setSelection(lv0);
				this.levelSpinner1.setSelection(lv1);
			}
		}

		Arrays.stream(FlagshipMVP.values()).map(fm -> new CalcuPracticeExpData(lv0, lv1, fm)).forEach(datas::add);
	}

	private class PracticeShipComposite extends Composite {
		private final int index;
		private int level;
		private String name;

		private final Label levelLabel;
		private final Label nameLabel;

		public PracticeShipComposite(Composite parent, int index) {
			super(parent, SWT.NONE);
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.setLayout(SwtUtils.makeGridLayout(3, 0, 0, 0, 0));

			this.index = index;
			this.level = 100;
			this.name = "new ship";

			Label indexLabel = new Label(this, SWT.LEFT);
			indexLabel.setLayoutData(SwtUtils.makeGridData(0, 40));
			indexLabel.setText(String.format("No.%d", this.index + 1));

			this.levelLabel = new Label(this, SWT.LEFT);
			this.levelLabel.setLayoutData(SwtUtils.makeGridData(0, 42));
			this.levelLabel.setText(String.format("Lv.%d", this.level));

			this.nameLabel = new Label(this, SWT.LEFT);
			this.nameLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.nameLabel.setText(this.name);
		}

		public void update(PracticeEnemyShip ship) {
			if (ship != null) {
				this.level = ship.getLv();
				this.name = ship.getName();
			} else {
				this.level = 1;
				this.name = "";
			}

			this.levelLabel.setText(String.format("Lv.%d", this.level));
			this.nameLabel.setText(this.name);
			this.nameLabel.setToolTipText(this.name);
		}
	}

	private enum FlagshipMVP {
		TRUE_TRUE("旗舰&MVP", true, true),
		FALSE_TRUE("MVP", false, true),
		TRUE_FALSE("旗舰", true, false),
		FALSE_FALSE("基本经验", false, false);

		protected final String name;
		protected final boolean isFlagship;
		protected final boolean isMVP;

		private FlagshipMVP(String name, boolean isFlagship, boolean isMVP) {
			this.name = name;
			this.isFlagship = isFlagship;
			this.isMVP = isMVP;
		}
	}

	protected class CalcuPracticeExpData {
		private final int lv0;
		private final int lv1;
		private final FlagshipMVP fm;

		public CalcuPracticeExpData(int lv0, int lv1, FlagshipMVP fm) {
			this.lv0 = lv0;
			this.lv1 = lv1;
			this.fm = fm;
		}

		private int calcu(Eval eval) {
			double exp = Math.floor(ShipExp.EXPMAP.get(this.lv0) / 100.0 + ShipExp.EXPMAP.get(this.lv1) / 300.0);

			if (exp > 500) exp = Math.floor(500 + Math.sqrt(exp - 500));
			exp = Math.floor(exp * eval.value);
			if (this.fm.isFlagship) exp *= 1.5;
			if (this.fm.isMVP) exp *= 2;

			return (int) Math.floor(exp);
		}
	}
}
