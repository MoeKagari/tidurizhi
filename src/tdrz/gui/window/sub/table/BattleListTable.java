package tdrz.gui.window.sub.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;

import tdrz.core.config.AppConstants;
import tdrz.core.util.SwtUtils;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.gui.window.sub.BattleFlowWindow;
import tdrz.gui.window.sub.BattleWindow;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.DataType;
import tdrz.update.dto.memory.battle.AbstractInfoBattleStartNext;
import tdrz.update.dto.memory.battle.BattleDto;
import tdrz.update.dto.memory.battle.info.InfoBattleStartDto;
import tool.function.FunctionUtils;

/**
 * 出击记录
 * @author MoeKagari
 */
public class BattleListTable extends AbstractTable<BattleListTable.SortBattle> {
	private final BattleWindow battleWindow;

	public BattleListTable() {
		BattleFlowWindow bfw = new BattleFlowWindow() {
			@Override
			public String defaultTitle() {
				return super.defaultTitle() + " for 出击记录";
			}

			@Override
			public String getWindowConfigKey() {
				return super.getWindowConfigKey() + "ForBattleListTable";
			}
		};

		this.battleWindow = new BattleWindow(bfw) {
			@Override
			public String defaultTitle() {
				return super.defaultTitle() + " for 出击记录";
			}

			@Override
			public String getWindowConfigKey() {
				return super.getWindowConfigKey() + "ForBattleListTable";
			}

			@Override
			public void update(DataType type) {}
		};

		SwtUtils.layoutRecursively(bfw.mainComposite);
		SwtUtils.layoutRecursively(this.battleWindow.mainComposite);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("时间", rd -> AppConstants.TABLE_TIME_FORMAT.format(rd.getTime())));
		tcms.add(new TableColumnManager("舰队", SortBattle::getFleet));
		tcms.add(new TableColumnManager("地图", SortBattle::getMap));
		tcms.add(new TableColumnManager("起点", true, SortBattle::getStart));
		tcms.add(new TableColumnManager("道中撤退", true, rd -> {
			return rd.getBattleStream().filter(battle -> battle instanceof AbstractInfoBattleStartNext)//
					.map(battle -> (AbstractInfoBattleStartNext) battle)//
					.anyMatch(AbstractInfoBattleStartNext::isGoal) ? "" : "是";
		}));
	}

	@Override
	public String defaultTitle() {
		return "出击记录";
	}

	@Override
	protected void updateData(List<SortBattle> datas) {
		Iterator<BattleDto> iter = GlobalContext.getMemorylist().memorys.stream()//
				.filter(memory -> memory instanceof BattleDto)//
				.map(memory -> (BattleDto) memory)//
				.sorted(Comparator.comparingLong(BattleDto::getTime)).iterator();

		SortBattle data = null;
		while (iter.hasNext()) {
			BattleDto battle = iter.next();
			if (battle instanceof InfoBattleStartDto) {
				FunctionUtils.notNull(data, datas::add);
				data = new SortBattle((InfoBattleStartDto) battle);
			} else {
				if (data != null) {
					data.battles.add(battle);
				}
			}
		}
		FunctionUtils.notNull(data, datas::add);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mouseDoubleClickEvent(Event ev, TableItem[] items) {
		if (ev.button == 1) {
			if (items.length > 0) {
				this.battleWindow.clearWindow();
				SortBattle data = ((DataTableItem) items[0]).data;
				data.getBattleStream().forEach(this.battleWindow::addNewBattle);
				this.battleWindow.displayWindow();
			}
		}
	}

	protected class SortBattle {
		private final InfoBattleStartDto start;
		private final List<BattleDto> battles = new ArrayList<>();

		public SortBattle(InfoBattleStartDto battle) {
			this.start = battle;
		}

		private Stream<BattleDto> getBattleStream() {
			return Stream.concat(Stream.of(this.start), this.battles.stream());
		}

		public String getFleet() {
			if (this.start.isCombined() && this.start.getDeckId() == 1) {
				return "联合舰队";
			} else {
				return AppConstants.DEFAULT_FLEET_NAME[this.start.getDeckId() - 1];
			}
		}

		public int getStart() {
			return this.start.getStart();
		}

		public long getTime() {
			return this.start.getTime();
		}

		public String getMap() {
			return this.start.getMapString();
		}
	}
}
