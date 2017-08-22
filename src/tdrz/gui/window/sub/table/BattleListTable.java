package tdrz.gui.window.sub.table;

import java.util.List;

import tdrz.core.config.AppConstants;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.memory.battle.info.InfoBattleStartDto;

/**
 * 战斗记录
 * @author MoeKagari
 */
public class BattleListTable extends AbstractTable<BattleListTable.SortBattle> {
	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("时间", rd -> AppConstants.TABLE_TIME_FORMAT.format(rd.getTime())));
		tcms.add(new TableColumnManager("舰队", SortBattle::getFleet));
		tcms.add(new TableColumnManager("地图", SortBattle::getMap));
		tcms.add(new TableColumnManager("起点", true, SortBattle::getStart));
	}

	@Override
	public String defaultTitle() {
		return "战斗记录";
	}

	@Override
	protected void updateData(List<SortBattle> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof InfoBattleStartDto) {
				datas.add(new SortBattle((InfoBattleStartDto) memory));
			}
		});
	}

	protected class SortBattle {
		private final InfoBattleStartDto battle;

		public SortBattle(InfoBattleStartDto battle) {
			this.battle = battle;
		}

		public String getFleet() {
			if (this.battle.isCombined() && this.battle.getDeckId() == 1) {
				return "联合舰队";
			} else {
				return AppConstants.DEFAULT_FLEET_NAME[this.battle.getDeckId() - 1];
			}
		}

		public int getStart() {
			return this.battle.getStart();
		}

		public long getTime() {
			return this.battle.getTime();
		}

		public String getMap() {
			return this.battle.getMapString();
		}
	}
}
