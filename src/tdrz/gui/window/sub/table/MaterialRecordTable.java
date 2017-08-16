package tdrz.gui.window.sub.table;

import java.util.List;
import java.util.stream.IntStream;

import tdrz.core.logic.TimeString;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.memory.ResourceRecordDto;
import tdrz.update.dto.word.ResourceDto;

/**
 * 资源记录
 * @author MoeKagari
 */
public class MaterialRecordTable extends AbstractTable<ResourceRecordDto> {
	public MaterialRecordTable(ApplicationMain main, String title) {
		super(main, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("事件", ResourceRecordDto::getEvent));
		tcms.add(new TableColumnManager("日期", rd -> TimeString.formatForTable(rd.getTime())));
		IntStream.range(0, ResourceDto.getResourceText().length)//
				.mapToObj(index -> new TableColumnManager(ResourceDto.getResourceText()[index], true, rd -> rd.getMaterial()[index]))//
				.forEach(tcms::add);
	}

	@Override
	protected void updateData(List<ResourceRecordDto> datas) {
		GlobalContext.getMemorylist().memorys.forEach(memory -> {
			if (memory instanceof ResourceRecordDto) {
				datas.add((ResourceRecordDto) memory);
			}
		});
	}
}
