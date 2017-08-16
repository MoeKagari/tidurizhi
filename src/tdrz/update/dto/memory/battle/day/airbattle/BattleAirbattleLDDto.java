package tdrz.update.dto.memory.battle.day.airbattle;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractBattleDay;

public class BattleAirbattleLDDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public BattleAirbattleLDDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}
