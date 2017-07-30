package tdrz.dto.memory.battle.practice;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractBattleMidnight;
import tdrz.update.data.ApiData;

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
