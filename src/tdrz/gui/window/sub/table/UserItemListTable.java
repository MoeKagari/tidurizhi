package tdrz.gui.window.sub.table;

import java.util.Comparator;
import java.util.List;

import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.DataType;
import tdrz.update.dto.word.UseItemDto;
import tool.function.FunctionUtils;

public class UserItemListTable extends AbstractTable<UseItemDto> {
	@Override
	public String defaultTitle() {
		return "所有道具";
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
		datas.sort(Comparator.comparingInt(UseItemDto::getId));
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
