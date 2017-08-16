package tdrz.update.dto.memory.battle.info;

import javax.json.JsonObject;

import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractInfoBattleStartNext;

public class InfoBattleNextDto extends AbstractInfoBattleStartNext {
	private static final long serialVersionUID = 1L;

	public InfoBattleNextDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}
