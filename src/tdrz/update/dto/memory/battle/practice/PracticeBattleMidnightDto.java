package tdrz.update.dto.memory.battle.practice;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractBattleMidnight;

public class PracticeBattleMidnightDto extends AbstractBattleMidnight {
	private static final long serialVersionUID = 1L;

	public PracticeBattleMidnightDto(ApiData data, JsonObject json) {
		super(data, json);
	}

	@Override
	public boolean isPractice() {
		return true;
	}
}
