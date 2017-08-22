package tdrz.gui.window.sub.table;

import java.util.List;
import java.util.function.IntFunction;

import tdrz.core.config.AppConstants;
import tdrz.core.logic.TimeString;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.memory.MissionResultDto;
import tdrz.update.dto.memory.MissionResultDto.MissionResultItem;
import tool.function.FunctionUtils;

/**
 * 远征记录
 * @author MoeKagari
 */
public class MissionResultTable extends AbstractTable<MissionResultDto> {
	@Override
	public String defaultTitle() {
		return "远征记录";
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.formatForTable(rd.getTime())));
		{
			TableColumnManager tcm = new TableColumnManager("舰队", rd -> AppConstants.DEFAULT_FLEET_NAME[rd.getDeckId() - 1]);
			tcm.setComparator((a, b) -> Integer.compare(a.getDeckId(), b.getDeckId()));
			tcms.add(tcm);
		}
		tcms.add(new TableColumnManager("远征", MissionResultDto::getName));
		tcms.add(new TableColumnManager("状态", MissionResultDto::getStateString));
		IntFunction<String> materialString = count -> count == 0 ? "" : String.valueOf(count);
		tcms.add(new TableColumnManager("油", true, rd -> materialString.apply(rd.getMaterial()[0])));
		tcms.add(new TableColumnManager("弹", true, rd -> materialString.apply(rd.getMaterial()[1])));
		tcms.add(new TableColumnManager("钢", true, rd -> materialString.apply(rd.getMaterial()[2])));
		tcms.add(new TableColumnManager("铝", true, rd -> materialString.apply(rd.getMaterial()[3])));
		tcms.add(new TableColumnManager("道具1", rd -> FunctionUtils.notNull(rd.getItems()[0], MissionResultItem::getName, "")));
		tcms.add(new TableColumnManager("数量", true, rd -> FunctionUtils.notNull(rd.getItems()[0], MissionResultItem::getCount, "")));
		tcms.add(new TableColumnManager("道具2", rd -> FunctionUtils.notNull(rd.getItems()[1], MissionResultItem::getName, "")));
		tcms.add(new TableColumnManager("数量", true, rd -> FunctionUtils.notNull(rd.getItems()[1], MissionResultItem::getCount, "")));
	}

	@Override
	protected void updateData(List<MissionResultDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof MissionResultDto) {
				datas.add((MissionResultDto) memory);
			}
		});
	}
}
