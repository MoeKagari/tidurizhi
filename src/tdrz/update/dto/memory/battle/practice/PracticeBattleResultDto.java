package tdrz.update.dto.memory.battle.practice;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractInfoBattleResult;

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
