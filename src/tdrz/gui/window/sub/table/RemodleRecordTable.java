package tdrz.gui.window.sub.table;

import java.util.List;

import tdrz.core.logic.TimeString;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.memory.RemodleRecordDto;
import tool.function.FunctionUtils;

/**
 * 改修记录
 * 
 * @author MoeKagari
 */
public class RemodleRecordTable extends AbstractTable<RemodleRecordDto> {
	@Override
	public String defaultTitle() {
		return "改修记录";
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.formatForTable(rd.getTime())));
		tcms.add(new TableColumnManager("装备ID", true, RemodleRecordDto::getSlotId));
		tcms.add(new TableColumnManager("成功", rd -> rd.isSuccess() ? "是" : ""));
		tcms.add(new TableColumnManager("确保", rd -> rd.isCertain() ? "是" : ""));
		tcms.add(new TableColumnManager("更新", rd -> rd.isUpdate() ? "是" : ""));
		tcms.add(new TableColumnManager("原装备", rd -> rd.getItem().toString()));
		tcms.add(new TableColumnManager("新装备", rd -> FunctionUtils.notNull(rd.getNewItem(), Object::toString, "")));
	}

	@Override
	protected void updateData(List<RemodleRecordDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof RemodleRecordDto) {
				datas.add((RemodleRecordDto) memory);
			}
		});
	}
}
