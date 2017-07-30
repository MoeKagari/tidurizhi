package tdrz.dto.memory.battle.info;

import javax.json.JsonObject;

import tdrz.dto.memory.battle.AbstractInfoBattle;
import tdrz.dto.translator.ShipDtoTranslator;
import tdrz.dto.word.ShipDto;
import tdrz.update.data.ApiData;

public class InfoBattleShipdeckDto extends AbstractInfoBattle {
	private static final long serialVersionUID = 1L;
	private final boolean hasDapo;

	public InfoBattleShipdeckDto(ApiData data, JsonObject json) {
		this.hasDapo = json.getJsonArray("api_ship_data").getValuesAs(JsonObject.class).stream().map(ShipDto::new).anyMatch(ShipDtoTranslator::dapo);
	}

	public boolean hasDapo() {
		return this.hasDapo;
	}
}
