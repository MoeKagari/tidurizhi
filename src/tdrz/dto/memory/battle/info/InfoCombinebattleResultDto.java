package tdrz.dto.memory.battle.info;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractInfoBattleResult;
import tdrz.update.data.ApiData;

public class InfoCombinebattleResultDto extends AbstractInfoBattleResult {
	private static final long serialVersionUID = 1L;

	public InfoCombinebattleResultDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}
