package tdrz.gui.window.sub.table;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.MenuItem;

import tdrz.dto.word.UseItemDto;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.GlobalContext;
import tdrz.update.data.DataType;
import tool.FunctionUtils;

public class UserItemListTable extends AbstractTable<UseItemDto> {
	public UserItemListTable(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("道具", rd -> FunctionUtils.notNull(rd.getMasterData(), md -> md.getName(), "")));
		tcms.add(new TableColumnManager("数量", rd -> rd.getCount()));
		tcms.add(new TableColumnManager("描述1", rd -> FunctionUtils.notNull(rd.getMasterData(), md -> md.getDescription()[0], "")));
		tcms.add(new TableColumnManager("描述2", rd -> FunctionUtils.notNull(rd.getMasterData(), md -> md.getDescription()[1], "")));
	}

	@Override
	protected boolean isTableSortable() {
		return false;
	}

	@Override
	protected void updateData(List<UseItemDto> datas) {
		datas.addAll(GlobalContext.getUseitemMap().values());
		Collections.sort(datas, (a, b) -> Integer.compare(a.getId(), b.getId()));
	}

	@Override
	protected boolean disposeAndUpdate() {
		return false;
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return type == DataType.REQUIRE_INFO || type == DataType.USEITEM;
	}
}
