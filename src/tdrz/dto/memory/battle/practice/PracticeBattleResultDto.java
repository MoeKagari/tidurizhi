package tdrz.dto.memory.battle.practice;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractInfoBattleResult;
import tdrz.update.data.ApiData;

public class PracticeBattleResultDto extends AbstractInfoBattleResult {
	private static final long serialVersionUID = 1L;

	public PracticeBattleResultDto(ApiData data, JsonObject json) {
		super(data, json);
	}

	@Override
	public boolean isPractice() {
		return true;
	}
}
