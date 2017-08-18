package tdrz.gui.window.sub.table;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import tdrz.core.config.AppConstants;
import tdrz.core.translator.BattleDtoTranslator;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.AbstractMemory;
import tdrz.update.dto.memory.battle.AbstractBattle;
import tdrz.update.dto.memory.battle.AbstractInfoBattleResult;
import tdrz.update.dto.memory.battle.AbstractInfoBattleResult.BattleResult_GetShip;
import tdrz.update.dto.memory.battle.AbstractInfoBattleStartNext;
import tdrz.update.dto.memory.battle.BattleDto;
import tdrz.update.dto.memory.battle.info.InfoBattleResultDto;
import tdrz.update.dto.memory.battle.info.InfoBattleStartAirBaseDto;
import tool.function.FunctionUtils;

/**
 * 掉落记录
 * @author MoeKagari
 */
public class DropListTable extends AbstractTable<DropListTable.SortDrop> {
	public DropListTable(ApplicationMain main, String title) {
		super(main, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> AppConstants.TABLE_TIME_FORMAT.format(rd.time)));
		{
			TableColumnManager tcm = new TableColumnManager("地图", rd -> String.format("%s(%s)", rd.battleResult.getQuestName(), rd.battleStartNext.getMapString()));
			tcm.setComparator((a, b) -> {
				int res = Integer.compare(a.battleStartNext.getMapareaId(), b.battleStartNext.getMapareaId());
				if (res == 0) {
					res = Integer.compare(a.battleStartNext.getMapareaNo(), b.battleStartNext.getMapareaNo());
				}
				return res;
			});
			tcms.add(tcm);
		}
		tcms.add(new TableColumnManager("起终", rd -> {
			boolean start = rd.battleStartNext.isStart();
			boolean goal = rd.battleStartNext.isGoal();
			if (start && goal) return "起终";
			if (start) return "起点";
			if (goal) return "终点";
			return "";
		}));
		tcms.add(new TableColumnManager("Cell", true, rd -> rd.battleStartNext.getNext()));
		tcms.add(new TableColumnManager("敌舰队", rd -> rd.battleResult.getDeckName()));
		tcms.add(new TableColumnManager("Boss", rd -> rd.battleStartNext.isBoss() ? "是" : ""));
		tcms.add(new TableColumnManager("评价", rd -> {
			String rank = rd.battleResult.getRank();
			if (!rd.haveDamage && rank.startsWith("S")) {
				return "S完全胜利";
			} else {
				return rank;
			}
		}));
		tcms.add(new TableColumnManager("舰种", rd -> FunctionUtils.notNull(rd.battleResult.getNewShip(), BattleResult_GetShip::getType, "")));
		tcms.add(new TableColumnManager("舰名", rd -> FunctionUtils.notNull(rd.battleResult.getNewShip(), BattleResult_GetShip::getName, "")));

	}

	@Override
	protected void updateData(List<SortDrop> datas) {
		Iterator<AbstractMemory> it = GlobalContext.getMemorylist().memorys.iterator();
		Supplier<BattleDto> next = () -> {
			while (it.hasNext()) {
				AbstractMemory memory = it.next();
				if (memory instanceof BattleDto) {
					return (BattleDto) memory;
				}
			}
			return null;
		};

		//没有演习
		BattleDto battle = null;
		while (it.hasNext()) {
			if (FunctionUtils.isFalse(battle instanceof AbstractInfoBattleStartNext)) {
				battle = next.get();
				continue;
			}
			AbstractInfoBattleStartNext battleStartNext = (AbstractInfoBattleStartNext) battle;

			battle = next.get();
			if (battle instanceof InfoBattleStartAirBaseDto) {
				battle = next.get();
			}
			if (FunctionUtils.isFalse(battle instanceof AbstractBattle)) continue;
			long time = battle.getTime();//展示时间选择为战斗开始时间
			boolean haveDamage = BattleDtoTranslator.haveDamage((AbstractBattle) battle);

			battle = next.get();
			if (battle instanceof AbstractBattle) {
				haveDamage |= BattleDtoTranslator.haveDamage((AbstractBattle) battle);
				battle = next.get();
			}
			if (FunctionUtils.isFalse(battle instanceof InfoBattleResultDto)) continue;
			InfoBattleResultDto battleResult = (InfoBattleResultDto) battle;

			datas.add(new SortDrop(battleStartNext, time, haveDamage, battleResult));
		}

		Collections.reverse(datas);
	}

	protected class SortDrop {
		private final long time;
		private final boolean haveDamage;
		private final AbstractInfoBattleStartNext battleStartNext;
		private final AbstractInfoBattleResult battleResult;

		public SortDrop(AbstractInfoBattleStartNext battleStartNext, long time, boolean haveDamage, AbstractInfoBattleResult battleResult) {
			this.time = time;
			this.haveDamage = haveDamage;
			this.battleStartNext = battleStartNext;
			this.battleResult = battleResult;
		}
	}
}
