package tdrz.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractBattleDay;
import tdrz.update.data.ApiData;

public class CombineBattleAirbattleDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public CombineBattleAirbattleDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}
