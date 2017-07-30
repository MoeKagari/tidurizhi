package tdrz.gui.window.sub.table;

import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import tdrz.dto.memory.CreateshipDto;
import tdrz.dto.translator.MasterDataTranslator;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.logic.TimeString;
import tdrz.update.GlobalContext;
import tool.FunctionUtils;

/**
 * 建造记录
 * @author MoeKagari
 */
public class CreateShipTable extends AbstractTable<CreateshipDto> {

	public CreateShipTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
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
		tcms.add(new TableColumnManager("秘书舰", rd -> FunctionUtils.notNull(rd.getFlagship(), FunctionUtils::returnSelf, "")));
		tcms.add(new TableColumnManager("秘书舰LV", rd -> FunctionUtils.ifSupplier(rd.getFlagship() != null, () -> String.valueOf(rd.getFlagshipLevel()), "")));
		tcms.add(new TableColumnManager("空渠", true, rd -> String.valueOf(rd.getEmptyCount())));
	}

	@Override
	protected void updateData(List<CreateshipDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof CreateshipDto) {
				datas.add((CreateshipDto) memory);
			}
		});
	}
}
