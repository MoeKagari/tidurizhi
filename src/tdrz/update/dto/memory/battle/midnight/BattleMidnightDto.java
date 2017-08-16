package tdrz.update.dto.memory.battle.midnight;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractBattleMidnight;

public class BattleMidnightDto extends AbstractBattleMidnight {
	private static final long serialVersionUID = 1L;

	public BattleMidnightDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}
