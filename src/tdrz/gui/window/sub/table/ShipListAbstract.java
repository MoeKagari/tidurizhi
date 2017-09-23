package tdrz.gui.window.sub.table;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import tdrz.core.logic.TimeString;
import tdrz.core.translator.ItemDtoTranslator;
import tdrz.core.translator.ShipDtoTranslator;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.word.ItemDto;
import tdrz.update.dto.word.MasterDataDto.MasterShipDto;
import tdrz.update.dto.word.ShipDto;
import tool.function.FunctionUtils;

public abstract class ShipListAbstract extends AbstractTable<ShipDto> {
	protected abstract ShipListTableMode getMode();

	protected enum ShipListTableMode {
		INFORMATION("所有舰娘(信息)"),
		PARAMENTER("所有舰娘(属性)"),
		ALL("所有舰娘(综合)");

		protected final String title;

		private ShipListTableMode(String title) {
			this.title = title;
		}
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("ID", true, ShipDto::getId));
		tcms.add(new TableColumnManager("舰娘", ShipDtoTranslator::getName));
		tcms.add(new TableColumnManager("舰种", ShipDtoTranslator::getTypeString));
		tcms.add(new TableColumnManager("等级", true, ShipDto::getLevel));
		tcms.add(new TableColumnManager("所处", ShipDtoTranslator::whichDeckString));

		switch (this.getMode()) {
			case INFORMATION:
				this.initTCMS_information(tcms);
				break;
			case PARAMENTER:
				this.initTCMS_paramenter(tcms);
				break;
			case ALL:
				this.initTCMS_information(tcms);
				this.initTCMS_paramenter(tcms);
				break;
		}

		IntStream.range(0, 5)//
				.mapToObj(index -> new TableColumnManager(//
						String.format("装备%d", index + 1), //
						rd -> {
							ItemDto item = GlobalContext.getItem(index == 4 ? rd.getSlotex() : rd.getSlots()[index]);
							return FunctionUtils.notNull(item, ItemDtoTranslator::getNameWithLevel, "");
						}//
				))//
				.forEach(tcms::add);
		tcms.add(new TableColumnManager("出击海域", rd -> FunctionUtils.ifFunction(rd.getSallyArea(), sa -> sa != 0, String::valueOf, "")));
	}

	private void initTCMS_information(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("补给", rd -> ShipDtoTranslator.needHokyo(rd) ? "需要" : ""));
		tcms.add(new TableColumnManager("Cond", true, ShipDto::getCond));
		tcms.add(new TableColumnManager("现有经验", true, ShipDto::getCurrentExp));
		tcms.add(new TableColumnManager("升级所需", true, ShipDto::getNextExp));
		tcms.add(new TableColumnManager("现在耐久", true, ShipDto::getNowHp));
		tcms.add(new TableColumnManager("最大耐久", true, ShipDto::getMaxHp));
		tcms.add(new TableColumnManager("速力", rd -> ShipDtoTranslator.getSokuString(rd, false)));
		tcms.add(new TableColumnManager("增设", rd -> rd.getSlotex() != 0 ? "有" : ""));
		tcms.add(new TableColumnManager("Lock", rd -> rd.isLocked() ? "" : "无"));
		tcms.add(new TableColumnManager("远征中", rd -> ShipDtoTranslator.isInMission(rd) ? "是" : ""));
		tcms.add(new TableColumnManager("入渠中", rd -> ShipDtoTranslator.isInNyukyo(rd) ? "是" : ""));
		tcms.add(new TableColumnManager("油耗", true, rd -> FunctionUtils.notNull(rd.getMasterData(), MasterShipDto::getFuelMax, "")));
		tcms.add(new TableColumnManager("弹耗", true, rd -> FunctionUtils.notNull(rd.getMasterData(), MasterShipDto::getBullMax, "")));
		tcms.add(new TableColumnManager("消耗", true, rd -> FunctionUtils.notNull(rd.getMasterData(), msd -> msd.getFuelMax() + msd.getBullMax(), "")));
		{
			TableColumnManager tcm = new TableColumnManager("状态", rd -> ShipDtoTranslator.getStateString(rd, false));
			tcm.setComparator(Comparator.comparingDouble(ShipDtoTranslator::getHPPercent));
			tcms.add(tcm);
		}
		{
			TableColumnManager tcm = new TableColumnManager("修理时间", rd -> TimeString.toDateRestString(rd.getNdockTime() / 1000, ""));
			tcm.setComparator(Comparator.comparingLong(ShipDto::getNdockTime));
			tcms.add(tcm);
		}
		{
			TableColumnManager tcm = new TableColumnManager("修理花费", rd -> ShipDtoTranslator.perfectState(rd) ? "" : Arrays.toString(rd.getNdockCost()));
			tcm.setComparator(Comparator.comparingInt(rd -> rd.getNdockCost()[0]));
			tcms.add(tcm);
		}
	}

	private void initTCMS_paramenter(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("火力", true, rd -> rd.getKaryoku()[0]));
		tcms.add(new TableColumnManager("雷装", true, rd -> rd.getRaisou()[0]));
		tcms.add(new TableColumnManager("对空", true, rd -> rd.getTaiku()[0]));
		tcms.add(new TableColumnManager("装甲", true, rd -> rd.getSoukou()[0]));
		tcms.add(new TableColumnManager("回避", true, rd -> rd.getKaihi()[0]));
		tcms.add(new TableColumnManager("对潜", true, rd -> rd.getTaisen()[0]));
		tcms.add(new TableColumnManager("索敌", true, rd -> rd.getSakuteki()[0]));
		tcms.add(new TableColumnManager("运", true, rd -> rd.getLuck()[0]));
		tcms.add(new TableColumnManager("改造等级", true, rd -> FunctionUtils.notNull(rd.getMasterData(), md -> {
			return FunctionUtils.ifFunction(md.getGaizhaoLv(), level -> level != 0, String::valueOf, "");
		}, "")));
		tcms.add(new TableColumnManager("炮击战", true, ShipDtoTranslator::getPowerHougeki));
		tcms.add(new TableColumnManager("雷击战", true, ShipDtoTranslator::getPowerRageki));
		tcms.add(new TableColumnManager("夜战", true, ShipDtoTranslator::getPowerMidnight));
	}
}
