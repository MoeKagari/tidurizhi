package tdrz.dto.memory.battle.info;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractInfoBattleStartNext;
import tdrz.update.data.ApiData;

public class InfoBattleNextDto extends AbstractInfoBattleStartNext {
	private static final long serialVersionUID = 1L;

	public InfoBattleNextDto(ApiData data, JsonObject json) {
		super(data, json);
	}
}
