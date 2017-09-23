package tdrz.update.dto.memory.battle.info;

import java.util.stream.IntStream;

import javax.json.JsonObject;

import tdrz.core.util.ToolUtils;
import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractInfoBattle;

public class InfoBattleStartAirBaseDto extends AbstractInfoBattle {
	private static final long serialVersionUID = 1L;
	/* 三支路基的打击点 */
	private final int strikePoint[][];

	public InfoBattleStartAirBaseDto(ApiData data, JsonObject json) {
		super(data.getTime());

		this.strikePoint = IntStream.rangeClosed(1, 3)//
				.mapToObj(i -> String.format("api_strike_point_%d", i))//
				.map(data::getField)//
				.map(sp -> {
					if (sp == null) {
						return null;
					} else {
						return ToolUtils.toIntArray(sp.split(","), Integer::parseInt);
					}
				}).toArray(int[][]::new);
	}

	public int[][] getStrikePoint() {
		return this.strikePoint;
	}
}
