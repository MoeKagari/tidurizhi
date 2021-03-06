package tdrz.gui.window.sub.table;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import tdrz.gui.window.sub.AbstractTable;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.DataType;
import tdrz.update.dto.word.MapinfoDto;
import tdrz.update.dto.word.MapinfoDto.OneMap;
import tool.function.FunctionUtils;

/**
 *  有血条的图的详情 
 * @author MoeKagari
 */
public class MapListTable extends AbstractTable<MapinfoDto.OneMap> {
	@Override
	public String defaultTitle() {
		return "地图详情";
	}

	@Override
	protected Predicate<OneMap> initFilter() {
		return map -> map.getHP()[0] <= 0;
	}

	@Override
	protected boolean disposeAndUpdate() {
		return false;
	}

	@Override
	protected boolean isTableSortable() {
		return false;
	}

	@Override
	protected boolean haveRowHeader() {
		return false;
	}

	@Override
	protected void initTCMS(List<TableColumnManager> tcms) {
		tcms.add(new TableColumnManager("地图", rd -> String.format("%d-%d", rd.getArea(), rd.getNo())));
		tcms.add(new TableColumnManager("血量", rd -> Arrays.toString(rd.getHP())));
		tcms.add(new TableColumnManager("可出击基地航空队", rd -> FunctionUtils.ifFunction(rd.getAirBaseDeckCount(), i -> i > 0, String::valueOf, "")));
		tcms.add(new TableColumnManager("活动地图", rd -> rd.isEventMap() ? "是" : ""));
		tcms.add(new TableColumnManager("Rank", rd -> rd.isEventMap() ? rd.getEventMap().getRank() : ""));
		tcms.add(new TableColumnManager("血条类型", rd -> rd.isEventMap() ? rd.getEventMap().getHptype() : ""));

	}

	@Override
	protected void updateData(List<OneMap> datas) {
		FunctionUtils.notNull(GlobalContext.getMapinfo(), mapinfo -> datas.addAll(mapinfo.getMaps()));
	}

	@Override
	protected boolean needUpdate(DataType type) {
		return type == DataType.MAPINFO;
	}
}
