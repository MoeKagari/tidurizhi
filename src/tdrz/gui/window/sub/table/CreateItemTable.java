package tdrz.gui.window.sub.table;

import java.util.List;

import tdrz.core.logic.TimeString;
import tdrz.core.translator.MasterDataTranslator;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.memory.CreateItemDto;

/**
 * 开发记录
 * 
 * @author MoeKagari
 */
public class CreateItemTable extends AbstractTable<CreateItemDto> {
	@Override
	public String defaultTitle() {
		return "开发记录";
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
		tcms.add(new TableColumnManager("秘书舰舰种", rd -> rd.getSecretaryShip().getTypeString()));
		tcms.add(new TableColumnManager("秘书舰", rd -> rd.getSecretaryShip().getName()));
		tcms.add(new TableColumnManager("秘书舰等级", rd -> rd.getSecretaryShip().getLevel()));
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
