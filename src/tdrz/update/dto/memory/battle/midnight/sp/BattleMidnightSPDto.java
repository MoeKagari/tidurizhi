package tdrz.update.dto.memory.battle.midnight.sp;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractBattleMidnight;

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
