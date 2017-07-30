package tdrz.dto.memory.battle.info;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractInfoBattleResult;
import tdrz.update.data.ApiData;

public class InfoBattleResultDto extends AbstractInfoBattleResult {
	private static final long serialVersionUID = 1L;

	public InfoBattleResultDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}
