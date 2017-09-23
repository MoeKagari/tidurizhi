package tdrz.gui.window.sub.table;

import java.util.List;

import tdrz.core.logic.TimeString;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.memory.DestroyItemDto;

/**
 * 废弃记录
 * 
 * @author MoeKagari
 */
public class DestroyItemTable extends AbstractTable<DestroyItemDto> {
	@Override
	public String defaultTitle() {
		return "废弃记录";
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.formatForTable(rd.getTime())));
		tcms.add(new TableColumnManager("事件", DestroyItemDto::getEvent));
		tcms.add(new TableColumnManager("ID", true, rd -> rd.getItem().getId()));
		tcms.add(new TableColumnManager("装备", rd -> rd.getItem().toString()));
		tcms.add(new TableColumnManager("组", true, DestroyItemDto::getGroup));
	}

	@Override
	protected void updateData(List<DestroyItemDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof DestroyItemDto) {
				datas.add((DestroyItemDto) memory);
			}
		});
	}
}
