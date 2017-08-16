package tdrz.gui.window.sub.table;

import java.util.List;
import java.util.function.IntFunction;

import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.DataType;
import tdrz.update.dto.word.QuestDto;
import tdrz.update.dto.word.QuestDto.QuestInformationDto;
import tool.FunctionUtils;

/**
 * 所有任务
 * @author MoeKagari
 */
public class QuestListTable extends AbstractTable<QuestDto> {
	public QuestListTable(ApplicationMain main, String title) {
		super(main, title);
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("编号", true, FunctionUtils.andThen(QuestDto::getInformation, QuestInformationDto::getNo)));
		tcms.add(new TableColumnManager("状态", FunctionUtils.andThen(QuestDto::getInformation, QuestInformationDto::getStateString)));
		tcms.add(new TableColumnManager("进度", FunctionUtils.andThen(QuestDto::getInformation, QuestInformationDto::getProcess)));
		tcms.add(new TableColumnManager("任务名", FunctionUtils.andThen(QuestDto::getInformation, QuestInformationDto::getTitle)));
		tcms.add(new TableColumnManager("种类", FunctionUtils.andThen(QuestDto::getInformation, QuestInformationDto::getCategoryString)));
		tcms.add(new TableColumnManager("类型", FunctionUtils.andThen(QuestDto::getInformation, QuestInformationDto::getTypeString)));
		IntFunction<String> materialString = material -> material <= 0 ? "" : String.valueOf(material);
		tcms.add(new TableColumnManager("油", true, rd -> materialString.apply(rd.getInformation().getMaterial()[0])));
		tcms.add(new TableColumnManager("弹", true, rd -> materialString.apply(rd.getInformation().getMaterial()[1])));
		tcms.add(new TableColumnManager("钢", true, rd -> materialString.apply(rd.getInformation().getMaterial()[2])));
		tcms.add(new TableColumnManager("铝", true, rd -> materialString.apply(rd.getInformation().getMaterial()[3])));
		tcms.add(new TableColumnManager("描述", rd -> rd.getInformation().getDetail()));
	}

	@Override
	protected boolean isTableSortable() {
		return false;
	}

	@Override
	protected void updateData(List<QuestDto> datas) {
		datas.addAll(GlobalContext.getQuestlist());
	}

	@Override
	protected boolean disposeAndUpdate() {
		return false;
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return type == DataType.QUEST_CLEAR || type == DataType.QUEST_LIST || type == DataType.QUEST_START || type == DataType.QUEST_STOP;
	}
}
