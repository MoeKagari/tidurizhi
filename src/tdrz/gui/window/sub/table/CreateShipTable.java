package tdrz.gui.window.sub.table;

import java.util.List;

import tdrz.core.logic.TimeString;
import tdrz.core.translator.MasterDataTranslator;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.memory.CreateShipDto;

/**
 * 建造记录
 * 
 * @author MoeKagari
 */
public class CreateShipTable extends AbstractTable<CreateShipDto> {
	@Override
	public String defaultTitle() {
		return "建造记录";
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("日期", rd -> TimeString.formatForTable(rd.getTime())));
		tcms.add(new TableColumnManager("舰娘", rd -> MasterDataTranslator.getShipName(rd.getShipId())));
		tcms.add(new TableColumnManager("油", true, rd -> Integer.toString(rd.cost()[0])));
		tcms.add(new TableColumnManager("弹", true, rd -> Integer.toString(rd.cost()[1])));
		tcms.add(new TableColumnManager("钢", true, rd -> Integer.toString(rd.cost()[2])));
		tcms.add(new TableColumnManager("铝", true, rd -> Integer.toString(rd.cost()[3])));
		tcms.add(new TableColumnManager("开发资材", true, rd -> Integer.toString(rd.zhicai())));
		tcms.add(new TableColumnManager("大型建造", rd -> rd.largeflag() ? "是" : ""));
		tcms.add(new TableColumnManager("高速建造", rd -> rd.highspeed() ? "是" : ""));
		tcms.add(new TableColumnManager("空渠", true, rd -> String.valueOf(rd.getEmptyCount())));
		tcms.add(new TableColumnManager("秘书舰舰种", rd -> rd.getSecretaryShip().getTypeString()));
		tcms.add(new TableColumnManager("秘书舰", rd -> rd.getSecretaryShip().getName()));
		tcms.add(new TableColumnManager("秘书舰等级", rd -> rd.getSecretaryShip().getLevel()));
	}

	@Override
	protected void updateData(List<CreateShipDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof CreateShipDto) {
				datas.add((CreateShipDto) memory);
			}
		});
	}
}
