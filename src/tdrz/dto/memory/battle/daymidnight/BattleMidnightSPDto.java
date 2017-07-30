package tdrz.dto.memory.battle.daymidnight;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractBattleMidnight;
import tdrz.update.data.ApiData;

public class BattleMidnightSPDto extends AbstractBattleMidnight {
	private static final long serialVersionUID = 1L;

	public BattleMidnightSPDto(ApiData data, JsonObject json) {
		super(data, json);
	}

	@Override
	public boolean isMidnightOnly() {
		return true;
	}
}
