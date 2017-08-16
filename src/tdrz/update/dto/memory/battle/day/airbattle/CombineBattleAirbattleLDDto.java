package tdrz.update.dto.memory.battle.day.airbattle;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractBattleDay;

public class CombineBattleAirbattleLDDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public CombineBattleAirbattleLDDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}
