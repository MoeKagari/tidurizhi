package tdrz.update.dto.memory.battle.practice;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractBattleDay;

public class PracticeBattleDayDto extends AbstractBattleDay {
	private static final long serialVersionUID = 1L;

	public PracticeBattleDayDto(ApiData data, JsonObject json) {
		super(data, json);
	}

	@Override
	protected int getRaigekiIndex() {
		return 4;
	}

	@Override
	public boolean isPractice() {
		return true;
	}
}
