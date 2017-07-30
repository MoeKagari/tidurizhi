package tdrz.gui.window.sub.table;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;

import tdrz.dto.word.PracticeEnemyDto;
import tdrz.dto.word.PracticeEnemyDto.PracticeEnemyShip;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.GlobalContext;
import tdrz.update.data.DataType;
import tdrz.utils.SwtUtils;
import tool.FunctionUtils;

public class CalcuPracticeExpTable extends CalcuTable<CalcuPracticeExpTable.CalcuPracticeExpData> {
	private final Label[] shipNameLabels;

	public CalcuPracticeExpTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);

		this.shipNameLabels = IntStream.range(0, 6).mapToObj(i -> "aaaaaaaaaaaaaaaaaa")//
				.map(name -> SwtUtils.setText(new Label(this.getLeftComposite(), SWT.CENTER), name))//
				.toArray(Label[]::new);
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
	protected boolean needUpdate(DataType type) {
		return type == DataType.PRACTICE_ENEMYINFO;
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("", rd -> rd.fm.name));
		Arrays.stream(Eval.values()).forEach(eval -> {
			tcms.add(new TableColumnManager(eval.name, rd -> rd.calcu(eval)));
		});
	}

	@Override
	protected void updateData(List<CalcuPracticeExpData> datas) {
		PracticeEnemyShip[] ships = FunctionUtils.notNull(GlobalContext.getPracticeEnemy(), PracticeEnemyDto::getShips, null);

		int[] lvs = IntStream.range(0, 2).mapToObj(i -> ships != null ? ships[i] : null)//
				.mapToInt(ship -> (ship != null && ship.exist()) ? ship.getLv() : 1).toArray();
		Arrays.stream(FlagshipMVP.values()).map(fm -> new CalcuPracticeExpData(lvs[0], lvs[1], fm)).forEach(datas::add);

		Object[] names = IntStream.range(0, 6).mapToObj(i -> ships != null ? ships[i] : null)//
				.map(ship -> (ship != null && ship.exist()) ? String.format("%s(Lv.%d)", ship.getName(), ship.getLv()) : "").toArray();
		FunctionUtils.forEach(this.shipNameLabels, names, (shipNameLabel, name) -> shipNameLabel.setText(name.toString()));
		SwtUtils.layoutCompositeRecursively(this.getLeftComposite());
	}

	protected class CalcuPracticeExpData {
		private final int lv1;
		private final int lv2;
		private final FlagshipMVP fm;

		public CalcuPracticeExpData(int lv1, int lv2, FlagshipMVP fm) {
			this.lv1 = lv1;
			this.lv2 = lv2;
			this.fm = fm;
		}

		private int calcu(Eval eval) {
			double exp = Math.floor(ShipExp.getExp(this.lv1) / 100.0 + ShipExp.getExp(this.lv2) / 300.0);

			if (exp > 500) exp = Math.floor(500 + Math.sqrt(exp - 500));
			exp = Math.floor(exp * eval.value);
			if (this.fm.isFlagship) exp *= 1.5;
			if (this.fm.isMVP) exp *= 2;

			return (int) Math.floor(exp);
		}
	}
}
