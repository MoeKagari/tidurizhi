package tdrz.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractBattleDay;
import tdrz.update.data.ApiData;

public class CombinebattleECDayDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public CombinebattleECDayDto(ApiData data, JsonObject json) {
		super(data, json);
	}

	@Override
	protected int getRaigekiIndex() {
		return 2;
	}
}
