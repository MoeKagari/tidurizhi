package tdrz.gui.window.sub.table;

import java.util.List;

import tdrz.core.logic.TimeString;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.memory.DestroyShipDto;

/**
 * 解体记录
 * @author MoeKagari
 */
public class DestroyShipTable extends AbstractTable<DestroyShipDto> {

	public DestroyShipTable(ApplicationMain main, String title) {
		super(main, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.formatForTable(rd.getTime())));
		tcms.add(new TableColumnManager("事件", DestroyShipDto::getEvent));
		tcms.add(new TableColumnManager("舰娘", DestroyShipDto::getName));
		tcms.add(new TableColumnManager("ID", true, DestroyShipDto::getId));
		tcms.add(new TableColumnManager("等级", true, DestroyShipDto::getLevel));
	}

	@Override
	protected void updateData(List<DestroyShipDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof DestroyShipDto) {
				datas.add((DestroyShipDto) memory);
			}
		});
	}
}
