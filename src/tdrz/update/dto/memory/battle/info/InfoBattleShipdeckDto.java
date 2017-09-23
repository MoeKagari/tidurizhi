package tdrz.update.dto.memory.battle.info;

import javax.json.JsonObject;

import tdrz.core.translator.ShipDtoTranslator;
import tdrz.update.context.data.ApiData;
import tdrz.update.dto.memory.battle.AbstractInfoBattle;
import tdrz.update.dto.word.ShipDto;

public class InfoBattleShipdeckDto extends AbstractInfoBattle {
	private static final long serialVersionUID = 1L;
	private final boolean hasDapo;

	public InfoBattleShipdeckDto(ApiData data, JsonObject json) {
		super(data.getTime());

		this.hasDapo = json.getJsonArray("api_ship_data").getValuesAs(JsonObject.class).stream()//
				.map(ShipDto::new)//
				.anyMatch(ShipDtoTranslator::dapo);
	}

	public boolean hasDapo() {
		return this.hasDapo;
	}
}
