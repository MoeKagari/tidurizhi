package tdrz.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractBattleDay;
import tdrz.update.data.ApiData;

public class BattleAirbattleDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public BattleAirbattleDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}