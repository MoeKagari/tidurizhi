package tdrz.update.dto.memory.battle.info;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractInfoBattleResult;

public class InfoBattleResultDto extends AbstractInfoBattleResult {
	private static final long serialVersionUID = 1L;

	public InfoBattleResultDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}
