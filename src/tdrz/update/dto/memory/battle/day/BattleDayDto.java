package tdrz.update.dto.memory.battle.day;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractBattleDay;

public class BattleDayDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public BattleDayDto(ApiData data, JsonObject json) {
		super(data, json);
	}

	@Override
	protected int getRaigekiIndex() {
		return 4;
	}
}
