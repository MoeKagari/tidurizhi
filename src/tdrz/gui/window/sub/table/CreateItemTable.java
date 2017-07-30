package tdrz.gui.window.sub.table;

import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import tdrz.dto.memory.CreateItemDto;
import tdrz.dto.translator.MasterDataTranslator;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.logic.TimeString;
import tdrz.update.GlobalContext;

/**
 * 开发记录
 * @author MoeKagari
 */
public class CreateItemTable extends AbstractTable<CreateItemDto> {

	public CreateItemTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.formatForTable(rd.getTime())));
		tcms.add(new TableColumnManager("状态", rd -> rd.isSuccess() ? "成功" : ""));
		tcms.add(new TableColumnManager("装备", rd -> rd.isSuccess() ? MasterDataTranslator.getSlotitemName(rd.getSlotitemId()) : ""));
		tcms.add(new TableColumnManager("油", true, rd -> rd.getMaterial()[0]));
		tcms.add(new TableColumnManager("弹", true, rd -> rd.getMaterial()[1]));
		tcms.add(new TableColumnManager("钢", true, rd -> rd.getMaterial()[2]));
		tcms.add(new TableColumnManager("铝", true, rd -> rd.getMaterial()[3]));
	}

	@Override
	protected void updateData(List<CreateItemDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof CreateItemDto) {
				datas.add((CreateItemDto) memory);
			}
		});
	}
}
